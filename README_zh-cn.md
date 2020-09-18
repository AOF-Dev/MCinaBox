# MCinaBox - A Minecraft Java Edition Launcher on Android
现在是简体中文[Change to English](./README_en.md)</br>
其他语言仍在翻译中。 请耐心等待（软件和某些页面）
## Warning
- 最新的源代码有问题。 某些功能无法正常工作。
- 由于我没时间维护项目，最新的源代码不完善。

## Table of Contents
- [Background](#Background)
- [Change Log](#Change-Log)
- [Build](#Build)
- [Usage](#Usage)
- [Preview](#Preview)
- [Known Issues](#Known-Issues)
- [Maintainers](#Maintainers)
- [Contribution](#Contribution)
- [Related](#Related)
- [License](#License)
- [Q&A]
## Background

`Origin`我几年前发现了`BoardwalkApp`，它可以在Android设备上启动Minecraft Java Edition。 看到我的世界在我的手机上启动真是令人兴奋，这确实是一个有趣而有趣的项目<br>
 `MCinaBox`是一个开源项目，后端由`BoatApp`项目支持。 并且在许多人的努力下使其得以运行。
>zhuowei 's BoardwalkApp https://github.com/zhuowei/Boardwalk <br>
>CosineMath 's BoatApp https://github.com/CosineMath/BoatApp

## Change Log

- [English](./CHANGELOG.md)
- [Chinese](./CHANGELOG_zh-cn.md)

## Build

### Environment
- Android Studio
- Android NDK
- Git

### Clone
- ```git clone https://github.com/longjunyu2/MCinaBox.git ```

### Import
- 将此项目导入您的Android Studio

### Build
- 通过Android Studio构建

#### 您也可以从[这里](https://github.com/longjunyu2/MCinaBox/releases)获得发布的APK。

## Usage

### Install
1. 将最新的APK和运行时包下载到您的Android手机。
2. 安装APK并至少启动一次。
3. 将运行时复制到
4. 然后启动APK并找到```Launcher Set```以导入运行时包。
### Play
1. Before you start the game, you need to configure the starter according to the instructions in the software. Until the indicator changes from red to blue.
2. What you need to do includes creating users, modifying the maximum memory, downloading game versions, and creating your own virtual keyboard templates.
### Forge
In theory, forge is supported, but we need to make some changes to start it.
1. Copy an installed version of forge from your computer to ```/sdcard/mcinabox/.minecraft/```, including ```libraries``` and ```versions```
2. Launch it in MCinaBox at least one time. (As a result, it can't be started)
3. Edit ```/sdcard/MCinaBox/.minecraft/config/splash.properties``` to change ```enabled=true``` to ```enabled=false``` to close forge loading animation.
4. Try to launch forge again.It's going to be black for a while, but it's usually useful
### Matters needing attention
1. Mipmap will cause rendering problems. Please turn it off in Minecraft ```settings - video settings - mipmap=0```

## Preview

## Known Issues

### Launcher
1. Unable to request more memory from the system, the upper limit is determined by the system

### Launch Minecraft
1. Forge's loading animation will cause a crash
2. It seems that there will be an X11 error when starting the Minecraft version below 1.6
3. Unable to initialize Minecraft 1.13.x
4. Minecraft 1.14.X ~ 1.15.X has rendering problems
5. Too fast key input may cause lwjgl to crash

## Maintainers
[@longjunyu2](https://github.com/longjunyu2)

## License
The software is distributed under [GPL v3](https://www.gnu.org/licenses/gpl-3.0.html) with additional terms.
### Additional terms under GPLv3 Section 7
1. When you distribute a modified version of the software, you must change the software name or the version number in a reasonable way in order to distinguish it from the original version. [under GPLv3, 7(c).]
2. You must not remove the copyright declaration displayed in the software. [under GPLv3, 7(b).]

Chinese Translation:
### 附加条款（依据 GPLv3 协议第七条）
1. 当你分发本程序的修改版本时，你必须以一种合理的方式修改本程序的名称或版本号，以示其与原始版本不同。 [依据 GPLv3, 7(c).]
2. 你不得移除本程序所显示的版权声明。[依据 GPLv3, 7(b).]

## Contribution
This project exists thanks to all the people who contribute.

### List of contributors:
- [ALL](https://github.com/longjunyu2/MCinaBox/graphs/contributors)
- `MCredbear`
- `TSaltedfishKing`
- All people who put forward issues.


If you want to submit a pull request, there're some requirements:
* IDE: Android Studio
* TargetSDK: 21
* MimniumSDK: 21
* Do NOT modify `gradle` files.

## Related
> [BoatApp (CosineMath,MIT)](https://github.com/CosineMath/BoatApp)</br>
> Gson (Google,Apache 2.0)</br>
> XZ for Java (Lasse Collin,Public Domain)</br>
> [JNDCrash (ivanarh,Apache-2.0)](https://github.com/ivanarh/jndcrash)</br>
> [GL4ES (pitiSeb,MIT)](https://github.com/ptitSeb/gl4es)</br>
> [FileDownloader (lingochamp,Apache-2.0)](https://github.com/lingochamp/FileDownloader)</br>
> [BubbleLayout (MasayukiSuda)](https://github.com/MasayukiSuda/BubbleLayout)</br>
> [AndroidRocker (kongqw)](https://github.com/kongqw/AndroidRocker)</br>
> [colorpicker (shixiuwen)](https://github.com/shixiuwen/colorpicker)</br>
> [OpenJDK-8 (CosineMath,GPL-2.0)](https://github.com/CosineMath/openjdk-jdk8u-aarch32-android)</br>
> [lwjgl2.x (CosineMath)](https://github.com/CosineMath/lwjgl-boat)</br>
> [lwjgl3.x (CosineMath)](https://github.com/CosineMath/lwjgl3-boat)</br>
> [glfw (CosineMath)](https://github.com/CosineMath/glfw-boat)</br>
> [openal-soft (kcat,GPL-2.0)](https://github.com/kcat/openal-soft)</br>
