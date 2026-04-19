package com.sisindia.ai.mtrainer.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TrainingCoursesRequest(
    @SerializedName("CompanyId")
    var companyId: Int,

    @SerializedName("UserId")
    var userId: Int
)

data class TrainingCoursesResponse(
    @SerializedName("Data")
    var data: List<TrainingCoursesDataResponse>,
) : BaseApiResponse()

@Parcelize
data class TrainingCoursesDataResponse(
    @SerializedName("CourseId")
    var courseId: Int = 0,

    @SerializedName("CourseTitle")
    var courseTitle: String?,

    @SerializedName("CompanyId")
    var companyId: Int?,

    @SerializedName("CourseDuration")
    var courseDuration: Int?,

    @SerializedName("CourseSequenceNo")
    var courseSequenceNo: Int?,

    @SerializedName("IsOffline")
    var isOffline: Int?,

    @SerializedName("SegmentType")
    var segmentType: String?,

    @SerializedName("CourseThumbnailURL")
    var courseThumbnailURL: String? = null,

    @SerializedName("IsActive")
    var isActive: Int?,

    ) : Parcelable {

}


