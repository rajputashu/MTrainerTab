package com.sisindia.ai.mtrainer.android.models.gps;

import com.google.gson.annotations.SerializedName;

public class GpsResponse{

	@SerializedName("StatusId")
	private int statusId;

	@SerializedName("DataTime")
	private String dataTime;

	@SerializedName("StatusCode")
	private int statusCode;

	@SerializedName("Key")
	private int key;

	@SerializedName("StatusMessage")
	private String statusMessage;

	public void setStatusId(int statusId){
		this.statusId = statusId;
	}

	public int getStatusId(){
		return statusId;
	}

	public void setDataTime(String dataTime){
		this.dataTime = dataTime;
	}

	public String getDataTime(){
		return dataTime;
	}

	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}

	public int getStatusCode(){
		return statusCode;
	}

	public void setKey(int key){
		this.key = key;
	}

	public int getKey(){
		return key;
	}

	public void setStatusMessage(String statusMessage){
		this.statusMessage = statusMessage;
	}

	public String getStatusMessage(){
		return statusMessage;
	}

	@Override
 	public String toString(){
		return 
			"GpsResponse{" + 
			"statusId = '" + statusId + '\'' + 
			",dataTime = '" + dataTime + '\'' + 
			",statusCode = '" + statusCode + '\'' + 
			",key = '" + key + '\'' + 
			",statusMessage = '" + statusMessage + '\'' + 
			"}";
		}
}