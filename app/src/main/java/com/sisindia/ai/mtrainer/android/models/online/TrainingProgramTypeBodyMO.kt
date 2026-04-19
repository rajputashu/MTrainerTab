package com.sisindia.ai.mtrainer.android.models.online

import com.google.gson.annotations.SerializedName

data class TrainingProgramTypeBodyMO(

    @SerializedName("CompanyId")
    var companyId: Int,

    @SerializedName("UserId")
    var userId: Int,

    @SerializedName("LanguageId")
    val languageId: Int = 1
)