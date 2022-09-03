#!/bin/bash
##### 杀死java服务      =========================>start  #####
pid=`ps -ef | grep -E "YuanShenScript-1.0-SNAPSHOT.jar" | grep -v grep  | awk '{print $2}'`

for i in $pid; do
        kill -9 $i
done

echo "YuanShenScript-1.0-SNAPSHOT.jar ===>已杀死"
##### 杀死java服务       =========================>end  #####

##### 编译打包发布       =========================>start  #####
cd /home/apply1/project/YuanShenScript
#git pull
mvn clean package -Dmaven.test.skip=true
##### 编译打包发布       =========================>end    #####

##### 项目发布       =========================>start  #####
nohup java -jar -Xms512m -Xmx1024m  /home/apply1/project/YuanShenScript/target/YuanShenScript-1.0-SNAPSHOT.jar &> /dev/null &
sleep 12
echo "YuanShenScript启动成功"
##### 项目发布       =========================>end  #####
