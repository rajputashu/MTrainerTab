package com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

public class UmbrellaDataResponse extends BaseApiResponse {
    @SerializedName("KeyId")
    public int keyId;
}
