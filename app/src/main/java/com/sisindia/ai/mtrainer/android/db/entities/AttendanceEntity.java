package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "attendance_table")
public class AttendanceEntity {

    @SerializedName("ID")
    public int employeeId;

    @PrimaryKey(autoGenerate = true)
    public int id;
    public int rotaId;

    @SerializedName("EmployeeCode")
    public String empCode;

    @SerializedName("EmployeeName")
    public String employeeName;

    @Ignore
    @SerializedName("EmployeeMobNo")
    public String employeeMobNo;

    @Ignore
    @SerializedName("UnitId")
    public String unitId;

    @SerializedName("PostId")
    public int postId;

    @SerializedName("PostName")
    public String postName;

    @Ignore
    @SerializedName("Lat")
    public String lat;

    @Ignore
    @SerializedName("Long")
    public String longi;
    //TODO : Assessment
    public float score = -1;

    public String photoId = "NA";

    public String signatureId = "NA";

    @Ignore
    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    @Ignore
    public boolean isselected;

    @Ignore
    public String header;

    @Ignore
    @SerializedName("EmployeeId")
    public String empId;

    @Ignore
    @SerializedName("FirstName")
    public String empName;
    @Ignore
    @SerializedName("TotalCourseCompleted")
    public int totalCourseCompleted;
    @Ignore
    @SerializedName("TotalCourseAssigned")
    public int totalCourseAssigned;
    @Ignore
    @SerializedName("TotalAttemptedAssessment")
    public int totalAttemptedAssessment;
    @Ignore
    @SerializedName("TotalAssessmentAssigned")
    public int totalAssessmentAssigned;
    @Ignore
    @SerializedName("TotalCorrectAttempt")
    public int totalCorrectAttempt;
    @Ignore
    @SerializedName("TotalQuestionAttempted")
    public int totalQuestionAttempted;
    @Ignore
    @SerializedName("TotalQuestionCount")
    public int totalQuestionCount;
    @Ignore
    @SerializedName("TotalMarks")
    public int totalMarks;
    @Ignore
    @SerializedName("Score")
    public int finalScore;
    @Ignore
    @SerializedName("CourseId")
    public int courseId;
    @Ignore
    @SerializedName("CourseName")
    public String courseName;
    @Ignore
    @SerializedName("TotalAccessedTopic")
    public int totalAccessedTopic;
    @Ignore
    @SerializedName("TotalTopicAssigned")
    public int totalTopicAssigned;

}


