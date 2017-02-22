import org.apache.hadoop.hive.ql.exec.UDF

/**
  * Created by Administrator on 2017/2/22.
  */

/**
  * 把这个函数打jar包到hive上面
  * 1 mvn clean assembly:assembly （打包）
  * 2 把jar包穿到spark集群上 scp xxx.jar spark@spark1:/home/spark/soledede/spark_recomend
  * 3 自定义UDF jar包使用方式 ADD JAR hdfs://<path to jar>;
  *   或者在本地将jar包传到hive环境，jar tf XXX.jar | grep com/soledede/udf
  *   然后create temporary function timeDecay as 'com.soledede.udf.DecayUDF';
  *   该种方式只有在session范围有效
  *另外几种方式addjar
  * 1设置hive配置文件hive-site.xml hive.aux.jars.path
  * 2在hive-env.sh文件中设置环境变量 export HIVE_AUX_JARS_PATH = "/home/xxx/xxx.jar"
  * 3在${HIVE_HOME}中创建文件夹auxlib, 将jar包放入文件夹中
  *
  * hive常用命令：
  * show database;
  * user default;
  * show tables;
  * select timeDecay(4*60*60*1000);
  *删除函数：
  * drop temporary function if exists timeDecay;
  */
class DecayUDF extends UDF{
  def evaluate(x: Double): Double = evaluate(3.6f, x, 3*60*60*1000f, 1f)
  // x表示理当前时间有多远，第三个参数是衰减指数，比如我们的3就是按3天来进行衰减
  def evaluate(m: Float, x: Double, a: Float, b: Float): Double = {
    /**
      * 时间衰减函数 a / (mx + b)
      * m 是衰减的速率
      */
    if(x == null) return 0
    var m1 = 3.16f
    var a1: Float = 1
    var b1: Float = 1
    if (m != null) m1 = m
    if (a != null) a1 = a
    if (b != null) b1 = b

    var r = f"${a1 / (m1 * x + b1)}%1.2f".toDouble
    if ( r >= 3) 3
    else if (r <= 0) 0
    else r
  }
}

object DecayUDF{
  def main(args: Array[String]) {
    println(new DecayUDF().evaluate(4*60*60*1000))
  }
}
