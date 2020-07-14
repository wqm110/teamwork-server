# mk-teamwork-server

#### 介绍
mk-teamwork是任务协同项目管理系统，

#### 软件架构
JAVA 1.8
MYSQL 8
基于前后端分离架构，服务端主要技术：springboot 、jwt  前端主要是vue;

#### 安装教程

1.  下载代码、编译打包，部署后端服务
2.  部署前端服务
3.  安装数据库

前端地址：https://gitee.com/wulon/mk-teamwork-ui

#### 使用说明

1.演示地址：http://teamwork.mokingsoft.com
admin/1

2.如果库运行时如果报 ONLY_FULL_GROUP_BY 相关错误，是由于mysql8的兼容配置引起，请做以下操作：

修改mysql配置文件，通过手动添加sql_mode的方式强制指定不需要ONLY_FULL_GROUP_BY属性，

my.cnf位于etc文件夹下，vim下光标移到最后，添加如下：

sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION

#### 参与贡献

1.  感谢vilson，本项目是pearProject的JAVA版本，后续会保持与pearProject同步，继续完善
2.  pearProject 地址：https://gitee.com/vilson/vue-projectManage


#### 问题反馈

1、欢迎大家使用，目前版本，可学习，可商用。

2、技术交流群（仅技术交流）：       
