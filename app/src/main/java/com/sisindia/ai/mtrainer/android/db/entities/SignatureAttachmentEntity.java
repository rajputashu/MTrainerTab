package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "signature_attachment_table")
public class SignatureAttachmentEntity {
    @PrimaryKey
    @SerializedName("id")
    public int id;
    @SerializedName("RotaId")
    public int rotaId;
    @SerializedName("TrainingSignatureURI")
    public String trainingSignatureURI;
    @SerializedName("TrainingSignatureId")
    public String trainingSignatureId;
    @SerializedName("SignatureTypeId")
    public String signatureTypeId;
    @SerializedName("Status")
    public String status;

    @SerializedName("IsSynced")
    public String IsSynced;

    public int getRotaId() {
        return rotaId;
    }

    public void setRotaId(int rotaId) {
        this.rotaId = rotaId;
    }

    public String getIsSynced() {
        return IsSynced;
    }

    public void setIsSynced(String isSynced) {
        IsSynced = isSynced;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainingId() {
        return rotaId;
    }

    public void setTrainingId(int trainingId) {
        this.rotaId = trainingId;
    }

    public String getTrainingSignatureURI() {
        return trainingSignatureURI;
    }

    public void setTrainingSignatureURI(String trainingSignatureURI) {
        this.trainingSignatureURI = trainingSignatureURI;
    }

    public String getTrainingSignatureId() {
        return trainingSignatureId;
    }

    public void setTrainingSignatureId(String trainingSignatureId) {
        this.trainingSignatureId = trainingSignatureId;
    }

    public String getSignatureTypeId() {
        return signatureTypeId;
    }

    public void setSignatureTypeId(String signatureTypeId) {
        this.signatureTypeId = signatureTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
