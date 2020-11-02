package com.aof.mcinabox.launcher.tipper.support;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.tipper.TipperManager;

import java.util.List;

public class TipperListAdapter extends BaseAdapter {

    private List<TipperListBean> tipperList;
    private LayoutInflater mLayoutInflater;
    public TipperListAdapter(List<TipperListBean> list){
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


    public TipperListAdapter(Context context, List<TipperListBean> list) {
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
            viewHolder.level = convertView.findViewById(R.id.tipper_level);

            switch (tipperList.get(position).getTipper_level()){
                case TipperManager.TIPPER_LEVEL_NOTE:
                    viewHolder.level.setText("N");
                    viewHolder.level.setTextColor(Color.BLUE);
                    break;
                case TipperManager.TIPPER_LEVEL_WARN:
                    viewHolder.level.setText("W");
                    viewHolder.level.setTextColor(Color.GRAY);
                    break;
                case TipperManager.TIPPER_LEVEL_ERROR:
                    viewHolder.level.setText("E");
                    viewHolder.level.setTextColor(Color.RED);
                    break;
            }

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.context = tipperList.get(position).getContext();
        viewHolder.tip.setText(tipperList.get(position).getTipper_info());
        viewHolder.help.setOnClickListener(v -> {
            if(tipperList.get(position).getTipper_runable() != null){
                tipperList.get(position).getTipper_runable().run();
            }
        });
        return convertView;

    }

    class ViewHolder{
        public TextView tip;
        public TextView level;
        public ImageButton help;
        Context context;
    }
}
