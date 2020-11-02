package com.aof.mcinabox.launcher.tipper;

import android.content.Context;
import android.view.View;

import com.aof.mcinabox.launcher.tipper.support.TipperListBean;
import com.aof.mcinabox.launcher.tipper.support.TipperRunable;

import java.util.ArrayList;

public class TipperManager {

    public final static int TIPPER_LEVEL_NOTE = 0;
    public final static int TIPPER_LEVEL_WARN = 1;
    public final static int TIPPER_LEVEL_ERROR = 2;

    private final static String TAG = "TipperManager";

    private Tipper mTipper;
    private ArrayList<TipperListBean> tipperList;

    public TipperManager(Context context){
        mTipper = new Tipper(context);
        tipperList = new ArrayList<>();
    }

    public static TipperListBean createTipBean(Context context, int level, String des /*描述*/, TipperRunable runable, int id){
        return new TipperListBean()
                .setContext(context)
                .setTipper_level(level)
                .setTipper_info(des)
                .setTipper_runable(runable)
                .setTipper_id(id);
    }

    public void addTip(TipperListBean tipbean){
        for(TipperListBean bean : tipperList){
            if(bean.getTipper_id() == tipbean.getTipper_id()){
                return;
            }
        }
        tipperList.add(tipbean);
    }

    public void removeTip(int id){
        for(TipperListBean bean : tipperList){
            if(bean.getTipper_id() == id){
                tipperList.remove(bean);
                break;
            }
        }
        ArrayList<TipperListBean> tmp = new ArrayList<>();
        for(TipperListBean bean : tipperList){
            if(bean != null){
                tmp.add(bean);
            }
        }
        this.tipperList = tmp;
    }

    public void showTipper(View underView){
        mTipper.showTipper(underView, this.tipperList);
    }

    public void clearTipper(){
        this.tipperList = new ArrayList<>();
    }

    public int getTipCounts(){
        return this.tipperList.size();
    }

    public int getTipCounts(int level){
        int a = 0;
        for(int b = 0; b < tipperList.size(); b++){
            if (tipperList.get(b).getTipper_level() == level){
                a++;
            }
        }
        return a;
    }

}
