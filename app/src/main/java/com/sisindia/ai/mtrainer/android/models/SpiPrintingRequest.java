package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class SpiPrintingRequest {

    @SerializedName("SpiId")
    int spiId;
    public SpiPrintingRequest(int spiid){
        this.spiId=spiid;
    }
}
