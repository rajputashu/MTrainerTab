package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "Spi_table_data")
public class SpiTableDetailsData {

    @SerializedName("BranchId")
    @PrimaryKey
    @NonNull
    public int branchId;

    @SerializedName("TrainerId")
    public String trainerID;
    @SerializedName("TrainerName")
    public int trainerName;

    @SerializedName("CustomerId")
    public int CustomerId;
    @SerializedName("CustomerName")
    public int CustomerName;
    @SerializedName("BranchName")
    public int BranchName;
    @SerializedName("Type")
    public int Type;
    @SerializedName("UnitId")
    public int UnitId;
    @SerializedName("UnitCode")
    public int UnitCode;
    @SerializedName("UnitName")
    public int UnitName;
    @SerializedName("Status")
    public int Status;

    public SpiTableDetailsData(){

    }


}
