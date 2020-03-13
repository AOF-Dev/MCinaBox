package com.aof.mcinabox;

//为避免循环依赖,且顾及到前后端适配的可扩展性
//需要copy一份adapt包保持对象的一致性

public class DataPathManifest extends com.aof.sharedmodule.Data.DataPathManifest{}
