package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Adhoc_saved_employee_table")
public class AdhocEmployeesSaved {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String employeeName;
    public String employeeCode;
    public int rotaId;

    public String getEmployeeName() {
        return employeeName;
    }
    public String getEmployeeCode(){
        return employeeCode;
    }


    public void setEmployeeCode(String empcode) {
        this.employeeCode = empcode;
    }
    public void setEmployeeName(String topicName) {
        this.employeeName = topicName;
    }
    public int getRotaId() {
        return rotaId;
    }

    public void setRotaId(int rotaId) {
        this.rotaId = rotaId;
    }


}
