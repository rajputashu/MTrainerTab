package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public
class BranchRequest {

    @SerializedName("TrainerId")
    int trainerId;


    public BranchRequest(int trainerId) {
        this.trainerId = trainerId;
    }

    public BranchRequest() {

    }
}


