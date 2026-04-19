package com.sisindia.ai.mtrainer.android.models.gps;

import com.google.gson.annotations.SerializedName;

public class GpsRequest{

	@SerializedName("TrainerId")
	private String trainerId;

	@SerializedName("StatusId")
	private String statusId;

	@SerializedName("Latitude")
	private String latitude;

	@SerializedName("Longitude")
	private String longitude;

	@SerializedName("DateTime")
	private String dateTime;

	@SerializedName("DutyOnOffId")
	private String dutyOnOffId;

	public void setTrainerId(String trainerId){
		this.trainerId = trainerId;
	}

	public String getTrainerId(){
		return trainerId;
	}

	public void setStatusId(String statusId){
		this.statusId = statusId;
	}

	public String getStatusId(){
		return statusId;
	}

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}

	public String getLatitude(){
		return latitude;
	}

	public void setLongitude(String longitude){
		this.longitude = longitude;
	}

	public String getLongitude(){
		return longitude;
	}

	public void setDateTime(String dateTime){
		this.dateTime = dateTime;
	}

	public String getDateTime(){
		return dateTime;
	}

	public void setDutyOnOffId(String dutyOnOffId){
		this.dutyOnOffId = dutyOnOffId;
	}

	public String getDutyOnOffId(){
		return dutyOnOffId;
	}
}