package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class VanResponse {
    @SerializedName("Data")
    public String data;
    @SerializedName("statusCode")
    public int statusCode;

    @SerializedName("statusMessage")
    public String statusMessage;
    public VanResponse(){

    }

}
