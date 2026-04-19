package com.sisindia.ai.mtrainer.android.models.previous;

import com.google.gson.annotations.SerializedName;

public class DetailsItem{

	@SerializedName("Post")
	private String post;

	@SerializedName("Image")
	private String image;

	@SerializedName("Attendance")
	private int attendence;

	public void setPost(String post){
		this.post = post;
	}

	public String getPost(){
		return post;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setAttendence(int attendence){
		this.attendence = attendence;
	}

	public int getAttendence(){
		return attendence;
	}
}