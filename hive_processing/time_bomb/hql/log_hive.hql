--加载数据
--load data info acesslog
load data local inpath '/home/spark/data/logs/*.log' into table acesslog;

--insert into etl_acesslog
set hive.exec.dynamic.partition=true
set hive.exec.dynamic.partition.mode=nostrict

insert overwrite table etl_acesslog
partition (year, month, day)
select
from_unixtime(unix_timestamp(concat(date,' ',time))) as date,
