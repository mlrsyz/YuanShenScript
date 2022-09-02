IntelliJ IDEA
# 脚本介绍
一键执行脚本即可自动获取 原神直播活动(需完成前置直播任务)
## 前置环境:
资源登录账户(请勿滥用) ymx:361905186<br>
[jdk1.8](http://mirrors.ondev.top:60080/tools/jdk-8.0_191.rar)  / [git-bash](http://mirrors.ondev.top:60080/tools/Git-2.31.1-64-bit.exe)
安装后解压(路径无中文) 复制路径文件(例如):C:\user\deskTop\jdk1.8<br>
获取路径windows使用git bash在C:\user\deskTop\jdk1.8执行```pwd```<br>
复制(+bin目录):```c/user/deskTop/jdk1.8/bin```
设置到start.sh中的javaPath后
<br><br>
仔细阅读```cookieFile.properties```文件设置对应的cookie信息
## 执行方式:
windows(使用git bash)/mac/linux: ```sudo sh start.sh```

| #   | 支持平台    | 需要的信息    | 获取路径(点击直达)                                                                                       |
| --- |---------|----------|--------------------------------------------------------------------------------------------------|
| 1   | 哔哩哔哩    | 用户cookie | [F12->XHR->cookie](https://www.bilibili.com/blackboard/activity-award-exchange.html?task_id=cd684a9d) |
| 2   | 斗鱼      | 用户cookie | [F12->XHR->cookie](https://www.douyu.com/topic/ys30?rid=479079)                                  |
| 3   | 虎牙(未支持) | 暂无       | 无                                                                                                |

## 个性化配置
#不更新脚本-不丢弃本地修改(1)自己保证代码无误<br>
#不更新脚本-丢弃本地修改(2)<br>
#更新脚本-不丢弃本地修改(3)自己保证代码无误<br>
#更新脚本-丢弃本地修改(4)<br>
start.sh 中的 isUpdate 默认为1 (不是2,3,4相当于1)

直播活动更新后会更新一次脚本活动数据<br>
所以start.sh脚本中的 isUpdate变量请自行考量定义

| #   | 文件            | 值        | 可改内容 |
| --- |---------------|----------|------|
| 1   | start.sh      | isUpdate | 2,3,4 |
| 1   | javaPath      | 请手动按要求填写 |

# 注意事项*
所有的执行会在获取时间前3s执行(执行后可以去睡觉)

| #   | 文件            | 函数入口                                                                                | 介绍                                                                                              |
| --- |---------------|-------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| 1   | YuanShen.java | main主函数                                                                             | YuanShen.startScript();<br>默认或根据当前环境时间获取之后的第一个活动自动执行                                            |
| 2   | cookieFile.properties | BlActivityType<br>hyActivityType<br>DyActivityType<br/>对应的key配合对应的活动                | bibi_35_1000<br/>huYa_3_330<br/>douYu_26_800<br/>对应的key可指定活动执行                                  |
| 3   | /Enmu/ActivityType.java | ActivityType                                                                        | (不要打乱顺序)活动类型 时间可以自行更改LocalDateTime.of(年, 月, 日, 小时, 分)                                           |
| 4   | /Script/Script.java | Script                                                                              | 有能力的可以自行扩展/修改 脚本实现<br>initData()初始化数据<br>preExecute()线程提交执行前自定义处理(获取网站的前置请求数据)<br>run()抢原石的执行逻辑 <br>goOn整体通过该字段控制最后程序退出(goOn=false)如果没控制好强行关掉dos窗即可(不影响程序功能)|
