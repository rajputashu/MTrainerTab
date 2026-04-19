package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "rplform_photo_table")
public class RplFormPhotoEntity {

    // rotaId_employeeId_pictureTypeId
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    public String id;
    @SerializedName("RplFormPhotoURI")
    public String rplFormPhotoURI;
    @SerializedName("RplFormPhotoId")
    public String rplFormPhotoId;
    @SerializedName("Status")
    public int status;
    public int pictureTypeId;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getRplFormPhotoURI() {
        return rplFormPhotoURI;
    }

    public void setRplFormPhotoURI(String rplFormPhotoURI) {
        this.rplFormPhotoURI = rplFormPhotoURI;
    }

    public String getRplFormPhotoId() {
        return rplFormPhotoId;
    }

    public void setRplFormPhotoId(String rplFormPhotoId) {
        this.rplFormPhotoId = rplFormPhotoId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPictureTypeId() {
        return pictureTypeId;
    }

    public void setPictureTypeId(int pictureTypeId) {
        this.pictureTypeId = pictureTypeId;
    }

    public int getRotaId() {
        return rotaId;
    }

    public void setRotaId(int rotaId) {
        this.rotaId = rotaId;
    }

    public int rotaId;


}
