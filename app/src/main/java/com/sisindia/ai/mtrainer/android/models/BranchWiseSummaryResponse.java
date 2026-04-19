package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class BranchWiseSummaryResponse {

    @SerializedName("StatusCode")
    public int statusCode;

    @SerializedName("StatusMessage")
    public String statusMessage;

    @SerializedName("Data")
    public String branchdata;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getBranchdata() {
        return branchdata;
    }

    public void setBranchdata(String branchdata) {
        this.branchdata = branchdata;
    }
}
