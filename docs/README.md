# MCinaBox - A Minecraft Java Edition Launcher on Android
[Now is English] [切换为简体中文](./README_zh-cn.md)
## Warning
- There is a problem with the latest source code. Some functions are not working properly.
- Because I have no time to maintain the project, the latest source code is not perfect.
  
If you find problems when you run, you can check [Q&A](./Q&A.md) for that at first  

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

`Origin` I discovered the `BoardwalkApp` a few years ago, which can launch Minecraft Java Edition on Android devices. It's so exciting to see minecraft start on my phone.It's really a curious and interesting project<br>
`MCinaBox` is an open source project, and the back end is supported by the `BoatApp` project. And in the efforts of many people to make it run.
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
- Import this project in your Android Studio.

### Build
- Build via Android Studio.

#### You can also get released apk from [here](https://github.com/longjunyu2/MCinaBox/releases).

## Usage

### Install
1. Download the latest APK and runtime pack to your Android phone.
2. Install APK and start it at least one time.
3. Copy the runtime to ```/sdcard/Android/data/com.aof.mcinabox/files/MCinaBox/runtimepack/```
4. Then start APK and find ```Launcher Set``` to import the runtime pack.
### Play
1. Before you start the game, you need to configure the starter according to the instructions in the software. Until the indicator changes from red to blue.
2. What you need to do includes creating users, modifying the maximum memory, downloading game versions, and creating your own virtual keyboard templates.
### Forge
In theory, forge is supported, but we need to make some changes to start it.
1. Copy an installed version of forge from your computer to ```/sdcard/mcinabox/.minecraft/```, including ```libraries``` and ```versions```
2. Launch it in MCinaBox at least one time. (As a result, it can't be started)
3. Edit ```/sdcard/MCinaBox/.minecraft/config/splash.properties``` to change ```enabled=true``` to ```enabled=false``` to close forge loading animation.
4. Try to launch forge again. It's going to be black for a while, but it's usually useful.
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
