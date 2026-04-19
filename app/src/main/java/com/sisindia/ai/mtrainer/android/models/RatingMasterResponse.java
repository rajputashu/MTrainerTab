package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class RatingMasterResponse extends BaseApiResponse{
    @SerializedName("Data")
    public List<RatingData> ratingDataList;

    @SerializedName("Data1")
    public List<RatingQuestionData> ratingQuestionList;

    @Parcel
    @Entity(tableName = "master_rating_table")
    public static class RatingData {


        @SerializedName("FeedbackQuestion")
        public String ratingQuestion;

        @SerializedName("FeedbackScore")
        public int ratingValue;

        @PrimaryKey
        @SerializedName("Id")
        public int ratingId;

        @SerializedName("FeedbackRemarks")
        public String ratingHeader;
    }

    @Parcel
    @Entity(tableName = "master_rating_question_table")
    public static class RatingQuestionData {

        @SerializedName("MaxScore")
        public int ratingValue;

        @SerializedName("Question")
        public String question;

        @PrimaryKey
        @SerializedName("Id")
        public int questionId;
    }
}
