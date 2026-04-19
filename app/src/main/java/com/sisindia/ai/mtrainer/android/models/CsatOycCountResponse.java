package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;
@Parcel
public class CsatOycCountResponse extends BaseApiResponse{

    @SerializedName("Data")
    public List <CsatOycCountData> csatOycCountDataList;

    public CsatOycCountResponse(){

    }

    @Parcel
    @Entity(tableName = "csat_oyc_data")
    public static class CsatOycCountData {

        @SerializedName("BranchId")
        @PrimaryKey
        @NonNull
        public int branchId;

        @SerializedName("CSAT")
        public String csat;


        @SerializedName("OYC")
        public String oyc;

        public CsatOycCountData() {

        }
    }


}
