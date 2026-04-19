package com.sisindia.ai.mtrainer.android.features.myconveyance

import com.sisindia.ai.mtrainer.android.models.ConveyanceDailyData
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyData
import com.sisindia.ai.mtrainer.android.models.ConveyanceTimeLineData
import com.sisindia.ai.mtrainer.android.uimodels.YearUIModel

interface MyconvenceListeners {
    fun onconveyanceitemclick(data: ConveyanceMonthlyData, position: Int)
}

interface MyconvencedailyListeners {
    fun onconveyanceitemclick(data: ConveyanceDailyData, position: Int)
}

interface MyconvencetimelineListeners {
    fun onconveyanceitemclick(data: ConveyanceTimeLineData, position: Int)
}

interface YearListeners {
    fun onyearitemclick(position: Int)
}

interface MonthListeners {
    fun onmonthitemclick(position: Int)
}