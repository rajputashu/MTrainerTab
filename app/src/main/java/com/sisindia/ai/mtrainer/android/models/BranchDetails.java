package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BranchDetails {

    @SerializedName("StatusCode")
    public int statusCode;

    @SerializedName("StatusMessage")
    public String statusMessage;

    @SerializedName("Data")
    public String regionDataList;

    public BranchDetails() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getRegionDataList() {
        return regionDataList;
    }
}
