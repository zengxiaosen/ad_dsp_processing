export PATH=$PATH:/opt/cloudera/parcels/CDH/lib/hive/bin/hive

start = $(date +%s)

hive -f /home/spark/time_bomb/hql/log_hive.hql
hive -f /home/spark/time_bomb/hql/export_userid.hql > /home/spark/data/recomend/output/userid.txt
hive -f /home/spark/time_bomb/hql/export_itemid.hql > /home/spark/data/recomend/output/itemid.txt
hive -f /home/spark/time_bomb/hql/export_scores.hql > /home/spark/data/recomend/output/scores.txt
hive -f /home/spark/time_bomb/hql/export_itemmapping.hql > /home/spark/data/recomend/output/itemmapping.txt

sleep 3m

#去掉第一行数据，清理

sed '1d' /home/spark/data/recomend/output/itemid.txt > /home/spark/data/recomend/output/fs/itemid.c
sed '1d' /home/spark/data/recomend/output/itemmapping.txt > /home/spark/data/recomend/output/fs/itemmapping.c
sed '1d' /home/spark/data/recomend/output/scores.txt > /home/spark/data/recomend/output/fs/scores.c
sed '1d' /home/spark/data/recomend/output/userid.txt > /home/spark/data/recomend/output/fs/userid.c

#HDFS上新建 /user/hadoop/mllib
sudo su hdfs
hadoop fs -mkdir /user/hadoop
hadoop fs -chown spark /user/hadoop
#hadoop fs -rm -r /user/hadoop/mllib
hadoop fs -mkdir /user/hadoop/mllib

#上传数据到hdfs
hadoop fs -put -f /home/spark/data/recomend/output/fs /user/hadoop/mllib

sleep 5m

#映射文件，同步至HBASE
java -jar /home/spark/soledede/spark_recomend/fruitrecomend-1.0.jar --zookeeper_quorum spark1.soledede.com,spark2.soledede.com,spark3.soledede.com /home/spark/data/recomend/output/fs/itemmapping.c

#运行spark进行推荐
/opt/cloudera/parcels/CDH/lib/spark/bin/spark-submit --class com.soledede.cf.FruitRecomendALS /home/spark/soledede/spark_recomend/fruitrecommend-1.0.jar --rank 5 --numIterations 20 --lambda 1.0 --recommendNum 3 --kryo /user/hadoop/mllib/fs/scores.c /user/hadoop/mllib/fs/userid.c /user/hadoop/mllib/fs/itemid.c

end=$(date +%s)

echo $(($end-$start))

#hadoop job -list
#hadoop job -kill job_id

#切换目录到idea的project目录下，直接打包 mvn clean assembly:assembly
#cd target/
#将jar包传到我们的服务器 scp XXX.jar spark1@spark1:/home/spark/hadoop/workcount/
#运行程序
#hadoop jar XXX.jar com.soledede.hadoop.wordcount /user/hadoop/wordcount/input /user/hadoop/wordcount/output




