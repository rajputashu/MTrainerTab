package com.sisindia.ai.mtrainer.android.features.spi.model;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

public class DraftImageUploadResponse extends BaseApiResponse {

    @SerializedName("Data")
    public boolean data;
}
