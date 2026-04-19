package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class PreAuthResponse {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("expires_in")
    public String expiresIn;

    @SerializedName("userName")
    public String userName;

    @SerializedName(".issued")
    public String issued;

    @SerializedName(".expires")
    public String expires;



    public PreAuthResponse() {
    }
}