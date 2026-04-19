package com.sisindia.ai.mtrainer.android.features.umbrellareport.model;

import com.google.gson.annotations.SerializedName;

public class DataItem{

	@SerializedName("Data")
	private String data;

	public void setData(String data){
		this.data = data;
	}

	public String getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"DataItem{" + 
			"data = '" + data + '\'' + 
			"}";
		}
}