package com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

public class UmbrellaImageResponse extends BaseApiResponse {
    @SerializedName("Data")
    public String data;
}
