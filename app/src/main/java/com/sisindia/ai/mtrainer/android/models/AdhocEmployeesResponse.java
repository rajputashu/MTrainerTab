package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;
@Parcel
public class AdhocEmployeesResponse extends  BaseApiResponse {

    @SerializedName("Data")
    public List<AdhocEmployees> adhocEmployeesResponses;

    public AdhocEmployeesResponse() {

    }

    @Parcel
    @Entity(tableName = "adhoc_employees_table")
    public static class AdhocEmployees {

        @PrimaryKey(autoGenerate = true)
        @SerializedName("Id")
        public int Id;

        @SerializedName("RotaId")
        public int rotaId;
        @SerializedName("Name")
        public String employeeName;
        @SerializedName("EmployeeCode")
        public String employeeCode;

    }
}
