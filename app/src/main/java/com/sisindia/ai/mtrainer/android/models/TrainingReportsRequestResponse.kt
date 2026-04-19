package com.sisindia.ai.mtrainer.android.models

import com.google.gson.annotations.SerializedName

data class EmployeeReportsRequest(
    @SerializedName("TrainerId")
    var trainerId: Int,
    @SerializedName("StartDate")
    var startDate: String?,
    @SerializedName("EndDate")
    var endDate: String,
    @SerializedName("BranchId")
    var branchId: Int
)

data class EmployeeReportsResponsedata(
    @SerializedName("EmployeeDetails")
    var employeeDetails: List<EmployeeReportsResponse>
)


data class EmployeeReportsResponse(
    @SerializedName("DateOfJoining")
    var dateOfJoining: String?,
    @SerializedName("ZoneId")
    var zoneId: Int?,
    @SerializedName("ZoneName")
    var zoneName: String?,
    @SerializedName("RegionId")
    var regionId: Int?,
    @SerializedName("RegionName")
    var regionName: String?,
    @SerializedName("BranchId")
    var branchId: Int?,
    @SerializedName("BranchName")
    var branchName: String?,
    @SerializedName("BranchCode")
    var branchCode: String?,
    @SerializedName("TrainerId")
    var trainerId: Int?,
    @SerializedName("TrainingOfficerEmployeeNo")
    var trainingOfficerEmployeeNo: String?,
    @SerializedName("TrainingOfficer")
    var trainingOfficer: String?,
    @SerializedName("EmployeeName")
    var employeeName: String?,
    @SerializedName("EmployeeNo")
    var employeeNo: String?,
    @SerializedName("PrimaryContactNo")
    var primaryContactNo: String?,
    @SerializedName("SiteName")
    var siteName: String?,
    @SerializedName("IsLogin")
    var isLogin: String?,
    @SerializedName("CourseStatus")
    var courseStatus: String?,
)