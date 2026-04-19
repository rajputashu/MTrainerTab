package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class TrainingCourseUpdateRequest {

    @SerializedName("CompanyId")
    int companyId;

    @SerializedName("UserId")
    int userId;

    @SerializedName("SessionId")
    String sessionId;

    @SerializedName("CourseId")
    int courseId;

    @SerializedName("TopicId")
    int topicId;

    @SerializedName("ContentId")
    int contentId;

    @SerializedName("Status")
    int status;

    @SerializedName("StartTime")
    String startTime;

    @SerializedName("LastSeen")
    String lastSeen;

    @SerializedName("SyncDateTime")
    String syncDateTime;

    public TrainingCourseUpdateRequest(int companyId, int userId, String sessionId, int courseId, int topicId, int contentId, int status, String startTime, String lastSeen, String syncDateTime) {
        this.companyId = companyId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.courseId = courseId;
        this.topicId = topicId;
        this.contentId = contentId;
        this.status = status;
        this.startTime = startTime;
        this.lastSeen = lastSeen;
        this.syncDateTime = syncDateTime;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getSyncDateTime() {
        return syncDateTime;
    }

    public void setSyncDateTime(String syncDateTime) {
        this.syncDateTime = syncDateTime;
    }
}
