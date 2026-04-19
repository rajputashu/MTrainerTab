package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

public class ImageUploadResponse extends BaseApiResponse {

    @SerializedName("Data")
    public String data;

}
