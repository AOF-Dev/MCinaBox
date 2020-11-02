package com.aof.mcinabox.manager;

import android.util.Log;

import com.aof.mcinabox.MCinaBox;
import com.aof.mcinabox.model.Account;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class AccountsManager {
    private static final String TAG = "AccountsManager";

    private static final String ACCOUNTS_FILENAME = "accounts.json";

    private List<Account> accounts;
    private transient List<OnAccountsChangedListener> accountsChangedListeners;

    private AccountsManager() {
        this.accounts = new ArrayList<>();
        this.accountsChangedListeners = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
        onAccountsChanged();
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        onAccountsChanged();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addOnAccountsChangedListener(OnAccountsChangedListener listener) {
        accountsChangedListeners.add(listener);
    }

    public void removeOnAccountsChangedListener(OnAccountsChangedListener listener) {
        accountsChangedListeners.remove(listener);
    }

    private void onAccountsChanged() {
        for (OnAccountsChangedListener listener : accountsChangedListeners) {
            listener.onAccountsChanged(accounts);
        }
    }

    public interface OnAccountsChangedListener {
        void onAccountsChanged(List<Account> accounts);
    }

    private boolean isValid() {
        return true;
    }

    public static AccountsManager fromFile(MCinaBox mCinaBox) {
        final File accountsFile = mCinaBox.getFileHelper().getManager(ACCOUNTS_FILENAME);
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(AccountsManager.class, (JsonDeserializer<AccountsManager>)
                        (json, typeOfT, context) -> new AccountsManager()).create();

        AccountsManager accountsManager = null;
        try (Reader reader = new FileReader(accountsFile)) {
            accountsManager = gson.fromJson(reader, AccountsManager.class);
        } catch (Exception e) {
            Log.d(TAG, "fromFile: Failed to read file!");
        }

        if (accountsManager == null || !accountsManager.isValid()) {
            accountsManager = new AccountsManager();

            try (Writer writer = new FileWriter(accountsFile)) {
                gson.toJson(accountsManager, writer);
            } catch (IOException e) {
                Log.d(TAG, "fromFile: Failed to write file!");
            }
        }

        return accountsManager;
    }
}
