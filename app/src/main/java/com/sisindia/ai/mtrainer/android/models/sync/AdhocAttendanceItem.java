package com.sisindia.ai.mtrainer.android.models.sync;

import com.google.gson.annotations.SerializedName;

public class AdhocAttendanceItem{

	@SerializedName("PhotoId")
	private String photoId;

	@SerializedName("SignatureId")
	private String signatureId;

	@SerializedName("Post")
	private String post;

	@SerializedName("EmpCode")
	private String empCode;

	@SerializedName("EmpName")
	private String empName;

	public void setPhotoId(String photoId){
		this.photoId = photoId;
	}

	public String getPhotoId(){
		return photoId;
	}

	public void setSignatureId(String signatureId){
		this.signatureId = signatureId;
	}

	public String getSignatureId(){
		return signatureId;
	}

	public void setPost(String post){
		this.post = post;
	}

	public String getPost(){
		return post;
	}

	public void setEmpCode(String empCode){
		this.empCode = empCode;
	}

	public String getEmpCode(){
		return empCode;
	}

	public void setEmpName(String empName){
		this.empName = empName;
	}

	public String getEmpName(){
		return empName;
	}

	@Override
 	public String toString(){
		return 
			"AdhocAttendanceItem{" + 
			"photoId = '" + photoId + '\'' + 
			",signatureId = '" + signatureId + '\'' + 
			",post = '" + post + '\'' + 
			",empCode = '" + empCode + '\'' + 
			",empName = '" + empName + '\'' + 
			"}";
		}
}