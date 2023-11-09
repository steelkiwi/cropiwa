package com.steelkiwi.cropiwa.sample.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.steelkiwi.cropiwa.sample.R;

/**
 * Created by yarolegovich https://github.com/yarolegovich
 * on 22.03.2017.
 */
public class ConfirmDeletePhotoFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private Uri image;
    private Listener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_delete, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_delete).setOnClickListener(this);
    }

    public void setListener(Listener listener, Uri image) {
        this.listener = listener;
        this.image = image;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_delete) {
            if (listener != null) {
                listener.onDeleteConfirmed(image);
            }
            dismiss();
        }
    }

    public interface Listener {
        void onDeleteConfirmed(Uri image);
    }
}
