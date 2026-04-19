package com.sisindia.ai.mtrainer.android.features.trainingprogram

import com.sisindia.ai.mtrainer.android.models.online.TrainingProgramTypeDataResponseMO

interface TrainingProgramListener {
    fun onItemClick(item: TrainingProgramTypeDataResponseMO)
}