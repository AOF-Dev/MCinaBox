package com.aof.mcinabox.launcher.user;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.aof.mcinabox.R;
import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    private ArrayList<UserListBean> userlist;
    private LayoutInflater mLayoutInflater;
    public UserListAdapter(ArrayList<UserListBean> list){
        userlist = list;
    }
    public ArrayList<RadioButton> recorder = new ArrayList<RadioButton>(){};
    Context context;

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
        userlist = list;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.listview_user, null);
            viewHolder = new ViewHolder();
            viewHolder.userimage = convertView.findViewById(R.id.user_image);
            viewHolder.username = convertView.findViewById(R.id.user_text_username);
            viewHolder.userstate = convertView.findViewById(R.id.user_text_userstate);
            viewHolder.removeuser = convertView.findViewById(R.id.user_button_removeuser);
            viewHolder.linearLayout = convertView.findViewById(R.id.small_layout_aboutuser);
            viewHolder.context = userlist.get(position).getContext();
            viewHolder.radioButton = convertView.findViewById(R.id.radiobutton_selecteduser);
            boolean isDif = true;
            for(RadioButton p1:recorder){
                if(p1 == viewHolder.radioButton){
                    isDif = false;
                }
            }
            if(isDif){
                recorder.add(viewHolder.radioButton);
            }
            if (userlist.get(position).isIsSelected()){
                viewHolder.radioButton.setChecked(true);
            }
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.username.setText(userlist.get(position).getUser_name());
        if(userlist.get(position).getUser_model().equals("offline")){
            viewHolder.userstate.setText(context.getString(R.string.title_offline));
        }else if(userlist.get(position).getUser_model().equals("online")){
            viewHolder.userstate.setText(context.getString(R.string.title_online));
        }else{
            viewHolder.userstate.setText(viewHolder.context.getString(R.string.title_unknown));
        }
        viewHolder.removeuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                final AlertDialog dialog ;
                final AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.context);
                //    设置Title的图标
                //builder.setIcon(R.drawable.ic_launcher);
                //    设置Title的内容
                builder.setTitle(viewHolder.context.getString(R.string.title_warn));
                //    设置Content来显示一个信息
                builder.setMessage(viewHolder.context.getString(R.string.tips_user_remove_warning));
                //    设置一个PositiveButton
                builder.setPositiveButton(viewHolder.context.getString(R.string.tips_ok), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        userlist.remove(position);
                        notifyDataSetChanged();
                    }
                });
                //    设置一个NegativeButton
                builder.setNegativeButton(viewHolder.context.getString(R.string.tips_no), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //如果取消
                        dialog.dismiss();
                    }
                });
                //    显示出该对话框
                dialog = builder.create();
                builder.show();

            }
        });

        //当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中
        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (RadioButton p1:recorder){
                    p1.setChecked(false);
                }
                for(UserListBean p1 : userlist){
                    p1.setIsSelected(false);
                }
                userlist.get(position).setIsSelected(true);
                viewHolder.radioButton.setChecked(true);
            }
        });

        return convertView;
    }

    class ViewHolder{
        public RadioButton radioButton;
        public ImageView userimage;
        public TextView username;
        public TextView userstate;
        Button removeuser;
        LinearLayout linearLayout;
        Context context;
    }
}
