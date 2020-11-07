package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.download.DownloadManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.minecraft.json.VersionManifestJson;
import com.aof.mcinabox.utils.dialog.DialogUtils;

import java.util.ArrayList;

import static com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest.MCINABOX_TEMP;

public class InstallVersionUI extends BaseUI implements RadioGroup.OnCheckedChangeListener {

    public InstallVersionUI(Context context) {
        super(context);
    }

    private LinearLayout layout_installversion;
    private LinearLayout buttonBack;
    private LinearLayout buttonRefresh;
    private TextView textSelectedVersion;
    private LinearLayout buttonDownload;
    private RadioGroup groupVersionType;
    private RadioButton buttonRelease;
    private RadioButton buttonSnapshot;
    private RadioButton buttonOld;
    private ListView listVersionsOnline;
    private SettingJson setting;

    private DownloadManager mDownloadManager;

    private final static String TAG = "InstallVersionUI";

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == buttonBack) {
                OldMainActivity.CURRENT_ACTIVITY.get().backFromHere();
            }
            if (v == buttonRefresh) {
                mDownloadManager.downloadManifestAndUpdateGameListUi(mDownloadManager.new Runable() {
                    @Override
                    public void run() {
                        refreshOnlineVersionList();
                    }
                });

            }
            if (v == buttonDownload) {
                DownloadSelectedVersion();
            }
        }
    };

    @Override
    public void refreshUI() {

    }

    @Override
    public void saveUIConfig() {

    }

    @Override
    public void setUIVisiability(int visiability) {
        layout_installversion.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_installversion.getVisibility();
    }

    /**
     * 【下载从网络版本列表中选择的版本】
     **/
    private int selectedVersionPos = -1;
    private VersionManifestJson.Version[] versionList;

    private void DownloadSelectedVersion() {

        if (versionList == null) {
            DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_please_refresh), mContext.getString(R.string.title_ok), null);
            return;
        }
        if (selectedVersionPos == -1) {
            DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_please_select_version), mContext.getString(R.string.title_ok), null);
            return;
        }
        mDownloadManager.startPresetDownload(DownloadManager.DOWNLOAD_PRESET_VERSION_JSON, listVersionsOnline.getAdapter().getItem(selectedVersionPos).toString());

    }

    /**
     * 【更新网络版本列表】
     **/
    public void refreshOnlineVersionList() {
        //获取实例化后的versionList
        versionList = com.aof.mcinabox.minecraft.JsonUtils.getVersionManifestFromFile(MCINABOX_TEMP + "/version_manifest.json").getVersions();
        String[] nameList;

        ArrayList<VersionManifestJson.Version> version_type_release = new ArrayList<>();
        ArrayList<VersionManifestJson.Version> version_type_snapshot = new ArrayList<>();
        ArrayList<VersionManifestJson.Version> version_type_old = new ArrayList<>();

        for (VersionManifestJson.Version version : versionList) {
            switch (version.getType()) {
                default:
                    break;
                case VersionManifestJson.TYPE_RELEASE:
                    version_type_release.add(version);
                    break;
                case VersionManifestJson.TYPE_SNAPSHOT:
                    version_type_snapshot.add(version);
                    break;
                case VersionManifestJson.TYPE_OLD_BETA:
                case VersionManifestJson.TYPE_OLD_ALPHA:
                    version_type_old.add(version);
                    break;
            }
        }

        switch (groupVersionType.getCheckedRadioButtonId()) {
            default:
                nameList = new String[0];
                break;
            case R.id.radiobutton_type_release:
                nameList = new String[version_type_release.size()];
                for (int i = 0; i < version_type_release.size(); i++) {
                    nameList[i] = version_type_release.get(i).getId();
                }
                break;
            case R.id.radiobutton_type_snapshot:
                nameList = new String[version_type_snapshot.size()];
                for (int i = 0; i < version_type_snapshot.size(); i++) {
                    nameList[i] = version_type_snapshot.get(i).getId();
                }
                break;
            case R.id.radiobutton_type_old:
                nameList = new String[version_type_old.size()];
                for (int i = 0; i < version_type_old.size(); i++) {
                    nameList[i] = version_type_old.get(i).getId();
                }
                break;
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, nameList);
        listVersionsOnline.setAdapter(adapter);
    }

    /**
     * 【当版本类型发生变化时】
     **/
    @Override
    public void onCheckedChanged(RadioGroup radioGroup_version_type, int checkedId) {
        if (versionList != null) {
            refreshOnlineVersionList();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.tips_no_manifest_data), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setting = OldMainActivity.Setting;
        layout_installversion = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_gamelist_install);
        buttonBack = layout_installversion.findViewById(R.id.gamelist_button_backfrom_installnewversion);
        buttonRefresh = layout_installversion.findViewById(R.id.gamelist_button_refresh);
        textSelectedVersion = layout_installversion.findViewById(R.id.gamelist_text_show_selectedversion);
        buttonDownload = layout_installversion.findViewById(R.id.gamelist_button_download);
        groupVersionType = layout_installversion.findViewById(R.id.radiogroup_version_type);
        buttonRelease = layout_installversion.findViewById(R.id.radiobutton_type_release);
        buttonSnapshot = layout_installversion.findViewById(R.id.radiobutton_type_snapshot);
        buttonOld = layout_installversion.findViewById(R.id.radiobutton_type_old);
        listVersionsOnline = layout_installversion.findViewById(R.id.list_minecraft_manifest);
        listVersionsOnline.setOnItemClickListener((adapterView, view, pos, l) -> {
            selectedVersionPos = pos;
            textSelectedVersion.setText(listVersionsOnline.getAdapter().getItem(pos).toString());
        });

        groupVersionType.setOnCheckedChangeListener(this);

        for (View v : new View[]{buttonBack, buttonRefresh, buttonDownload}) {
            v.setOnClickListener(clickListener);
        }
        //初始化下载管理器
        mDownloadManager = new DownloadManager(mContext);
    }
}