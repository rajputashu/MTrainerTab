package com.sisindia.ai.mtrainer.android.models.master

import com.google.gson.annotations.SerializedName

data class StarProgramMasterBodyMO(

    @SerializedName("CompanyId")
    var companyId: Int,

    @SerializedName("UserId")
    var userId: Int,
)