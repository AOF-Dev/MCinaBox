package com.aof.mcinabox.gamecontroller.ckb.achieve;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.ckb.button.GameButton;
import com.aof.mcinabox.gamecontroller.ckb.support.CkbThemeMarker;
import com.aof.mcinabox.gamecontroller.ckb.support.QwertButton;
import com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent;
import com.aof.mcinabox.utils.ColorUtils;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import java.util.Arrays;

public class GameButtonDialog extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, Dialog.OnCancelListener, View.OnFocusChangeListener, Spinner.OnItemSelectedListener {

    private final Context mContext;
    private final GameButton mGameButton;
    private final CkbManager mManager;

    private EditText editKeyName;

    private Button buttonReduceWidth;
    private EditText editKeyWidth;
    private Button buttonPlusWidth;

    private Button buttonReduceHeight;
    private EditText editKeyHeight;
    private Button buttonPlusHeight;

    private Button buttonReduceLeft;
    private EditText editKeyLeft;
    private Button buttonPlusLeft;

    private Button buttonReduceTop;
    private EditText editKeyTop;
    private Button buttonPlusTop;

    private TextView textMap1;
    private TextView textMap2;
    private TextView textMap3;
    private TextView textMap4;

    private EditText editBackColor;
    private View viewBackColorPreview;

    private EditText editTextColor;
    private View viewTextColorPreview;

    private SeekBar seekbarTextSize;
    private TextView textTextSize;

    private SeekBar seekbarCornerSize;
    private TextView textCornerSize;

    private SeekBar seekbarAlpha;
    private TextView textAlpha;

    private SwitchCompat switchKeep;
    private SwitchCompat switchHide;
    private SwitchCompat switchViewerFollow;

    private RadioButton rbtShowAll;
    private RadioButton rbtShowInGame;
    private RadioButton rbtShowOutGame;

    private Spinner spinnerDesign;

    private Button buttonOK;
    private Button buttonCancel;
    private Button buttonDel;
    private Button buttonCopy;

    public final static int DEFAULT_MOVE_DISTANCE = 1;
    public final static int DEFAULT_MARGIN_DISTANCE = 5;
    private final static String TAG = "GameButtonDialog";


    public GameButtonDialog(@NonNull Context context, GameButton gamebutton, CkbManager manager) {
        super(context);
        setContentView(R.layout.dialog_gamebutton_config);
        this.mContext = context;
        this.mGameButton = gamebutton;
        this.mManager = manager;
        initUI();
    }

