package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "training_photo_attachment_table")
public class TrainingPhotoAttachmentEntity {

    @SerializedName("Rotaid")
    public int rotaId;

    public String postName;

    @PrimaryKey
    @NonNull
    @SerializedName("TrainingPhotoURI")
    public String trainingPhotoURI;
    @SerializedName("TrainingPhotoId")
    public String trainingPhotoId;
    @SerializedName("Status")
    public int status;
    public String pictureTypeId;
    public int postId;
    public String waterMark;
    public int isMark = 0;
    public int isCompress = 0;


    public String getPictureTypeId() {
        return pictureTypeId;
    }

    public void setPictureTypeId(String pictureTypeId) {
        this.pictureTypeId = pictureTypeId;
    }

    public String getTrainingPhotoURI() {
        return trainingPhotoURI;
    }

    public void setTrainingPhotoURI(String trainingPhotoURI) {
        this.trainingPhotoURI = trainingPhotoURI;
    }

    public String getTrainingPhotoId() {
        return trainingPhotoId;
    }

    public void setTrainingPhotoId(String trainingPhotoId) {
        this.trainingPhotoId = trainingPhotoId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public int getRotaId() {
        return rotaId;
    }

    public void setRotaId(int rotaId) {
        this.rotaId = rotaId;
    }
}
