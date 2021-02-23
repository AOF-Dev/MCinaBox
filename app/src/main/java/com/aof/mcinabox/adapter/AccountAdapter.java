package com.aof.mcinabox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aof.mcinabox.MCinaBox;
import com.aof.mcinabox.databinding.AccountRowBinding;
import com.aof.mcinabox.model.Account;
import com.aof.mcinabox.utils.SkinUtils;

public class AccountAdapter extends ArrayAdapter<Account> {

    private final MCinaBox mCinaBox;

    public AccountAdapter(@NonNull MCinaBox mCinaBox, @NonNull Account[] accounts) {
        super(mCinaBox, 0, accounts);
        this.mCinaBox = mCinaBox;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final AccountRowBinding binding;

        if (convertView == null) {
            binding = AccountRowBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        } else {
            binding = AccountRowBinding.bind(convertView);
        }

        final Account account = getItem(position);
        binding.head.setImageBitmap(SkinUtils.getUserHead(mCinaBox, account.getName()));
        binding.name.setText(account.getName());
        binding.description.setText(account.getAccountType() == Account.Type.ONLINE ? "Online mode" : "Offline mode");

        return binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
