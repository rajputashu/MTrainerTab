package com.sisindia.ai.mtrainer.android.features.pretraining;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class PreTrainingViewBindings {

    @BindingAdapter(value = {"setPreviousTrainingViewPager", "previousTrainingViewListeners"})
    public static void bindDashBoardPerformanceViewPager(ViewPager2 viewPager, PreviousTrainingViewPagerAdapter recyclerAdapter, PreTrainingViewListeners viewListeners) {
//        CompositePageTransformer pageTransformer = new CompositePageTransformer();
//        pageTransformer.addTransformer(new MarginPageTransformer(DensityUtils.dp2px(viewPager.getContext(), 20)));
//        pageTransformer.addTransformer(new ScalePageTransformer());
//        viewPager.setPageTransformer(pageTransformer);

        recyclerAdapter.setListeners(viewListeners);
        viewPager.setAdapter(recyclerAdapter);
    }

    @BindingAdapter(value = {"setTopicItemViewPagerAdapter", "setTopicItemViewListeners"})
    public static void setTopicItemViewPagerAdapter(ViewPager2 viewPager, TrainingTopicItemViewPagerAdapter recyclerAdapter, PreTrainingViewListeners viewListeners) {
//        CompositePageTransformer pageTransformer = new CompositePageTransformer();
//        viewPager.setPageTransformer(new MarginPageTransformer(DensityUtils.dp2px(viewPager.getContext(), 2)));
//        viewPager.setPageTransformer(new ScalePageTransformer());
//        viewPager.setPageTransformer(new DepthPageTransformer());


        recyclerAdapter.setListeners(viewListeners);
        viewPager.setAdapter(recyclerAdapter);
    }


    @BindingAdapter(value = {"setTrainingDetailImageViewPager"})
    public static void setTrainingDetailImageViewPager(ViewPager2 viewPager, TrainingDetailImageViewPagerAdapter recyclerAdapter) {
//        CompositePageTransformer pageTransformer = new CompositePageTransformer();
//        viewPager.setPageTransformer(new MarginPageTransformer(DensityUtils.dp2px(viewPager.getContext(), 2)));
//        viewPager.setPageTransformer(new ScalePageTransformer());
//        viewPager.setPageTransformer(new DepthPageTransformer());
        viewPager.setAdapter(recyclerAdapter);
    }


    @BindingAdapter(value = {"setTopicsItemRecyclerAdapter", "setViewListeners"})
    public static void setTopicsItemRecyclerAdapter(RecyclerView recyclerView, TopicItemRecyclerAdapter recyclerAdapter, PreTrainingViewListeners viewListeners) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter.setViewListeners(viewListeners);
        recyclerView.setAdapter(recyclerAdapter);
    }


    @BindingAdapter(value = {"setTrainingTopicListRecyclerAdapter"})
    public static void setTrainingTopicListRecyclerAdapter(RecyclerView recyclerView, TrainingTopicListRecyclerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    //app:setTextListRecyclerAdapter="@{viewModel.attendeesAdapter}"
    @BindingAdapter(value = {"setTextListRecyclerAdapter"})
    public static void setTextListRecyclerAdapter(RecyclerView recyclerView, TextListRecyclerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }


//this topics covered
    @BindingAdapter(value = {"setTopicsCoveredRecyclerAdapter1"})
    public static void setTopicsCoveredRecyclerAdapter1(RecyclerView recyclerView, CoveredTopicsRecyclerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }



}

