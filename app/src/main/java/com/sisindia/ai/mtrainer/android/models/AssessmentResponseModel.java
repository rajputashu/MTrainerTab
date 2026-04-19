package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class AssessmentResponseModel {

    @SerializedName("statusCode")
    public String statusCode;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("Data")
    public List<AssesmentQustions> assesmentQustionsList;

    public AssessmentResponseModel() {
    }

}
