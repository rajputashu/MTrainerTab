package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class AdhoctopicItem {

    String EmployeeCode;
    String AdhocEmployee;
    public String getAdhocEmployee() {
        return AdhocEmployee;
    }
    public String getEmployeeCode(){
        return EmployeeCode;
    }
    public  void setEmployeeCode(String empcode){
        this.EmployeeCode=empcode;
    }

    public void setAdhocEmployee(String emp) {
        this.AdhocEmployee = emp;
    }

}
