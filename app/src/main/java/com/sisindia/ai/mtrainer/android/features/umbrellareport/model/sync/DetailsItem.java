package com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync;

import com.google.gson.annotations.SerializedName;

public class DetailsItem{

	@SerializedName("isAdhocPost")
	private int isAdhocPost;

	@SerializedName("ImageId")
	private String imageId;

	@SerializedName("PostName")
	private String postName;

	@SerializedName("PostId")
	private int postId;

	public void setIsAdhocPost(int isAdhocPost){
		this.isAdhocPost = isAdhocPost;
	}

	public int getIsAdhocPost(){
		return isAdhocPost;
	}

	public void setImageId(String imageId){
		this.imageId = imageId;
	}

	public String getImageId(){
		return imageId;
	}

	public void setPostName(String postName){
		this.postName = postName;
	}

	public String getPostName(){
		return postName;
	}

	public void setPostId(int postId){
		this.postId = postId;
	}

	public int getPostId(){
		return postId;
	}

	@Override
 	public String toString(){
		return 
			"DetailsItem{" + 
			"isAdhocPost = '" + isAdhocPost + '\'' + 
			",imageId = '" + imageId + '\'' + 
			",postName = '" + postName + '\'' + 
			",postId = '" + postId + '\'' + 
			"}";
		}
}