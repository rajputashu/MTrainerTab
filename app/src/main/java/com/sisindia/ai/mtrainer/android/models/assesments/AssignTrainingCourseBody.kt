package com.sisindia.ai.mtrainer.android.models.assesments

import com.google.gson.annotations.SerializedName

data class AssignTrainingCourseBody(
    @SerializedName("TaskExecutionResult")
    val taskExecutionResult: CourseExecutionResultMO
)

data class CourseExecutionResultMO(
    @SerializedName("SelectedCourseIDs")
    val selectedCourseIDs: List<String>,
    @SerializedName("SelectedTopicIDs")
    val selectedTopicIds: List<String>,
    @SerializedName("EmployeeIDs")
    val employeeIDs: List<String>,
    @SerializedName("TrainingId")
    val trainingId: Int,
    @SerializedName("TrainerId")
    val trainerId: Int
)
