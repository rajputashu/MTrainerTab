package com.sisindia.ai.mtrainer.android.features.spi.model;

import com.google.gson.annotations.SerializedName;

public class SpiPostReFetchRequest {
    @SerializedName("KeyId")
    public int keyId;

    public SpiPostReFetchRequest(int keyId) {
        this.keyId = keyId;
    }
}
