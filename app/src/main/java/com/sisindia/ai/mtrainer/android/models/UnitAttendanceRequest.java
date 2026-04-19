

package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class UnitAttendanceRequest {

    /*

        @SerializedName("EmployeeId")
        public String employeeId;

        @SerializedName("Status")
        public String status;
        @SerializedName("Latitude")
        public String latitude;
        @SerializedName("Longitude")
        public String longitude;
    */
    @SerializedName("UnitId")
    public String unitid;


    @SerializedName("LastAccessTime")
    public String lastAccessTime = "0";

    @SerializedName("PageNumber")
    public int currentPage;

    @SerializedName("Range")
    public int range;

    public UnitAttendanceRequest() {
    }

    /* public TrainingAttendanceRequest(String empId, String status, String latitude, String longitude) {
         this.employeeId = empId;
         this.status=status;
         this.latitude=latitude;
         this.longitude=longitude;
     }*/
    public UnitAttendanceRequest(String unitid) {
        this.unitid = unitid;
    }
}


