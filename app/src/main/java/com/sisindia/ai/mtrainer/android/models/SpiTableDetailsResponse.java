package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class SpiTableDetailsResponse extends BaseApiResponse{


    @SerializedName("Data")
    public List<SpiTableDetailsData> spiTableDetailsDataList;

    public SpiTableDetailsResponse() {
    }


    @Parcel
    @Entity(tableName = "Spi_table")
    public static class SpiTableDetailsData {

        @PrimaryKey(autoGenerate = true)
        @SerializedName("Id")
        public int id;

       // @PrimaryKey
      //  @NonNull

        @SerializedName("SpiId")
        public int spiId;
        @SerializedName("BranchId")
        public int branchId;

        @SerializedName("TrainerId")
        public int trainerID;
        @SerializedName("TrainerName")
        public String trainerName;

        @SerializedName("CustomerId")
        public int CustomerId;
        @SerializedName("CustomerName")
        public String customerName;
        @SerializedName("BranchName")
        public String branchName;
        @SerializedName("Type")
        public String type;
        @SerializedName("UnitId")
        public int UnitId;
        @SerializedName("UnitCode")
        public String unitCode;
        @SerializedName("UnitName")
        public String UnitName;
        @SerializedName("Status")
        public String Status;
        @SerializedName("LastUpdatedOn")
        public String LastUpdatedOn;
        @SerializedName("TypeId")
        public int typeId;
        @SerializedName("StatusId")
        public int StatusId;


        public SpiTableDetailsData(){

        }


    }
}
