package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "assessment_question_master")
public class AssesmentQustions {
    @SerializedName("Id")
    @PrimaryKey
    @NonNull
    public String questionId;

    @SerializedName("Question")
    public String question;

    @SerializedName("SectorID")
    public String sectorID;

    @SerializedName("SectorSiteID")
    public String sectorSiteID;

    @SerializedName("TopicId")
    public String topicId;

    public AssesmentQustions() {
    }
}
