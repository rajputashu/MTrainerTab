package com.sisindia.ai.mtrainer.android.models.online

import com.google.gson.annotations.SerializedName

data class TrainingCourseBodyMO(

    @SerializedName("CompanyId")
    var companyId: Int,

    @SerializedName("UserId")
    var userId: Int,

    @SerializedName("LanguageId")
    val languageId: Int = 1,

    @SerializedName("ProgramId")
    val programId: Int = 0,

    @SerializedName("CourseTypeId")
    val courseTypeId: Int = 0,
)