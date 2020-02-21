package com.aof.mcinabox.userUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
        ViewHolder viewHolder;
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
        final RadioButton radioButton = convertView.findViewById(R.id.radiobutton_selecteduser);
        viewHolder.radioButton = radioButton;



        //当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中
        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //重置，确保最多只有一项被选中
                for(String key:states.keySet()){
                    states.put(key, false);
                }
                states.put(String.valueOf(position), radioButton.isChecked());
                UserListAdapter.this.notifyDataSetChanged();
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
    }
}
