package com.sisindia.ai.mtrainer.android.features.spi.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

public class ReuploadDraftSpiResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<ReuploadDraftSpiData> reuploadDraftSpiData;

    public ReuploadDraftSpiResponse() {
    }


    @Parcel
    @Entity(tableName = "reupload_draft_spi_table")
    public static class ReuploadDraftSpiData {
        @PrimaryKey(autoGenerate = true)
        @SerializedName("SNo")
        public int id;
        @SerializedName("KeyId")
        public int keyid;
        @SerializedName("PostId")
        public int postId;

        @SerializedName("PostName")
        public String postName;
        @SerializedName("Status")
        public String status;
        @SerializedName("StatusId")
        public String statusId;
        @SerializedName("Remarks")
        public String remarks;



        public  void ReuploadDraftSpiData(){

        }

    }
}
