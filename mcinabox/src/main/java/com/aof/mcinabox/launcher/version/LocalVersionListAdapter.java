package com.aof.mcinabox.launcher.version;

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

import com.aof.mcinabox.R;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalVersionListAdapter extends BaseAdapter {

    private ArrayList<LocalVersionListBean> versionlist;
    private LayoutInflater mLayoutInflater;
    HashMap<String,Boolean> states=new HashMap<String,Boolean>();//用于记录每个RadioButton的状态，并保证只可选一个


    public LocalVersionListAdapter(ArrayList<LocalVersionListBean> list){
        versionlist = list;
    }

    @Override
    public int getCount(){
        return versionlist.size();
    }
    @Override
    public Object getItem(int position){
        return versionlist.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }


    public LocalVersionListAdapter(Context context, ArrayList<LocalVersionListBean> list) {
        versionlist = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LocalVersionListAdapter.ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.listview_version, null);
            viewHolder = new LocalVersionListAdapter.ViewHolder();
            viewHolder.versionimage = convertView.findViewById(R.id.version_image);
            viewHolder.versionId = convertView.findViewById(R.id.versionlist_text_versionId);
            viewHolder.removeversion = convertView.findViewById(R.id.gamelist_button_removeversion);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (LocalVersionListAdapter.ViewHolder)convertView.getTag();
        }
        viewHolder.versionId.setText(versionlist.get(position).getVersion_Id());
        final RadioButton radioButton = convertView.findViewById(R.id.radiobutton_selectedversion);
        viewHolder.radioButton = radioButton;



        //当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中
        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //重置，确保最多只有一项被选中
                for(String key:states.keySet()){
                    states.put(key, false);
                }
                states.put(String.valueOf(position), radioButton.isChecked());
                LocalVersionListAdapter.this.notifyDataSetChanged();
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
        public ImageView versionimage;
        public TextView versionId;
        Button removeversion;
        LinearLayout linearLayout;
    }
}
