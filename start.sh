#! /bin/bash
#不更新脚本-不丢弃本地修改(1)自己保证代码无误
#不更新脚本-丢弃本地修改(2)
#更新脚本-不丢弃本地修改(3)自己保证代码无误
#更新脚本-丢弃本地修改(4)
isUpdate=1
#jdk 目录 例如: c/user/deskTop/jdk1.8/bin 请手动填写
javaPath=c/user/deskTop/jdk1.8/bin
if [ x"$1" = x ]; then
  echo "java path no cmd param!"
  exit 1
fi

uNames=$(uname -s)
osName=${uNames:0:4}
if [ "$osName" == "Darw" ] || [ "$osName" == "Linu" ]; then # Darwin
  if [ "$osName" == "Linu" ]; then                          # Linux
    echo "GNU/Linux"
  else
    echo "Mac OS X"
  fi
  if [ ! -f "${javaPath}/javac" ]; then
    echo "java path does not exist!"
    exit 1
  fi
elif [ "$osName" == "MING" ]; then # MINGW, windows, git-bash
  echo "Windows, git-bash"
  if [ ! -f "${javaPath}/javac.exe" ]; then
    echo "java path does not exist!"
    exit 1
  fi
else
  echo "unknown os not support"
fi

#初始化脚本文件目录
cd "$(
  cd "$(dirname "$0")"
  pwd
)" || return
rm -rf ./*.class

gitPull() {
  while :; do
    if ! git pull; then
      echo "update success,next execute script"
      break
    else
      echo "update fail,Retry"
    fi
  done
}

if [ ${isUpdate} == 4 ]; then
  git checkout -- *
  gitPull
elif [ ${isUpdate} == 3 ]; then
  gitPull
elif [ ${isUpdate} == 2 ]; then
  git checkout -- *
fi

if [ -f "./YuanShen.java" ]; then
  if [ "$osName" == "Darw" ] || [ "$osName" == "Linu" ]; then
    "${javaPath}"/javac -encoding utf-8 YuanShen.java
    echo "The script is compiled"
    "${javaPath}"/java YuanShen
  elif [ "$osName" == "MING" ]; then
    "${javaPath}"/javac.exe -encoding utf-8 YuanShen.java
    echo "The script is compiled"
    "${javaPath}"/java.exe YuanShen
  else
    echo "unknown os not support execute"
  fi
else
  echo "YuanShen.java not exist"
fi

echo "Exit after 10 seconds"
sleep 10
