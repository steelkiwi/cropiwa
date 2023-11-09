package com.steelkiwi.cropiwa.sample.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.steelkiwi.cropiwa.sample.R;
import com.steelkiwi.cropiwa.sample.ViewImageActivity;

import java.util.ArrayList;
import java.util.List;

public class CropGalleryAdapter extends RecyclerView.Adapter<CropGalleryAdapter.ViewHolder> {

    private static final int VIEW_TYPE_IMAGE = 0;
    private static final int VIEW_TYPE_BUTTON = 1;

    private List<Uri> imageUris;

    private Listener listener;

    public CropGalleryAdapter() {
        imageUris = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutRes = viewType == VIEW_TYPE_IMAGE ? R.layout.item_image : R.layout.item_new_crop;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(layoutRes, parent, false);
        v.setTag(viewType);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (isImage(position)) {
            holder.item = getItem(position);
            Glide.with(holder.itemView.getContext())
                    .load(holder.item)
                    .into(holder.image);
        }
    }

    private boolean isImage(int position) {
        return position > 0;
    }

    private Uri getItem(int position) {
        return imageUris.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return isImage(position) ? VIEW_TYPE_IMAGE : VIEW_TYPE_BUTTON;
    }

    @Override
    public int getItemCount() {
        return imageUris.size() + 1;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void addImages(List<Uri> images) {
        int positionStart = imageUris.size();
        imageUris.addAll(images);
        notifyItemRangeInserted(positionStart, images.size());
    }

    public void addImage(Uri newImage) {
        imageUris.add(0, newImage);
        notifyItemInserted(1);
    }

    public void removeImage(Uri image) {
        int index = imageUris.indexOf(image);
        if (index != -1) {
            imageUris.remove(index);
            notifyItemRemoved(index + 1);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private ImageView image;
        private Uri item;

        public ViewHolder(View itemView) {
            super(itemView);
            if (isImage()) {
                image = (ImageView) itemView.findViewById(R.id.image);
            }
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (isImage()) {
                Intent viewImageIntent = ViewImageActivity.callingIntent(v.getContext(), item);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        (Activity) v.getContext(), image,
                        image.getTransitionName());
                v.getContext().startActivity(viewImageIntent, options.toBundle());
            } else if (listener != null) {
                listener.onNewCropButtonClicked();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (isImage()) {
                if (listener != null) {
                    listener.onLongPressOnImage(item);
                }
                return true;
            }
            return false;
        }

        private boolean isImage() {
            return ((Integer) itemView.getTag()) == VIEW_TYPE_IMAGE;
        }
    }

    public interface Listener {
        void onNewCropButtonClicked();

        void onLongPressOnImage(Uri image);
    }
}
