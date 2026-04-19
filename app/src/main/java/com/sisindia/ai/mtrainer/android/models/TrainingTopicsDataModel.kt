package com.sisindia.ai.mtrainer.android.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "training_topics_courses")
@Parcelize
data class TrainingTopicsDataModel(
    @SerializedName("CourseId")
    var courseId: Int,


    @SerializedName("CourseTitle")
    var courseTitle: String?,


    @SerializedName("CompanyId")
    var companyId: Int,


    @SerializedName("LastSeen")
    var lastseen: String?,

    @SerializedName("Session")
    var session: String?,

    @SerializedName("StartTime")
    var startTime: String?,


    @SerializedName("CourseDuration")
    var courseDuration: Double,


    @SerializedName("IsOffline")
    var isOffline: Int,


    @SerializedName("SegmentType")
    var segmentType: String?,


    @SerializedName("CourseTopicId")
    var courseTopicId: Int,


    @SerializedName("CourseTopicTitle")
    var courseTopicTitle: String?,


    @SerializedName("TopicDuration")
    var topicDuration: Double,


    @SerializedName("TopicSequence")
    var topicSequence: Int,


    @SerializedName("CourseTopicType")
    var courseTopicType: String?,

    @PrimaryKey
    @SerializedName("CourseContentId")
    var courseContentId: Int,


    @SerializedName("FileDownloadName")
    var fileDownloadName: String?,


    @SerializedName("FileViewName")
    var fileViewName: String?,


    @SerializedName("FileURL")
    var fileURL: String?,


    @SerializedName("ContentSize")
    var contentSize: Int,


    @SerializedName("ContentVersion")
    var contentVersion: Int,


    @SerializedName("CourseContentDuration")
    var courseContentDuration: Double,


    @SerializedName("CourseContentType")
    var courseContentType: String? = null,


    @SerializedName("LanguageType")
    var languageType: String?,


    @SerializedName("CourseContentThumbnailURL")
    var courseContentThumbnailURL: String?,


    @SerializedName("IsActive")
    var isActive: Int?,

    @SerializedName("SyncStatus")
    var SyncStatus: Int? = 0,

    ) : Parcelable