package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.commons.TrainingTopicsRecyclerAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

public class TrainingItemDetailViewModel extends MTrainerViewModel {

    public CoveredTopicsRecyclerAdapter topicsRecyclerAdapter = new CoveredTopicsRecyclerAdapter();
    public TrainingDetailImageViewPagerAdapter imageViewPagerAdapter = new TrainingDetailImageViewPagerAdapter();
    public TextListRecyclerAdapter attendeesAdapter = new TextListRecyclerAdapter();

    @Inject
    public TrainingItemDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void initViewModel() {

        ArrayList<String> strings = new ArrayList<>();
        strings.add("Maintenance of fire safety equipments");
        strings.add("Fire evacuation in high rise");
        topicsRecyclerAdapter.clearAndSetItems(strings);


        ArrayList<Object> images = new ArrayList<>();
        images.add("Maintenance of fire safety equipments");
        images.add("Fire evacuation in high rise");
        images.add("Fire evacuation in high rise");
        images.add("Fire evacuation in high rise");
        images.add("Fire evacuation in high rise");
        images.add("Fire evacuation in high rise");
        images.add("Fire evacuation in high rise");
        images.add("Fire evacuation in high rise");
        imageViewPagerAdapter.clearAndSetItems(images);

        ArrayList<String> attendance = new ArrayList<>();
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");
        attendance.add("adfads");

        attendeesAdapter.clearAndSetItems(attendance);

    }
}
