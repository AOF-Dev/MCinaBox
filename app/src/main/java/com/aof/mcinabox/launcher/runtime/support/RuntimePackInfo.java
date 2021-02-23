package com.aof.mcinabox.launcher.runtime.support;

import java.util.Map;

public class RuntimePackInfo {

    public String releaseTime; //发行时间
    public String platform; //架构
    public String versionName; //包版本名称
    public int versionCode; //包版本
    public String backEnd; //后端名称
    public String description; //描述
    public Manifest[] manifest;

    public RuntimePackInfo setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
        return this;
    }

    public RuntimePackInfo setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public RuntimePackInfo setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public RuntimePackInfo setBackEnd(String backEnd) {
        this.backEnd = backEnd;
        return this;
    }

    public RuntimePackInfo setDescription(String description) {
        this.description = description;
        return this;
    }

    public RuntimePackInfo setManifest(Manifest[] manifest) {
        this.manifest = manifest;
        return this;
    }

    public class Manifest {
        public String name; //清单名称
        public String condition; //清单执行条件类型
        /* 语法定义
         元素1|元素2 依此类推
        */
        public String condition_info; //执行条件
        public String description; //清单描述
        public String jre_home; //java运行环境目录
        public String so; //动态链接库路径
        public String java_library_path; //环境目录
        public String classpath; //classpath参数
        public String jvmMode; //启动器模式
        public Map<String, String> systemEnv; //启动器环境

        public Manifest setName(String name) {
            this.name = name;
            return this;
        }

        public Manifest setCondition(String condition) {
            this.condition = condition;
            return this;
        }

        public Manifest setDescription(String description) {
            this.description = description;
            return this;
        }

        public Manifest setJre_home(String jre_home) {
            this.jre_home = jre_home;
            return this;
        }

        public Manifest setSo(String so) {
            this.so = so;
            return this;
        }

        public Manifest setConditionInfo(String info){
            this.condition_info = info;
            return this;
        }

        public Manifest setSystemEnv(Map<String, String> env){
            this.systemEnv = env;
            return this;
        }
    }

}
