package com.sisindia.ai.mtrainer.android.models.previous;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataItem{

	@SerializedName("Order")
	private int order;

	@SerializedName("Details")
	private List<DetailsItem> details;

	@SerializedName("TotalTopic")
	private int totalTopic;

	@SerializedName("Rating")
	private String rating;

	@SerializedName("TrainerName")
	private String trainerName;

	@SerializedName("TrainingHours")
	private String trainingHours;

	@SerializedName("Topic")
	private List<String> topic;

	@SerializedName("Date")
	private String date;

	@SerializedName("TrainingType")
	private int trainingType;

	public void setOrder(int order){
		this.order = order;
	}

	public int getOrder(){
		return order;
	}

	public void setDetails(List<DetailsItem> details){
		this.details = details;
	}

	public List<DetailsItem> getDetails(){
		return details;
	}

	public void setTotalTopic(int totalTopic){
		this.totalTopic = totalTopic;
	}

	public int getTotalTopic(){
		return totalTopic;
	}

	public void setRating(String rating){
		this.rating = rating;
	}

	public String getRating(){
		return rating;
	}

	public void setTrainerName(String trainerName){
		this.trainerName = trainerName;
	}

	public String getTrainerName(){
		return trainerName;
	}

	public void setTrainingHours(String trainingHours){
		this.trainingHours = trainingHours;
	}

	public String getTrainingHours(){
		return trainingHours;
	}

	public void setTopic(List<String> topic){
		this.topic = topic;
	}

	public List<String> getTopic(){
		return topic;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setTrainingType(int trainingType){
		this.trainingType = trainingType;
	}

	public int getTrainingType(){
		return trainingType;
	}

}