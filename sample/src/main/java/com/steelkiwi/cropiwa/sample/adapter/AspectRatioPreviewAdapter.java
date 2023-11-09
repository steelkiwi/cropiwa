package com.steelkiwi.cropiwa.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.steelkiwi.cropiwa.AspectRatio;
import com.steelkiwi.cropiwa.sample.R;
import com.steelkiwi.cropiwa.sample.view.AspectRatioPreviewView;

import java.util.Arrays;
import java.util.List;

public class AspectRatioPreviewAdapter extends RecyclerView.Adapter<AspectRatioPreviewAdapter.ViewHolder> {

    private AspectRatioPreviewView lastSelectedView;
    private AspectRatio selectedRatio;

    private List<AspectRatio> ratios;

    private OnNewSelectedListener listener;

    public AspectRatioPreviewAdapter() {
        ratios = Arrays.asList(
                new AspectRatio(3, 2),
                new AspectRatio(4, 3),
                new AspectRatio(5, 4),
                new AspectRatio(1, 1),
                new AspectRatio(4, 5),
                new AspectRatio(3, 4),
                new AspectRatio(2, 3));
        selectedRatio = ratios.get(3);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_aspect_ratio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AspectRatio ratio = ratios.get(position);
        holder.ratioView.setAspectRatio(ratio);
        if (ratio.equals(selectedRatio)) {
            lastSelectedView = holder.ratioView;
            holder.ratioView.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return ratios.size();
    }

    public void setListener(OnNewSelectedListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AspectRatioPreviewView ratioView;

        public ViewHolder(View itemView) {
            super(itemView);
            ratioView = (AspectRatioPreviewView) itemView.findViewById(R.id.aspect_ratio_preview);
            ratioView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (lastSelectedView == ratioView) {
                selectedRatio = ratioView.getRatio();
                return;
            }
            if (ratioView.getRatio().equals(selectedRatio)) {
                return;
            }
            if (lastSelectedView != null) {
                lastSelectedView.setSelected(false);
            }
            ratioView.setSelected(true);

            selectedRatio = ratioView.getRatio();
            lastSelectedView = ratioView;

            if (listener != null) {
                listener.onNewAspectRatioSelected(selectedRatio);
            }
        }
    }

    public interface OnNewSelectedListener {
        void onNewAspectRatioSelected(AspectRatio ratio);
    }
}

