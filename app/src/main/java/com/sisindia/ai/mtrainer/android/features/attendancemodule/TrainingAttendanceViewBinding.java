package com.sisindia.ai.mtrainer.android.features.attendancemodule;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.views.ink.InkView;

public class TrainingAttendanceViewBinding  {

    @BindingAdapter(value = {"setTrainingAttendanceRecyclerAdapter"/*,"trainingattendanceViewListener"*/})
    public static void setTakeAssessmentReclerAdpter(RecyclerView recyclerView, TrainingAttendanceRecyclerAdapter recyclerAdapter/*TrainingAttendanceViewListeners viewListeners*/) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        //recyclerAdapter.setListener(viewListeners);

    }

/*
    @BindingAdapter(value = {"setTrainingAdhocEmployeeRecyclerAdapter"})
    public static void setAdhocEmployeeReclerAdpter(RecyclerView recyclerView, TrainingAdhocEmployeeRecyclerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

    }*/


    @BindingAdapter(value = {"addInkListeners"})
    public static void addInkViewListener(InkView inkView, InkView.InkListener inkListener) {
        inkView.addListener(inkListener);
    }

    @BindingAdapter(value = {"queryListener"})
    public static void setQueryListener(SearchView searchView, OnQuery queryListener) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryListener.query(newText);
                return false;
            }
        });
    }
}