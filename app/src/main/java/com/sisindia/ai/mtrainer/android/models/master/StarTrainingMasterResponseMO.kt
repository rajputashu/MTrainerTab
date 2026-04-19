package com.sisindia.ai.mtrainer.android.models.master

import com.google.gson.annotations.SerializedName
import com.sisindia.ai.mtrainer.android.db.entities.ProgramEntity
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse

data class StarTrainingMasterResponseMO(
    @SerializedName("Data")
    var data: List<ProgramEntity>,
) : BaseApiResponse()

