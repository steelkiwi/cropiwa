package com.steelkiwi.cropiwa.sample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.sample.config.CropViewConfigurator;
import com.yarolegovich.mp.MaterialPreferenceScreen;

public class CropActivity extends AppCompatActivity {

    private static final String EXTRA_URI = "https://pp.vk.me/c637119/v637119751/248d1/6dd4IPXWwzI.jpg";

    public static Intent callingIntent(Context context, Uri imageUri) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_URI, imageUri);
        return intent;
    }

    private CropIwaView cropView;
    private CropViewConfigurator configurator;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_crop);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uri imageUri = getIntent().getParcelableExtra(EXTRA_URI);
        cropView = (CropIwaView) findViewById(R.id.crop_view);
        cropView.setImageUri(imageUri);

        MaterialPreferenceScreen cropPrefScreen = (MaterialPreferenceScreen) findViewById(R.id.crop_preference_screen);
        configurator = new CropViewConfigurator(cropView, cropPrefScreen);
        cropPrefScreen.setStorageModule(configurator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            cropView.crop(configurator.getSelectedSaveConfig());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
