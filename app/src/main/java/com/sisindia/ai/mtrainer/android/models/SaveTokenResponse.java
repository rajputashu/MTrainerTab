package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class SaveTokenResponse {

    @SerializedName("Status")
    String status;

    @SerializedName("LoginCodeToken")
    String loginCodeToken;

    public SaveTokenResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLoginCodeToken() {
        return loginCodeToken;
    }

    public void setLoginCodeToken(String loginCodeToken) {
        this.loginCodeToken = loginCodeToken;
    }
}
