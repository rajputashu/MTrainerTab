package com.sisindia.ai.mtrainer.android.features.umbrellareport;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class UmbrellaViewBinding {

    @BindingAdapter(value = {"umbrellaAdapter", "umbrellaItemListener", "imageHandler"})
    public static void setUmbrellaAdapter(RecyclerView recyclerView, UmbrellaReportAdapter recyclerAdapter, ImageCaptureListener listener, Picasso picasso) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerAdapter.setListener(listener);
        recyclerAdapter.setPicasso(picasso);
        recyclerView.setAdapter(recyclerAdapter);
    }
}