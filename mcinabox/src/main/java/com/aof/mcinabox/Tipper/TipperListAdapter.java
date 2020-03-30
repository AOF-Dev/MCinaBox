package com.aof.mcinabox.Tipper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.aof.mcinabox.R;
import java.util.ArrayList;

public class TipperListAdapter extends BaseAdapter {

    private ArrayList<TipperListBean> tipperList;
    private LayoutInflater mLayoutInflater;


    public TipperListAdapter(ArrayList<TipperListBean> list){
        tipperList = list;
    }

    @Override
    public int getCount(){
        return tipperList.size();
    }
    @Override
    public Object getItem(int position){
        return tipperList.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }


    public TipperListAdapter(Context context, ArrayList<TipperListBean> list) {
        tipperList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.listview_tipper, null);
            viewHolder = new ViewHolder();
            viewHolder.tip = convertView.findViewById(R.id.tipper_info);
            viewHolder.help = convertView.findViewById(R.id.tipper_help);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.context = tipperList.get(position).getContext();
        String tips = "";
        switch(tipperList.get(position).getTipper_index()){
            case 1:
                tips = viewHolder.context.getString(R.string.tipper_warn_user_notselected);
                break;
            case 2:
                tips = viewHolder.context.getString(R.string.tipper_warn_version_notselected);
                break;
            case 3:
                tips = viewHolder.context.getString(R.string.tipper_warn_keyboard_notselected);
                break;
            case 4:
                tips = viewHolder.context.getString(R.string.tipper_warn_runtime_notinstall);
                break;
            default:
                break;
        }
        viewHolder.tip.setText(tips);

        viewHolder.help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }});
        return convertView;

    }

    class ViewHolder{
        public TextView tip;
        public ImageButton help;
        Context context;
    }
}