    private void initUI() {
        this.setCanceledOnTouchOutside(false);

        //初始化对象
        this.editKeyName = findViewById(R.id.gamebutton_config_dialog_edittext_keyname);

        this.buttonReduceWidth = findViewById(R.id.gamebutton_config_dialog_button_reduce_width);
        this.editKeyWidth = findViewById(R.id.gamebutton_config_dialog_edittext_width);
        this.buttonPlusWidth = findViewById(R.id.gamebutton_config_dialog_button_plus_width);

        this.buttonReduceHeight = findViewById(R.id.gamebutton_config_dialog_button_reduce_height);
        this.editKeyHeight = findViewById(R.id.gamebutton_config_dialog_edittext_height);
        this.buttonPlusHeight = findViewById(R.id.gamebutton_config_dialog_button_plus_height);

        this.buttonReduceLeft = findViewById(R.id.gamebutton_config_dialog_button_reduce_left);
        this.editKeyLeft = findViewById(R.id.gamebutton_config_dialog_edittext_left);
        this.buttonPlusLeft = findViewById(R.id.gamebutton_config_dialog_button_plus_left);

        this.buttonReduceTop = findViewById(R.id.gamebutton_config_dialog_button_reduce_top);
        this.editKeyTop = findViewById(R.id.gamebutton_config_dialog_edittext_top);
        this.buttonPlusTop = findViewById(R.id.gamebutton_config_dialog_button_plus_top);

        this.textMap1 = findViewById(R.id.gamebutton_config_dialog_text_map_1);
        this.textMap2 = findViewById(R.id.gamebutton_config_dialog_text_map_2);
        this.textMap3 = findViewById(R.id.gamebutton_config_dialog_text_map_3);
        this.textMap4 = findViewById(R.id.gamebutton_config_dialog_text_map_4);

        this.editBackColor = findViewById(R.id.gamebutton_config_dialog_edittext_backcolor_hex);
        this.viewBackColorPreview = findViewById(R.id.gamebutton_config_dialog_view_backcolor_preview);

        this.editTextColor = findViewById(R.id.gamebutton_config_dialog_edittext_textcolor_hex);
        this.viewTextColorPreview = findViewById(R.id.gamebutton_config_dialog_view_textcolor_preview);

        this.seekbarAlpha = findViewById(R.id.gamebutton_config_dialog_seekbar_alpha);
        this.textAlpha = findViewById(R.id.gamebutton_config_dialog_text_alpha);

        this.seekbarCornerSize = findViewById(R.id.gamebutton_config_dialog_seekbar_cornersize);
        this.textCornerSize = findViewById(R.id.gamebutton_config_dialog_text_cornersize);

        this.seekbarTextSize = findViewById(R.id.gamebutton_config_dialog_seekbar_textsize);
        this.textTextSize = findViewById(R.id.gamebutton_config_dialog_text_textsize);

        this.switchKeep = findViewById(R.id.gamebutton_config_dialog_switch_keep);
        this.switchHide = findViewById(R.id.gamebutton_config_dialog_switch_hide);
        this.switchViewerFollow = findViewById(R.id.gamebutton_config_dialog_switch_viewerfollow);

        this.rbtShowAll = findViewById(R.id.gamebutton_config_dialog_rb_all);
        this.rbtShowInGame = findViewById(R.id.gamebutton_config_dialog_rb_in_game);
        this.rbtShowOutGame = findViewById(R.id.gamebutton_config_dialog_rb_out_game);

        this.spinnerDesign = findViewById(R.id.gamebutton_config_dialog_spinner_design);

        this.buttonOK = findViewById(R.id.gamebutton_config_dialog_button_ok);
        this.buttonCancel = findViewById(R.id.gamebutton_config_dialog_button_cancel);
        this.buttonDel = findViewById(R.id.gamebutton_config_dialog_button_del);
        this.buttonCopy = findViewById(R.id.gamebutton_config_dialog_button_copy);

        //设定控件属性
        seekbarAlpha.setMax(GameButton.MAX_ALPHA_SIZE_PT);
        seekbarCornerSize.setMax(GameButton.MAX_CORNER_SIZE_PT);
        seekbarTextSize.setMax(GameButton.MAX_TEXT_SIZE_SP);
        spinnerDesign.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, Arrays.asList(CkbThemeMarker.DESIGNS)));


        //设定监听
        for (View v : new View[]{buttonReduceWidth, buttonPlusWidth, buttonReduceHeight, buttonPlusHeight, buttonReduceLeft, buttonPlusLeft,
                buttonReduceTop, buttonPlusTop, textMap1, textMap2, textMap3, textMap4, viewBackColorPreview, viewTextColorPreview, buttonOK, buttonCancel,
                buttonDel, buttonCopy}) {
            v.setOnClickListener(this);
        }
        for (SeekBar s : new SeekBar[]{seekbarTextSize, seekbarCornerSize, seekbarAlpha}) {
            s.setOnSeekBarChangeListener(this);
        }
        for (SwitchCompat s : new SwitchCompat[]{switchHide, switchKeep, switchViewerFollow}) {
            s.setOnCheckedChangeListener(this);
        }
        for (EditText e : new EditText[]{editKeyName, editKeyWidth, editKeyHeight, editKeyLeft, editKeyTop, editBackColor, editTextColor}) {
            e.setOnFocusChangeListener(this);
        }
        for (RadioButton rbt : new RadioButton[]{rbtShowOutGame, rbtShowInGame, rbtShowAll}) {
            rbt.setOnCheckedChangeListener(this);
        }
        this.setOnCancelListener(this);
        this.spinnerDesign.setOnItemSelectedListener(this);

        //从GameButton设定控件状态
        setUIStateFromGameButton();

    }

    private void setUIStateFromGameButton() {
        editKeyName.setText(mGameButton.getKeyName());
        editKeyWidth.setText(String.valueOf(mGameButton.getKeySize()[0]));
        editKeyHeight.setText(String.valueOf(mGameButton.getKeySize()[1]));
        editKeyLeft.setText(String.valueOf(mGameButton.getKeyPos()[0]));
        editKeyTop.setText(String.valueOf(mGameButton.getKeyPos()[1]));
        textMap1.setText(mGameButton.getKeyMaps()[0]);
        textMap2.setText(mGameButton.getKeyMaps()[1]);
        textMap3.setText(mGameButton.getKeyMaps()[2]);
        textMap4.setText(mGameButton.getKeyMaps()[3]);
        editBackColor.setText(mGameButton.getBackColorHex());
        editTextColor.setText(mGameButton.getTextColorHex());
        seekbarAlpha.setProgress(mGameButton.getAlphaSize() - GameButton.MIN_ALPHA_SIZE_PT);
        seekbarTextSize.setProgress(mGameButton.getTextProgress() - GameButton.MIN_TEXT_SIZE_SP);
        seekbarCornerSize.setProgress(mGameButton.getCornerRadius() - GameButton.MIN_CORNER_SIZE_PT);
        switchKeep.setChecked(mGameButton.isKeep());
        switchHide.setChecked(mGameButton.isHide());
        viewBackColorPreview.setBackgroundColor(ColorUtils.hex2Int(mGameButton.getBackColorHex()));
        viewTextColorPreview.setBackgroundColor(ColorUtils.hex2Int(mGameButton.getTextColorHex()));
        switchViewerFollow.setChecked(mGameButton.isViewerFollow());
        switch (mGameButton.getShow()) {
            case GameButton.SHOW_ALL:
                rbtShowAll.setChecked(true);
                break;
            case GameButton.SHOW_IN_GAME:
                rbtShowInGame.setChecked(true);
                break;
            case GameButton.SHOW_OUT_GAME:
                rbtShowOutGame.setChecked(true);
                break;
        }
        spinnerDesign.setSelection(mGameButton.getDesignIndex() - 1);
    }

    @Override
    public void onCancel(DialogInterface dialog) {

        if (dialog == this) {
            if (mGameButton.isFirstAdded()) {
                mGameButton.removeSelfFromParent();
            } else {
                restoreGameButon();
            }
        }

    }

    @Override
    public void show() {
        super.show();
        //在显示的时候记录下GameButton的初始状态
        recordGameButton();
    }

    public void clearEditTextFocus() {
        for (EditText et : new EditText[]{editKeyName, editBackColor, editTextColor, editKeyWidth, editKeyHeight, editKeyLeft, editKeyTop}) {
            et.clearFocus();
        }
    }

    @Override
    public void onClick(View v) {

        clearEditTextFocus();

        if (v == buttonOK) {
            mManager.addGameButton(mGameButton);
            this.dismiss();
        }

        if (v == buttonCancel) {
            this.cancel();
        }

        if (v == buttonDel) {
            DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), mContext.getString(R.string.tips_are_you_sure_to_delete_button), mContext.getString(R.string.title_ok), mContext.getString(R.string.title_cancel), new DialogSupports() {
                @Override
                public void runWhenPositive() {
                    mGameButton.removeSelfFromParent();
                    dismiss();
                }
            });
        }

        if (v == buttonCopy) {
            DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), mContext.getString(R.string.tips_are_you_sure_to_clone_button), mContext.getString(R.string.title_ok), mContext.getString(R.string.title_cancel), new DialogSupports() {
                @Override
                public void runWhenPositive() {
                    buttonOK.performClick();
                    GameButton g = mGameButton.getNewButtonLikeThis().setFirstAdded();
                    new GameButtonDialog(mContext, g, mManager).show();
                    mManager.addGameButton(g);
                }
            });
        }

        if (v == buttonReduceWidth) {
            float wDp = Float.parseFloat(editKeyWidth.getText().toString()) - DEFAULT_MOVE_DISTANCE;
            if (mGameButton.setKeySize(wDp, mGameButton.getKeySize()[1])) {
                editKeyWidth.setText(String.valueOf(wDp));
            }
        }

        if (v == buttonPlusWidth) {
            float wDp = Float.parseFloat(editKeyWidth.getText().toString()) + DEFAULT_MOVE_DISTANCE;
            if (mGameButton.setKeySize(wDp, mGameButton.getKeySize()[1])) {
                editKeyWidth.setText(String.valueOf(wDp));
            }
        }

        if (v == buttonReduceHeight) {
            float hDp = Float.parseFloat(editKeyHeight.getText().toString()) - DEFAULT_MOVE_DISTANCE;
            if (mGameButton.setKeySize(mGameButton.getKeySize()[0], hDp)) {
                editKeyHeight.setText(String.valueOf(hDp));
            }
        }

        if (v == buttonPlusHeight) {
            float hDp = Float.parseFloat(editKeyHeight.getText().toString()) + DEFAULT_MOVE_DISTANCE;
            if (mGameButton.setKeySize(mGameButton.getKeySize()[0], hDp)) {
                editKeyHeight.setText(String.valueOf(hDp));
            }
        }

        if (v == buttonReduceLeft) {
            float lPx = Float.parseFloat(editKeyLeft.getText().toString()) - DEFAULT_MARGIN_DISTANCE;
            float[] result = mGameButton.setKeyPos(lPx, mGameButton.getKeyPos()[1]);
            editKeyLeft.setText(String.valueOf(result[0]));
            editKeyTop.setText(String.valueOf(result[1]));
        }

        if (v == buttonPlusLeft) {
            float lPx = Float.parseFloat(editKeyLeft.getText().toString()) + DEFAULT_MARGIN_DISTANCE;
            float[] result = mGameButton.setKeyPos(lPx, mGameButton.getKeyPos()[1]);
            editKeyLeft.setText(String.valueOf(result[0]));
            editKeyTop.setText(String.valueOf(result[1]));
        }

        if (v == buttonReduceTop) {
            float tPx = Float.parseFloat(editKeyTop.getText().toString()) - DEFAULT_MARGIN_DISTANCE;
            float[] result = mGameButton.setKeyPos(mGameButton.getKeyPos()[0], tPx);
            editKeyLeft.setText(String.valueOf(result[0]));
            editKeyTop.setText(String.valueOf(result[1]));
        }

        if (v == buttonPlusTop) {
            float tPx = Float.parseFloat(editKeyTop.getText().toString()) + DEFAULT_MARGIN_DISTANCE;
            float[] result = mGameButton.setKeyPos(mGameButton.getKeyPos()[0], tPx);
            editKeyLeft.setText(String.valueOf(result[0]));
            editKeyTop.setText(String.valueOf(result[1]));
        }

        if (v == textMap1 || v == textMap2 || v == textMap3 || v == textMap4) {
            int i = 0;
            String n;
            if (v == textMap1) {
                n = textMap1.getText().toString();
            } else if (v == textMap2) {
                i = 1;
                n = textMap2.getText().toString();
            } else if (v == textMap3) {
                i = 2;
                n = textMap3.getText().toString();
            } else {
                i = 3;
                n = textMap4.getText().toString();
            }
            new CkbKeyMapSelecterDialog(mContext, this, i, n).show();
        }

        if (v == viewBackColorPreview) {
            showColorPicker(editBackColor.getText().toString(), editBackColor, v, COLOR_TYPE_BACK);
        }

        if (v == viewTextColorPreview) {
            showColorPicker(editTextColor.getText().toString(), editTextColor, v, COLOR_TYPE_TEXT);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == switchKeep) {
            mGameButton.setKeep(isChecked);
        }

        if (buttonView == switchHide) {
            mGameButton.setHide(isChecked);
        }

        if (buttonView == switchViewerFollow) {
            mGameButton.setViewerFollow(isChecked);
        }

        if (buttonView == rbtShowAll) {
            if (isChecked) {
                mGameButton.setShow(GameButton.SHOW_ALL);
            }
        }

        if (buttonView == rbtShowInGame) {
            if (isChecked) {
                mGameButton.setShow(GameButton.SHOW_IN_GAME);
            }
        }

        if (buttonView == rbtShowOutGame) {
            if (isChecked) {
                mGameButton.setShow(GameButton.SHOW_OUT_GAME);
            }
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (seekBar == this.seekbarAlpha) {
            int a = seekBar.getProgress() + GameButton.MIN_ALPHA_SIZE_PT;
            textAlpha.setText(String.valueOf(a));
            mGameButton.setAlphaSize(a);
        }

        if (seekBar == this.seekbarCornerSize) {
            int a = seekBar.getProgress() + GameButton.MIN_CORNER_SIZE_PT;
            textCornerSize.setText(String.valueOf(a));
            mGameButton.setCornerRadius(a);
        }

        if (seekBar == this.seekbarTextSize) {
            int a = seekBar.getProgress() + GameButton.MIN_TEXT_SIZE_SP;
            textTextSize.setText(String.valueOf(a));
            mGameButton.setTextSize(a);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (v == editKeyName) {
            if (!hasFocus) {
                if (!mGameButton.setKeyName(editKeyName.getText().toString())) {
                    editKeyName.setText(mGameButton.getKeyName());
                }
            }
        }

        if (v == editKeyWidth) {
            if (!hasFocus) {
                if (!editKeyWidth.getText().toString().equals("")) {
                    if (!mGameButton.setKeySize(Float.parseFloat(editKeyWidth.getText().toString()), mGameButton.getKeySize()[1])) {
                        editKeyWidth.setText(String.valueOf(mGameButton.getKeySize()[0]));
                    }
                } else {
                    editKeyWidth.setText(String.valueOf(mGameButton.getKeySize()[0]));
                }
            }
        }

        if (v == editKeyHeight) {
            if (!hasFocus) {
                if (!editKeyHeight.getText().toString().equals("")) {
                    if (!mGameButton.setKeySize(mGameButton.getKeySize()[0], Float.parseFloat(editKeyHeight.getText().toString()))) {
                        editKeyHeight.setText(String.valueOf(mGameButton.getKeySize()[1]));
                    }
                } else {
                    editKeyHeight.setText(String.valueOf(mGameButton.getKeySize()[1]));
                }
            }
        }

        if (v == editKeyLeft) {
            if (!hasFocus) {
                if (!editKeyLeft.getText().toString().equals("")) {
                    mGameButton.setKeyPos(Float.parseFloat(editKeyLeft.getText().toString()), mGameButton.getKeyPos()[1]);
                }
                editKeyLeft.setText(String.valueOf(mGameButton.getKeyPos()[0]));
            }
        }

        if (v == editKeyTop) {
            if (!hasFocus) {
                if (!editKeyTop.getText().toString().equals("")) {
                    mGameButton.setKeyPos(mGameButton.getKeyPos()[0], Float.parseFloat(editKeyTop.getText().toString()));
                }
                editKeyTop.setText(String.valueOf(mGameButton.getKeyPos()[1]));
            }
        }

        if (v == editBackColor) {
            if (!hasFocus) {
                if (!mGameButton.setBackColor(editBackColor.getText().toString())) {
                    editBackColor.setText(mGameButton.getBackColorHex());
                }
            }
        }

        if (v == editTextColor) {
            if (!hasFocus) {
                if (!mGameButton.setTextColor(editTextColor.getText().toString())) {
                    editTextColor.setText(mGameButton.getTextColorHex());
                }
            }
        }

    }

    private String originalKeyName;
    private float[] originalKeySize;
    private float[] originalKeyPos;
    private String[] originalMaps;
    private String originalBackColorHex;
    private String originalTextColorHex;
    private int originalAlpha;
    private int originalTextSize;
    private int originalCornerSize;
    private boolean originalKeep;
    private boolean originalHide;
    private boolean originalViewerFollow;
    private boolean originalAutoHide;
    private int originalDesignIndex;
    private int originalShow;

    private void recordGameButton() {
        this.originalKeyName = mGameButton.getKeyName();
        this.originalKeySize = mGameButton.getKeySize();
        this.originalKeyPos = mGameButton.getKeyPos();
        this.originalMaps = mGameButton.getKeyMaps();
        this.originalBackColorHex = mGameButton.getBackColorHex();
        this.originalTextColorHex = mGameButton.getTextColorHex();
        this.originalAlpha = mGameButton.getAlphaSize();
        this.originalTextSize = mGameButton.getTextProgress();
        this.originalCornerSize = mGameButton.getCornerRadius();
        this.originalKeep = mGameButton.isKeep();
        this.originalHide = mGameButton.isHide();
        this.originalViewerFollow = mGameButton.isViewerFollow();
        this.originalShow = mGameButton.getShow();
        this.originalDesignIndex = mGameButton.getDesignIndex();
    }

    private void restoreGameButon() {
        mGameButton.setKeyName(this.originalKeyName);
        mGameButton.setKeySize(this.originalKeySize[0], this.originalKeySize[1]);
        mGameButton.setKeyPos(this.originalKeyPos[0], this.originalKeyPos[1]);
        mGameButton.setKeyMaps(this.originalMaps);
        mGameButton.setBackColor(this.originalBackColorHex);
        mGameButton.setTextColor(this.originalTextColorHex);
        mGameButton.setAlphaSize(this.originalAlpha);
        mGameButton.setTextSize(this.originalTextSize);
        mGameButton.setCornerRadius(this.originalCornerSize);
        mGameButton.setKeep(this.originalKeep);
        mGameButton.setHide(this.originalHide);
        mGameButton.setShow(this.originalShow);
        mGameButton.setViewerFollow(this.originalViewerFollow);
        mGameButton.setDesignIndex(originalDesignIndex);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spinnerDesign) {
            mGameButton.setDesignIndex(position + 1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setKeyMap(int index, String name, int type) {
        //设置数据
        String[] tmp = mGameButton.getKeyMaps();
        tmp[index] = name;
        mGameButton.setKeyMaps(tmp);

        int[] tmp2 = mGameButton.getKeyTypes();
        tmp2[index] = type;
        mGameButton.setKeyTypes(tmp2);

        //更新UI
        textMap1.setText(tmp[0]);
        textMap2.setText(tmp[1]);
        textMap3.setText(tmp[2]);
        textMap4.setText(tmp[3]);
    }

    private final static int COLOR_TYPE_TEXT = 0;
    private final static int COLOR_TYPE_BACK = 1;

    private void showColorPicker(String originalHex, final EditText et, final View v, final int type) {
        int color = ColorUtils.hex2Int(originalHex);
        if (color == -1) {
            color = Color.BLACK;
        }
        DialogUtils.createColorPickerDialog(mContext, mContext.getString(R.string.title_colorpicker), mContext.getString(R.string.title_ok), mContext.getString(R.string.title_cancel), color, DialogUtils.COLORPICKER_LIGHTNESS_ONLY, new DialogSupports() {
            @Override
            public void runWhenColorSelected(int[] colors) {
                if (colors.length >= 1) {
                    et.setText(ColorUtils.int2Hex(colors[0]));
                    v.setBackgroundColor(colors[0]);
                    switch (type) {
                        case COLOR_TYPE_BACK:
                            mGameButton.setBackColor(ColorUtils.int2Hex(colors[0]));
                            break;
                        case COLOR_TYPE_TEXT:
                            mGameButton.setTextColor(ColorUtils.int2Hex(colors[0]));
                            break;
                    }
                }
            }
        });
    }

    private static class CkbKeyMapSelecterDialog extends Dialog implements View.OnClickListener, Dialog.OnCancelListener {

        private final static String TAG = "CkbKMSDialog";
        private final Context mContext;
        private final GameButtonDialog mDialog;
        private final int index;
        private LinearLayout keyboardLayer;
        private LinearLayout mouseLayer;
        private TextView textKeyName;
        private Button buttonOK;
        private Button buttonCancel;
        private Button buttonClear;
        private String selectedData;
        private int type = KeyEvent.KEYBOARD_BUTTON;

        public CkbKeyMapSelecterDialog(@NonNull Context context, GameButtonDialog dialog, int index, String data) {
            super(context);
            this.mContext = context;
            this.mDialog = dialog;
            this.index = index;
            selectedData = data;
            this.setContentView(R.layout.dialog_keymap_selecter);
            init();
        }

        private void init() {
            this.setOnCancelListener(this);

            keyboardLayer = findViewById(R.id.dialog_keymap_selecter_keyboard_layout);
            mouseLayer = findViewById(R.id.dialog_keymap_selecter_mouse_layout);
            textKeyName = findViewById(R.id.dialog_keymap_selecter_text_keyname);
            buttonOK = findViewById(R.id.dialog_keymap_selecter_button_ok);
            buttonCancel = findViewById(R.id.dialog_keymap_selecter_button_cancel);
            buttonClear = findViewById(R.id.dialog_keymap_selecter_button_clear);

            //设置监听
            //keyboardlayer
            for (int a = 0; a < keyboardLayer.getChildCount(); a++) {
                if (keyboardLayer.getChildAt(a) instanceof LinearLayout) {
                    for (int b = 0; b < ((LinearLayout) keyboardLayer.getChildAt(a)).getChildCount(); b++) {
                        ((LinearLayout) keyboardLayer.getChildAt(a)).getChildAt(b).setOnClickListener(this);
                    }
                }
            }
            //mouselayer
            for (int a = 0; a < mouseLayer.getChildCount(); a++) {
                mouseLayer.getChildAt(a).setOnClickListener(this);
            }

            for (View v : new View[]{buttonOK, buttonCancel, buttonClear}) {
                v.setOnClickListener(this);
            }
            //设置属性
            textKeyName.setText(selectedData);

        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }

        @Override
        public void onClick(View v) {
            if (v instanceof QwertButton) {
                this.selectedData = ((QwertButton) v).getButtonName();
                if (v.getParent() == mouseLayer) {
                    this.type = KeyEvent.MOUSE_BUTTON;
                } else {
                    this.type = KeyEvent.KEYBOARD_BUTTON;
                }
                updateUI();
            }
            if (v == buttonOK) {
                mDialog.setKeyMap(index, selectedData, type);
                dismiss();
            }
            if (v == buttonCancel) {
                cancel();
            }
            if (v == buttonClear) {
                this.selectedData = "";
                updateUI();
            }
        }

        private void updateUI() {
            if (selectedData != null) {
                this.textKeyName.setText(selectedData);
            } else {
                this.textKeyName.setText("");
            }
        }
    }
}

