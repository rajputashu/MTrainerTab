package com.sisindia.ai.mtrainer.android.models.online

import com.google.gson.annotations.SerializedName
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse

data class TrainingProgramTypeResponseMO(
    @SerializedName("Data")
    var data: List<TrainingProgramTypeDataResponseMO>,
) : BaseApiResponse()

data class TrainingProgramTypeDataResponseMO(
    @SerializedName("CourseTypeId")
    val courseTypeId: Int,
    @SerializedName("CourseType")
    val courseType: String,
    @SerializedName("ProgramId")
    val programId: Int,
    @SerializedName("ProgramName")
    val programName: String,
    @SerializedName("ProgramThumbnail")
    val programThumbnail: String,
    @SerializedName("TotalCourseAccessed")
    val totalCourseAccessed: Int,
    @SerializedName("TotalCourseAssigned")
    val totalCourseAssigned: Int,
    @SerializedName("TotalAssessmenAttempted")
    val totalAssessmenAttempted: Int?,
    @SerializedName("TotalAssessmentAssigned")
    val totalAssessmentAssigned: Int,
    @SerializedName("StarProgramSeq")
    val starProgramSeq: Int,
    @SerializedName("IsCertificateEnable")
    val isCertificateEnable: Int
)
