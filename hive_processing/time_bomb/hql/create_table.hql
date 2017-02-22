--基于模型ALS的协同过滤-hive建表语法（统计PageView，我们的日志表结构）
drop table if exists acesslog;
create external table acesslog(
date string,
time string,
cs_method string,
cs_uri_stem string,
cs_uri_query string,
cs_uri_username string,
c_ip string,
cs_user_agent string,
cs_cookie string,
cs_referer string,
cs_host string,
sc_status string,
sc_substatus string,
sc_win32_status string,
sc_bytes string,
cs_bytes string,
time_taken int)
row format delimited fields terminated by ' ' lines
terminated by '\n';

--hive中null默认是以'\N'表示的，我们想把它变成''空字符串
alter table acesslog set serdeproperties('serialization.null.format'='');

--etl_acesslog建表脚本
drop table if exists etl_acesslog;
create table etl_acesslog (
date string,
userid string,
itemid string,
fruitr_current_visit string,
fruits_pageviews int,
fruits_timestamps string
)
partitioned by (year int,month int, day int)
row format delimited fields terminated by '\t' lines
terminated by '\n';

alter table etl_acesslog set serdeproperties('serialization.null.format' = '');

--创建iis_pageview_scores
drop table if exists iis_pageview_scores;
create table iis_pageview_scores (
userid int,
itemid string,
view_num_score double,
view_time_score double,
view_latest_time string
)
row format delimited fields terminated by '\t' lines
terminated by '\n';

alter table iis_pageview_scores set serdeproperites('serialization.null.format' = '');

--创建item表 item
drop table if exists item;
create table item(
itemid string,
item_time string
)
row format delimited fields terminated by '\t' lines
terminated by '\n';

ALTER TABLE item SET SERDEPROPERTIES('serialization.null.format' = '');

--创建item_mapping表
DROP TABLE IF EXISTS item_mapping;
create table item_mapping(
itemid int,
itemcharc string,
update_time string,
city_id int
)
row format delimited fields terminated by '\t' lines
terminated by '\n';

ALTER TABLE item_mapping SET SERDEPROPERTIES('serialization.null.format' = '');


--创建item_mapping_distinct
DROP TABLE IF EXISTS item_mapping_distinct;
create table item_mapping_distinct(
itemid int,
itemcharc string,
update_time string,
city_id int
)
row format delimited fields terminated by '\t' lines
terminated by '\n';

--创建order_scores 表
DROP TABLE IF EXISTS order_scores;
create table order_scores (
userid int,
itemcharc string,
order_num_score  double,
order_latest_time string
)
row format delimited fields terminated by '\t' lines
terminated by '\n';

ALTER TABLE order_scores SET SERDEPROPERTIES('serialization.null.format' = '');


--创建order_scores_sum
DROP TABLE IF EXISTS order_scores_sum;
create table order_scores_sum(
userid int,
itemcharc string,
order_num_score  double
)
row format delimited fields terminated by '\t' lines
terminated by '\n';
alter table order_scores_sum set serdeproperties('serialization.null.format' = '');

--创建总的score表
DROP TABLE IF EXISTS scores;
create table scores(
userid int,
itemid int,
 scores  double
)
row format delimited fields terminated by '\t' lines
terminated by '\n';
ALTER TABLE scores SET SERDEPROPERTIES('serialization.null.format' = '');


