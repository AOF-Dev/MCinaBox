package com.aof.mcinabox.gamecontroller.input.screen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;
import com.aof.mcinabox.gamecontroller.input.screen.button.BaseButton;
import com.aof.mcinabox.gamecontroller.input.screen.button.ItemButton;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;

public class ItemBar implements OnscreenInput {

    private final static String TAG = "ItemBar";
    private final static int type = KEYBOARD_BUTTON;
    private Context mContext;
    private Controller mController;
    private LinearLayout itemBar;
    private final boolean moveable = false;
    private int screenWidth;
    private int screenHeight;
    private final ItemButton[] itemButtons = new ItemButton[9];
    private boolean isGrabbed = false;
    private boolean enable;
    private ItembarConfigDialog configDialog;

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        itemBar = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.virtual_itembar, null);
        mController.addContentView(itemBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        itemButtons[0] = itemBar.findViewById(R.id.itembutton_1);
        itemButtons[1] = itemBar.findViewById(R.id.itembutton_2);
        itemButtons[2] = itemBar.findViewById(R.id.itembutton_3);
        itemButtons[3] = itemBar.findViewById(R.id.itembutton_4);
        itemButtons[4] = itemBar.findViewById(R.id.itembutton_5);
        itemButtons[5] = itemBar.findViewById(R.id.itembutton_6);
        itemButtons[6] = itemBar.findViewById(R.id.itembutton_7);
        itemButtons[7] = itemBar.findViewById(R.id.itembutton_8);
        itemButtons[8] = itemBar.findViewById(R.id.itembutton_9);

        //设定监听器
        for (View v : itemButtons) {
            v.setOnTouchListener(this);
        }

        //计算并设定物品栏大小
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int scale = 1;
        while (width / (scale + 1) >= 320 && height / (scale + 1) >= 240) {
            scale++;
        }
        ViewGroup.LayoutParams lp = itemBar.getLayoutParams();
        lp.height = 20 * scale;
        lp.width = 20 * scale * 9;
        itemBar.setLayoutParams(lp);

        //默认不可见
        this.setUiVisibility(View.INVISIBLE);

        //设定配置器
        this.configDialog = new ItembarConfigDialog(mContext, this);

        return true;
    }


    @Override
    public void setUiMoveable(boolean moveable) {
        // to do nothing.
    }

    @Override
    public boolean isEnable() {
        return this.enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
        updateUI();
    }

    @Override
    public void setUiVisibility(int visiablity) {
        switch (visiablity) {
            case View.VISIBLE:
                enable = true;
                if (this.isGrabbed) {
                    itemBar.setVisibility(visiablity);
                }
                break;
            case View.GONE:
            case View.INVISIBLE:
                itemBar.setVisibility(visiablity);
                enable = false;
                break;
            default:
                break;
        }

    }

    @Override
    public float[] getPos() {
        return (new float[]{itemBar.getX(), itemBar.getY()});
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams p = itemBar.getLayoutParams();
        ((ViewGroup.MarginLayoutParams) p).setMargins(left, top, 0, 0);
        itemBar.setLayoutParams(p);
    }

    @Override
    public int[] getSize() {
        return new int[]{itemBar.getLayoutParams().width, itemBar.getLayoutParams().height};
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        if (isGrabbed) {
            if (enable) {
                itemBar.setVisibility(View.VISIBLE);
            }
        } else {
            itemBar.setVisibility(View.INVISIBLE);
        }
        this.isGrabbed = isGrabbed;
    }

    @Override
    public void runConfigure() {
        configDialog.show();
    }

    @Override
    public void saveConfig() {
        configDialog.saveConfigToFile();
    }

    private void updateUI() {
        if (enable) {
            setUiVisibility(View.VISIBLE);
        } else {
            setUiVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof ItemButton) {
            sendKeyEvent(v, event);
        }
        return false;
    }

    @Override
    public boolean unload() {
        itemBar.setVisibility(View.INVISIBLE);
        ViewGroup vg = (ViewGroup) itemBar.getParent();
        vg.removeView(itemBar);
        return true;
    }

    @Override
    public View[] getViews() {
        return new View[]{this.itemBar};
    }

    private void sendKeyEvent(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mController.sendKey(new BaseKeyEvent(TAG, ((BaseButton) v).getButtonName(), true, type, null));
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            mController.sendKey(new BaseKeyEvent(TAG, ((BaseButton) v).getButtonName(), false, type, null));
        }
    }

    public void setAlpha(float a) {
        itemBar.setAlpha(a);
    }

    public void setSize(int width, int height) {
        ViewGroup.LayoutParams p = itemBar.getLayoutParams();
        p.width = width;
        p.height = height;
        //控件重绘
        itemBar.requestLayout();
        itemBar.invalidate();
    }

    public int getUiVisiability() {
        return itemBar.getVisibility();
    }

    private static class ItembarConfigDialog extends Dialog implements View.OnClickListener, Dialog.OnCancelListener, SeekBar.OnSeekBarChangeListener {

        private final static String TAG = "ItembarConfigDialog";
        private final static int DEFAULT_ALPHA_PROGRESS = 60;
        private final static int DEFAULT_SIZE_PROGRESS = 50;
        private final static int DEFAULT_MOVE_DISTANCE = 5;
        private final static int MAX_ALPHA_PROGRESS = 100;
        private final static int MIN_ALPHA_PROGRESS = 0;
        private final static int MAX_SIZE_PROGRESS = 100;
        private final static int MIN_SIZE_PROGRESS = -50;
        private final static int MARK_MOVE_UP = 1;
        private final static int MARK_MOVE_DOWN = 2;
        private final static int MARK_MOVE_LEFT = 3;
        private final static int MARK_MOVE_RIGHT = 4;
        private final static String spFileName = "input_itembar_config";
        private final static int spMode = Context.MODE_PRIVATE;
        private final static String sp_alpha_name = "alpha";
        private final static String sp_size_name = "size";
        private final static String sp_pos_x_name = "pos_x";
        private final static String sp_pos_y_name = "pos_y";
        private final Context mContext;
        private final OnscreenInput mInput;
        private Button buttonOK;
        private Button buttonCancel;
        private Button buttonRestore;
        private Button buttonMoveLeft;
        private Button buttonMoveRight;
        private Button buttonMoveUp;
        private Button buttonMoveDown;
        private SeekBar seekbarAlpha;
        private SeekBar seekbarSize;
        private TextView textAlpha;
        private TextView textSize;
        private int originalAlphaProgress;
        private int originalSizeProgress;
        private int originalMarginLeft;
        private int originalMarginTop;
        private int originalInputWidth;
        private int originalInputHeight;
        private int screenWidth;
        private int screenHeight;


        public ItembarConfigDialog(@NonNull Context context, OnscreenInput input) {
            super(context);
            setContentView(R.layout.dialog_itembar_config);
            mContext = context;
            mInput = input;
            init();
        }

        private void init() {
            this.setCanceledOnTouchOutside(false);
            this.setOnCancelListener(this);

            buttonOK = this.findViewById(R.id.input_itembar_dialog_button_ok);
            buttonCancel = this.findViewById(R.id.input_itembar_dialog_button_cancel);
            buttonRestore = this.findViewById(R.id.input_itembar_dialog_button_restore);
            buttonMoveLeft = this.findViewById(R.id.input_itembar_dialog_button_move_left);
            buttonMoveRight = this.findViewById(R.id.input_itembar_dialog_button_move_right);
            buttonMoveUp = this.findViewById(R.id.input_itembar_dialog_button_move_up);
            buttonMoveDown = this.findViewById(R.id.input_itembar_dialog_button_move_down);
            seekbarAlpha = this.findViewById(R.id.input_itembar_dialog_seekbar_alpha);
            seekbarSize = this.findViewById(R.id.input_itembar_dialog_seekbar_size);
            textAlpha = this.findViewById(R.id.input_itembar_dialog_text_alpha);
            textSize = this.findViewById(R.id.input_itembar_dialog_text_size);

            for (View v : new View[]{buttonOK, buttonCancel, buttonRestore, buttonMoveUp, buttonMoveDown, buttonMoveLeft, buttonMoveRight}) {
                v.setOnClickListener(this);
            }
            for (SeekBar s : new SeekBar[]{seekbarAlpha, seekbarSize}) {
                s.setOnSeekBarChangeListener(this);
            }

            originalInputWidth = mInput.getSize()[0];
            originalInputHeight = mInput.getSize()[1];
            screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;

            //初始化控件属性
            this.seekbarAlpha.setMax(MAX_ALPHA_PROGRESS);
            this.seekbarSize.setMax(MAX_SIZE_PROGRESS);

            loadConfigFromFile();

        }

        @Override
        public void onClick(View v) {

            if (v == buttonCancel) {
                this.cancel();
            }

            if (v == buttonOK) {
                this.dismiss();
            }

            if (v == buttonRestore) {

                DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), mContext.getString(R.string.tips_are_you_sure_to_restore_setting), mContext.getString(R.string.title_ok), mContext.getString(R.string.title_cancel), new DialogSupports() {
                    @Override
                    public void runWhenPositive() {
                        restoreConfig();
                    }
                });

            }

            if (v == buttonMoveUp) {
                moveItembarByButton(MARK_MOVE_UP);
            }
            if (v == buttonMoveDown) {
                moveItembarByButton(MARK_MOVE_DOWN);
            }
            if (v == buttonMoveLeft) {
                moveItembarByButton(MARK_MOVE_LEFT);
            }
            if (v == buttonMoveRight) {
                moveItembarByButton(MARK_MOVE_RIGHT);
            }

        }

        @Override
        public void show() {
            super.show();
            originalAlphaProgress = seekbarAlpha.getProgress();
            originalSizeProgress = seekbarSize.getProgress();
            originalMarginLeft = (int) mInput.getPos()[0];
            originalMarginTop = (int) mInput.getPos()[1];
        }

        @Override
        public void onCancel(DialogInterface dialog) {

            if (dialog == this) {
                seekbarAlpha.setProgress(originalAlphaProgress);
                seekbarSize.setProgress(originalSizeProgress);
                mInput.setMargins(originalMarginLeft, originalMarginTop, 0, 0);
            }

        }

        private void restoreConfig() {
            seekbarAlpha.setProgress(DEFAULT_ALPHA_PROGRESS);
            seekbarSize.setProgress(DEFAULT_SIZE_PROGRESS);

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == seekbarAlpha) {
                int p = progress + MIN_ALPHA_PROGRESS;
                String str = p + "%";
                textAlpha.setText(str);
                //设置透明度
                float alpha = 1 - p * 0.01f;
                ((ItemBar) mInput).setAlpha(alpha);
            }

            if (seekBar == seekbarSize) {
                int p = progress + MIN_SIZE_PROGRESS;
                textSize.setText(String.valueOf(p));
                //设置大小
                int centerX = (int) (mInput.getPos()[0] + mInput.getSize()[0] / 2);
                int centerY = (int) (mInput.getPos()[1] + mInput.getSize()[1] / 2);
                int tmpWidth = (int) ((1 + p * 0.01f) * originalInputWidth);
                int tmpHeight = (int) ((1 + p * 0.01f) * originalInputHeight);
                ((ItemBar) mInput).setSize(tmpWidth, tmpHeight);
                //调整位置
                adjustPos(centerX, centerY);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStop() {
            super.onStop();
            saveConfigToFile();
        }

        private void adjustPos(int originalCenterX, int originalCenterY) {
            int viewWidth = mInput.getSize()[0];
            int viewHeight = mInput.getSize()[1];
            int marginLeft = originalCenterX - viewWidth / 2;
            int margeinTop = originalCenterY - viewHeight / 2;

            //左边界检测
            if (marginLeft < 0) {
                marginLeft = 0;
            }
            //上边界检测
            if (margeinTop < 0) {
                margeinTop = 0;
            }
            //右边界检测
            if (marginLeft + viewWidth > screenWidth) {
                marginLeft = screenWidth - viewWidth;
            }
            //下边界检测
            if (margeinTop + viewHeight > screenHeight) {
                margeinTop = screenHeight - viewHeight;
            }

            mInput.setMargins(marginLeft, margeinTop, 0, 0);
        }

        private void moveItembarByButton(int mark) {
            float posX = mInput.getPos()[0];
            float posY = mInput.getPos()[1];

            int marginLeft = (int) posX;
            int marginTop = (int) posY;

            int viewWidth = mInput.getSize()[0];
            int viewHeight = mInput.getSize()[1];

            //获得移动后的边距
            switch (mark) {
                case MARK_MOVE_UP:
                    marginTop -= DEFAULT_MOVE_DISTANCE;
                    break;
                case MARK_MOVE_DOWN:
                    marginTop += DEFAULT_MOVE_DISTANCE;
                    break;
                case MARK_MOVE_LEFT:
                    marginLeft -= DEFAULT_MOVE_DISTANCE;
                    break;
                case MARK_MOVE_RIGHT:
                    marginLeft += DEFAULT_MOVE_DISTANCE;
                    break;
                default:
                    break;
            }

            //边缘检测
            //上边界
            if (marginTop < 0) {
                marginTop = 0;
            }
            //下边界
            if (marginTop + viewHeight > screenHeight) {
                marginTop = screenHeight - viewHeight;
            }
            //左边界
            if (marginLeft < 0) {
                marginLeft = 0;
            }
            //右边界
            if (marginLeft + viewWidth > screenWidth) {
                marginLeft = screenWidth - viewWidth;
            }

            mInput.setMargins(marginLeft, marginTop, 0, 0);

        }

        private void loadConfigFromFile() {
            SharedPreferences sp = mContext.getSharedPreferences(spFileName, spMode);

            //先设定一个最大值，防止Seebar的监听器无法监听到事件
            seekbarAlpha.setProgress(MAX_ALPHA_PROGRESS);
            seekbarSize.setProgress(MAX_SIZE_PROGRESS);
            //设定存储的数据
            seekbarAlpha.setProgress(sp.getInt(sp_alpha_name, DEFAULT_ALPHA_PROGRESS));
            seekbarSize.setProgress(sp.getInt(sp_size_name, DEFAULT_SIZE_PROGRESS));
            mInput.setMargins(sp.getInt(sp_pos_x_name, 0), sp.getInt(sp_pos_y_name, 0), 0, 0);
        }

        public void saveConfigToFile() {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(spFileName, spMode).edit();
            editor.putInt(sp_alpha_name, seekbarAlpha.getProgress());
            editor.putInt(sp_size_name, seekbarSize.getProgress());
            if (mInput.getUiVisiability() == View.VISIBLE) {
                editor.putInt(sp_pos_x_name, (int) mInput.getPos()[0]);
                editor.putInt(sp_pos_y_name, (int) mInput.getPos()[1]);
            }
            editor.apply();
        }

    }

}

