package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class DraftSpiResponse extends  BaseApiResponse {

    @SerializedName("Data")
    public List<DraftSpiDetailsData> draftSpiDetailsData;

    public DraftSpiResponse() {
    }


    @Parcel
    @Entity(tableName = "draft_spi_table")
    public static class DraftSpiDetailsData {

        @SerializedName("id")
        @PrimaryKey
        @NonNull
        public int branchId;
        @SerializedName("Post")
        public String post;
        @SerializedName("Status")
        public String status;
        @SerializedName("Remark")
        public String remark;
        @SerializedName("Image1")
        public String image1;
        @SerializedName("Image2")
        public String image2;
        @SerializedName("Image2")
        public String image3;

        public DraftSpiDetailsData(){

        }
    }
}
