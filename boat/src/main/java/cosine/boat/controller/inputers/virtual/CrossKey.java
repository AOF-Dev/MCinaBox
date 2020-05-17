package cosine.boat.controller.inputers.virtual;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aof.mcinabox.plugin.controller.controller.Controller;
import com.aof.mcinabox.plugin.controller.inputer.BaseScreenInputer;
import com.aof.mcinabox.plugin.controller.keyevent.Event;
import com.aof.mcinabox.plugin.controller.keyevent.BaseKeyEvent;
import com.aof.sharedmodule.Button.CrossButton;

import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.HashMap;

import cosine.boat.R;

public class CrossKey implements BaseScreenInputer {
    private Activity mContext;
    private Controller mController;
    private LinearLayout CrossKey;
    private CrossButton[] crosskeychildren;
    private int[] tempCrossKey;
    private Button crosskey_move;
    private RelativeLayout base;
    private static int PAUSE_TIME = 10;
    private int screenWidth;
    private int screenHeight;
    private HashMap<Object,int[]> layoutsPos;

    @Override
    public boolean load(Activity context, Controller controller) {
        mContext = context;
        mController = controller;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        layoutsPos = new HashMap<>();
        base = context.findViewById(R.id.base);
        CrossKey = base.findViewById(R.id.CrossKey);
        crosskeychildren = new CrossButton[]{CrossKey.findViewById(R.id.crosskey_up_left), CrossKey.findViewById(R.id.crosskey_up_right), CrossKey.findViewById(R.id.crosskey_down_left), CrossKey.findViewById(R.id.crosskey_down_right)};
        crosskey_move = CrossKey.findViewById(R.id.crosskey_move);

        //设定CrossKey十字键的监听
        for(int i = 0;i < ((LinearLayout)base.findViewById(R.id.CrossKey)).getChildCount();i++){
            if(((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i) instanceof Button){
                ((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i).setOnTouchListener(this);
            }else{
                for(int a = 0;a < ((LinearLayout)((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i)).getChildCount();a++){
                    for(int b = 0;b< ((LinearLayout)(((LinearLayout)((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i)).getChildAt(a))).getChildCount();b++){
                        ((LinearLayout)(((LinearLayout)((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i)).getChildAt(a))).getChildAt(b).setOnTouchListener(this);
                        ((LinearLayout)(((LinearLayout)((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i)).getChildAt(a))).getChildAt(b).getBackground().setAlpha(150);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean unload() {
        CrossKey.setVisibility(View.GONE);
        return true;
    }


    @Override
    public boolean onTouch(View p1, MotionEvent p2) {
        //十字键手势
        if(p1 instanceof CrossButton){
            //Log.i("StartTouchCross","true");
            OnTouchCrossKey(p1,p2);
            return false;
        }

        //移动十字键
        if(p1 == crosskey_move){
            OnMoveCrossKey((Button)p1,p2);
            return true;
        }

        return false;
    }

    @Override
    public void setUiMoveable(boolean moveable) {

    }

    @Override
    public void setUiVisiability(int visiablity) {

    }

    private void OnTouchCrossKey(View p1,MotionEvent p2){
        int[] Indexs = ApplyCrossKeyByTouchPosition(CrossKey.findViewById(R.id.crosskey_shift),CrossKey.findViewById(R.id.crosskey_parent),p2);
        switch (p2.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i("Action","Down");
                for(int temp:Indexs){
                    Log.i("OnTouchCrossKey","ACTION_DOWN " + temp);
                }
                SendDownOrUpToInput(tempCrossKey,Indexs,1);
                tempCrossKey = Indexs;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("Action","Move");
                SendDownOrUpToInput(tempCrossKey,Indexs,1);
                break;
            case MotionEvent.ACTION_UP:
                Log.i("Action","Up");
                for(int temp:Indexs){
                    Log.i("OnTouchCrossKey","Release Index: "+temp);
                    sendKeyEvent(temp,false);

                    //时序
                    try {
                        Thread.sleep(PAUSE_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                SendDownOrUpToInput(tempCrossKey,Indexs,1);
                tempCrossKey = null;
            default:
                break;
        }
    }

    private void OnMoveCrossKey(Button p1,MotionEvent p2){
        MoveViewByTouch(p1,CrossKey,p2);
    }

    public int[] ApplyCrossKeyByTouchPosition(View p1,View p2,MotionEvent p3){
        int[] initPos = new int[2];
        p2.getLocationOnScreen(initPos);
        int[] changPos = {(int) p3.getRawX() - initPos[0],(int) p3.getRawY() - initPos[1]};
        int[] targetPos = new int[2];
        p1.getLocationOnScreen(targetPos);
        Log.i("CrossKeyTouchDebug","TouchX: " + p3.getRawX() + " TouchY: " + p3.getRawY());
        Log.i("CrossKeyTochDebug","ChangeX " + changPos[0] + " ChangeY: " + changPos[1]);
        //自左向右，第一列
        if(changPos[0] < targetPos[0] - initPos[0] && changPos[0] >= 0){
            if(changPos[1] < targetPos[1] - initPos[1] && changPos[1] >= 0){
                //左上
                Log.i("CrossKey","Up-Left");
                if(p3.getAction() != MotionEvent.ACTION_MOVE) {
                    ReflectCrossKeyToScreen(new View[]{}, p3);
                }
                return (new int[]{Keyboard.KEY_W,Keyboard.KEY_A});
            }else if(changPos[1] <= targetPos[1] + p1.getHeight() - initPos[1] && changPos[1] >= targetPos[1] -initPos[1]){
                //左中
                Log.i("CrossKey","Center-Left");
                ReflectCrossKeyToScreen(new View[]{crosskeychildren[0],crosskeychildren[2]},p3);
                return (new int[]{Keyboard.KEY_A});
            }else if(changPos[1] > 0 && changPos[1] > changPos[1] + p1.getHeight() - initPos[1] && changPos[1] <= p2.getHeight()){
                //左下
                Log.i("CrossKey","Down-Left");
                if(p3.getAction() != MotionEvent.ACTION_MOVE) {
                    ReflectCrossKeyToScreen(new View[]{}, p3);
                }
                return (new int[]{Keyboard.KEY_S,Keyboard.KEY_A});
            }else{
                SendDownOrUpToInput(tempCrossKey,new int[]{},1);
                p3.setAction(MotionEvent.ACTION_UP);
                ReflectCrossKeyToScreen(new View[]{},p3);
            }
            //第二列
        }else if(changPos[0] <= targetPos[0] + p1.getWidth() - initPos[0] && changPos[0] >= targetPos[0] - initPos[0]){
            if(changPos[1] < targetPos[1] - initPos[1] && changPos[1] >= 0){
                //上
                Log.i("CrossKey","Up");
                ReflectCrossKeyToScreen(new View[]{crosskeychildren[0],crosskeychildren[1]},p3);
                return (new int[]{Keyboard.KEY_W});
            }else if(changPos[1] <= targetPos[1] + p1.getHeight() - initPos[1] && changPos[1] >= targetPos[1] -initPos[1]){
                //中
                Log.i("CrossKey","Center");
                ReflectCrossKeyToScreen(new View[]{},p3);
                if(p3.getAction() == MotionEvent.ACTION_MOVE){
                    return(new int[]{});
                }else{
                    return (new int[]{Keyboard.KEY_LSHIFT});
                }
            }else if(changPos[1] > 0 && changPos[1] > changPos[1] + p1.getHeight() - initPos[1] && changPos[1] <= p2.getHeight()){
                //下
                Log.i("CrossKey","Down");
                ReflectCrossKeyToScreen(new View[]{crosskeychildren[2],crosskeychildren[3]},p3);
                return (new int[]{Keyboard.KEY_S});
            }else{
                SendDownOrUpToInput(tempCrossKey,new int[]{},1);
                p3.setAction(MotionEvent.ACTION_UP);
                ReflectCrossKeyToScreen(new View[]{},p3);
            }
            //第三列
        }else if(changPos[0] > targetPos[0] + p1.getWidth() - initPos[0] && changPos[0] <= p2.getWidth()){
            if(changPos[1] < targetPos[1] - initPos[1] && changPos[1] >= 0){
                //右上
                Log.i("CrossKey","Up-Right");
                if(p3.getAction() != MotionEvent.ACTION_MOVE) {
                    ReflectCrossKeyToScreen(new View[]{}, p3);
                }
                return (new int[]{Keyboard.KEY_W,Keyboard.KEY_D});
            }else if(changPos[1] <= targetPos[1] + p1.getHeight() - initPos[1] && changPos[1] >= targetPos[1] -initPos[1]){
                //右中
                Log.i("CrossKey","Right");
                ReflectCrossKeyToScreen(new View[]{crosskeychildren[1],crosskeychildren[3]},p3);
                return (new int[]{Keyboard.KEY_D});
            }else if(changPos[1] > 0 && changPos[1] > changPos[1] + p1.getHeight() - initPos[1] && changPos[1] <= p2.getHeight()){
                //右下
                Log.i("CrossKey","Down-Right");
                if(p3.getAction() != MotionEvent.ACTION_MOVE) {
                    ReflectCrossKeyToScreen(new View[]{}, p3);
                }
                return (new int[]{Keyboard.KEY_S,Keyboard.KEY_D});
            }else{
                SendDownOrUpToInput(tempCrossKey,new int[]{},1);
                p3.setAction(MotionEvent.ACTION_UP);
                ReflectCrossKeyToScreen(new View[]{},p3);
            }
        }else{
            SendDownOrUpToInput(tempCrossKey,new int[]{},1);
            p3.setAction(MotionEvent.ACTION_UP);
            ReflectCrossKeyToScreen(new View[]{},p3);
        }
        return (new int[]{});
    }

    private void ReflectCrossKeyToScreen(View[] views,MotionEvent p1){
        switch(p1.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                for(View v1:crosskeychildren){
                    v1.setVisibility(View.INVISIBLE);
                    for(View v2:views){
                        if(v1 == v2){
                            v1.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                for(View v1:crosskeychildren){
                    v1.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void SendDownOrUpToInput(int[] recordKeys,int[] downKeys,int mode){
        if(recordKeys == null){
                tempCrossKey = downKeys;
            for (int temp : downKeys) {
                Log.i("DnOrUpInput", "Catch Index: " + temp);
                sendKeyEvent(temp, true);

                //时序
                paused();

            }
        }else if(Arrays.equals(recordKeys,downKeys)){
            Log.i("DnOrUpInput","KeepPressed.");
            return;
        }else{
            for(int temp:recordKeys){
                Log.i("DnOrUpInput","Release Index: "+temp);
                sendKeyEvent(temp, false);

                //时序
                paused();

            }
                tempCrossKey = downKeys;
            for (int temp : downKeys) {
                Log.i("DnOrUpInput", "Catch Index: " + temp);
                sendKeyEvent(temp, true);
            }
        }
    }

    private void MoveViewByTouch(View p1,View p2, MotionEvent p3){
        switch(p3.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!layoutsPos.containsKey(p2)){
                    layoutsPos.put(p2,(new int[]{(int)p3.getRawX(),(int)p3.getRawY()}));
                }else{
                    layoutsPos.remove(p2);
                    layoutsPos.put(p2,(new int[]{(int)p3.getRawX(),(int)p3.getRawY()}));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) p3.getRawX() - layoutsPos.get(p2)[0];
                int dy = (int) p3.getRawY() - layoutsPos.get(p2)[1];
                int l = p2.getLeft() + dx;
                int b = p2.getBottom() + dy;
                int r = p2.getRight() + dx;
                int t = p2.getTop() + dy;
                //下面判断移动是否超出屏幕
                if(l < 0){
                    l = 0;
                    r = l + p2.getWidth();
                }
                if(t < 0){
                    t = 0;
                    b = t+ p2.getHeight();
                }
                if(r > screenWidth){
                    r = screenWidth;
                    l = r - p2.getWidth();
                }
                if(b > screenHeight){
                    b = screenHeight;
                    t = b - p2.getHeight();
                }
                p2.layout(l,t,r,b);
                layoutsPos.remove(p2);
                layoutsPos.put(p2,(new int[]{(int)p3.getRawX(),(int)p3.getRawY()}));
                p2.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

    }

    private void paused(){
        try {
            Thread.sleep(PAUSE_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendKeyEvent(int keyCode,boolean pressed){
        mController.sendKey(new BaseKeyEvent("Controller(CrossKey)",null,keyCode,pressed, Event.KEYBOARD_BUTTON,null));
    }

}
