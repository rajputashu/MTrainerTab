package com.sisindia.ai.mtrainer.android.features.rota;

import androidx.core.view.ViewCompat;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsViewListeners;
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopisAdapter;
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCourseViewListeners;
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCoursesAdapter;

public class DashBoardViewBindings {

    @BindingAdapter(value = {"setDashBoardRotaRecyclerAdapter", "myDashBoardRotaViewListener"})
    public static void bindDashBoardRotaRecyclerAdapter(RecyclerView recyclerView, DashBoardRotaRecyclerAdapter recyclerAdapter, DashBoardRotaViewListeners viewListeners) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(viewListeners);
    }


    @BindingAdapter(value = {"setDashBoardPerformanceViewPager", "dashBoardPerformanceViewListeners"})
    public static void bindDashBoardPerformanceViewPager(ViewPager2 viewPager, DashBoardPerformanceRecyclerAdapter recyclerAdapter, DashBoardRotaViewListeners viewListeners) {
        recyclerAdapter.setListeners(viewListeners);
        viewPager.setAdapter(recyclerAdapter);
    }


    @BindingAdapter(value = {"timelineRecyclerAdapter"})
    public static void setTimelineRecyclerAdapter1 (RecyclerView recyclerView, TimeLineRecyclerAdapter recyclerAdapter){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @BindingAdapter(value = {"setTrainingCoursesRecyclerAdapter", "myTrainingCoursesViewListener"})
    public static void bindTrainingCoursesRecyclerAdapter(RecyclerView recyclerView, TrainingCoursesAdapter recyclerAdapter, TrainingCourseViewListeners viewListeners) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(viewListeners);
    }

    @BindingAdapter(value = {"setTrainingTopicsRecyclerAdapter", "myTrainingTopicsViewListener"})
    public static void bindTrainingTopicsAdapter(RecyclerView recyclerView, TrainingTopisAdapter recyclerAdapter, TrainingTopicsViewListeners viewListeners) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(viewListeners);
    }
}
