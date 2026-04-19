package com.sisindia.ai.mtrainer.android.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel


data class TrainingCoursesModel(
    @SerializedName("CourseId")
    var courseId: Int = 0,

    @SerializedName("Title")
var title: String? = null,

    @SerializedName("Mode")
var mode: String? = null,
    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("ContentUrl")
    var contentUrl: String? = null,

@SerializedName("DownloadUrl")
var downloadUrl: String? = null,

@SerializedName("Version")
var version: String? = null,
    @SerializedName("Duration")
    var duration: String? = null
    )