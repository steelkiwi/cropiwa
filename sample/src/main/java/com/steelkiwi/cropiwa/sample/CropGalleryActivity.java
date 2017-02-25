package com.steelkiwi.cropiwa.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.steelkiwi.cropiwa.image.CropIwaResultReceiver;
import com.steelkiwi.cropiwa.sample.adapter.CropGalleryAdapter;

public class CropGalleryActivity extends AppCompatActivity implements CropIwaResultReceiver.Listener {

    private CropIwaResultReceiver cropResultReceiver;
    private CropGalleryAdapter cropGalleryAdapter;

    private View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        CropGallery cropGallery = new CropGallery();
        cropGalleryAdapter = new CropGalleryAdapter();
        cropGalleryAdapter.addImages(cropGallery.getCroppedImageUris());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(cropGalleryAdapter);

        cropResultReceiver = new CropIwaResultReceiver();
        cropResultReceiver.setListener(this);
        cropResultReceiver.register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_crop_gallery);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onCropSuccess(Uri croppedUri) {
        cropGalleryAdapter.addImage(croppedUri);
    }

    @Override
    public void onCropFailed(Throwable e) {
        Snackbar.make(container,
                getString(R.string.msg_crop_failed, e.getMessage()),
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cropResultReceiver.unregister(this);
    }
}
