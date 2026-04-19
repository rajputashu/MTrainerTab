package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel

public class CancelTrainingResponse extends BaseApiResponse {
    @SerializedName("Data")
    public List<CancelTrainingResponse> cancelTrainingResponses;

    public CancelTrainingResponse() {


    }
}
