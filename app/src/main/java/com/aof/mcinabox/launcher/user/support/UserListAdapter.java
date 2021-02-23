package com.aof.mcinabox.launcher.user.support;

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
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.user.UserManager;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;
import com.aof.mcinabox.utils.dialog.support.TaskDialog;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    private final ArrayList<SettingJson.Account> userlist;
    private Context context;
    private final ArrayList<RadioButton> recorder = new ArrayList<RadioButton>() {
    };
    private final static String TAG = "UserListAdapter";

    public UserListAdapter(ArrayList<SettingJson.Account> list) {
        userlist = list;
    }

    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return userlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public UserListAdapter(Context context, ArrayList<SettingJson.Account> list) {
        this.userlist = list;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_user, null);
            holder = new ViewHolder();
            holder.ivUser = convertView.findViewById(R.id.user_image);
            holder.textUsername = convertView.findViewById(R.id.user_text_username);
            holder.userstate = convertView.findViewById(R.id.user_text_userstate);
            holder.buttonDel = convertView.findViewById(R.id.user_button_removeuser);
            holder.buttonRelogin = convertView.findViewById(R.id.user_button_relogin);
            holder.layout = convertView.findViewById(R.id.small_layout_aboutuser);
            holder.radioSelecter = convertView.findViewById(R.id.radiobutton_selecteduser);
            holder.textUsername.setText(userlist.get(position).getUsername());

            //用户选择切换
            boolean isDif = true;
            for (RadioButton p1 : recorder) {
                if (p1 == holder.radioSelecter) {
                    isDif = false;
                    break;
                }
            }

            if (isDif) {
                recorder.add(holder.radioSelecter);
            }

            if (userlist.get(position).isSelected()) {
                holder.radioSelecter.setChecked(true);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //判断是否启用账户刷新按钮
        if (userlist.get(position).getType().equals(SettingJson.USER_TYPE_OFFLINE)) {
            holder.buttonRelogin.setVisibility(View.GONE);
        } else {
            holder.buttonRelogin.setVisibility(View.VISIBLE);

            holder.buttonRelogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.createBothChoicesDialog(context, context.getString(R.string.title_warn), context.getString(R.string.tips_are_you_sure_to_refresh_online_account), context.getString(R.string.title_ok), context.getString(R.string.title_cancel), new DialogSupports() {
                        @Override
                        public void runWhenPositive() {
                            new LoginServer(userlist.get(position)).setCallback(new LoginServer.Callback() {
                                final TaskDialog mDialog = DialogUtils.createTaskDialog(context, context.getString(R.string.tips_verifying_account), "", false);
                                @Override
                                public void onStart() {
                                    mDialog.show();
                                }

                                @Override
                                public void onFailed(Exception e) {
                                    DialogUtils.createSingleChoiceDialog(context,context.getString(R.string.title_error),String.format(context.getString(R.string.tips_error),e.getMessage()),context.getString(R.string.title_ok),null);
                                }

                                @Override
                                public void onLoginSuccess(SettingJson.Account account, AuthenticateResponse response) {}

                                @Override
                                public void onValidateSuccess(SettingJson.Account account) {
                                    DialogUtils.createSingleChoiceDialog(context,context.getString(R.string.title_note),context.getString(R.string.tips_account_is_valid),context.getString(R.string.title_ok),null);
                                }

                                @Override
                                public void onValidateFailed(final SettingJson.Account account) {
                                    DialogUtils.createBothChoicesDialog(context,context.getString(R.string.title_note),context.getString(R.string.tips_account_is_invalid),context.getString(R.string.title_ok),context.getString(R.string.title_cancel),new DialogSupports(){
                                        @Override
                                        public void runWhenPositive() {
                                            super.runWhenPositive();
                                            new LoginServer(account, context).setCallback(new LoginServer.Callback() {
                                                final TaskDialog mDialog = DialogUtils.createTaskDialog(context, context.getString(R.string.tips_refreshing_account), "", false);
                                                @Override
                                                public void onStart() {
                                                    mDialog.show();
                                                }

                                                @Override
                                                public void onFailed(Exception e) {
                                                    DialogUtils.createSingleChoiceDialog(context,context.getString(R.string.title_error),String.format(context.getString(R.string.tips_error),e.getMessage()),context.getString(R.string.title_ok),null);
                                                }

                                                @Override
                                                public void onLoginSuccess(SettingJson.Account account, AuthenticateResponse response) {
                                                        account.setAccessToken(response.accessToken);
                                                        account.setUuid(response.selectedProfile.id);
                                                        account.setUsername(response.selectedProfile.name);
                                                        account.setSelected(false);
                                                }

                                                @Override
                                                public void onValidateSuccess(SettingJson.Account account) {}

                                                @Override
                                                public void onValidateFailed(SettingJson.Account account) {}

                                                @Override
                                                public void onRefreshSuccess(SettingJson.Account account, AuthenticateResponse response) {}

                                                @Override
                                                public void onFinish() {
                                                    mDialog.dismiss();
                                                }
                                            }).refreshToken();
                                        }
                                    });
                                }

                                @Override
                                public void onRefreshSuccess(SettingJson.Account account, AuthenticateResponse response) {}

                                @Override
                                public void onFinish() {
                                    mDialog.dismiss();
                                }
                            }).verifyToken();
                        }
                    });
                }
            });
        }

        //设置账户模式
        switch (userlist.get(position).getType()) {
            case SettingJson.USER_TYPE_OFFLINE:
                holder.userstate.setText(context.getString(R.string.title_offline));
                break;
            case SettingJson.USER_TYPE_ONLINE:
                holder.userstate.setText(context.getString(R.string.title_online));
                break;
            case SettingJson.USER_TYPE_EXTERNAL:
                holder.userstate.setText(context.getString(R.string.title_server).concat(": ").concat(userlist.get(position).getServerName()));
                break;
            default:
                holder.userstate.setText(context.getString(R.string.title_unknown));
                break;
        }

        //添加删除键监听
        holder.buttonDel.setOnClickListener(v -> DialogUtils.createBothChoicesDialog(context, context.getString(R.string.title_warn), context.getString(R.string.tips_warning_delect_user), context.getString(R.string.title_ok), context.getString(R.string.title_cancel), new DialogSupports() {
            @Override
            public void runWhenPositive() {
                UserManager.removeAccount(OldMainActivity.Setting, userlist.get(position).getUsername());
                //删除后重置用户列表
                OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiUser.reloadListView();
            }
        }));

        //当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中
        holder.radioSelecter.setOnClickListener(v -> {
            for (RadioButton p1 : recorder) {
                p1.setChecked(false);
            }
            holder.radioSelecter.setChecked(true);
            UserManager.setAccountSelected(userlist.get(position).getUsername());
        });

        return convertView;
    }

    class ViewHolder {
        public RadioButton radioSelecter;
        public ImageView ivUser;
        public TextView textUsername;
        public TextView userstate;
        public Button buttonRelogin;
        public Button buttonDel;
        public LinearLayout layout;
    }
}
