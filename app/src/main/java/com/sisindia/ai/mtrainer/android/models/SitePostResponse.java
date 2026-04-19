package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class SitePostResponse extends  BaseApiResponse {

    @SerializedName("Data")
    public List<PostResponse> postResponses;

    public SitePostResponse() {

    }

    @Parcel
    @Entity(tableName = "post_master_table")
    public static class PostResponse {

        @PrimaryKey
        @SerializedName("ID")
        public int postId;

        @SerializedName("Code")
        public String code;

        @SerializedName("Name")
        public String postName;

        @SerializedName("Lat")
        public String lat;

        @SerializedName("Long")
        public String longi;

        @SerializedName("SiteID")
        public int siteID;

        @SerializedName("SiteName")
        public String siteName;

        @SerializedName("SPIID")
        public int spiID;

        @SerializedName("LastUpdated")
        public String lastUpdated;

    }
}
