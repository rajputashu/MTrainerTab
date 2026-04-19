package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class RefreshResponse extends BaseApiResponse {
    @SerializedName("Data")
    public List<SiteResponse> siteResponses;

    @Parcel
    public static class SiteResponse {


        @SerializedName("EmployeeNo")
        public String regNo;

        @SerializedName("SiteId")
        public int unitId;

        @SerializedName("SiteName")
        public String unitName;
        @SerializedName("isDebug")
        public int isDebug = 0;

    }
}
