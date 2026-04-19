package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class SpiPrintingResponse extends  BaseApiResponse {


    @SerializedName("Data")
    public List<SpiPrintingStatus> spiPrintingStatus;

    public SpiPrintingResponse() {
    }

    @Parcel
    @Entity(tableName = "spi_printing_table")
    public static class SpiPrintingStatus {
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;
        @SerializedName("Status")
        public String status;
        @SerializedName("Date")
        public String Date;

        public SpiPrintingStatus() {

        }
    }
}
