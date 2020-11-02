package com.aof.mcinabox.launcher.lang;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.lang.support.LanguageUtils;

import java.util.Arrays;


public class LanguageDialog extends Dialog {

    private ListView listLanguages;
    private Context mContext;
    private LangManager mManager;

    public LanguageDialog(Context context){
        super(context);
        this.mContext = context;
        this.mManager = new LangManager(mContext);
        setContentView(R.layout.dialog_languages);
        initUI();
    }

    private void initUI(){
        listLanguages = findViewById(R.id.dialog_listview_languages);
        listLanguages.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, Arrays.asList(LanguageUtils.LANG_TAGS)));
        listLanguages.setOnItemClickListener((adapterView, view, pos, l) -> {
            ChangeLauncherLanguage(listLanguages.getAdapter().getItem(pos).toString());
            dismiss();
        });
    }

    private void ChangeLauncherLanguage(String language){
        mManager.switchLang(language);
    }
}
