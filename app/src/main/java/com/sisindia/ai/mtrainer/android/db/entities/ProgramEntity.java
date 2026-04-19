package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
@Entity(tableName = "program_table")
public class ProgramEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @SerializedName("ProgramId")
    public int programId;

    @SerializedName("ProgramName")
    public String programName;

    @SerializedName("ProgramThumbnail")
    public String programThumbnail;

    @SerializedName("StarProgramSeqNo")
    public int starProgramSeqNo;

    @Ignore
    @SerializedName("Languages")
    public List<LanguageEntity> languagesList;
}
