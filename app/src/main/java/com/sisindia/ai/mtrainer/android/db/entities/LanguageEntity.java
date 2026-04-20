package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
@Entity(tableName = "language_table"
        /*foreignKeys = @ForeignKey(
                entity = ProgramEntity.class,
                parentColumns = "id",
                childColumns = "programFk",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("programFk")}*/
)
public class LanguageEntity {

    /*@PrimaryKey(autoGenerate = true)
    public int id;*/

    @PrimaryKey
    @SerializedName("LanguageId")
    public int languageId;

    @SerializedName("LanguageType")
    public String languageType;

    // FK → ProgramEntity.id
//    public int programFk;

    @Ignore
    @SerializedName("Courses")
    public List<CourseEntity> coursesList;

}