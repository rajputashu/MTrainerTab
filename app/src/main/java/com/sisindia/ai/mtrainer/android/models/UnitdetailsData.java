package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class UnitdetailsData {

    @SerializedName("StatusCode")
    int statusCode;

    @SerializedName("StatusMessage")
    String statusMessage;

    @SerializedName("Data")
    String unitsdata;

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getUnitsdata() {
        return unitsdata;
    }

    public UnitdetailsData() {
    }


}
