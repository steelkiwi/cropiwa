package com.steelkiwi.cropiwa.sample.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.steelkiwi.cropiwa.sample.CropActivity;
import com.steelkiwi.cropiwa.sample.R;
import com.steelkiwi.cropiwa.sample.util.Permissions;

import java.util.Locale;
import java.util.Random;

/**
 * Created by yarolegovich https://github.com/yarolegovich
 * on 2.03.2017.
 */

public class ChooseImageForCropFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final int REQUEST_CHOOSE_PHOTO = 1101;
    private static final int REQUEST_STORAGE_PERMISSION = 9;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_for_crop, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_random_image).setOnClickListener(this);
        view.findViewById(R.id.btn_from_gallery).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_random_image:
                startCropActivity(getRandomImageUri());
                break;
            case R.id.btn_from_gallery:
                startGalleryApp();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            startCropActivity(data.getData());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGalleryApp();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startGalleryApp() {
        if (Permissions.isGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent = Intent.createChooser(intent, getString(R.string.title_choose_image));
            startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }

    private Uri getRandomImageUri() {
        Random sizeRand = new Random();
        int max = 1400;
        int width = (600 + sizeRand.nextInt(max));
        int height = (600 + sizeRand.nextInt(max));
        width -= (width % 100);
        height -= (height % 100);
        String url = String.format(Locale.US, "http://lorempixel.com/%d/%d/", width, height);
        return Uri.parse(url);
    }

    private void startCropActivity(Uri uri) {
        startActivity(CropActivity.callingIntent(getContext(), uri));
        dismiss();
    }
}
