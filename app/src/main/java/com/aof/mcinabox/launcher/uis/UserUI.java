package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.user.CreateUserDialog;
import com.aof.mcinabox.launcher.user.support.UserListAdapter;

import java.util.ArrayList;

public class UserUI extends BaseUI {

    public UserUI(Context context) {
        super(context);
    }

    private LinearLayout layout_user;
    private LinearLayout buttonCreateUser;
    private LinearLayout buttonRefreshUserList;
    private ListView listUsers;
    private Animation showAnim;
    private SettingJson setting;

    private final View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonCreateUser) {
                new CreateUserDialog(mContext).show();
            }
            if (v == buttonRefreshUserList) {
                refreshList();
            }
        }
    };

    @Override
    public void refreshUI() {

    }

    @Override
    public void saveUIConfig() {
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

    @Override
    public void onCreate() {
        super.onCreate();
        setting = OldMainActivity.Setting;
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_user = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_user);
        buttonCreateUser = layout_user.findViewById(R.id.layout_user_adduser);
        buttonRefreshUserList = layout_user.findViewById(R.id.layout_user_reflash_userlist);
        listUsers = layout_user.findViewById(R.id.list_user);

        for (View v : new View[]{buttonCreateUser, buttonRefreshUserList}) {
            v.setOnClickListener(clickListener);
        }

        refreshList();
    }

    public void reloadListView() {
        for (SettingJson.Account account : OldMainActivity.Setting.getAccounts()) {
            if (account != null) {
                usersList.add(account);
            }
        }
        this.listUsers.setAdapter(new UserListAdapter(mContext, usersList));
        refreshList();
    }

    private ArrayList<SettingJson.Account> usersList;
    public void refreshList(){
        if(usersList == null){
            usersList = new ArrayList<>();
            listUsers.setAdapter(new UserListAdapter(mContext,usersList));
        }else{
            usersList.clear();
        }
        for(SettingJson.Account account : OldMainActivity.Setting.getAccounts()){
            if(account != null){
                usersList.add(account);
            }
        }
        ((BaseAdapter)listUsers.getAdapter()).notifyDataSetChanged();
    }

    public boolean addFormatedUser(SettingJson.Account account){
        if(account == null){
            return false;
        }else{
            for(SettingJson.Account bean : usersList){
                if(bean.equals(account)){
                    return false;
                }
            }
            usersList.add(account);
            refreshList();
            return true;
        }
    }

}
