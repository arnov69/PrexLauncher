package com.prexlauncher.core;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.prexlauncher.InfoCenter;
import com.prexlauncher.R;

public class MissingStorageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_test_no_sdcard);
        ((TextView) findViewById(R.id.warning_text)).setText(InfoCenter.replaceName(this, R.string.storage_required));
    }
}