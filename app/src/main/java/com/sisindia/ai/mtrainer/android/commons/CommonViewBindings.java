package com.sisindia.ai.mtrainer.android.commons;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.features.pretraining.CoveredTopicsRecyclerAdapter;

public class CommonViewBindings {

    @BindingAdapter(value = {"setTopicsCoveredRecyclerAdapter"})
    public static void setTopicsCoveredRecyclerAdapter(RecyclerView recyclerView, TrainingTopicsRecyclerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }


    @BindingAdapter(value = {"setNavigationImageResource"})
    public static void setImageResourceUsingIntValue(AppCompatImageView imageView, @DrawableRes int resId) {
        if (resId != 0) {
            imageView.setImageResource(resId);
        }
    }
}
