# MCinaBox - A Minecraft Java Edition Launcher on Android
## Table of Contents
- [Background](#Background)
- [Change Log](#Change-Log)
- [Install](#Install)
- [Usage](#Usage)
- [Preview](#Preview)
- [Known Issues](#Known-Issues)
- [Maintainers](#Maintainers)
- [Contribution](#Contribution)
- [Related Efforts](#Related-Efforts)
- [License](#License)

## Background

`Origin` I discovered the BoardwalkApp a few years ago, which can launch Minecraft Java Edition on Android devices. It's so exciting to see minecraft start on my phone. Thanks to Boardwalk, it left a deep impression on me, and since then I have also developed interest and love in programming. I learned Java programming for 8 days through 'Head First Java', and started to write my first Android program MCinaBox with the support of BoatApp Project.<br>
>zhuowei 's BoardwalkApp https://github.com/zhuowei/Boardwalk <br>
>CosineMath 's BoatApp https://github.com/CosineMath/BoatApp

## Change Log

### v0.1.0
- First Release Version.
### v0.1.1
- 修复 内存为空时因异步消息管理器刷新而引发的崩溃
- 修复 添加自定义按键时一些值为空引发的崩溃
- 修复 下载器重复下载已经存在的文件
- 修复 boat-3模式下主键值为鼠标的自定义按键无效
- 修复 Android媒体扫描minecraft文件夹而引发的图库崩溃
- 更改 自定义按键对话框操作按钮置顶
- 添加 自定义按键时拖动来改变位置
- 添加 状态指示器增加对内存大小设置的限制
- 移除 暂时移除整合包导入功能入口
- 移除 暂时移除Toolbar 更多选项

## Install

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

#### You can also get released apk from [here]().

## Usage
## Preview
## Known Issues
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

If you want to submit a pull request, there're some requirements:
* IDE: Android Studio
* TargetSDK: 28
* MimniumSDK: 21
* Do NOT modify `gradle` files.

## Related Efforts
>CosineMath 's BoatApp https://github.com/CosineMath/BoatApp
