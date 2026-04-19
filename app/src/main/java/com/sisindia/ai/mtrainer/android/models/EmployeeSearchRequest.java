package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class EmployeeSearchRequest {

	@SerializedName("EmployeeCode")
	private String employeeCode;

	@SerializedName("CompanyId")
	private int companyId;

	public void setEmployeeCode(String employeeCode){
		this.employeeCode = employeeCode;
	}

	public String getEmployeeCode(){
		return employeeCode;
	}

	public void setCompanyId(int companyId){
		this.companyId = companyId;
	}

	public int getCompanyId(){
		return companyId;
	}
}