package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageDataUploadResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<String> data;
}
