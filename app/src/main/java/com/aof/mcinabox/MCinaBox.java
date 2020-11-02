package com.aof.mcinabox;

import android.app.Application;

import com.aof.mcinabox.helper.FileHelper;
import com.aof.mcinabox.manager.AccountsManager;
import com.aof.mcinabox.manager.SettingsManager;
import com.aof.mcinabox.manager.VersionsManager;
import com.aof.mcinabox.model.Account;
import com.aof.mcinabox.model.Version;

import java.io.IOError;
import java.io.IOException;

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
        versionsManager.addVersion(new Version("Latest release", "1.16.2"));
        versionsManager.addVersion(new Version("It works! \uD83D\uDE04", "1.12"));
        accountsManager.addAccount(new Account("Iscle", "Online mode"));
        accountsManager.addAccount(new Account("longjunyu2", "Offline mode"));
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
