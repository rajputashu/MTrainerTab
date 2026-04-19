package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName="rota_task_table")
public class MtrainerRotaTaskEntity {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    public int id;

    @SerializedName("IsSynced")
    public boolean isSynced = false;

    @SerializedName("SiteId")
    public int siteId;

    @SerializedName("SiteName")
    public String siteName;

    @SerializedName("SiteAddress")
    public String siteAddress;

    @SerializedName("ReasonText")
    public String reasonText;

    @SerializedName("ReasonId")
    public int reasonId;

    @SerializedName("ActualTaskExecutionStartDateTime")
    public String actualTaskExecutionStartDateTime;

    @SerializedName("ActualTaskExecutionEndDateTime")
    public String actualTaskExecutionEndDateTime;

    @SerializedName("EstimatedTaskExecutionStartDateTime")
    public String estimatedTaskExecutionStartDateTime;

    @SerializedName("EstimatedTaskExecutionEndDateTime")
    public String estimatedTaskExecutionEndDateTime;


    @SerializedName("TaskStatus")
    public int taskStatus;

    @SerializedName("TaskExecutionResult")
    public String taskExecutionResult;

    public MtrainerRotaTaskEntity() {
    }

    public enum TaskStatus {

        CREATED(1),
        IN_PROGRESS(2),
        IN_ACTIVE(3),
        COMPLETED(4);

        private final int taskStatus;

        TaskStatus(int taskStatus) {
            this.taskStatus = taskStatus;
        }

        public int getTaskStatus() {
            return taskStatus;
        }
    }
}
