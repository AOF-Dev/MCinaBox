package com.aof.mcinabox.launcher.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aof.mcinabox.DataPathManifest;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.launcher.uis.UserUI;
import com.aof.mcinabox.minecraft.Login;

import java.util.UUID;

public class CreateUserDialog extends StandDialog {

    public CreateUserDialog(MainActivity context, int layoutID){
        super(context,layoutID);
    }

    private Button buttonOK;
    private Button buttonCancel;
    private EditText editUsername;
    private EditText editPassword;
    private LinearLayout layoutPassword;
    private CheckBox checkboxUsermodel;

    private View[] views;

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

        views = new View[]{buttonOK,buttonCancel};
        for(View v : views){
            v.setOnClickListener(clickListener);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if(v == buttonOK){
                CreateNewUser();
                dismiss();
            }
            if(v == buttonCancel){
                dismiss();
            }
        }
    };

    /**
     * 【添加一个新用户】
     **/
    private void CreateNewUser() {
        SettingJson setting = com.aof.mcinabox.launcher.JsonUtils.getSettingFromFile(DataPathManifest.MCINABOX_FILE_JSON);
        SettingJson.Accounts[] accounts = setting.getAccounts();
        SettingJson.Accounts newAccount = new SettingJson().newAccounts;


        String username = editUsername.getText().toString();
        String userpasswd = editPassword.getText().toString();
        boolean usermodel = checkboxUsermodel.isChecked();

        if(username.equals("")){
            Toast.makeText(mContext, mContext.getString(R.string.tips_user_nousername), Toast.LENGTH_SHORT).show();
            return;
        }
        if(accounts != null){
            for(SettingJson.Accounts account : accounts){
                if (account.getUsername().equals(username)) {
                    Toast.makeText(mContext, mContext.getString(R.string.tips_user_sameusername), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        if (usermodel) {
            Toast.makeText(mContext, mContext.getString(R.string.tips_login_wait), Toast.LENGTH_SHORT).show();
            new Login(this,(MainActivity) mContext).execute(username, userpasswd);
            //Must Stop here...
            //The Online Account will be added in the OnlineLogin(String e).
            return;
        } else {
            newAccount.setUsername(username);
            newAccount.setType("offline");
            newAccount.setSelected(false);
            newAccount.setUuid(UUID.nameUUIDFromBytes((username).getBytes()).toString());
            newAccount.setAccessToken("0");
            Toast.makeText(mContext, mContext.getString(R.string.tips_add_success), Toast.LENGTH_SHORT).show();
        }

        UserUI uiUser = ((MainActivity)mContext).uiUser;
        uiUser.addFormedUser(newAccount);

    }

    public void OnlineLogin(String e) {

        SettingJson.Accounts newAccount = new SettingJson().newAccounts;

        if(e == null){
            SharedPreferences prefs = mContext.getSharedPreferences("launcher_prefs", 0);
            String accessToken = prefs.getString("auth_accessToken", "0");
            String userUUID = prefs.getString("auth_profile_id", "00000000-0000-0000-0000-000000000000");
            String username = prefs.getString("auth_profile_name", "Player");

            newAccount.setUsername(username);
            newAccount.setType("online");
            newAccount.setSelected(false);
            newAccount.setUuid(userUUID);
            newAccount.setAccessToken(accessToken);
        }else{
            Toast.makeText(mContext, e, Toast.LENGTH_SHORT).show();
        }

        UserUI uiUser = ((MainActivity)mContext).uiUser;
        uiUser.addFormedUser(newAccount);

    }



}
