package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

public class TrainingCalenderEntity {

    public String unitCode;

    public String unitName;

    public String unitAddress;

    @PrimaryKey
    public int rotaId;

    public int unitId;

    public int trainerId;

    public String trainerName;

    public int trainingTypeId;

    public int topicId;

    public String nfcId;

    public String isAdhoc;

    public String trainingType;

    public String lattitude;

    public String longitude;

    public String estimatedStartDatetime;

    public String estimatedEndDatetime;

    public String givenRating = "NA";

    public String totalTrained = "NA";

    public String savedStartTime = "NA";

    public String savedEndTime = "NA";
}