package com.aof.mcinabox.userUtil;

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

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import java.util.ArrayList;
import java.util.HashMap;

public class UserListAdapter extends BaseAdapter {

    private ArrayList<UserListBean> userlist;
    private LayoutInflater mLayoutInflater;
    HashMap<String,Boolean> states=new HashMap<String,Boolean>();//用于记录每个RadioButton的状态，并保证只可选一个


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
        userlist = list;
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
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.username.setText(userlist.get(position).getUser_name());
        if(userlist.get(position).getUser_model().equals("offline")){
            viewHolder.userstate.setText("离线模式");
        }else if(userlist.get(position).getUser_model().equals("online")){
            viewHolder.userstate.setText("在线模式");
        }else{
            viewHolder.userstate.setText("无法解析");
        }
        viewHolder.context = userlist.get(position).getContext();
        final RadioButton radioButton = convertView.findViewById(R.id.radiobutton_selecteduser);
        viewHolder.radioButton = radioButton;
        viewHolder.removeuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                final AlertDialog dialog ;
                final AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.context);
                //    设置Title的图标
                //builder.setIcon(R.drawable.ic_launcher);
                //    设置Title的内容
                builder.setTitle("警告");
                //    设置Content来显示一个信息
                builder.setMessage("您确定要删除这个用户吗？");
                //    设置一个PositiveButton
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        userlist.remove(position);
                        notifyDataSetChanged();
                    }
                });
                //    设置一个NegativeButton
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
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
                //重置，确保最多只有一项被选中
                for(String key:states.keySet()){
                    states.put(key, false);
                }
                states.put(String.valueOf(position), radioButton.isChecked());
                UserListAdapter.this.notifyDataSetChanged();
                if(viewHolder.radioButton.isChecked()){
                    userlist.get(position).setIsSelected(true);
                }else{
                    userlist.get(position).setIsSelected(false);
                }
            }
        });
        boolean res=false;
        if(states.get(String.valueOf(position)) == null || states.get(String.valueOf(position))== false){
            res=false;
            states.put(String.valueOf(position), false);
        }
        else
            res = true;
        viewHolder.radioButton.setChecked(res);
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
