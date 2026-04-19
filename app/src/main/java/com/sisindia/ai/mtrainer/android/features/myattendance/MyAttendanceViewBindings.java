package com.sisindia.ai.mtrainer.android.features.myattendance;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyAttendanceViewBindings {

    @BindingAdapter(value = {"setMyAttendanceRecyclerAdapter", "myAttendanceViewListener"})
    public static void bindMyAttendanceRecycler(RecyclerView recyclerView, MyAttendanceRecyclerAdapter recyclerAdapter, MyAttendanceViewListeners viewListeners) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(viewListeners);
    }
}
