package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseTypeResponse extends BaseApiResponse {
    @SerializedName("Data")
    public List<CourseTypeData> dataList;

    @NonNull
    @Override
    public String toString() {
        return statusMessage;
    }
}
