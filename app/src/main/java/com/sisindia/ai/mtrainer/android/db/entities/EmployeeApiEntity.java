package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "employee_api")
public class EmployeeApiEntity {
    @PrimaryKey
    public int siteId;
    public String lastAccessedTime;
}