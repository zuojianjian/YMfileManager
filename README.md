# YMfileManager功能需求统一和界面统一

内容：

* 1、功能需求
* 3、存在问题
* 4、项目计划
* 5、设计实现


# 1、项目简介
　本应用属于Openthos项目的一部分，提供 Openthos 系统本地文件管理以及Seafile/owncloud云存储支持。

##　当前开发人员 (20160801-20160831)
 左剑剑　王棋
 
# 2、功能需求
####详细的功能需求请参考：<br>
[总体功能需求](https://github.com/openthos/oto-filemanager/tree/master/requirement)   
[seafile功能说明](https://github.com/openthos/oto-filemanager/blob/master/doc/summary/seafile%E5%8A%9F%E8%83%BD%E5%91%BD%E4%BB%A4%E5%88%97%E8%A1%A8.md)<br>
####完成情况如下所示：
| 完成     | 描述     | 模块     | 完成度 |
| ---- |-------    |:---------|:---------| 
| √     | FileManager顶部导航栏    | 界面     | 100% |
| √     | FileManager左侧面板      | 界面     | 100% |
| √     | FileManager右侧文件列表  | 界面     | 100% |
| √     | 云服务界面               | 界面     | 100% |
| √     | 右键菜单                 | 界面     | 100% |
| √     | FileManager左侧USB列表   | 界面     | 100% |
| √     | 属性显示界面             | 界面     | 100% |
| √     | 文件复制进度界面         | 界面     | 100% |
| √     | 目录前进、后退           | 功能     | 100% |
| √     | 鼠标点击区分单击和双击事件（单击锁定，双击打开）                                      | 功能     | 100% |
| √     | 两种本地文件浏览视图：图标形式，列表形式                                              | 功能     | 100% |
| √     | 右键菜单：打开、打开方式、刷新、新建文件/文件夹、复制、剪切、粘贴、删除、重命名、属性 | 功能     | 100% |
| √     | 路径框（显示当前路径，输入路径）                                                      | 功能     | 100% |
| √     | 搜索框文件搜索                                                                        | 功能     | 100% |
| √     | 文件多选（按住ctrl键进行多选）                                                        | 功能     | 100% |
| √     | 热键功能支持：Ctrl+C、Ctrl+V、Ctrl+A、Ctrl+X、Ctrl+D                                  | 功能     | 100% |
| √     | U盘动态识别，手动弹出U盘                                                              | 功能     | 100% |
| x     | 文件拖拽                                                                              | 功能     |   0% |
| x     | 文件框选                                                                              | 功能     |   0% |
| x     | 网上邻居                                                                              | 功能     |   0% |
| x     | Seafile文件/文件夹自动同步                                                            | 功能     |  50% |
| x     | Seafile显示文件目录列表盘                                                             | 功能     |  50% |
| x     | Seafile增加/解除同步文件夹                                                            | 功能     |  50% |
| x     | Owncloud文件/文件夹自动同步                                                            | 功能     | 0% |
| x     | Owncloud显示文件目录列表盘                                                             | 功能     | 0% |
| x     | Owncloud增加/解除同步文件夹                                                            | 功能     | 0% |
# 3、存在问题
| 简述  | 类别  | 备注 |
| ---- |------- |:---------|
| U盘识别                        | 功能 |系统对部分U盘识别有问题 |
| 网上邻居                       | 功能 |后期需要做的            |
| Seafile文件/文件夹自动同步     | 功能 |工程师未提供接口        |  
| Seafile显示文件目录列表        | 功能 |工程师未提供接口        | 
| Seafile增加/解除同步文件夹     | 功能 |工程师未提供接口        |
| Owncloud功能　　　　　　　　    | 功能 |未实现        | 
# 4、项目进展

| 开始时间  | 结束时间  | 内容 | 人员|
| ---- |------- |-------|:---------|
|2016.07.01| 2016.07.05| 调研开源Filemanager实现原理，初步制定基于Win10界面的Filemanager应用开发方案| 朱思敏和孙智鹏|
|2016.07.06|	2016.07.12|	实现Filemanager界面及部分本地文件管理功能|朱思敏和孙智鹏|
|2016.07.12|	2016.07.13|	与刘总交流，确定新的界面、功能需求|朱思敏和孙智鹏|
|2016.07.14|	2016.07.22|	按照新的需求修改应用界面，完善本地文件管理功能并测试|朱思敏和孙智鹏|
|2016.07.23|	2016.07.24|	调研分析总结terminal版Seafile客户端的CLI接口，并发送给王琪工程师|朱思敏和孙智鹏|
|2016.07.25|	2016.07.30|	完成Seafile云存储基本功能，整合程序进Openthos系统，编写开发文档|朱思敏和孙智鹏|

#5、设计与实现
##5.1主要结构
代码的主要结构如图所示：[主要结构](https://github.com/openthos/oto-filemanager/blob/master/doc/summary/%E7%B3%BB%E7%BB%9F%E7%BB%93%E6%9E%84.md)
##5.2应用设计与实现
请查看：[FileManager设计与实现.md](https://github.com/openthos/oto-filemanager/blob/master/doc/summary/FileManager%E8%AE%BE%E8%AE%A1%E4%B8%8E%E5%AE%9E%E7%8E%B0.md)
##5.3代码结构及说明
请查看：[FileManager代码结构及说明.md](https://github.com/openthos/oto-filemanager/blob/master/doc/summary/FileManager%E4%BB%A3%E7%A0%81%E7%BB%93%E6%9E%84%E5%8F%8A%E8%AF%B4%E6%98%8E.md)
##5.4流程图
FileManager的几个基本功能流程图如下所示：[流程图.md](https://github.com/openthos/oto-filemanager/blob/master/doc/summary/%E6%B5%81%E7%A8%8B%E5%9B%BE.md)
##5.5构建 && 安装 && 运行
请查看：[building.md](https://github.com/openthos/oto-filemanager/blob/master/doc/summary/building.md)<br>
apk程序及压缩包地址：https://github.com/openthos/oto-filemanager/tree/master/app
##5.6工程文件地址
请查看：[FileManagerOpenOS](https://github.com/openthos/oto-filemanager/tree/master/FileManagerOpenOS)
