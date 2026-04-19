package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class ContactListResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<ClientData> clientDataList;

    @Parcel
    @Entity(tableName = "master_client_contact_table")
    public static class ClientData {

        @Ignore
        @SerializedName("Status")
        public int status;

        @Ignore
        @SerializedName("Designation")
        public String designation;

        @SerializedName("Email")
        public String email;

        @SerializedName("MobileNo1")
        public String firstMobileNumber;

        @SerializedName("MobileNo2")
        public String secondMobileNumber;

        @PrimaryKey
        @SerializedName("ClientId")
        public int clientId;

        @Ignore
        @SerializedName("ID")
        public int id;

        @SerializedName("Name")
        public String clientName;

        @SerializedName("UnitId")
        public int unitId;

        @Ignore
        @SerializedName("UnitName")
        public String unitName;
    }
}
