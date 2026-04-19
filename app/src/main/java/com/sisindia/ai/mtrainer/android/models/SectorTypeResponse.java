package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class SectorTypeResponse{
    @SerializedName("StatusCode")
    public int statusCode;

    @SerializedName("StatusMessage")
    public String statusMessage;
    @SerializedName("Data")
    private String Data;


    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public SectorTypeResponse() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
