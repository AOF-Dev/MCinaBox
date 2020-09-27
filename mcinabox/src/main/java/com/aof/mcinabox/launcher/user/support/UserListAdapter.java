package com.aof.mcinabox.launcher.user.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.user.UserManager;
import com.aof.utils.dialog.support.DialogSupports;
import com.aof.utils.dialog.DialogUtils;
import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    private ArrayList<UserListBean> userlist;
    private Context context;
    private ArrayList<RadioButton> recorder = new ArrayList<RadioButton>(){};
    private final static String TAG = "UserListAdapter";

    public UserListAdapter(ArrayList<UserListBean> list){
        userlist = list;
    }

    @Override
    public int getCount(){
        return userlist.size();
    }

    @Override
    public Object getItem(int position){
        return userlist.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public UserListAdapter(Context context, ArrayList<UserListBean> list) {
        this.userlist = list;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_user, null);
            holder = new ViewHolder();
            holder.ivUser = convertView.findViewById(R.id.user_image);
            holder.textUsername = convertView.findViewById(R.id.user_text_username);
            holder.userstate = convertView.findViewById(R.id.user_text_userstate);
            holder.buttonDel = convertView.findViewById(R.id.user_button_removeuser);
            holder.buttonRelogin = convertView.findViewById(R.id.user_button_relogin);
            holder.layout = convertView.findViewById(R.id.small_layout_aboutuser);
            holder.radioSelecter = convertView.findViewById(R.id.radiobutton_selecteduser);
            holder.textUsername.setText(userlist.get(position).getUser_name());

            //用户选择切换
            boolean isDif = true;
            for(RadioButton p1:recorder){
                if(p1 == holder.radioSelecter){
                    isDif = false;
                }
            }

            if(isDif){
                recorder.add(holder.radioSelecter);
            }

            if (userlist.get(position).isSelected()){
                holder.radioSelecter.setChecked(true);
            }

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        //判断是否启用账户刷新按钮
        if(userlist.get(position).getUser_model().equals(SettingJson.USER_TYPE_OFFLINE)){
            holder.buttonRelogin.setVisibility(View.GONE);
        }else{
            holder.buttonRelogin.setVisibility(View.VISIBLE);
            holder.buttonRelogin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    DialogUtils.createBothChoicesDialog(context, context.getString(R.string.title_warn), context.getString(R.string.tips_are_you_sure_to_refresh_online_account), context.getString(R.string.title_ok), context.getString(R.string.title_cancel), new DialogSupports(){
                        @Override
                        public void runWhenPositive(){
                            //TODO:添加正版用户状态刷新功能
                        }
                    });
                }
            });
        }

        //设置账户模式
        if(userlist.get(position).getUser_model().equals(SettingJson.USER_TYPE_OFFLINE)){
            holder.userstate.setText(context.getString(R.string.title_offline));
        }else if(userlist.get(position).getUser_model().equals(SettingJson.USER_TYPE_ONLINE)){
            holder.userstate.setText(context.getString(R.string.title_online));
        }else{
            holder.userstate.setText(context.getString(R.string.title_unknown));
        }

        //添加删除键监听
        holder.buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.createBothChoicesDialog(context, context.getString(R.string.title_warn), context.getString(R.string.tips_warning_delect_user), context.getString(R.string.title_ok), context.getString(R.string.title_cancel), new DialogSupports(){
                    @Override
                    public void runWhenPositive(){
                        UserManager.removeAccount(MainActivity.Setting, userlist.get(position).getUser_name());
                        //删除后重置用户列表
                        MainActivity.CURRENT_ACTIVITY.mUiManager.uiUser.reloadListView();
                    }
                });
            }
        });


        //当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中
        holder.radioSelecter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (RadioButton p1:recorder){
                    p1.setChecked(false);
                }
                holder.radioSelecter.setChecked(true);
                UserManager.setAccountSelected(userlist.get(position).getUser_name());
            }
        });

        return convertView;
    }

    class ViewHolder{
        public RadioButton radioSelecter;
        public ImageView ivUser;
        public TextView textUsername;
        public TextView userstate;
        public Button buttonRelogin;
        public Button buttonDel;
        public LinearLayout layout;
    }
}
