package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
@Entity(
        tableName = "course_table"
        /*foreignKeys = @ForeignKey(
                entity = LanguageEntity.class,
                parentColumns = "id",
                childColumns = "languageFk",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("languageFk")}*/
)
public class CourseEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // Not unique PK — same courseId (e.g. 121) exists per language
    @SerializedName("CourseId")
    public int courseId;

    @SerializedName("ProgramId")
    public int programId;   // denormalized for quick queries

    @SerializedName("CompanyId")
    public int companyId;

    @SerializedName("CourseTitle")
    public String courseTitle;

    @SerializedName("CourseThumbnailURL")
    public String courseThumbnailURL;

    @SerializedName("SegmentType")
    public String segmentType;

    @SerializedName("CourseSeqNo")
    public int courseSeqNo;

    public int languageId;

    @Ignore
    @SerializedName("Topics")
    public List<TopicEntity> topicsList;
}