package com.aof.mcinabox.launcher.tipper;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.tipper.support.TipperListAdapter;
import com.aof.mcinabox.launcher.tipper.support.TipperListBean;
import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;

import java.util.List;

public class Tipper {

    private Context mContext;
    private ListView listTipper;
    private BubbleLayout bubbleTipper;
    private PopupWindow popupWindow;

    public Tipper(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        bubbleTipper = (BubbleLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_popup_tipper, null);
        popupWindow = BubblePopupHelper.create(mContext, bubbleTipper);
        listTipper = bubbleTipper.findViewById(R.id.tipper_list);

    }

    /**
     * 【在View下方显示Tipper】
     **/
    public void showTipper(View v, List<TipperListBean> list) {
        if(list == null || list.size() == 0){
            return;
        }
        TipperListAdapter tipperListAdapter = new TipperListAdapter(mContext, list);
        listTipper.setAdapter(tipperListAdapter);
        int[] location = new int[2];
        v.getLocationInWindow(location);
        bubbleTipper.setArrowDirection(ArrowDirection.TOP);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], v.getHeight() + location[1]);
    }

}
