package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.launcher.user.UserListAdapter;
import com.aof.mcinabox.launcher.user.UserListBean;

import static com.aof.mcinabox.DataPathManifest.*;

import java.util.ArrayList;

public class UserUI extends BaseUI {

    public UserUI(Activity context) {
        super(context);
    }

    private LinearLayout layout_user;
    private LinearLayout buttonCreateUser;
    private LinearLayout buttonRefreshUserList;
    private ListView listUsers;
    private Animation showAnim;

    private View[] views;


    @Override
    public void onCreate(SettingJson setting) {
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_user = mContext.findViewById(R.id.layout_user);
        buttonCreateUser = layout_user.findViewById(R.id.layout_user_adduser);
        buttonRefreshUserList = layout_user.findViewById(R.id.layout_user_reflash_userlist);
        listUsers = layout_user.findViewById(R.id.list_user);

        views = new View[]{buttonCreateUser, buttonRefreshUserList};
        for (View v : views) {
            v.setOnClickListener(clickListener);
        }
        refreshUI(setting);
    }

    @Override
    public void refreshUI(SettingJson setting) {
        refreshLocalUserList(setting);
    }

    @Override
    public SettingJson saveUIConfig(SettingJson setting) {
        saveUserList(setting);
        return setting;
    }

    @Override
    public void setUIVisiability(int visiability) {
        if(visiability == View.VISIBLE){
            layout_user.startAnimation(showAnim);
        }
        layout_user.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_user.getVisibility();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonCreateUser) {
                ((MainActivity) mContext).dialogCreateUser.show();
            }
            if (v == buttonRefreshUserList) {
                ((MainActivity) mContext).refreshLauncher(null, true);
            }
        }

    };

    /**
     * 【刷新本地用户列表】
     * Refresh User list
     **/
    private ArrayList<UserListBean> userlist = new ArrayList<UserListBean>();
    private void refreshLocalUserList(SettingJson setting) {
        SettingJson.Accounts[] accounts = setting.getAccounts();
        ArrayList<UserListBean> tmp = new ArrayList<UserListBean>() {
        };
        if (accounts == null) {
            userlist = new ArrayList<UserListBean>() {
            };
        } else {
            for (SettingJson.Accounts account : accounts) {
                UserListBean user = new UserListBean();
                user.setUser_name(account.getUsername());
                user.setUser_model(account.getType());
                user.setIsSelected(account.isSelected());
                user.setAuth_UUID(account.getUuid());
                user.setAuth_Access_Token(account.getAccessToken());
                user.setContext(mContext);
                tmp.add(user);
            }
        }
        userlist = tmp;
        if (listUsers.getAdapter() == null) {
            UserListAdapter userlistadapter = new UserListAdapter(mContext, userlist);
            listUsers.setAdapter(userlistadapter);
        } else {
            listUsers.deferNotifyDataSetChanged();
        }
    }

    /**
     * 【添加一个配制好的用户】
     **/
    public void addFormedUser(SettingJson.Accounts account) {
        UserListBean user = new UserListBean();
        user.setUser_name(account.getUsername());
        user.setUser_model(account.getType());
        user.setIsSelected(account.isSelected());
        user.setAuth_UUID(account.getUuid());
        user.setAuth_Access_Token(account.getAccessToken());
        user.setContext(mContext);
        userlist.add(user);
        listUsers.deferNotifyDataSetChanged();
    }

    /**
     * 【保存用户列表】
     * Save user data.
     **/
    private SettingJson saveUserList(SettingJson setting) {
        SettingJson.Accounts[] accounts;
        if (listUsers.getAdapter() == null) {
            accounts = new SettingJson.Accounts[0];
        } else {
            accounts = new SettingJson.Accounts[listUsers.getAdapter().getCount()];
            for (int i = 0; i < listUsers.getAdapter().getCount(); i++) {
                SettingJson.Accounts account = new SettingJson().newAccounts;
                UserListBean user = (UserListBean) listUsers.getAdapter().getItem(i);
                account.setSelected(user.isIsSelected());
                account.setUsername(user.getUser_name());
                account.setType(user.getUser_model());
                account.setUuid(user.getAuth_UUID());
                account.setAccessToken(user.getAuth_Access_Token());

                accounts[i] = account;
            }
        }
        setting.setAccounts(accounts);
        return setting;
    }

}
