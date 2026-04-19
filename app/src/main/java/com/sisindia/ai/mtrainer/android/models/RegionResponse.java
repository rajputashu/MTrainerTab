package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;


@Parcel
public
class RegionResponse {


    @SerializedName("statusCode")
    public int statusCode;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("Data")
    public List<RegionData> regionDataList;

    public RegionResponse() {
    }


}


