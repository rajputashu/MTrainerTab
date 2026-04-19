package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.sync.UserData;

import org.parceler.Parcel;

import retrofit2.http.Field;


public class TrainingFinalSubmitRequest {

    @SerializedName("Id")
    public int rotaid;
    @SerializedName("ActualEnddatetime")
    public String taskEndTime;
    @SerializedName("StartTime")
    public String startTime;

    @SerializedName("Remarks")
    public String remarks;

    @SerializedName("TaskExecutionResult")
    public UserData taskExecutionResult;

}
