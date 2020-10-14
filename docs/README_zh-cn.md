# MCinaBox - A Minecraft Java Edition Launcher on Android
[现在是简体中文] [Change to English](./README.md)</br>
其他语言仍在翻译中。 请耐心等待（软件和某些页面）

## 面向开发者
- 当前分支为Dev分支，面向开发者们。

## 目录
- [项目背景](#项目背景)
- [更新日志](#更新日志)
- [构建](#构建)
- [使用](#使用)
- [预览](#预览)
- [已知问题](#已知问题)
- [维护者](#维护者)
- [须知](#须知)
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
- ```git clone https://github.com/longjunyu2/MCinaBox.git ```

### 导入项目到AS
- 将此项目导入您的 Android Studio

### 编译
- 通过 Android Studio 编译项目

#### 您也可以从[这里](https://github.com/longjunyu2/MCinaBox/releases)获得发布的APK。

## 使用

### 安装
1. 将最新的APK和运行时包下载到您的Android手机。
2. 然后启动APK并找到 `启动器设置` - `导入运行库` 点击 `导入` 以导入运行库。

### 开始
1. 首先，您需要创建一个用户。 请在 `左侧导航栏` - `用户` - `添加新用户` 来创建一个用户
2. 其次，您需要下载Minecraft。 请在 `左侧导航栏` - `游戏列表` - `安装新游戏版本` 下载Minecraft游戏
3. 最后，您将要启动游戏。 请在`左侧导航栏` - `主页` 选择一个Minecraft版本并启动。

### Forge API
1. 首先，请在Forge官网下载Forge-Installer，必须是`通用版`的且是`jar`格式。
2. 然后，请将下载的jar文件放入`/sdcard/Android/com.aof.mcinabox/files/MCinaBox/forge`文件夹中。
3. 最后，请打开MCinabox，在`左侧导航栏` - `启动器设置` - `Forge本地安装其` 选择并安装forge版本。

### 自定义
1. MCinaBox提供了大量的自定义选项，您可以在 `左侧导航栏你` - `游戏列表` - `全局游戏设置` 中配置您的启动参数。
2. MCinaBox提供了自定义Minecraft工作目录的功能，您可以在 `左侧导航栏` - `游戏目录` 中配置您的Minecraft工作目录。
3. MCinaBox提供了自定义游戏控制器的功能，您可以在 `左侧导航栏` - `虚拟键盘设置` 中创建您的虚拟键盘布局。

### 服务器验证
1. MCinaBox支持Minecraft的官方服务器验证，您可以在创建用户时，勾选 `在线登陆` ，输入您的Mojang账户和密码来进行登录。
2. MCinaBox支持Authlib-Injector的服务器验证，您可以在创建用户时，勾选 `在线登陆` ，输入您的账户和密码和验证服务器的地址来进行登录。
3. MCinaBox按照规范优先选择https协议进行通信。
4. MCinaBox不会以任何形式记录您的密码。

## 预览

## 已知问题

### 启动器
1. 启动器可用的内存大小和系统可用内存相比小很多，且调高内存易发生崩溃。

### 我的世界
1. Forge的加载动画会导致崩溃。
2. 在低于1.6的Minecraft版本中启动似乎会出现X11错误。
3. 无法初始化Minecraft 1.13.x。
5. 有时候会发生lwjgl崩溃。

## 维护者
[@AOF-Dev](https://github.com/AOF-Dev)
[@longjunyu2](https://github.com/longjunyu2)

## 须知
1. 本项目采用GPLv3开源协议，因此本项目所使用的全部开源项目均兼容GPLv3协议。
2. 本项目所修改的开源项目均依照其开源许可进行源代码分发。 您可以在AOF-Dev所拥有的仓库中找到对应源码
3. 本项目所使用的被修改过的二进制文件均依照其开源许可进行源代码分发。 您可以在AOF-Dev所拥有的仓库中找到对应源码。
4. 当您使用该项目时(包括二进制文件)，请您务必依据GPL分发您的源代码，否则您无权使用本项目，敬请知悉。

## 许可
该软件根据[GPL v3](https://www.gnu.org/licenses/gpl-3.0.html)和附加条款进行分发。
### 附加条款（依据 GPLv3 协议第七条）
1. 当你分发本程序的修改版本时，你必须以一种合理的方式修改本程序的名称或版本号，以示其与原始版本不同。 [依据 GPLv3, 7(c).]
2. 你不得移除本程序所显示的版权声明。[依据 GPLv3, 7(b).]

## 贡献者
这个项目的存在要感谢所有贡献者。

### 贡献者列表:
- [全部](https://github.com/longjunyu2/MCinaBox/graphs/contributors)
- [补充]:
- `MCredbear`
- `TSaltedfishKing`
- 所有提出Issues的人。


如果要提交拉取请求，则有一些要求：
* IDE: Android Studio
* TargetSDK: 21
* MimniumSDK: 21
* 不要修改`gradle`文件。

## 依赖的开源项目
* [BoatApp (CosineMath,MIT)](https://github.com/CosineMath/BoatApp)
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
