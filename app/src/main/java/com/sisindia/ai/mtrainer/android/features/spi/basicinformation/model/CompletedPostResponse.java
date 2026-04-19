package com.sisindia.ai.mtrainer.android.features.spi.basicinformation.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CompletedPostResponse{

	@SerializedName("Data")
	private List<CompletedDataItem> data;

	@SerializedName("statusMessage")
	private String statusMessage;

	@SerializedName("statusCode")
	private int statusCode;

	public void setData(List<CompletedDataItem> data){
		this.data = data;
	}

	public List<CompletedDataItem> getData(){
		return data;
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

	@Override
 	public String toString(){
		return 
			"CompletedPostResponse{" + 
			"data = '" + data + '\'' + 
			",statusMessage = '" + statusMessage + '\'' + 
			",statusCode = '" + statusCode + '\'' + 
			"}";
		}
}