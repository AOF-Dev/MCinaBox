package com.aof.mcinabox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.aof.mcinabox.R;
import com.aof.mcinabox.adapter.AccountAdapter;
import com.aof.mcinabox.adapter.VersionAdapter;
import com.aof.mcinabox.manager.AccountsManager;
import com.aof.mcinabox.manager.VersionsManager;
import com.aof.mcinabox.model.Account;
import com.aof.mcinabox.model.Profile;

import java.util.List;

public class HomeFragment extends BaseFragment {

    private VersionAdapter versionAdapter;
    private AccountAdapter accountAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button settingsButton = view.findViewById(R.id.settings_button);
        Button loginButton = view.findViewById(R.id.login_button);
        Spinner versionSpinner = view.findViewById(R.id.version_spinner);
        Spinner accountSpinner = view.findViewById(R.id.account_spinner);

        settingsButton.setOnClickListener(this::handleSettings);
        loginButton.setOnClickListener(this::handleLogin);

        final VersionsManager versionsManager = getMCinaBox().getVersionsManager();
        versionAdapter = new VersionAdapter(getMCinaBox(), versionsManager.getProfiles());
        versionsManager.addOnVersionsChangedListener(versionsChangedListener);

        final AccountsManager accountsManager = getMCinaBox().getAccountsManager();
        accountAdapter = new AccountAdapter(getMCinaBox(), accountsManager.getAccounts());
        accountsManager.addOnAccountsChangedListener(accountsChangedListener);

        versionSpinner.setAdapter(versionAdapter);
        accountSpinner.setAdapter(accountAdapter);
    }

    private void handleSettings(View v) {
        Navigation.findNavController(v)
                .navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment());
    }

    private void handleLogin(View v) {

    }

    @Override
    public void onDestroyView() {
        getMCinaBox().getVersionsManager().removeOnVersionsChangedListener(versionsChangedListener);
        getMCinaBox().getAccountsManager().removeOnAccountsChangedListener(accountsChangedListener);
        super.onDestroyView();
    }

    private final VersionsManager.OnVersionsChangedListener versionsChangedListener = new VersionsManager.OnVersionsChangedListener() {
        @Override
        public void onVersionsChanged(List<Profile> profiles) {
            versionAdapter.notifyDataSetChanged();
        }
    };

    private final AccountsManager.OnAccountsChangedListener accountsChangedListener = new AccountsManager.OnAccountsChangedListener() {
        @Override
        public void onAccountsChanged(List<Account> accounts) {
            accountAdapter.notifyDataSetChanged();
        }
    };
}
