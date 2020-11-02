package com.aof.mcinabox.activity;

import android.os.Bundle;

import com.aof.mcinabox.BaseActivity;
import com.aof.mcinabox.adapter.AccountAdapter;
import com.aof.mcinabox.adapter.VersionAdapter;
import com.aof.mcinabox.databinding.ActivityMainBinding;
import com.aof.mcinabox.manager.AccountsManager;
import com.aof.mcinabox.manager.VersionsManager;
import com.aof.mcinabox.model.Account;
import com.aof.mcinabox.model.Version;

import java.util.List;

public class MainActivity extends BaseActivity implements VersionsManager.OnVersionsChangedListener, AccountsManager.OnAccountsChangedListener {

    private ActivityMainBinding binding;
    private VersionAdapter versionAdapter;
    private AccountAdapter accountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final VersionsManager versionsManager = getMCinaBox().getVersionsManager();
        versionAdapter = new VersionAdapter(this, versionsManager.getVersions());
        versionsManager.addOnVersionsChangedListener(this);

        final AccountsManager accountsManager = getMCinaBox().getAccountsManager();
        accountAdapter = new AccountAdapter(this, accountsManager.getAccounts());
        accountsManager.addOnAccountsChangedListener(this);

        binding.bottomBar.versionSpinner.setAdapter(versionAdapter);
        binding.bottomBar.accountSpinner.setAdapter(accountAdapter);
    }

    @Override
    protected void onDestroy() {
        getMCinaBox().getVersionsManager().removeOnVersionsChangedListener(this);
        getMCinaBox().getAccountsManager().removeOnAccountsChangedListener(this);
        super.onDestroy();
    }

    @Override
    public void onVersionsChanged(List<Version> versions) {
        versionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAccountsChanged(List<Account> accounts) {
        accountAdapter.notifyDataSetChanged();
    }
}