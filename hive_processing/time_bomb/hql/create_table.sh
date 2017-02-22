#!/bin/bash
export PATH=$PATH:/opt/cloudera/parcels/CDH/lib/hive/bin/hive
hive -f /home/spark/time_bomb/hql/create_table.sql

#sudo yum -y install ntpdate
#sudo ntpdate O.asia.pool.ntp.org
#将当前时期，时间写入BIOS
#hwclock -w
