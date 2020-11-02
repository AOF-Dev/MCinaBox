package com.aof.mcinabox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aof.mcinabox.databinding.AccountRowBinding;
import com.aof.mcinabox.model.Account;

import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account> {

    public AccountAdapter(@NonNull Context context, @NonNull List<Account> accounts) {
        super(context, 0, accounts);
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
        binding.name.setText(account.getName());
        binding.description.setText(account.getDescription());

        return binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
