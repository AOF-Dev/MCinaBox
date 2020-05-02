package com.aof.mcinabox.launcher.tipper;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.launcher.uis.MainToolbarUI;
import com.aof.mcinabox.utils.FileTool;
import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;

import java.util.ArrayList;

import static com.aof.sharedmodule.Data.DataPathManifest.MCINABOX_RUNTIME_FILES;

public class Tipper {
    public Tipper(Activity context){
        mContext = context;
        initTipper();
    }
    private Activity mContext;
    private ListView listTipper;
    private BubbleLayout bubbleTipper;
    private PopupWindow popupWindow;
    private ArrayList<TipperListBean> tipslist;

    private void initTipper(){
        bubbleTipper = (BubbleLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_popup_tipper, null);
        popupWindow = BubblePopupHelper.create(mContext, bubbleTipper);
        listTipper = bubbleTipper.findViewById(R.id.tipper_list);

    }

    /**【在View下方显示Tipper】**/
    public void showTipper(View v){
        TipperListAdapter tipperListAdapter = new TipperListAdapter(mContext, tipslist);
        listTipper.setAdapter(tipperListAdapter);
        int[] location = new int[2];
        v.getLocationInWindow(location);
        bubbleTipper.setArrowDirection(ArrowDirection.TOP);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], v.getHeight() + location[1]);
    }

    public void refreshTipper(SettingJson setting,MainToolbarUI toolbarUI){
        ArrayList<Integer> tip_indexs = new ArrayList<>();

        boolean User_isSelected = false;
        boolean Keyboard_isSelected = false;
        boolean Minecraft_isSelected = false;
        boolean Runtime_isImported = false;

        //检查用户是否选择
        SettingJson.Accounts[] accounts = setting.getAccounts();
        for(SettingJson.Accounts p1 : accounts){
            if(p1.isSelected()){
                User_isSelected = true;
            }
        }

        //检查键盘模版是否选择
            if(setting.getKeyboard() != null && !setting.getKeyboard().equals("")){
                Keyboard_isSelected = true;
            }


        //检查游戏版本是否选择
        if(setting.getLastVersion() != null && !setting.getLastVersion().equals("")){
            Minecraft_isSelected = true;
        }

        //检查运行库是否导入
        for(String p1 : MCINABOX_RUNTIME_FILES) {
            if (FileTool.isFileExists(p1)) {
                Runtime_isImported = true;
            }
        }

        //检查内存大小设置是否正确
            if(setting.getConfigurations().getMaxMemory() >= 128 && setting.getConfigurations().getMaxMemory() <= 1024){
                //nothing
            }else{
                tip_indexs.add(5);
            }

        //MainActivity context = (MainActivity) mContext;
        //MainToolbarUI toolbarUI = ((MainActivity) mContext).uiMainToolbar;

        if(User_isSelected && Keyboard_isSelected && Minecraft_isSelected && Runtime_isImported ){
            toolbarUI.setTaskInfoBackground(R.drawable.ic_info_outline_blue_500_24dp);
            return;
        }else{
            toolbarUI.setTaskInfoBackground(R.drawable.ic_info_red_500_24dp);
            if(!User_isSelected){
                tip_indexs.add(1);
            }
            if(!Keyboard_isSelected){
                tip_indexs.add(3);
            }
            if(!Minecraft_isSelected){
                tip_indexs.add(2);
            }
            if(!Runtime_isImported){
                tip_indexs.add(4);
            }
        }

        if(tip_indexs.size() != 0){
            ArrayList<TipperListBean> tipperlist = new ArrayList<TipperListBean>();
            for (int index : tip_indexs){
                TipperListBean tmp = new TipperListBean();
                tmp.setContext(mContext);
                tmp.setTipper_index(index);
                tipperlist.add(tmp);
            }
            tipslist = tipperlist;
        }

    }


}
