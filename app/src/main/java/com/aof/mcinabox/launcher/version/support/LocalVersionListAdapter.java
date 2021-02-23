package com.aof.mcinabox.launcher.version.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.version.VersionManager;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import java.util.ArrayList;

public class LocalVersionListAdapter extends BaseAdapter {

    private ArrayList<LocalVersionListBean> versionlist;
    private Context mContext;

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
        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LocalVersionListAdapter.ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_version, null);
            viewHolder = new LocalVersionListAdapter.ViewHolder();
            viewHolder.versionimage = convertView.findViewById(R.id.version_image);
            viewHolder.versionId = convertView.findViewById(R.id.versionlist_text_versionId);
            viewHolder.removeversion = convertView.findViewById(R.id.gamelist_button_removeversion);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (LocalVersionListAdapter.ViewHolder)convertView.getTag();
        }
        viewHolder.versionId.setText(versionlist.get(position).getVersion_Id());
        viewHolder.removeversion.setOnClickListener(v -> DialogUtils.createBothChoicesDialog(mContext,mContext.getString(R.string.title_warn),String.format(mContext.getString(R.string.tips_are_you_sure_to_delete_version),versionlist.get(position).getVersion_Id()),mContext.getString(R.string.title_ok),mContext.getString(R.string.title_cancel),new DialogSupports(){
            @Override
            public void runWhenPositive(){
                VersionManager.removeVersion(versionlist.get(position).getVersion_Id(),VersionManager.REMOVE_VERSION_ONLY);
            }
        }));

        return convertView;
    }

    class ViewHolder{
        public ImageView versionimage;
        public TextView versionId;
        Button removeversion;
        LinearLayout linearLayout;
    }
}
