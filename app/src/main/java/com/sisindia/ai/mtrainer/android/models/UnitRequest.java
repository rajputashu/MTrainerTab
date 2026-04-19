package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.sync.UserData;

public class UnitRequest {
    @SerializedName("Id")
    public int rotaid;
    @SerializedName("ActualEnddatetime")
    public String taskEndTime;
    @SerializedName("StartTime")
    public String startTime;
    @SerializedName("TaskExecutionResult")
    public UserData taskExecutionResult;
}
