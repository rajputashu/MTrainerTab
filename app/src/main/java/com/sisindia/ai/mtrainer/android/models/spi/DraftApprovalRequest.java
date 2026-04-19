package com.sisindia.ai.mtrainer.android.models.spi;

import com.google.gson.annotations.SerializedName;

public class DraftApprovalRequest {
    @SerializedName("SpiId")
    public int  spiId;

    public DraftApprovalRequest(int spiid) {
        this.spiId=spiid;
    }
}
