package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class UserRequest {

    @SerializedName("PhoneNumber")
    public String phoneNumber;
    @SerializedName("Version")
    public int version;
    @SerializedName("OTP")
    public String otp;
    @SerializedName("FireBaseToken")
    public String firebaseToken;

    public UserRequest() {

    }
}