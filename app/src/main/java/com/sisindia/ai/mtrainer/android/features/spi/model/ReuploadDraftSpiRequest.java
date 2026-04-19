package com.sisindia.ai.mtrainer.android.features.spi.model;

import com.google.gson.annotations.SerializedName;

public class ReuploadDraftSpiRequest {
    @SerializedName("KeyId")
    public int  spiId;

    public ReuploadDraftSpiRequest(int spiid) {
        this.spiId=spiid;
    }
}
