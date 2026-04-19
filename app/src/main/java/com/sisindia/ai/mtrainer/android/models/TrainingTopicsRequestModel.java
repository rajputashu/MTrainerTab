package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
public class TrainingTopicsRequestModel {
    @SerializedName("CompanyId")
    public int companyId;
    @SerializedName("UserId")
    public int userId;
    @SerializedName("CourseId")
    public int courseId;

    public TrainingTopicsRequestModel() {
    }
}


