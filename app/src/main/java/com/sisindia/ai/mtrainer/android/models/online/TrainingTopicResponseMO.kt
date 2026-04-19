package com.sisindia.ai.mtrainer.android.models.online

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse
import kotlinx.parcelize.Parcelize

data class TrainingTopicResponseMO(
    @SerializedName("Data")
    var data: List<TrainingTopicDataResponseMO>,
) : BaseApiResponse()

@Parcelize
data class TrainingTopicDataResponseMO(

    @SerializedName("CourseId")
    val courseId: Int,

    @SerializedName("CourseTitle")
    val courseTitle: String,

    @SerializedName("CourseSequenceNo")
    val courseSequenceNo: Int,

    @SerializedName("CompanyId")
    val companyId: Int,

    @SerializedName("CourseDuration")
    val courseDuration: Double,

    @SerializedName("IsOffline")
    val isOffline: Int,

    @SerializedName("SegmentType")
    val segmentType: String,

    // 🔹 Topic Level
    @SerializedName("CourseTopicId")
    val courseTopicId: Int,

    @SerializedName("CourseTopicTitle")
    val courseTopicTitle: String,

    @SerializedName("TopicDuration")
    val topicDuration: Double,

    @SerializedName("TopicSequence")
    val topicSequence: Int,

    @SerializedName("CourseTopicType")
    val courseTopicType: String,

    // 🔹 Content Level
    @SerializedName("CourseContentId")
    val courseContentId: Int,

    @SerializedName("FileDownloadName")
    val fileDownloadName: String,

    @SerializedName("FileViewName")
    val fileViewName: String,

    @SerializedName("FileURL")
    val fileURL: String,

    @SerializedName("ContentSize")
    val contentSize: Int,

    @SerializedName("ContentVersion")
    val contentVersion: Int,

    @SerializedName("CourseContentDuration")
    val courseContentDuration: Double,

    @SerializedName("CourseContentType")
    val courseContentType: String,

    @SerializedName("CourseContentThumbnailURL")
    val courseContentThumbnailURL: String,

    @SerializedName("LanguageType")
    val languageType: String,

    @SerializedName("LastSeen")
    val lastSeen: String?,   // nullable

    @SerializedName("IsActive")
    val isActive: Int,

    @SerializedName("AssessmentId")
    val assessmentId: Int,

    @SerializedName("AssessmentName")
    val assessmentName: String,

    @SerializedName("LastAccessDateTime")
    val lastAccessDateTime: String,

    @SerializedName("TotalQuestionCount")
    val totalQuestionCount: Int,

    @SerializedName("AttemptQuestionCount")
    val attemptQuestionCount: Int,

    @SerializedName("TotalScore")
    val totalScore: Int,

    @SerializedName("Score")
    val score: Int?,   // keep nullable for safety

    @SerializedName("IsReAttempt")
    val isReAttempt: Int,

    @SerializedName("TotalViews")
    val totalViews: Int

) : Parcelable