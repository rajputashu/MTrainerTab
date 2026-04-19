package com.sisindia.ai.mtrainer.android.models.online

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainingTopicsBodyMO(

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

    @SerializedName("CourseId")
    val courseId: Int = 0,
) : Parcelable