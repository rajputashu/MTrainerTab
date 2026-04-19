package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class VersionInfo{

	@SerializedName("VersionDescription")
	private String versionDescription;

	@SerializedName("PublishedDateTime")
	private String publishedDateTime;

	@SerializedName("VersionNo")
	private String versionNo;

	@SerializedName("Id")
	private int id;

	@SerializedName("ApkFileName")
	private String apkFileName;

	@SerializedName("ApkFileHostedUrl")
	private String apkFileHostedUrl;

	public void setVersionDescription(String versionDescription){
		this.versionDescription = versionDescription;
	}

	public String getVersionDescription(){
		return versionDescription;
	}

	public void setPublishedDateTime(String publishedDateTime){
		this.publishedDateTime = publishedDateTime;
	}

	public String getPublishedDateTime(){
		return publishedDateTime;
	}

	public void setVersionNo(String versionNo){
		this.versionNo = versionNo;
	}

	public String getVersionNo(){
		return versionNo;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setApkFileName(String apkFileName){
		this.apkFileName = apkFileName;
	}

	public String getApkFileName(){
		return apkFileName;
	}

	public void setApkFileHostedUrl(String apkFileHostedUrl){
		this.apkFileHostedUrl = apkFileHostedUrl;
	}

	public String getApkFileHostedUrl(){
		return apkFileHostedUrl;
	}
}