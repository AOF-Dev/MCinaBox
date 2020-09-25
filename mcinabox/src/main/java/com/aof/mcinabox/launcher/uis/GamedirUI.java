package com.aof.mcinabox.launcher.uis;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.gamedir.GamedirManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.utils.PromptUtils;
import com.aof.utils.dialog.DialogUtils;
import com.aof.utils.dialog.support.DialogSupports;

import java.io.File;

public class GamedirUI extends BaseUI {

    public GamedirUI(Context context) {
        super(context);
    }

    private LinearLayout layout_gamedir;
    private LinearLayout layoutPrivate;
    private LinearLayout layoutPublic;
    private EditText editGamedir;
    private Button buttonSave;
    private Animation showAnim;
    private SettingJson setting;

    @Override
    public void onCreate() {
        super.onCreate();
        setting = MainActivity.Setting;
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_gamedir = MainActivity.CURRENT_ACTIVITY.findViewById(R.id.layout_gamedir);
        layoutPrivate = layout_gamedir.findViewById(R.id.gamedir_select_private);
        layoutPublic = layout_gamedir.findViewById(R.id.gamedir_select_public);
        buttonSave = layout_gamedir.findViewById(R.id.gamedir_button_save);
        editGamedir = layout_gamedir.findViewById(R.id.gamedir_edit_gamedir);

        for(View v : new View[]{buttonSave,layoutPublic,layoutPrivate}){
            v.setOnClickListener(clickListener);
        }

        init();

    }

    private void init(){
        editGamedir.setText(GamedirManager.getGamedir(MainActivity.Setting));
    }

    @Override
    public void refreshUI() {

    }

    @Override
    public void saveUIConfig() {

    }

    @Override
    public void setUIVisiability(int visiability) {
        if(visiability == View.VISIBLE){
            layout_gamedir.startAnimation(showAnim);
        }
        layout_gamedir.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_gamedir.getVisibility();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == buttonSave){
                String t = editGamedir.getText().toString();
                //过滤掉最后一个反斜杠
                if(t.charAt(t.length() - 1) == '/'){
                    StringBuilder tmp = new StringBuilder();
                    for(int a = 0; a < t.length() - 1; a++){
                        tmp.append(t.charAt(a));
                    }
                    t = tmp.toString();
                    editGamedir.setText(t);
                }

                final File dir = new File(t);
                if(dir.exists() && !dir.isDirectory()){
                    DialogUtils.createSingleChoiceDialog(mContext,"错误","目标路径是文件而不是文件夹","确定",null);
                }else if(!dir.exists()){
                    DialogUtils.createBothChoicesDialog(mContext,"警告","目标路径不存在，是否创建文件夹？","确定","取消",new DialogSupports(){
                        @Override
                        public void runWhenPositive() {
                            if(!GamedirManager.setGamedir(mContext, MainActivity.Setting, dir.getAbsolutePath())){
                                DialogUtils.createSingleChoiceDialog(mContext,"错误","发生未知错误，路径设置失败！","确定",null);
                            }else{
                                DialogUtils.createSingleChoiceDialog(mContext,"提示","路径修改成功！","确定",null);
                            }
                        }
                    });
                }else{
                    if(!GamedirManager.setGamedir(mContext, MainActivity.Setting, dir.getAbsolutePath())){
                        DialogUtils.createSingleChoiceDialog(mContext,"错误","发生未知错误，路径设置失败！","确定",null);
                    }else{
                        DialogUtils.createSingleChoiceDialog(mContext,"提示","路径修改成功！","确定",null);
                    }
                }
            }

            if(v == layoutPublic){
                editGamedir.setText(GamedirManager.PUBLIC_GAMEDIR);
            }

            if(v == layoutPrivate){
                editGamedir.setText(GamedirManager.PRIVATE_GAMEDIR);
            }
        }
    };
}
