package com.sisindia.ai.mtrainer.android.models.sync;

import com.google.gson.annotations.SerializedName;

public class TrainingAttendanceItem{

	@SerializedName("PhotoId")
	private String photoId;

	@SerializedName("SignatureId")
	private String signatureId;

	@SerializedName("Post")
	private String post;

	@SerializedName("EmpName")
	private String empName;

	@SerializedName("EmpID")
	private String empID;

	public void setPhotoId(String photoId){
		this.photoId = photoId;
	}

	public void setSignatureId(String signatureId){
		this.signatureId = signatureId;
	}

	public void setPost(String post){
		this.post = post;
	}

	public void setEmpName(String empName){
		this.empName = empName;
	}

	public void setEmpID(String empID){
		this.empID = empID;
	}
}