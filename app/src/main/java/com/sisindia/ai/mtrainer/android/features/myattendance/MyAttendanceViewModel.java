package com.sisindia.ai.mtrainer.android.features.myattendance;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

public class MyAttendanceViewModel extends MTrainerViewModel {

    public MyAttendanceRecyclerAdapter recyclerAdapter = new MyAttendanceRecyclerAdapter();

    public MyAttendanceViewListeners viewListeners = new MyAttendanceViewListeners() {

    };


    @Inject
    public MyAttendanceViewModel(@NonNull Application application) {
        super(application);
    }

    public void initViewModel() {
        ArrayList<Object> list = new ArrayList<>();

        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        list.add("ddd");
        list.add("eee");
        list.add("fff");
        recyclerAdapter.clearAndSetItems(list);
    }
}
