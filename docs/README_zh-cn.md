# MCinaBox - A Minecraft Java Edition Launcher on Android
[现在是简体中文] [Change to English](./README.md)</br>

如果你在使用时发现问题，可以先查看[Q&A](./Q&A.md)

## 简介
- MCinaBox是一个运行在Android系统的 Minecraft Java Edition 启动器， 它的核心功能由[CosineMath](https://github.com/CosineMath)的[BoatApp](https://github.com/AOF-Dev/BoatApp)项目提供。

## 目录
- [项目背景](#项目背景)
- [更新日志](#更新日志)
- [构建](#构建)
- [使用](#使用)
- [预览](#预览)
- [已知问题](#已知问题)
- [维护者](#维护者)
- [许可](#许可)
- [贡献者](#贡献者)
- [依赖的开源项目库](#依赖的开源项目)
- [Q&A](./Q&A.md)

## 项目背景
- `MCinaBox`是一个开源项目，目标是构建并发展一个运行在Android系统上的Minecraft Java版的启动器。
- `前端` 提供用户管理、Minecraft版本管理、Minecraft游戏控制器、Minecraft启动参数生成、配置后端等`配置`和`管理`功能，减少完整的启动器开发的工作量。
- `后端` 提供JRE运行环境、Minecraft运行环境等`核心`功能。
- `组成` MCinaBox 启动器由前端和后端共同组成

## 更新日志
- [English](./CHANGELOG.md)
- [Chinese](./CHANGELOG_zh-cn.md)

## 构建

### 准备开发环境
- Android Studio
- Android NDK
- Git

### 克隆到本地
- ```git clone https://github.com/AOF-Dev/MCinaBox.git ```

### 导入项目到AS
- 将此项目导入你的 Android Studio

### 编译
- 通过 Android Studio 编译项目

#### 你也可以从[这里](https://github.com/AOF-Dev/MCinaBox/releases)获得发布的APK。

## 使用

### 安装
1. 将最新的APK和运行库下载到你的Android手机。
2. 然后启动APK并找到 `启动器设置` - `导入运行库` 点击 `导入` 以导入运行库。
3. 或者，你也可以将运行库移动到 `/sdcard/Android/com.aof.mcinabox/files/MCinaBox/runtime` 文件夹下，然后重复上一步的操作。

### 开始
1. 首先，你需要创建一个用户。 请在 `左侧导航栏` - `用户` - `添加新用户` 来创建一个用户
2. 其次，你需要下载Minecraft。 请在 `左侧导航栏` - `游戏列表` - `安装新游戏版本` 下载Minecraft游戏
3. 最后，你将要启动游戏。 请在`左侧导航栏` - `主页` 选择一个Minecraft版本并启动。

### Forge API
1. 首先，请在Forge官网下载Forge-Installer，必须是`通用版`的且是`jar`格式。
2. 然后，请将下载的jar文件放入`/sdcard/Android/com.aof.mcinabox/files/MCinaBox/forge`文件夹中。
3. 最后，请打开MCinabox，在`左侧导航栏` - `启动器设置` - `Forge本地安装其` 选择并安装forge版本。

### 自定义
1. MCinaBox提供了大量的自定义选项，你可以在 `左侧导航栏你` - `游戏列表` - `全局游戏设置` 中配置你的启动参数。
2. MCinaBox提供了自定义Minecraft工作目录的功能，你可以在 `左侧导航栏` - `游戏目录` 中配置你的Minecraft工作目录。
3. MCinaBox提供了自定义游戏控制器的功能，你可以在 `左侧导航栏` - `虚拟键盘设置` 中创建你的虚拟键盘布局。

### 服务器验证
1. MCinaBox支持Minecraft的官方服务器验证，你可以在创建用户时，勾选 `在线登陆` ，输入你的Mojang账户和密码来进行登录。
2. MCinaBox支持Authlib-Injector的服务器验证，你可以在创建用户时，勾选 `在线登陆` ，输入你的账户和密码和验证服务器的地址来进行登录。
3. MCinaBox按照规范优先选择https协议进行通信。
4. MCinaBox不会以任何形式记录你的密码。

## 预览

## 已知问题

### 启动器

### 我的世界
1. Forge的加载动画会导致崩溃。
2. 在低于1.6的Minecraft版本中启动似乎会出现X11错误。
3. 无法初始化Minecraft 1.13.x。

## 维护者
[@AOF-Dev](https://github.com/AOF-Dev)

## 许可
该软件根据[GPL v3](https://www.gnu.org/licenses/gpl-3.0.html)和附加条款进行分发。
### 附加条款（依据 GPLv3 协议第七条）
1. 当你分发本程序的修改版本时，你必须以一种合理的方式修改本程序的名称或版本号，以示其与原始版本不同。 [依据 GPLv3, 7(c).]
2. 你不得移除本程序所显示的版权声明。[依据 GPLv3, 7(b).]

## 贡献者
这个项目的存在要感谢所有贡献者。

### 贡献者列表:
- [全部](https://github.com/longjunyu2/MCinaBox/graphs/contributors)
- `MCredbear`
- 所有提出Issues的人。


如果要提交拉取请求，则有一些要求：
* IDE: Android Studio
* TargetSDK: 22
* MimniumSDK: 21
* 不要修改`gradle`文件。

## 依赖的开源项目
* [BoatApp (CosineMath,GPL 2.0)](https://github.com/AOF-Dev/BoatApp)
* Gson (Google,Apache 2.0)
* XZ for Java (Lasse Collin,Public Domain)
* [JNDCrash (ivanarh,Apache-2.0)](https://github.com/ivanarh/jndcrash)
* [FileDownloader (lingochamp,Apache-2.0)](https://github.com/lingochamp/FileDownloader)
* [BubbleLayout (MasayukiSuda,All)](https://github.com/MasayukiSuda/BubbleLayout)
* [AndroidRocker (kongqw,All)](https://github.com/kongqw/AndroidRocker)
* [colorpicker (QuadFlask,All)](https://github.com/QuadFlask/colorpicker)
- `运行库`
* [GL4ES (ptitSeb,MIT)](https://github.com/ptitSeb/gl4es)
* [OpenJDK-8 (CosineMath,GPL-2.0)](https://github.com/CosineMath/openjdk-jdk8u-aarch32-android)
* [OpenJDK-8 (CosineMath,GPL-2.0)](https://github.com/AOF-Dev/openjdk-aarch64-jdk8u-androidport)
* [lwjgl2.x (CosineMath,All)](https://github.com/CosineMath/lwjgl-boat)
* [lwjgl3.x (CosineMath,All)](https://github.com/CosineMath/lwjgl3-boat)
* [glfw (CosineMath,All)](https://github.com/CosineMath/glfw-boat)
* [openal-soft (kcat,GPL-2.0)](https://github.com/kcat/openal-soft)
