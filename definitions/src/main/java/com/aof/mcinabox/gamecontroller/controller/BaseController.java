package com.aof.mcinabox.gamecontroller.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.gamecontroller.client.ClientInput;
import java.util.ArrayList;

import static com.aof.mcinabox.definitions.id.key.KeyMode.MARK_INPUT_MODE_ALONE;

public abstract class BaseController implements Controller {
    public ArrayList<Input> inputs;
    public ClientInput client;
    public Context context;
    private int inputMode = MARK_INPUT_MODE_ALONE;
    private final static String TAG = "BaseController";

    public BaseController(Context context, ClientInput client){
        this.context = context;
        this.client = client;
        inputs = new ArrayList<>();
    }

    @Override
    public boolean containInput(Input input){
        for(Input i : inputs){
            if(i == input){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addInput(Input input){
        if(containInput(input) || input == null){
            return false;
        }else{
            if(input.load(context,this)){
                inputs.add(input);
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    public boolean removeInput(Input input){
        if(!containInput(input) || input == null){
            return false;
        }else{
            if(input.unload()){
                ArrayList<Input> tmp = new ArrayList<>();
                for(Input i : inputs){
                    if(input != i){
                        tmp.add(i);
                    }
                }
                inputs = tmp;
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    public int getInputCounts(){
        return inputs.size();
    }

    @Override
    public boolean removeAllInputs(){
        boolean success = true;
        for(Input i : inputs){
            if(!removeInput(i)){
                success = false;
            }
        }
        return success;
    }

    @Override
    public ArrayList<Input> getAllInputs(){
        return inputs;
    }

    @Override
    public void setInputMode(int mode){
        this.inputMode = mode;
        for(Input i : inputs){
            i.setInputMode(mode);
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params){
        client.addContentView(view,params);
    }

    @Override
    public void addView(View view){
        client.addView(view);
    }

    @Override
    public void typeWords(String str){
        client.typeWords(str);
    }

    @Override
    public void onStop(){
        this.saveConfig();
    }
    @Override
    public int getInputMode(){
        return this.inputMode;
    }

    @Override
    public int[] getPointer(){
        return client.getPointer();
    }

    @Override
    public void saveConfig(){
        for(Input i : inputs){
            i.saveConfig();
        }
    }
}


