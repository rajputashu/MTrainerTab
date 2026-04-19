package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class SiteResponseModel {

    @SerializedName("StatusCode")
    public int statusCode;

    @SerializedName("StatusMessage")
    public String statusMessage;

    @SerializedName("Data")
    public String regionDataList;

    public SiteResponseModel() {
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
