package com.sisindia.ai.mtrainer.android.models.sync;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class TrainingPicturesItem{

	@SerializedName("PictureId")
	private List<String> pictureId;

	@SerializedName("Post")
	private String post;

	public void setPictureId(List<String> pictureId){
		this.pictureId = pictureId;
	}

	public void setPost(String post){
		this.post = post;
	}
}