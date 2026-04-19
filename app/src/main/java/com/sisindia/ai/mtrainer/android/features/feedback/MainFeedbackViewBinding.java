package com.sisindia.ai.mtrainer.android.features.feedback;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainFeedbackViewBinding {

    @BindingAdapter(value = {"setFeedbackRecyclerAdapter"})
    public static void setMainFeedbackFragmentRecylerAdapter(RecyclerView recyclerView, MainFeedbackFragmentRecylerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @BindingAdapter(value = {"setClientNaRecyclerAdapter"})
    public static void setClientNaRecylerBottomSheetAdapter(RecyclerView recyclerView, ClientNARecylerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @BindingAdapter(value = {"setFeedbackFinalRecyclerAdapter"})
    public static void setFinalRecyclerAdapter(RecyclerView recyclerView, FeedbackFinalRecylerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
