package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class UnitListResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<Unit> unitList;

    @SerializedName("totalCount")
    public int totalCount;

    @SerializedName("currPage")
    public int currPage;

    @Parcel
    @Entity(tableName = "master_unit_table")
    public static class Unit {

        @PrimaryKey
        @SerializedName("SiteId")
        public int unitId;

        @SerializedName("SiteName")
        public String unitName;

        @SerializedName("SiteCode")
        public String unitCode;

        @SerializedName("Address")
        public String SiteAddress;

        @SerializedName("EmpCount")
        public int empCount;

        @NonNull
        @Override
        public String toString() {
            return unitName;
        }
    }
}