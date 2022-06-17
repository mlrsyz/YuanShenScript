#! /bin/bash
javaPath=c:/java
echo ${javaPath}

rm -rf ./*.class

if [ -f "./YuanShen.java" ]; then
	${javaPath}/javac.exe -encoding utf-8 ./YuanShen.java
	echo "The script is compiled"
	${javaPath}/java.exe YuanShen
else
	echo "YuanShen.java not exist"
fi