package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class VersionCheckResponse{

	@SerializedName("VersionInfo")
	private VersionInfo versionInfo;

	@SerializedName("statusMessage")
	private String statusMessage;

	@SerializedName("statusCode")
	private int statusCode;

	public void setVersionInfo(VersionInfo versionInfo){
		this.versionInfo = versionInfo;
	}

	public VersionInfo getVersionInfo(){
		return versionInfo;
	}

	public void setStatusMessage(String statusMessage){
		this.statusMessage = statusMessage;
	}

	public String getStatusMessage(){
		return statusMessage;
	}

	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}

	public int getStatusCode(){
		return statusCode;
	}
}