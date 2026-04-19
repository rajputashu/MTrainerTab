package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class CheckInRequest {

    @SerializedName("RotaId")
    public String trainingId;

    @SerializedName("Latitude")
    public double latitude;

    @SerializedName("Longitude")
    public double longitude;

    @SerializedName("status")
    public String status;

    @SerializedName("remark")
    public String remark;

    public CheckInRequest() {
    }

    public CheckInRequest(String trainingId, double latitude, double longitude,String status, String reason) {
        this.trainingId = trainingId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.remark = reason;

    }
}