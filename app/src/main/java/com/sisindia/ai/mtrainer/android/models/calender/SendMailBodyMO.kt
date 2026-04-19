package com.sisindia.ai.mtrainer.android.models.calender

import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel
data class SendMailBodyMO(
    @SerializedName("TrainingId")
    val trainingId: Int=0
)