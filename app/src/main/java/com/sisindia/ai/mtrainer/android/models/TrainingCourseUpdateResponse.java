package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class TrainingCourseUpdateResponse extends BaseApiResponse{
    @SerializedName("Data")
    String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
