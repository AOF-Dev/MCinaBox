package com.aof.mcinabox.gamecontroller.ckb.achieve;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.FileObserver;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.ckb.button.GameButton;
import com.aof.mcinabox.gamecontroller.ckb.support.CustomizeKeyboardMaker;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CkbManagerDialog extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, Dialog.OnCancelListener {

    private final Context mContext;
    private final CkbManager mManager;
    private RadioButton radioEditable;
    private RadioButton radioGame;
    private TextView textButtonSum;
    private Button buttonAdd;
    private Spinner spinnerSelected;
    private EditText editFileName;
    private Button buttonLoad;
    private Button buttonExport;
    private Button buttonOK;
    private Button buttonDel;
    private Button buttonClear;
    private Button buttonDefault;
    private KeyboardFileListener fileListener;


    private final static String TAG = "CkbConfigDialog";

    public CkbManagerDialog(@NonNull Context context, CkbManager manager) {
        super(context);
        this.setContentView(R.layout.dialog_customize_keyboard_config);
        this.mContext = context;
        this.mManager = manager;
        initUI();
    }

    private void initUI() {

        radioEditable = findViewById(R.id.input_customize_keyboard_dialog_radio_editable);
        radioGame = findViewById(R.id.input_customize_keyboard_dialog_radio_ingame);
        textButtonSum = findViewById(R.id.input_customize_keyboard_dialog_text_button_sum);
        buttonAdd = findViewById(R.id.input_customize_keyboard_dialog_button_add);
        spinnerSelected = findViewById(R.id.input_customize_keyboard_dialog_spinner_select);
        editFileName = findViewById(R.id.input_customize_keyboard_dialog_edit_filename);
        buttonLoad = findViewById(R.id.input_customize_keyboard_dialog_button_load);
        buttonExport = findViewById(R.id.input_customize_keyboard_dialog_button_export);
        buttonOK = findViewById(R.id.input_customize_keyboard_dialog_button_ok);
        buttonDel = findViewById(R.id.input_customize_keyboard_dialog_button_delete);
        buttonClear = findViewById(R.id.input_customize_keyboard_dialog_button_clear);
        buttonDefault = findViewById(R.id.input_customize_keyboard_dialog_button_default);

        //设定监听
        for (View v : new View[]{buttonAdd, buttonLoad, buttonExport, buttonOK, buttonDel, buttonClear, buttonDefault}) {
            v.setOnClickListener(this);
        }
        for (RadioButton r : new RadioButton[]{radioGame, radioEditable}) {
            r.setOnCheckedChangeListener(this);
        }
        this.setOnCancelListener(this);

        //是否显示模式选项
        /*if (mManager.getController() == null) {
            findViewById(R.id.input_customize_keyboard_dialog_layout_mode).setVisibility(View.GONE);
        }
         */

        //当进入游戏的时候自动设定客制化键盘模式为生效，如果是编辑界面，则不自动设置
        if (mManager.getController() != null) {
            radioGame.setChecked(true);
        }else {
            radioGame.setChecked(false);
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        //关闭目录监听
        fileListener.stopWatching();
        //关闭按键数量刷新
        setCountsRefresh(false);
    }

    @Override
    public void show() {
        super.show();
        //启用目录变化监听
        fileListener = new KeyboardFileListener(this);
        fileListener.startWatching();
        updataUI();
        //启用按键数量刷新
        setCountsRefresh(true);
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    private void removeSelectedFile() {
        String filePath = AppManifest.MCINABOX_KEYBOARD + "/" + spinnerSelected.getSelectedItem().toString();
        FileTool.deleteFile(new File(filePath));
    }

    private void loadSelectedFile() {
        String fileName = spinnerSelected.getSelectedItem().toString();
        if (!mManager.loadKeyboard(fileName)) {
            DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_failed_to_import_keyboard_layout), mContext.getString(R.string.title_ok), null);
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.tips_successed_to_import_keyboard_layout), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonOK) {
            this.dismiss();
        }
        if (v == buttonAdd) {
            mManager.addGameButton(null);
        }
        if (v == buttonExport) {
            if (editFileName.getText() == null) {
                Toast.makeText(mContext, mContext.getString(R.string.tips_filename_can_not_be_void), Toast.LENGTH_SHORT).show();
                return;
            }
            if (editFileName.getText().toString().equals(CkbManager.LAST_KEYBOARD_LAYOUT_NAME)) {
                Toast.makeText(mContext, mContext.getString(R.string.tips_please_change_file_name), Toast.LENGTH_SHORT).show();
                return;
            }
            final String fn = editFileName.getText().toString();
            if (fn.equals("")) {
                Toast.makeText(mContext, mContext.getString(R.string.tips_filename_can_not_be_void), Toast.LENGTH_SHORT).show();
                return;
            }

            //检查文件是否存在重复，如果重复，提示是否覆盖。
            boolean run = true;
            for (String str : FileTool.listChildFilesFromTargetDir(AppManifest.MCINABOX_KEYBOARD)) {
                if (str.equals(fn + ".json")) {
                    run = false;
                    DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), mContext.getString(R.string.tips_filename_has_been_used), mContext.getString(R.string.title_over_write), mContext.getString(R.string.title_cancel), new DialogSupports() {
                        @Override
                        public void runWhenPositive() {
                            mManager.exportKeyboard(fn);
                        }
                    });
                }
            }
            if (run) {
                mManager.exportKeyboard(fn);
            }
        }

        if (v == buttonDel) {
            if (spinnerSelected.getSelectedItem() != null) {
                String str = spinnerSelected.getSelectedItem().toString();
                if (!str.equals("")) {
                    DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), String.format(mContext.getString(R.string.tips_are_you_sure_to_delete_file), str), mContext.getString(R.string.title_delete), mContext.getString(R.string.title_cancel), new DialogSupports() {
                        @Override
                        public void runWhenPositive() {
                            removeSelectedFile();
                        }
                    });
                }
            }
        }

        if (v == buttonLoad) {
            if (spinnerSelected.getSelectedItem() != null) {
                String str = spinnerSelected.getSelectedItem().toString();
                if (!str.equals("")) {
                    DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), String.format(mContext.getString(R.string.tips_are_you_sure_to_import_keyboard_layout), str), mContext.getString(R.string.title_import), mContext.getString(R.string.title_cancel), new DialogSupports() {
                        @Override
                        public void runWhenPositive() {
                            loadSelectedFile();
                        }
                    });
                }
            }
        }

        if (v == buttonClear) {
            DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), mContext.getString(R.string.tips_are_you_sure_to_clear_all_buttons), mContext.getString(R.string.title_ok), mContext.getString(R.string.title_cancel), new DialogSupports() {
                @Override
                public void runWhenPositive() {
                    mManager.clearKeyboard();
                }
            });
        }

        if(v == buttonDefault){
            DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn),"您确定要使用默认键盘布局吗？", mContext.getString(R.string.title_ok), mContext.getString(R.string.title_cancel), new DialogSupports(){
                @Override
                public void runWhenPositive() {
                    super.runWhenPositive();
                    mManager.loadKeyboard(new CustomizeKeyboardMaker(mContext).createDefaultKeyboard());
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == radioEditable) {
            if (isChecked) {
                mManager.setButtonsMode(GameButton.MODE_MOVEABLE_EDITABLE);
                buttonAdd.setVisibility(View.VISIBLE);
            }
        }

        if (buttonView == radioGame) {
            if (isChecked) {
                mManager.setButtonsMode(GameButton.MODE_GAME);
                buttonAdd.setVisibility(View.GONE);
            }
        }
    }

    private ArrayList<String> data;

    public void updataUI() {
        if (data == null) {
            data = new ArrayList<>();
            data.addAll(FileTool.listChildFilesFromTargetDir(AppManifest.MCINABOX_KEYBOARD));
            spinnerSelected.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, data));
        } else {
            data.clear();
            data.addAll(FileTool.listChildFilesFromTargetDir(AppManifest.MCINABOX_KEYBOARD));
            ((BaseAdapter) spinnerSelected.getAdapter()).notifyDataSetChanged();
        }
    }

    public void setButtonCounts(final int counts) {
        this.textButtonSum.post(() -> textButtonSum.setText(String.valueOf(counts)));
    }

    private Timer mTimer;

    public void setCountsRefresh(boolean able) {
        if (able) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setButtonCounts(mManager.getButtonCounts());
                }
            }, 500, 500);
        } else {
            if (mTimer != null) {
                mTimer.cancel();
            }
        }
    }

    private static class KeyboardFileListener extends FileObserver {

        private final CkbManagerDialog mDialog;

        public KeyboardFileListener(CkbManagerDialog dialog) {
            super(AppManifest.MCINABOX_KEYBOARD);
            this.mDialog = dialog;
        }

        @Override
        public void onEvent(int event, @Nullable String path) {
            switch (event) {
                case FileObserver.CREATE:
                case FileObserver.DELETE:
                    mDialog.updataUI();
                    break;
                default:
                    break;
            }
        }
    }
}
