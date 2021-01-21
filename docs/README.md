# MCinaBox - A Minecraft Java Edition Launcher on Android
[Now in English] [切换为简体中文](./README_zh-cn.md)</br>

If you find problems when you run MCinaBox, you can check [Q&A](./Q&A.md) first.  

## Instruction
- MCinaBox is a Launcher of Minecraft Java Edition on Android. The core functionality is powered by [CosineMath](Https://github.com/CosineMath)'s [BoatApp](https://github.com/AOF-Dev/BoatApp) project.

## Contents
- [Background](#Background)
- [Change Log](#Change-Log)
- [Build](#Build)
- [Usage](#Usage)
- [Preview](#Preview)
- [Known Issues](#Known-Issues)
- [Maintainers](#Maintainers)
- [License](#License)
- [Contribution](#Contribution)
- [Related](#Related)
- [Q&A](./Q&A.md)

## Background
- `MCinaBox` is an open source project with the goal of building and developing a Minecraft Java Edition launcher that runs on an Android.
- `Front End` provides User Management, Minecraft Version Management, Minecraft Game Controller, Minecraft Startup Parameter Generation, Configuration Backend, etc`Configuration` and `Management` functions to reduce the workload of complete launcher development.
- `Backend` provides JRE runtime environment, Minecraft runtime environment, etc `core` functionality.
- `Composition` MCinaBox consists of a front end and a back end.

## Change Log
- [English](./CHANGELOG.md)
- [Chinese](./CHANGELOG_zh-cn.md)

## Build

### Environment
- Android Studio
- Android NDK
- Git

### Clone
- ```git clone https://github.com/AOF-Dev/MCinaBox.git ```

### Import
- Import this project in your Android Studio.

### Build
- Build via Android Studio.

#### You can also get released apk from [here](https://github.com/AOF-Dev/MCinaBox/releases).

## Usage

### Installation
1. Download the latest APK and runtime to your Android phone.
2. Then start APK and find `Launcher Settings` - `Import Runtime`. Click `Import` to import runtime.
3. Alternatively, you can move the runtime to `/ sdcard / Android/ com.aof.mcinabox/files/MCinaBox/runtime` , and then repeat the previous step.

### Start
1. First, you need to create a user. Click `User` - `Add new User` to create a user.
2. Second, you need to download Minecraft. Click `Game List` - `Install new version` to download Minecraft.
3. Finally, you will start the game. Please select a Minecraft version on `home page` and start the game.

### Forge API
1. First, download Forge-Installer from Forge. It must be in `Universal` and `jar` format.
2. Then put the downloaded jar file in `/sdcard/Android/com.aof.mcinabox/files/MCinaBox/forge`.
3. Finally, open the MCinaBox and click `Launcher Settings` - `Forge Installer` to select and install the forge.

### Customize
1. MCinaBox offers a number of customization options, you can configure your launcher parameters in the `Game List` - `Global Game Settings`.
2. MCinaBox provides the ability to customize your Minecraft working directory by configuring your Minecraft working directory in the `Game Directory`.
3. MCinaBox provides the ability to customize your game controller by creating your virtual keyboard layout in the `Virtual Keyboard Settings`.

### Online
1. MCinaBox supports Mojang's official server authentication. When you create a user, you can check `Online login` and enter your Mojang account and password to log in.
2. MCinaBox supports server authentication for Authlib-Injector. When you create a user, you can check `Online login`, enter your account and password, and verify the address of the server to log on.
3. MCinaBox will not record your password in any way.

## Preview

## Known Issues

### Launcher

### Launch Minecraft
1. Forge's loading animation will cause a crash
2. It seems that there will be an X11 error when starting the Minecraft version below 1.6
3. Unable to initialize Minecraft 1.13.x

## Maintainers
[@AOF-Dev](https://github.com/AOF-Dev)

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
- All people who put forward issues.


If you want to submit a pull request, there're some requirements:
* IDE: Android Studio
* TargetSDK: 22
* MimniumSDK: 21
* DO NOT modify `gradle` files.

## Related
* [BoatApp (CosineMath,GPL 2.0)](https://github.com/AOF-Dev/BoatApp)
* Gson (Google,Apache 2.0)
* XZ for Java (Lasse Collin,Public Domain)
* [JNDCrash (ivanarh,Apache-2.0)](https://github.com/ivanarh/jndcrash)
* [FileDownloader (lingochamp,Apache-2.0)](https://github.com/lingochamp/FileDownloader)
* [BubbleLayout (MasayukiSuda,All)](https://github.com/MasayukiSuda/BubbleLayout)
* [AndroidRocker (kongqw,All)](https://github.com/kongqw/AndroidRocker)
* [colorpicker (QuadFlask,All)](https://github.com/QuadFlask/colorpicker)
- `Runtime Pack`
* [GL4ES (ptitSeb,MIT)](https://github.com/ptitSeb/gl4es)
* [OpenJDK-8 (CosineMath,GPL-2.0)](https://github.com/CosineMath/openjdk-jdk8u-aarch32-android)
* [OpenJDK-8 (CosineMath,GPL-2.0)](https://github.com/AOF-Dev/openjdk-aarch64-jdk8u-androidport)
* [lwjgl2.x (CosineMath,All)](https://github.com/CosineMath/lwjgl-boat)
* [lwjgl3.x (CosineMath,All)](https://github.com/CosineMath/lwjgl3-boat)
* [glfw (CosineMath,All)](https://github.com/CosineMath/glfw-boat)
* [openal-soft (kcat,GPL-2.0)](https://github.com/kcat/openal-soft)
