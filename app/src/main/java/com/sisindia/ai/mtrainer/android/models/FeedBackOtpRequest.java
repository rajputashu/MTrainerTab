package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class FeedBackOtpRequest {
    @SerializedName("PhoneNumber")
    public String phoneNumber;

    @SerializedName("TrainingId")
    public int trainingId;

    @SerializedName("ClientEmailId")
    public String clientEmailId;

    public FeedBackOtpRequest(){

    }

}
