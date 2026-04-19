package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class UserResponse extends BaseApiResponse {


    @SerializedName("Data")
    public GenerateOtpResponse otpResponse;

    //todo Data1 is null

    public UserResponse() {
    }

    @Parcel
    public static class GenerateOtpResponse {
        //"OTP": null,

        @SerializedName("EmployeeId")
        public String employeeId;

        @SerializedName("CompanyId")
        public String companyId;
        @SerializedName("OTP")
        public String otp;

        @SerializedName("RetMessage")
        public String retMessage;

        @SerializedName("statusCode")
        public int statusCode;

        // statusMessage

        public GenerateOtpResponse() {
        }
    }
}