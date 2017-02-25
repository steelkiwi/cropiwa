package com.steelkiwi.cropiwa.sample;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.steelkiwi.cropiwa.CropIwaView;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "https://pp.vk.me/c637119/v637119751/248d1/6dd4IPXWwzI.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CropIwaView cropIwaView = (CropIwaView) findViewById(R.id.crop);
        cropIwaView.setImageUri(Uri.parse(URL));
    }
}
