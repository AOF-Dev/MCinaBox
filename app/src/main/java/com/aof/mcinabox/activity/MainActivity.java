package com.aof.mcinabox.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aof.mcinabox.adapter.AccountAdapter;
import com.aof.mcinabox.adapter.VersionAdapter;
import com.aof.mcinabox.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomBar.versionSpinner.setAdapter(new VersionAdapter(this, null));
        binding.bottomBar.accountSpinner.setAdapter(new AccountAdapter(this, null));
    }
}