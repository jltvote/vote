#!/bin/bash

mvn -Dmaven.test.skip=true clean -P product -e package
if [ ! -d "/opt/vote/server" ];then
        sudo mkdir -p /opt/vote/server
fi;

echo '==================bak============'
cp target/vote-1.0.war /opt/vote-bak/vote-1.0-$(date +%y%m%d_%H:%M:%S).war

echo ==================kill vote================
ps -ef|grep Dcatalina.home=/opt/tomcat-vote|grep -v grep |awk '{print $2}'|xargs kill -9
sleep 3


rm -rf /opt/web-vote/*
unzip target/vote-1.0.war -d /opt/web-vote/

cd /opt/tomcat-vote/bin
./startup.sh
tail -f ../logs/catalina.$(date +%Y-%m-%d).out