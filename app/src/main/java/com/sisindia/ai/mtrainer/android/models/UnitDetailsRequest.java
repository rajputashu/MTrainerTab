package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class UnitDetailsRequest {

    @SerializedName("TrainerId")
    int trainerId;

    @SerializedName("BranchId")
    int branchId;

    public UnitDetailsRequest(int trainerId, int branchId) {
        this.trainerId = trainerId;
        this.branchId = branchId;
    }

    public UnitDetailsRequest() {

    }
}


