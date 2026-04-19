package com.sisindia.ai.mtrainer.android.features.spi.model;

import com.google.gson.annotations.SerializedName;

public class SpiPostRequest {

    @SerializedName("SiteId")
    int siteid;
    public SpiPostRequest(int siteid){
        this.siteid=siteid;

    }
}
