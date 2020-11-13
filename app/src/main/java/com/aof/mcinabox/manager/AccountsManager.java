package com.aof.mcinabox.manager;

import android.util.Log;

import com.aof.mcinabox.MCinaBox;
import com.aof.mcinabox.model.Account;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountsManager {
    private static final String TAG = "AccountsManager";

    private static final String ACCOUNTS_FILENAME = "accounts.json";

    private final transient MCinaBox mCinaBox;
    private final transient File accountsFile;
    private final transient Gson gson;
    private String clientToken;
    private final List<Account> accounts;
    private final transient List<OnAccountsChangedListener> accountsChangedListeners;

    private AccountsManager(MCinaBox mCinaBox, File accountsFile) {
        this.mCinaBox = mCinaBox;
        this.accountsFile = accountsFile;
        this.gson = new Gson();
        this.clientToken = null;
        this.accounts = new ArrayList<>();
        this.accountsChangedListeners = new ArrayList<>();
    }

    public String getClientToken() {
        if (clientToken == null) {
            clientToken = UUID.randomUUID().toString();
            save();
        }
        return clientToken;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        onAccountsChanged();
        save();
    }

    public void removeAccount(Account account) {
        File skinFile = mCinaBox.getFileHelper().getHead(account.getName());
        if (skinFile.exists()) skinFile.delete();
        accounts.remove(account);
        onAccountsChanged();
        save();
    }

    public Account[] getAccounts() {
        return accounts.toArray(new Account[0]);
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

    private void save() {
        try (Writer writer = new FileWriter(accountsFile)) {
            gson.toJson(this, writer);
        } catch (IOException | JsonIOException e) {
            Log.e(TAG, "save: Failed to save!", e);
        }
    }

    private boolean isValid() {
        return true;
    }

    public static AccountsManager fromFile(MCinaBox mCinaBox) {
        final File accountsFile = mCinaBox.getFileHelper().getManager(ACCOUNTS_FILENAME);
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(AccountsManager.class, (JsonDeserializer<AccountsManager>)
                        (json, typeOfT, context) -> new AccountsManager(mCinaBox, accountsFile)).create();

        AccountsManager accountsManager = null;
        try (Reader reader = new FileReader(accountsFile)) {
            accountsManager = gson.fromJson(reader, AccountsManager.class);
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            Log.d(TAG, "fromFile: Failed to read file!");
        }

        if (accountsManager == null || !accountsManager.isValid()) {
            accountsManager = new AccountsManager(mCinaBox, accountsFile);

            try (Writer writer = new FileWriter(accountsFile)) {
                gson.toJson(accountsManager, writer);
            } catch (IOException e) {
                Log.d(TAG, "fromFile: Failed to write file!");
            }
        }

        return accountsManager;
    }

    public interface OnAccountsChangedListener {
        void onAccountsChanged(List<Account> accounts);
    }
}
