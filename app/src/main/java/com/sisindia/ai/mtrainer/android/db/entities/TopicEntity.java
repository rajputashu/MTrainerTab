package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(
        tableName = "topic_table"
        /*foreignKeys = @ForeignKey(
                entity = CourseEntity.class,
                parentColumns = "id",
                childColumns = "courseFk",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("courseFk")}*/
)
public class TopicEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @SerializedName("CourseId")
    public int courseId;

    @SerializedName("CourseTopicId")
    public int courseTopicId;

    @SerializedName("CourseTopicTitle")
    public String courseTopicTitle;

    @SerializedName("TopicSequence")
    public int topicSequence;

    @SerializedName("CourseContentId")
    public int courseContentId;

    @SerializedName("FileViewName")
    public String fileViewName;

    @SerializedName("FileURL")
    public String fileURL;

    @SerializedName("CourseContentThumbnailURL")
    public String thumbnailURL;

    @SerializedName("CourseContentTypeId")
    public int contentTypeId;

    @SerializedName("CourseContentType")
    public String contentType;  // e.g. "Scorm"

    @SerializedName("ContentLanguageId")
    public int contentLanguageId;

    @SerializedName("ContentLanguageType")
    public String contentLanguageType;

    // FK → CourseEntity.id
//    public int courseFk;
}