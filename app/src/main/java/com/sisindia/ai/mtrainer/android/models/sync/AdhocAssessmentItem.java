package com.sisindia.ai.mtrainer.android.models.sync;

import com.google.gson.annotations.SerializedName;

public class AdhocAssessmentItem{

	@SerializedName("VideoId")
	private String videoId;

	@SerializedName("EmpCode")
	private String empCode;

	@SerializedName("EmpName")
	private String empName;

	public String getVideoId(){
		return videoId;
	}

	public String getEmpCode(){
		return empCode;
	}

	public String getEmpName(){
		return empName;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}
}