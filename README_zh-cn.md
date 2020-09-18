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
- [Q&A](./Q&A.md)
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
3. 将运行时复制到```/sdcard/Android/data/com.aof.mcinabox/files/MCinaBox/runtimepack/```
4. 然后启动APK并找到```Launcher Set```以导入运行时包。
### Play
1. 在开始游戏之前，您需要根据软件中的说明配置启动器。 直到指示灯从红色变为蓝色。
2. 您需要做的包括创建用户，修改最大内存，下载游戏版本以及创建自己的虚拟键盘模板。
### Forge
理论上，支持forge，但是我们需要进行一些更改以启动它。
1. 将已安装的forge版本从计算机复制到```/sdcard/mcinabox/.minecraft/```，包括```libraries```和```versions```。
2. 至少一次在MCinaBox中启动它。（因此，它无法启动）
3. Edit ```/sdcard/MCinaBox/.minecraft/config/splash.properties``` to change ```enabled=true``` to ```enabled=false``` to close forge loading animation.
编辑```/sdcard/MCinaBox/.minecraft/config/splash.properties```以将```enabled=true```更改为```enabled=false```以关闭forge加载动画。
4. 尝试再次启动forge。它会变黑替换，但通常很有用。
### Matters needing attention
1. Mipmap将导致渲染问题。 请在Minecraft```settings - video settings - mipmap=0```中将其关闭。

## Preview

## Known Issues

### Launcher
1. 无法从系统请求更多内存，上限由系统确定。

### Launch Minecraft
1. Forge的加载动画会导致崩溃
2. 在低于1.6的Minecraft版本中启动似乎会出现X11错误
3. 无法初始化Minecraft 1.13.x
4. Minecraft 1.14.X〜1.15.X出现渲染问题
5. 太快的按键输入可能导致lwjgl崩溃

## Maintainers
[@longjunyu2](https://github.com/longjunyu2)

## License
该软件根据[GPL v3](https://www.gnu.org/licenses/gpl-3.0.html)和附加条款进行分发。
### 附加条款（依据 GPLv3 协议第七条）
1. 当你分发本程序的修改版本时，你必须以一种合理的方式修改本程序的名称或版本号，以示其与原始版本不同。 [依据 GPLv3, 7(c).]
2. 你不得移除本程序所显示的版权声明。[依据 GPLv3, 7(b).]

## Contribution
这个项目的存在要感谢所有贡献者。

### List of contributors:
- [ALL](https://github.com/longjunyu2/MCinaBox/graphs/contributors)
- `MCredbear`
- `TSaltedfishKing`
- 提出问题的所有人。


如果要提交拉取请求，则有一些要求：
* IDE: Android Studio
* TargetSDK: 21
* MimniumSDK: 21
* 不要修改`gradle`文件。

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
