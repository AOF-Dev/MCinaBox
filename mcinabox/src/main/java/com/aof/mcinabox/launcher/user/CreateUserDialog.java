package com.aof.mcinabox.launcher.user;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.user.support.Login;
import com.aof.utils.PromptUtils;

public class CreateUserDialog extends Dialog implements View.OnClickListener, CheckBox.OnCheckedChangeListener {

    private Context mContext;

    private Button buttonOK;
    private Button buttonCancel;
    private EditText editUsername;
    private EditText editPassword;
    private LinearLayout layoutPassword;
    private CheckBox checkboxUsermodel;

    private boolean enableLegal = false;

    public CreateUserDialog(Context context) {
        super(context);
        this.mContext = context;
        setContentView(R.layout.dialog_createuser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        buttonOK = findViewById(R.id.dialog_button_confirm_createuser);
        buttonCancel = findViewById(R.id.dialog_button_cancle_createuser);
        editUsername = findViewById(R.id.dialog_edittext_input_username);
        editPassword = findViewById(R.id.dialog_edittext_input_userpasswd);
        layoutPassword = findViewById(R.id.dialog_linearlayout_input_userpasswd);
        checkboxUsermodel = findViewById(R.id.dialog_checkbox_online_model);
        checkboxUsermodel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) layoutPassword.setVisibility(View.VISIBLE);
                else layoutPassword.setVisibility(View.GONE);
            }
        });

        for (View v : new View[]{buttonOK, buttonCancel}) {
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {

        if(v == buttonOK){
            if(addUser()){
                dismiss();
            }
        }
        if(v == buttonCancel){
            this.cancel();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == checkboxUsermodel){
            if(isChecked){
                editPassword.setVisibility(View.VISIBLE);
                enableLegal = true;
            }else{
                editPassword.setVisibility(View.GONE);
                enableLegal = false;
            }
        }
    }

    private boolean addUser(){
        String password = editPassword.getText().toString();
        String username = editUsername.getText().toString();
        //检查用户名
        for(String str : UserManager.getUsersName(MainActivity.Setting)){
            if (str.equals(username)){
                PromptUtils.createPrompt(mContext, "用户已经存在!");
                return false;
            }
        }
        if(username.equals("")){
            PromptUtils.createPrompt(mContext, "用户名不能为空!");
            return false;
        }
        //检查密码是否为空
        if(enableLegal){
            if(password.equals("")){
                PromptUtils.createPrompt(mContext, "密码不能为空!");
                return false;
            }
        }
        //创建用户
        if(enableLegal){
            new Login(mContext).execute(username, password);
        }else{
            UserManager.addAccount(MainActivity.Setting,UserManager.getOfflineAccount(username));
        }
        //主动调用UIManager
        MainActivity.CURRENT_ACTIVITY.mUiManager.uiUser.reloadListView();
        return true;
    }
}
