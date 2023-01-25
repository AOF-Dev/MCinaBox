package com.aof.mcinabox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aof.mcinabox.MCinaBox;
import com.aof.mcinabox.R;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_row, parent, false);
        }

        Account account = getItem(position);
        ImageView head = convertView.findViewById(R.id.head);
        TextView name = convertView.findViewById(R.id.name);
        TextView description = convertView.findViewById(R.id.description);

        head.setImageBitmap(SkinUtils.getUserHead(mCinaBox, account.getName()));
        name.setText(account.getName());
        description.setText(account.getAccountType() == Account.Type.ONLINE ? "Online mode" : "Offline mode");

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
