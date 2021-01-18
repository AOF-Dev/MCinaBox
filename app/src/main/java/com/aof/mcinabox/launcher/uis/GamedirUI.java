package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.gamedir.GamedirManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import java.io.File;

public class GamedirUI extends BaseUI {

    public GamedirUI(Context context) {
        super(context);
    }

    private LinearLayout layout_gamedir;
    private LinearLayout layoutPrivate;
    private LinearLayout layoutPublic;
    private EditText editGamedir;
    private Button buttonSave;
    private Animation showAnim;
    private SettingJson setting;

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == buttonSave) {
                String t = editGamedir.getText().toString();
                //过滤掉最后一个反斜杠
                if (t.charAt(t.length() - 1) == '/') {
                    StringBuilder tmp = new StringBuilder();
                    for (int a = 0; a < t.length() - 1; a++) {
                        tmp.append(t.charAt(a));
                    }
                    t = tmp.toString();
                    editGamedir.setText(t);
                }

                final File dir = new File(t);
                if (dir.exists() && !dir.isDirectory()) {
                    DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_target_dir_is_file), mContext.getString(R.string.title_ok), null);
                } else if (!dir.exists()) {
                    DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), mContext.getString(R.string.tips_target_dir_is_not_exist), mContext.getString(R.string.title_ok), mContext.getString(R.string.title_cancel), new DialogSupports() {
                        @Override
                        public void runWhenPositive() {
                            if (!GamedirManager.setGamedir(mContext, OldMainActivity.Setting, dir.getAbsolutePath())) {
                                DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_failed_to_revise_game_dir), mContext.getString(R.string.title_ok), null);
                            } else {
                                DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_note), mContext.getString(R.string.tips_successed_to_revise_game_dir), mContext.getString(R.string.title_ok), null);
                            }
                        }
                    });
                } else {
                    if (!GamedirManager.setGamedir(mContext, OldMainActivity.Setting, dir.getAbsolutePath())) {
                        DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_failed_to_revise_game_dir), mContext.getString(R.string.title_ok), null);
                    } else {
                        DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_note), mContext.getString(R.string.tips_successed_to_revise_game_dir), mContext.getString(R.string.title_ok), null);
                    }
                }
            }

            if (v == layoutPublic) {
                editGamedir.setText(GamedirManager.PUBLIC_GAMEDIR);
            }

            if (v == layoutPrivate) {
                editGamedir.setText(GamedirManager.PRIVATE_GAMEDIR);
            }
        }
    };

    private void init() {
        editGamedir.setText(GamedirManager.getGamedir(OldMainActivity.Setting));
    }

    @Override
    public void refreshUI() {

    }

    @Override
    public void saveUIConfig() {

    }

    @Override
    public void setUIVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            layout_gamedir.startAnimation(showAnim);
        }
        layout_gamedir.setVisibility(visibility);
    }

    @Override
    public int getUIVisibility() {
        return layout_gamedir.getVisibility();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setting = OldMainActivity.Setting;
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_gamedir = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_gamedir);
        layoutPrivate = layout_gamedir.findViewById(R.id.gamedir_select_private);
        layoutPublic = layout_gamedir.findViewById(R.id.gamedir_select_public);
        buttonSave = layout_gamedir.findViewById(R.id.gamedir_button_save);
        editGamedir = layout_gamedir.findViewById(R.id.gamedir_edit_gamedir);

        for (View v : new View[]{buttonSave, layoutPublic, layoutPrivate}) {
            v.setOnClickListener(clickListener);
        }

        init();

    }
}
