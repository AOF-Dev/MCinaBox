package com.aof.mcinabox.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getMCinaBox().isInitFailed()) {
            handleInitFailed();
            return;
        }

        if (!checkPermissions()) {
            return;
        }

        startApp();
    }

    private void startApp() {
        Intent i = new Intent(this, OldMainActivity.class);
        startActivity(i);
        finishAffinity();
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
            return false;
        }
    }

    private void handleInitFailed() {
        Toast.makeText(this, "Oops! Something went wrong while starting the app.", Toast.LENGTH_SHORT).show();
        finishAffinity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length < 2 || grantResults[0] != PERMISSION_GRANTED || grantResults[1] != PERMISSION_GRANTED) {
                handlePermissionsNotGranted();
            } else {
                startApp();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handlePermissionsNotGranted() {
        Toast.makeText(this, "Oops! Permissions are required to run the app.", Toast.LENGTH_SHORT).show();
        finishAffinity();
    }
}