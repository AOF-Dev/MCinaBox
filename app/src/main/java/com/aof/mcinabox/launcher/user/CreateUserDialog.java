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
import android.widget.Toast;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.user.support.AuthenticateResponse;
import com.aof.mcinabox.launcher.user.support.LoginServer;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;
import com.aof.mcinabox.utils.dialog.support.TaskDialog;

public class CreateUserDialog extends Dialog implements View.OnClickListener, CheckBox.OnCheckedChangeListener {

    private final Context mContext;

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

    private String pre_username;
    private String pre_url;
    private boolean useCustom = false;
    public CreateUserDialog(Context context, String username, String url){
        super(context);
        this.mContext = context;
        this.pre_username = username;
        this.pre_url = url;
        setContentView(R.layout.dialog_createuser);
        useCustom = true;
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

        if(useCustom){
            checkboxUsermodel.setChecked(true);
            checkboxUsermodel.setClickable(false);
            editUsername.setText(pre_username);
            editServer.setText(pre_url);
            editServer.setEnabled(false);
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
        for(String str : UserManager.getUsersName(OldMainActivity.Setting)){
            if (str.equals(username)){
                Toast.makeText(mContext, mContext.getString(R.string.tips_the_user_has_been_created), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(username.equals("")){
            Toast.makeText(mContext, mContext.getString(R.string.tips_user_name_can_not_be_void), Toast.LENGTH_SHORT).show();
            return false;
        }
        //检查密码是否为空
        if(enableLegal){
            if(password.equals("")){
                Toast.makeText(mContext, mContext.getString(R.string.tips_password_can_not_be_void), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //创建用户
        if(enableLegal){
            new LoginServer(server).setCallback(new LoginServer.Callback() {
                final TaskDialog mDialog = DialogUtils.createTaskDialog(mContext,mContext.getString(R.string.tips_logging),"",false);
                @Override
                public void onStart() {
                    mDialog.show();
                }

                @Override
                public void onFailed(Exception e) {
                    DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_error),String.format(mContext.getString(R.string.tips_error),e.getMessage()),mContext.getString(R.string.title_ok),null);
                }

                @Override
                public void onLoginSuccess(final SettingJson.Account account,final AuthenticateResponse response) {
                    if(response.availableProfiles == null || response.availableProfiles.length == 0){
                        DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_error),mContext.getString(R.string.tips_no_roles_in_current_account),mContext.getString(R.string.title_ok),null);
                        return;
                    }
                    if(response.availableProfiles.length != 1){
                        String[] names = new String[response.availableProfiles.length];
                        for(int a = 0; a < response.availableProfiles.length; a++){
                            names[a] = response.availableProfiles[a].name;
                        }
                        DialogUtils.createItemsChoiceDialog(mContext,mContext.getString(R.string.title_choice),null,null,mContext.getString(R.string.title_cancel),false,names,new DialogSupports(){
                            @Override
                            public void runWhenItemsSelected(int pos) {
                                super.runWhenItemsSelected(pos);
                                account.setAccessToken(response.accessToken);
                                account.setUuid(response.availableProfiles[pos].id);
                                account.setUsername(response.availableProfiles[pos].name);
                                account.setSelected(false);
                                UserManager.addAccount(OldMainActivity.Setting, account);
                            }
                        });
                    }else{
                        account.setAccessToken(response.accessToken);
                        account.setUuid(response.selectedProfile.id);
                        account.setUsername(response.selectedProfile.name);
                        account.setSelected(false);
                        UserManager.addAccount(OldMainActivity.Setting, account);
                    }
                }

                @Override
                public void onValidateSuccess(SettingJson.Account account) {}

                @Override
                public void onValidateFailed(SettingJson.Account account) {}

                @Override
                public void onRefreshSuccess(SettingJson.Account account, AuthenticateResponse response) {}

                @Override
                public void onFinish() {
                    mDialog.dismiss();
                }

            }).login(username, password);
            return true;
        }else{
            UserManager.addAccount(OldMainActivity.Setting,UserManager.getOfflineAccount(username));
        }
        return true;
    }
}
