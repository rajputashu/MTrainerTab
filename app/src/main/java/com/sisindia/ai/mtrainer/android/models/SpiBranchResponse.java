package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;
@Parcel
public class SpiBranchResponse {

    @SerializedName("statusCode")
    public int statusCode;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("Data")
    public List<SpiBranchData> spibranchDataList;

    public SpiBranchResponse() {
    }
}
