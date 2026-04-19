package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.db.Topic;

import org.parceler.Parcel;

import java.util.List;
@Parcel

public class ChooseTopicsResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<TopicsResponse> topicsResponses;

    public ChooseTopicsResponse() {

    }

    @Parcel
    @Entity(tableName = "master_topic_table")
    public static class TopicsResponse {

        @PrimaryKey
        @SerializedName("Id")
        public int topicId;

        @SerializedName("Name")
        public String topicName;

        @SerializedName("TopicCategoryId")
        public int topicCatId;

        @SerializedName("isSPI")
        public int isSpi;

        @SerializedName("Status")
        public int status;

        @SerializedName("LastUpdated")
        public String lastUpdated;

        @SerializedName("Category")
        public String topicCatName;

        @SerializedName("CompanyID")
        public String companyId;
    }
}
