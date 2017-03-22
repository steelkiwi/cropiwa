package com.steelkiwi.cropiwa.sample;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.sample.config.CropViewConfigurator;
import com.yarolegovich.mp.MaterialPreferenceScreen;

public class CropActivity extends AppCompatActivity {

    private static final String EXTRA_URI = "https://pp.vk.me/c637119/v637119751/248d1/6dd4IPXWwzI.jpg";

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

        cropView = (CropIwaView) findViewById(R.id.crop_view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cropView.setImageUri(Uri.parse(EXTRA_URI));
            }
        }, 4000);


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
