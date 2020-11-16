package com.aof.mcinabox;

import android.app.Application;

import com.aof.mcinabox.helper.FileHelper;
import com.aof.mcinabox.manager.AccountsManager;
import com.aof.mcinabox.manager.SettingsManager;
import com.aof.mcinabox.manager.VersionsManager;
import com.aof.mcinabox.model.Account;
import com.aof.mcinabox.model.Profile;
import com.aof.mcinabox.network.MojangRepository;
import com.aof.mcinabox.network.model.ErrorResponse;

import java.util.UUID;

public class MCinaBox extends Application {
    private static final String TAG = "MCinaBox";

    private boolean initFailed;
    private FileHelper fileHelper;
    private AccountsManager accountsManager;
    private SettingsManager settingsManager;
    private VersionsManager versionsManager;

    @Override
    public void onCreate() {
        super.onCreate();

        initFailed = false;

        // Has to be initialized before the managers!
        fileHelper = new FileHelper(this);

        accountsManager = AccountsManager.fromFile(this);
        settingsManager = SettingsManager.fromFile(this);
        versionsManager = VersionsManager.fromFile(this);

        // Demo code
        versionsManager.addVersion(new Profile("Latest release", "1.16.2"));
        versionsManager.addVersion(new Profile("It works! \uD83D\uDE04", "1.12"));
        accountsManager.addAccount(new Account("Iscle", UUID.randomUUID().toString()));
        accountsManager.addAccount(new Account("longjunyu2"));
        MojangRepository.getInstance().head("Iscle", getFileHelper().getHead("Iscle"), new MojangRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void response) {
            }

            @Override
            public void onError(ErrorResponse response) {
            }
        });
    }

    public boolean isInitFailed() {
        return initFailed;
    }

    public FileHelper getFileHelper() {
        return fileHelper;
    }

    public AccountsManager getAccountsManager() {
        return accountsManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public VersionsManager getVersionsManager() {
        return versionsManager;
    }
}
