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
import com.aof.mcinabox.launcher.user.support.LoginServer;
import com.aof.utils.PromptUtils;

public class CreateUserDialog extends Dialog implements View.OnClickListener, CheckBox.OnCheckedChangeListener {

    private Context mContext;

    private Button buttonOK;
    private Button buttonCancel;
    private EditText editUsername;
    private EditText editPassword;
    private EditText editServer;
    private LinearLayout layoutPassword;
    private LinearLayout layoutServer;
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
        editServer = findViewById(R.id.dialog_edittext_input_server);
        layoutPassword = findViewById(R.id.dialog_linearlayout_input_userpasswd);
        layoutServer = findViewById(R.id.dialog_linearlayout_input_server);
        checkboxUsermodel = findViewById(R.id.dialog_checkbox_online_model);
        checkboxUsermodel.setOnCheckedChangeListener(this);

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
                layoutPassword.setVisibility(View.VISIBLE);
                layoutServer.setVisibility(View.VISIBLE);
                enableLegal = true;
            }else{
                layoutPassword.setVisibility(View.GONE);
                layoutServer.setVisibility(View.GONE);
                enableLegal = false;
            }
        }
    }

    private boolean addUser(){
        String password = editPassword.getText().toString();
        String username = editUsername.getText().toString();
        String server = editServer.getText().toString();
        //检查用户名
        for(String str : UserManager.getUsersName(MainActivity.Setting)){
            if (str.equals(username)){
                PromptUtils.createPrompt(mContext, mContext.getString(R.string.tips_the_user_has_been_created));
                return false;
            }
        }
        if(username.equals("")){
            PromptUtils.createPrompt(mContext, mContext.getString(R.string.tips_user_name_can_not_be_void));
            return false;
        }
        //检查密码是否为空
        if(enableLegal){
            if(password.equals("")){
                PromptUtils.createPrompt(mContext, mContext.getString(R.string.tips_password_can_not_be_void));
                return false;
            }
        }
        //创建用户
        if(enableLegal){
            new LoginServer(server).login(username, password, UserManager.createUUID(username));
            return true;
        }else{
            UserManager.addAccount(MainActivity.Setting,UserManager.getOfflineAccount(username));
        }
        return true;
    }
}
