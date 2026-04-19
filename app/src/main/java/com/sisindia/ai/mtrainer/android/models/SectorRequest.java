package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class SectorRequest {
    @SerializedName("BranchId")
    public int branchId;

    @SerializedName("TrainerId")
    public int trainerId;

    public SectorRequest(int branchId, int trainerId) {
        this.branchId = branchId;
        this.trainerId = trainerId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }
}
