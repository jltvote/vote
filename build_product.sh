#!/bin/bash
export JAVA_HOME=/usr/java/jdk1.8.0_131
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

log_path=/root/logs/vote
mvn -Dmaven.test.skip=true clean -e package
if [ ! -d "/opt/vote/server" ];then
        sudo mkdir -p /opt/vote/server
fi;

echo '==================move============'
mv target/vote-1.0-SNAPSHOT.war /opt/vote/server
cd /opt/vote/server

echo ==================kill vote================
ps -ef|grep vote-1.0-SNAPSHOT.war|grep -v grep |awk '{print $2}'|xargs kill -9

echo ==================start Bootstrap================
nohup java -Xms128m -Xmx128m -XX:MaxPermSize=128M -XX:SurvivorRatio=3 -XX:NewRatio=1 -server -jar vote-1.0-SNAPSHOT.war --spring.profiles.active=product &
tail -f $log_path/vote.log