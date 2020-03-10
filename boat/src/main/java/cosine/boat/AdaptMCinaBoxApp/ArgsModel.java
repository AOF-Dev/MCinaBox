package cosine.boat.AdaptMCinaBoxApp;

//为避免循环依赖,且顾及到前后端适配的可扩展性
//需要copy一份adapt包保持对象的一致性

import java.io.Serializable;

import static cosine.boat.AdaptMCinaBoxApp.DataPathManifest.*;

public class ArgsModel implements Serializable {
    String KeyboardPath = MCINABOX_KEYBOARD;
    String KeyboardName; //键盘模板名称
    String[] args; //全部启动参数
    Boolean forceRootRuntime; //强制运行库提权
    Boolean notEnableVirtualKeyboard; //不启用虚拟键盘
    Boolean doEnableOTG; //启用OTG

    public String getKeyboardFilePath(){
        if(KeyboardName != null){
            return (KeyboardPath + "/" + KeyboardName);
        }else{
            return null;
        }
    }

    public String getKeyboardName() { return KeyboardName; }

    public void setKeyboardName(String keyboardName) { KeyboardName = keyboardName; }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public Boolean getForceRootRuntime() {
        return forceRootRuntime;
    }

    public void setForceRootRuntime(Boolean forceRootRuntime) {
        this.forceRootRuntime = forceRootRuntime;
    }

    public Boolean getNotEnableVirtualKeyboard() {
        return notEnableVirtualKeyboard;
    }

    public void setNotEnableVirtualKeyboard(Boolean notEnableVirtualKeyboard) {
        this.notEnableVirtualKeyboard = notEnableVirtualKeyboard;
    }

    public Boolean getDoEnableOTG() {
        return doEnableOTG;
    }

    public void setDoEnableOTG(Boolean doEnableOTG) {
        this.doEnableOTG = doEnableOTG;
    }
}
