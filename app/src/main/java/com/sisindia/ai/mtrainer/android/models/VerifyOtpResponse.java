package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class VerifyOtpResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<LoginResponse> loginResponse;

    // TODO: 2020-03-29 Data1

    public VerifyOtpResponse() {
    }

    @Parcel
    public static class LoginResponse {

        @SerializedName("Flag")
        public String flag;

        @SerializedName("TrainerId")
        public int trainerId;

        @SerializedName("UserImageurl")
        public String userImageurl;

        @SerializedName("Lat")
        public String lattitude;

        @SerializedName("Long")
        public String longitude;

        @SerializedName("AlternateAddress")
        public String alternateAddress;

        @SerializedName("AlternateNo")
        public String alternateNo;

        @SerializedName("SessionId")
        public String sessionId;

        @SerializedName("RegdNo")
        public String regdNo;

        @SerializedName("LastName")
        public String lastName;

        @SerializedName("MiddleName")
        public String middleName;

        @SerializedName("FirstName")
        public String firstName;

        @SerializedName("ContactNo")
        public String contactNo;

        @SerializedName("EmailAddress")
        public String emailAddress;

        @SerializedName("Empname")
        public String empName;

        @SerializedName("CompanyId")
        public int companyId;

        @SerializedName("BusinessType")
        public String businessType;

        @SerializedName("CompanyLogoUrl")
        public String companyLogoUrl;

        @SerializedName("CompanyColorPrefrence")
        public String companyColorPrefrence;

        @SerializedName("Role")
        public String role;

        @SerializedName("SiteId")
        public int unitId;

        @SerializedName("SiteName")
        public String unitName;

        @SerializedName("RegionId")
        public int regionId;

        public LoginResponse() {
        }
    }
}
