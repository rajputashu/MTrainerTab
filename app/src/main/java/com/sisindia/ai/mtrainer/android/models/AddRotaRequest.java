package com.sisindia.ai.mtrainer.android.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AddRotaRequest{

	@SerializedName("SelectedTopicsID")
	private List<Integer> selectedTopicsID;

	@SerializedName("UnitId")
	private String unitId;

	@SerializedName("StartTime")
	private String startTime;

	@SerializedName("EndTime")
	private String endTime;

	@SerializedName("TrainerId")
	private String trainerId;

	@SerializedName("ExpectedTrainee")
	private String expectedTrainee;

	@SerializedName("TrainingTypeId")
	private String trainingTypeId;

	public void setTrainerId(String trainerId){
		this.trainerId = trainerId;
	}

	public String getTrainerId(){
		return trainerId;
	}

	public void setUnitId(String unitId){
		this.unitId = unitId;
	}

	public String getUnitId(){
		return unitId;
	}

	public void setEndTime(String endTime){
		this.endTime = endTime;
	}

	public String getEndTime(){
		return endTime;
	}

	public void setSelectedTopicsID(List<Integer> selectedTopicsID){
		this.selectedTopicsID = selectedTopicsID;
	}

	public List<Integer> getSelectedTopicsID(){
		return selectedTopicsID;
	}

	public void setStartTime(String startTime){
		this.startTime = startTime;
	}

	public String getStartTime(){
		return startTime;
	}

	public void setTrainingTypeId(String trainingTypeId){
		this.trainingTypeId = trainingTypeId;
	}

	public String getTrainingTypeId(){
		return trainingTypeId;
	}

	public void setExpectedTrainee(String expectedTrainee){
		this.expectedTrainee = expectedTrainee;
	}

	public String getExpectedTrainee(){
		return expectedTrainee;
	}
}