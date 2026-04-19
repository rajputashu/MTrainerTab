package com.sisindia.ai.mtrainer.android.features.spi.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

public class DesignStatusResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<designStatus> designStatuses;

    public DesignStatusResponse() {
    }

    @Parcel
    @Entity(tableName = "design_status_table")
    public static class designStatus {
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;
        @SerializedName("Status")
        public String status;

        public designStatus() {

        }
    }
}
