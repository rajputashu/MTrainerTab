package com.sisindia.ai.mtrainer.android.features.umbrellareport.db;

import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import java.util.List;

public class UmbrellaPostResponse extends BaseApiResponse {
    @SerializedName("Data")
    public List<UmbrellaPost> data;
}