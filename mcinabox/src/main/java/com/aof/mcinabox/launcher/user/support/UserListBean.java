package com.aof.mcinabox.launcher.user.support;

import android.content.Context;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.setting.support.SettingJson;

public class UserListBean {
    private String user_name;
    private String auth_uuid;
    private String auth_access_token;
    private String user_model;
    private int user_image;
    private boolean selected;
    private Context context;

    public UserListBean() {
        this.user_name = "Steve";
        this.user_model = SettingJson.USER_TYPE_OFFLINE;
        this.user_image = R.drawable.ic_account_box_black_24dp;
    }


    public String getUser_name() {
        return user_name;
    }

    public UserListBean setUser_name(String user_name) {
        this.user_name = user_name;
        return this;
    }

    public int getUser_image() {
        return user_image;
    }

    public UserListBean setUser_image(int user_image_id) {
        this.user_image = user_image_id;
        return this;
    }

    public String getUser_model() {
        return user_model;
    }

    public UserListBean setUser_model(String user_model) {
        this.user_model = user_model;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public UserListBean setSelected(boolean isSelected) {
        this.selected = isSelected;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public UserListBean setContext(Context context) {
        this.context = context;
        return this;
    }

    public String getAuth_UUID() {
        return auth_uuid;
    }

    public UserListBean setAuth_UUID(String auth_uuid) {
        this.auth_uuid = auth_uuid;
        return this;
    }

    public String getAuth_Access_Token() {
        return auth_access_token;
    }

    public UserListBean setAuth_Access_Token(String auth_access_token) {
        this.auth_access_token = auth_access_token;
        return this;
    }

}
