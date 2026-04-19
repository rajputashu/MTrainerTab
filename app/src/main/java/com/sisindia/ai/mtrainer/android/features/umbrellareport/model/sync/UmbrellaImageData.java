package com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync;

import com.google.gson.annotations.SerializedName;

public class UmbrellaImageData{

	@SerializedName("isAdhocPost")
	private int isAdhocPost;

	@SerializedName("ImageId")
	private String imageId;

	@SerializedName("KeyId")
	private int keyId;

	@SerializedName("Imageurl")
	private String imageurl;

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

	public void setKeyId(int keyId){
		this.keyId = keyId;
	}

	public int getKeyId(){
		return keyId;
	}

	public void setImageurl(String imageurl){
		this.imageurl = imageurl;
	}

	public String getImageurl(){
		return imageurl;
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
			"UmbrellaImageData{" + 
			"isAdhocPost = '" + isAdhocPost + '\'' + 
			",imageId = '" + imageId + '\'' + 
			",keyId = '" + keyId + '\'' + 
			",imageurl = '" + imageurl + '\'' + 
			",postName = '" + postName + '\'' + 
			",postId = '" + postId + '\'' + 
			"}";
		}
}