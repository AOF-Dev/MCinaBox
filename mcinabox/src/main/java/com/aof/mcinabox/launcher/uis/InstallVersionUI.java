package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.download.DownloadManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.minecraft.json.VersionManifestJson;
import java.util.ArrayList;
import static com.aof.mcinabox.definitions.manifest.AppManifest.MCINABOX_TEMP;

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

    @Override
    public void onCreate() {
        super.onCreate();
        setting = MainActivity.Setting;
        layout_installversion = MainActivity.CURRENT_ACTIVITY.findViewById(R.id.layout_gamelist_install);
        buttonBack = layout_installversion.findViewById(R.id.gamelist_button_backfrom_installnewversion);
        buttonRefresh = layout_installversion.findViewById(R.id.gamelist_button_refresh);
        textSelectedVersion = layout_installversion.findViewById(R.id.gamelist_text_show_selectedversion);
        buttonDownload = layout_installversion.findViewById(R.id.gamelist_button_download);
        groupVersionType = layout_installversion.findViewById(R.id.radiogroup_version_type);
        buttonRelease = layout_installversion.findViewById(R.id.radiobutton_type_release);
        buttonSnapshot = layout_installversion.findViewById(R.id.radiobutton_type_snapshot);
        buttonOld = layout_installversion.findViewById(R.id.radiobutton_type_old);
        listVersionsOnline = layout_installversion.findViewById(R.id.list_minecraft_manifest);
        listVersionsOnline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                selectedVersionPos = pos;
                textSelectedVersion.setText(listVersionsOnline.getAdapter().getItem(pos).toString());
            }
        });

        for (View v : new View[]{buttonBack, buttonRefresh, buttonDownload}) {
            v.setOnClickListener(clickListener);
        }
        //初始化下载管理器
        mDownloadManager = new DownloadManager(mContext);
    }

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
        //TODO:修复下载功能

        if (versionList == null) {
            Toast.makeText(mContext, mContext.getString(R.string.tips_online_version_reflash), Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedVersionPos == -1) {
            Toast.makeText(mContext, mContext.getString(R.string.tips_online_version_select), Toast.LENGTH_SHORT).show();
            return;
        }
        mDownloadManager.startPresetDownload(DownloadManager.DOWNLOAD_PRESET_VERSION_JSON ,listVersionsOnline.getAdapter().getItem(selectedVersionPos).toString());

    }

    /**
     * 【更新网络版本列表】
     **/
    public void refreshOnlineVersionList() {
        //获取实例化后的versionList
        versionList = com.aof.mcinabox.minecraft.JsonUtils.getVersionManifestFromFile(MCINABOX_TEMP + "/version_manifest.json").getVersions();
        String[] nameList;

        ArrayList<VersionManifestJson.Version> version_type_release = new ArrayList<VersionManifestJson.Version>() {
        };
        ArrayList<VersionManifestJson.Version> version_type_snapsht = new ArrayList<VersionManifestJson.Version>() {
        };
        ArrayList<VersionManifestJson.Version> version_type_old = new ArrayList<VersionManifestJson.Version>() {
        };

        for (VersionManifestJson.Version version : versionList) {
            switch (version.getType()) {
                default:
                    break;
                case VersionManifestJson.TYPE_RELEASE:
                    version_type_release.add(version);
                    break;
                case VersionManifestJson.TYPE_SNAPSHOT:
                    version_type_snapsht.add(version);
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
                nameList = new String[version_type_snapsht.size()];
                for (int i = 0; i < version_type_snapsht.size(); i++) {
                    nameList[i] = version_type_snapsht.get(i).getId();
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
            Toast.makeText(mContext, mContext.getString(R.string.tips_online_version_nodata), Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == buttonBack) {
                MainActivity.CURRENT_ACTIVITY.backFromHere();
            }
            if (v == buttonRefresh) {
                mDownloadManager.downloadManifestAndUpdateGameListUi(mDownloadManager.new Runable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "开始刷新列表");
                        refreshOnlineVersionList();
                    }
                });

            }
            if (v == buttonDownload) {
                DownloadSelectedVersion();
            }
        }
    };
}