# ProcessKeepAlive
进程保活防杀

进程保活防杀之使用前台service提高进程优先级，降低被kill概率（注意：在8.0以下系统生效！！！）

app启动位于前台时：

![image](https://github.com/weimodeweimo/ProcessKeepAlive/blob/master/screenshot/one.jpg)

使用命令查看app所在进程的oom_adj值为0，即前台进程：
![image](https://github.com/weimodeweimo/ProcessKeepAlive/blob/master/screenshot/two.jpg)

若不使用前台service，当按home键退到桌面时，
使用命令查看app所在进程的oom_adj值为6：

![image](https://github.com/weimodeweimo/ProcessKeepAlive/blob/master/screenshot/four.jpg)


![image](https://github.com/weimodeweimo/ProcessKeepAlive/blob/master/screenshot/five.jpg)


若不使用前台service，当按back键退到桌面时，
使用命令查看app所在进程的oom_adj值为8：
![image](https://github.com/weimodeweimo/ProcessKeepAlive/blob/master/screenshot/six.jpg)

若使用前台service，当按back键或者home键退到桌面时，
使用命令查看app所在进程的oom_adj值均为1:
![image](https://github.com/weimodeweimo/ProcessKeepAlive/blob/master/screenshot/three.jpg)
