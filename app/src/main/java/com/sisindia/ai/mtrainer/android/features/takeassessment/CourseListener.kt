package com.sisindia.ai.mtrainer.android.features.takeassessment

import com.sisindia.ai.mtrainer.android.models.assesments.CourseListResponse

interface CourseListener {
    fun onCourseListSelected(position: Int)
    fun onCourseListSelected(position: Int, data: CourseListResponse.CourseListData?)
}