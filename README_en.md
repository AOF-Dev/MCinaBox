# MCinaBox - A Minecraft Java Edition Launcher on Android
[Now is English]language|[简体中文](./README.md)
## Warning
- There is a problem with the latest source code. Some functions are not working properly.
- Because I have no time to maintain the project, the latest source code is not perfect.

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

## Background

`Origin` I discovered the `BoardwalkApp` a few years ago, which can launch Minecraft Java Edition on Android devices. It's so exciting to see minecraft start on my phone.It's really a curious and interesting project<br>
`MCinaBox` is an open source project, and the back end is supported by the `BoatApp` project. And in the efforts of many people to make it run.
>zhuowei 's BoardwalkApp https://github.com/zhuowei/Boardwalk <br>
>CosineMath 's BoatApp https://github.com/CosineMath/BoatApp

## Change Log

### v0.1.3
- Fix create new user crash (By by-scott)
- Fix minecraft dependency priority is higher than Forge
- Add minecraft genuine validation (By by-scott)
- Add mouse mode to switch (manual)
- Add Spanish (By salted fish King)
### v0.1.2
- Fix reediting keys caused a crash while repairing virtual keyboard configurations
- Fix some control behavior exceptions in Boat-3 mode
- Add a new downloader to provide visual download progress feedback
- Add the Minecraft Json Parsing Tool Library
- Add multilingual support (preliminary)
- Add Forge and LiteLoader support (see documentation)
### v0.1.1
- Fix crashes caused by asynchronous message manager refresh when memory is empty
- Fix crashes caused by null values when adding custom keys
- Fix downloader to duplicate download of existing files
- Fix invalid custom keys with primary key value of mouse in boat-3 mode
- Fix Gallery crash caused by Android media scanning minecraft folder
- Change custom key dialog action button set top
- Add drag to change position when adding custom keys
- Add a status indicator to increase the limit on memory size settings
- Remove temporary removal of integration package import function entry
- Remove more options for temporarily removing Toolbar
### v0.1.0
- First Release Version.

Chinese Translation:

### v0.1.3
- Repair Creating new users crashes (By by-scott)
- Repair Minecraft dependency has higher priority than forge
- Add Minecraft Genuine verification (By by-scott)
- Add Mouse mode switch(Manual)
- Add Spanish(By TSaltedfishKing)

### v0.1.2
- Repair The virtual keyboard configuration edit key again lead to collapse
- Repair Some of the Boat - 3 mode control behavior
- Add Download progress download a new program to provide visual feedback
- Add Minecraft Json parse tool library
- Add Multilingual support(Preliminary)
- Add Forge and LiteLoader support(Please refer to the documentation)
### v0.1.1
- Repair Memory is empty collapse caused by asynchronous message manager refresh
- Repair Add a custom button triggered the collapse of some value is empty
- Repair Repeated downloader to download the already existing files
- Repair Boat - 3 mode primary key value for the custom of the mouse button is invalid
- Repair Android media gallery collapse caused scan minecraft folder
- Change Custom dialog operation buttons placed at the top
- Add Custom button drag to change position
- Add Status indicator to increase the limitation on the memory size Settings
- Remove Temporarily remove integration package import entry
- Remove Temporarily remove the Toolbar more options

### v0.1.0
- First release

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
1. Download the lastest APK and runtime pack to your Android phone.
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

