package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class ClientApprovalResponse extends  BaseApiResponse {

    @SerializedName("Data")
    public List<ClientApprovalStatus> clientApprovalStatuses;

    public ClientApprovalResponse() {
    }


    @Parcel
    @Entity(tableName = "client_approval_table")
    public static class ClientApprovalStatus {
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;
        @SerializedName("Status")
        public String status;
        @SerializedName("Date")
        public String Date;

        public ClientApprovalStatus() {

        }
    }
}
