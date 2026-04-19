package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class DesignSpiRequest {

    @SerializedName("SpiId")
    public  int spiId ;
    public DesignSpiRequest(){

    }

    public DesignSpiRequest(int spiId) {
        this.spiId=spiId;
    }
}
