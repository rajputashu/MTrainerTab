package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class ClientApprovalRequest {
    @SerializedName("SpiId")
    int spiId;
    public ClientApprovalRequest(int spiid){
        this.spiId=spiid;
    }

}
