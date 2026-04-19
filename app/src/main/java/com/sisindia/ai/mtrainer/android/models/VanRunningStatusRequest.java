package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class VanRunningStatusRequest {
    @SerializedName("TrainerId")
    public int trainerId;

    @SerializedName("CompanyId")
    public int companyId;

    @SerializedName("TrainingType")
    public int TrainingType;
    @SerializedName("OnRoad")
    public String OnRoad;
    @SerializedName("Reason")
    public String Reason;



    public VanRunningStatusRequest() {
    }

    public VanRunningStatusRequest(int trainerId, int companyid, int trainingType,String OnRoad, String reason) {
        this.trainerId = trainerId;
        this.companyId = companyid;
        this.TrainingType = trainingType;
        this.OnRoad = OnRoad;
        this.Reason = reason;

    }
}
