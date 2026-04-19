package com.sisindia.ai.mtrainer.android.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "master_topic_table")
public class Topic {

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
