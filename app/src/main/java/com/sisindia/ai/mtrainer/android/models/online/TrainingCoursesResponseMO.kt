package com.sisindia.ai.mtrainer.android.models.online

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse
import kotlinx.parcelize.Parcelize

data class TrainingCoursesResponseMO(
    @SerializedName("Data")
    var data: List<TrainingCourseDataResponseMO>,
) : BaseApiResponse()

@Parcelize
data class TrainingCourseDataResponseMO(

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

    @SerializedName("CourseThumbnailURL")
    val courseThumbnailURL: String,

    @SerializedName("SegmentType")
    val segmentType: String,

    @SerializedName("IsActive")
    val isActive: Int,

    @SerializedName("ContentTypeId")
    val contentTypeId: Int,

    @SerializedName("ContentType")
    val contentType: String,

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
    val score: Int?,   // nullable because API gives null

    @SerializedName("TotalTopicCount")
    val totalTopicCount: Int,

    @SerializedName("AttemptTopicCount")
    val attemptTopicCount: Int,

    @SerializedName("IsReAttempt")
    val isReAttempt: Int
) : Parcelable
