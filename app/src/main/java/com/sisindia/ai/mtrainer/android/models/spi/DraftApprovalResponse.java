package com.sisindia.ai.mtrainer.android.models.spi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

public class DraftApprovalResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<DraftApprovalTableDetailsData> draftApprovalTableDetailsData;

    public DraftApprovalResponse() {
    }


    @Parcel
    @Entity(tableName = "Spi_draft_approval_table")
    public static class DraftApprovalTableDetailsData {
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;
        @SerializedName("SpiId")
        public int spiId;
        @SerializedName("StatusId")
        public int statusid;
        @SerializedName("PostName")
        public String postName;
        @SerializedName("Status")
        public String status;
        @SerializedName("Remarks")
        public String remark;

         public DraftApprovalTableDetailsData(){

        }
}

    }
