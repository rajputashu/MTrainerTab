package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

public class FeedBackOtpResponse {

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("Data")
    public FeedBackOtpGeneratedResponse feedBackOtpGeneratedResponse;

    public FeedBackOtpResponse() {
    }

    @Parcel
    public static class FeedBackOtpGeneratedResponse extends BaseApiResponse {
//    public static class FeedBackOtpGeneratedResponse {

        @SerializedName("PhoneNumber")
        public String phoneNumber;

        @SerializedName("OTP")
        public String otp;

        public FeedBackOtpGeneratedResponse() {
        }
    }
}
