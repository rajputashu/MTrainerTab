package com.sisindia.ai.mtrainer.android.features.spi.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

public class MountedStatusResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<mountedStatus> mountedStatuses;

    public MountedStatusResponse() {
    }

    @Parcel
    @Entity(tableName = "mounted_status_table")
    public static class mountedStatus {
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;
        @SerializedName("Status")
        public String status;

        public mountedStatus() {

        }
    }
}
