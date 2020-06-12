package cosine.boat.controller.inputers.physcial;

import android.app.Activity;
import android.view.KeyEvent;

import com.aof.mcinabox.plugin.controller.controller.Controller;
import com.aof.mcinabox.plugin.controller.inputer.Inputer;
import com.aof.mcinabox.plugin.controller.keyevent.BaseKeyEvent;

public class Keyboard extends KeyboardSupport implements Inputer {

    Controller mController;
    Activity mContext;

    @Override
    public boolean load(Activity context, Controller controller) {
        this.mController = controller;
        this.mContext = context;
        return false;
    }

    @Override
    public boolean unload() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        sendKeyEvent(event,true);
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        sendKeyEvent(event,false);
        return false;
    }

    public void sendKeyEvent(KeyEvent event, boolean pressed){
        /*TODO: 这里写 物理键盘 按下时的操作
        暂时用屏幕控制器传输数据，如果有必要的话以后重写一个物理控制器
        */
        BaseKeyEvent mEvent = new BaseKeyEvent("Controller(Keyboard)",event.getCharacters(),CodeTranslation.to(event.getKeyCode()),pressed,KEYBOARD_BUTTON,null);
        mController.sendKey(mEvent);
    }
}


