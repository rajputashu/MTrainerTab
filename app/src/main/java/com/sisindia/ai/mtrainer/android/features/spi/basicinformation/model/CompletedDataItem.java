package com.sisindia.ai.mtrainer.android.features.spi.basicinformation.model;

import com.google.gson.annotations.SerializedName;

public class CompletedDataItem {

	@SerializedName("KeyId")
	private int keyId;

	@SerializedName("PostName")
	private String postName;

	@SerializedName("PostId")
	private int postId;

	public void setKeyId(int keyId){
		this.keyId = keyId;
	}

	public int getKeyId(){
		return keyId;
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
			"DataItem{" + 
			"keyId = '" + keyId + '\'' + 
			",postName = '" + postName + '\'' + 
			",postId = '" + postId + '\'' + 
			"}";
		}
}