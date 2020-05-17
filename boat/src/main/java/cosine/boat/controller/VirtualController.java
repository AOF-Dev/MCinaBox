package cosine.boat.controller;

import android.app.Activity;
import android.util.Log;

import com.aof.mcinabox.plugin.controller.controller.BaseController;
import com.aof.mcinabox.plugin.controller.keyevent.BaseKeyEvent;
import com.aof.mcinabox.plugin.controller.keyevent.Event;

import cosine.boat.controller.inputers.virtual.CrossKey;

public class VirtualController extends BaseController {

    public VirtualController(Activity activity) {
        super(activity);
        addInputer(new CrossKey());
    }

    @Override
    public void sendKey(BaseKeyEvent event) {
        toLog(event);
        switch (event.getType()){
            case Event.KEYBOARD_BUTTON:
                client.setKey(event.getKeyCode(),event.isPressed());
                break;
            case Event.MOUSE_BUTTON:
                client.setMouseButton(event.getKeyCode(),event.isPressed());
                break;
            case Event.MOUSE_POINTER:
                client.setMousePoniter(event.getPointer()[0],event.getPointer()[1]);

                break;
            default:
                break;
        }
    }

    private void toLog(BaseKeyEvent event){
        String info;
        switch (event.getType()){
            case Event.KEYBOARD_BUTTON:
                info = "Type: " + event.getType() + " KeyCode: " + event.getKeyCode() + " Pressed: " + event.isPressed();
                break;
            case Event.MOUSE_BUTTON:
                info = "Type: " + event.getType() + " MouseCode " + event.getKeyCode() + " Pressed: " + event.isPressed();
                break;
            case Event.MOUSE_POINTER:
                info = "Type: " + event.getType() + " PointerX: " + event.getPointer()[0] + " PointerY: " + event.getPointer()[1];
                break;
            default:
                info = "Unknown Type: " + event.getType();
        }
        Log.i(event.getTag(),info);

    }
}
