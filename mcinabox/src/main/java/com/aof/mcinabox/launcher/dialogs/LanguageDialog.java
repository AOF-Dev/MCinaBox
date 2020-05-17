package com.aof.mcinabox.launcher.dialogs;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.utils.LanguageUtils;


public class LanguageDialog extends BaseDialog {

    public LanguageDialog(MainActivity context, int layoutID){
        super(context,layoutID);
    }

    ListView listLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initUI();
    }

    private void initUI(){
        listLanguages = findViewById(R.id.dialog_listview_languages);
        listLanguages.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ChangeLauncherLanguage(listLanguages.getAdapter().getItem(pos).toString());
                dismiss();
            }
        });
    }

    private void ChangeLauncherLanguage(String language){
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = LanguageUtils.getLocaleFromConfig(language);
        resources.updateConfiguration(config, dm);
        ((MainActivity)mContext).restartLauncher();
    }
}
