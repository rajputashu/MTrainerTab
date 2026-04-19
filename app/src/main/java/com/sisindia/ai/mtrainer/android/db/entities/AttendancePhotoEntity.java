package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "attendance_photo_table")
public class AttendancePhotoEntity {

    // rotaId_employeeId_pictureTypeId
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    public String id;
    @SerializedName("AttendancePhotoURI")
    public String attendancePhotoURI;
    @SerializedName("AttendancePhotoId")
    public String attendancePhotoId;
    @SerializedName("Status")
    public int status;
    public String waterMark="";
    public int isMark = 0;
    public int isCompress = 0;

    public int pictureTypeId;

    public int rotaId;

    public int postId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttendancePhotoURI() {
        return attendancePhotoURI;
    }

    public void setAttendancePhotoURI(String attendancePhotoURI) {
        this.attendancePhotoURI = attendancePhotoURI;
    }

    public String getAttendancePhotoId() {
        return attendancePhotoId;
    }

    public void setAttendancePhotoId(String attendancePhotoId) {
        this.attendancePhotoId = attendancePhotoId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
