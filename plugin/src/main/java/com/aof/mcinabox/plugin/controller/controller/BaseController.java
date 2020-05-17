package com.aof.mcinabox.plugin.controller.controller;

import android.app.Activity;

import com.aof.mcinabox.plugin.controller.inputer.Inputer;
import com.aof.mcinabox.plugin.controller.client.Client;

import java.util.ArrayList;

public abstract class BaseController implements Controller {
    public ArrayList<Inputer> inputers;
    public Client client;
    public Activity context;

    public BaseController(Activity activity){
        context = activity;
        client = (Client) activity;
        inputers = new ArrayList<>();
    }

    @Override
    public boolean containInputer(Inputer inputer){
        for(Inputer i : inputers){
            if(i == inputer){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addInputer(Inputer inputer){
        if(containInputer(inputer) || inputer == null){
            return false;
        }else{
            if(inputer.load(context,this)){
                inputers.add(inputer);
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    public boolean removeInputer(Inputer inputer){
        if(!containInputer(inputer) || inputer == null){
            return false;
        }else{
            if(inputer.unload()){
                ArrayList<Inputer> tmp = new ArrayList<>();
                for(Inputer i : inputers){
                    if(inputer != i){
                        tmp.add(i);
                    }
                }
                inputers = tmp;
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    public int getInputerCounts(){
        return inputers.size();
    }

    @Override
    public boolean removeAllInputers(){
        boolean success = true;
        for(Inputer i : inputers){
            if(!removeInputer(i)){
                success = false;
            }
        }
        return success;
    }

    @Override
    public ArrayList<Inputer> getAllInputers(){
        return inputers;
    }
}
