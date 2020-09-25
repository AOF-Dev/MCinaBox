package com.aof.mcinabox.launcher.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.user.support.UserListBean;
import com.aof.utils.FileTool;
import java.io.File;
import java.util.UUID;

public class UserManager {

    public final static String launcher_prefs_file = "launcher_prefs";
    public final static String auth_accessToken = "auth_accessToken";
    public final static String auth_profile_id = "auth_profile_id";
    public final static String auth_profile_name = "auth_profile_name";
    public final static String auth_clientId = "auth_clientId";
    public final static String auth_importedCredentials = "auth_importedCredentials";

    public static SettingJson.Account getOfflineAccount(String username) {
        SettingJson.Account user = new SettingJson().new Account();

        user.setAccessToken("0");
        user.setSelected(false);
        user.setType(SettingJson.USER_TYPE_OFFLINE);
        user.setUuid(UUID.nameUUIDFromBytes((username).getBytes()).toString());
        user.setUsername(username);

        return user;
    }

    public static boolean addAccount(SettingJson setting, SettingJson.Account account){
        if (setting.getAccounts() == null){
            return false;
        }

        SettingJson.Account[] accounts = new SettingJson.Account[setting.getAccounts().length +1];
        SettingJson.Account[] lastAccounts = setting.getAccounts();
        int a = 0;

        for(; a < lastAccounts.length ; a++){
            if(lastAccounts[a] != null){
                accounts[a] = lastAccounts[a];
            }
        }

        accounts[a] = account;
        setting.setAccounts(accounts);
        return true;
    }

    public static SettingJson.Account getOnlineAccount(Context context){
        SharedPreferences prefs = context.getSharedPreferences("launcher_prefs", 0);

        if( ! prefs.contains(auth_profile_name)){
            return null;
        }

        SettingJson.Account account = new SettingJson().new Account();
        account.setUsername(prefs.getString(auth_profile_name,"Player"));
        account.setType(SettingJson.USER_TYPE_ONLINE);
        account.setSelected(false);
        account.setUuid(prefs.getString(auth_profile_id,"00000000-0000-0000-0000-000000000000"));
        account.setAccessToken(prefs.getString(auth_accessToken,"0"));

        return account;
    }

    public static UserListBean account2Bean(Context context, SettingJson.Account account){
        if(account != null){
            return new UserListBean()
                    .setUser_name(account.getUsername())
                    .setAuth_Access_Token(account.getAccessToken())
                    .setAuth_UUID(account.getUuid())
                    .setContext(context)
                    .setSelected(account.isSelected())
                    .setUser_model(account.getType());
        }else{
            return null;
        }
    }

    public static SettingJson.Account bean2Account(UserListBean bean){
        if(bean != null){
            return new SettingJson().new Account()
                    .setAccessToken(bean.getAuth_Access_Token())
                    .setSelected(bean.isSelected())
                    .setType(bean.getUser_model())
                    .setUuid(bean.getAuth_UUID())
                    .setUsername(bean.getUser_name());
        }else{
            return null;
        }
    }

    public static String[] getUsersName(SettingJson setting){
        String[] strs = new String[setting.getAccounts().length];
        for(int i = 0; i < strs.length ; i ++){
            strs[i] = setting.getAccounts()[i].getUsername();
        }
        return strs;
    }

    public static SettingJson.Account getSelectedAccount(SettingJson setting){
        for(SettingJson.Account account : setting.getAccounts()){
            if(account.isSelected()){
                return account;
            }
        }
        return null;
    }

    public static SettingJson.Account getAccountByUsername(SettingJson setting, String username){
        for(SettingJson.Account account : setting.getAccounts()){
            if(account.getUsername().equals(username)){
                return account;
            }
        }
        return null;
    }

    public static boolean removeAccount(SettingJson setting, String username){
        SettingJson.Account[] accounts = setting.getAccounts();
        SettingJson.Account target = null;
        for(SettingJson.Account account : accounts){
            if(account.getUsername().equals(username)){
                target = account;
            }
        }
        if(target == null){
            return false;
        }else{
            SettingJson.Account[] tmp = new SettingJson.Account[setting.getAccounts().length - 1];
            int a = 0;
            for(SettingJson.Account account : accounts){
                if(target != account){
                    tmp[a] = account;
                    a++;
                }
            }
            setting.setAccounts(tmp);
            return true;
        }
    }

    public static boolean cantainAccount(SettingJson setting, String username){
        for(SettingJson.Account account : setting.getAccounts()){
            if(account.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public static boolean cantainAccount(SettingJson setting, SettingJson.Account account){
        return cantainAccount(setting, account.getUsername());
    }

    public static boolean replaceAccount(SettingJson setting, SettingJson.Account originalAccount  , SettingJson.Account account){
        if(cantainAccount(setting,originalAccount)){
            removeAccount(setting, originalAccount.getUsername());
            addAccount(setting, account);
            return true;
        }else{
            return false;
        }
    }

    public static boolean replaceAccount(SettingJson setting, String originalUsername  , SettingJson.Account account){
        return replaceAccount(setting, getAccountByUsername(setting, originalUsername), account);
    }

    public static void clearLegalData(Context context){
        FileTool.deleteFile(new File(context.getExternalFilesDir(null) + "/shared_prefs/" + launcher_prefs_file + ".xml"));
    }

    public static void setAccountSelected(String username){
        SettingJson.Account a = getAccountByUsername(MainActivity.Setting,username);
        for(SettingJson.Account account : MainActivity.Setting.getAccounts()){
            if(account == a){
                account.setSelected(true);
            }else{
                account.setSelected(false);
            }
        }
    }


}
