package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class RplFormEmplyoeeNoRequest {
    @SerializedName("RegNo")
    String employeeNo;


    public RplFormEmplyoeeNoRequest(String regno) {
        this.employeeNo = regno;
    }

    public RplFormEmplyoeeNoRequest() {

    }
}
