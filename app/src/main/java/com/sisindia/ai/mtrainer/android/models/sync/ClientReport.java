package com.sisindia.ai.mtrainer.android.models.sync;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ClientReport{

	@SerializedName("CC")
	private List<String> cC;

	@SerializedName("To")
	private List<String> to;

	public void setCC(List<String> cC){
		this.cC = cC;
	}

	public List<String> getCC(){
		return cC;
	}

	public void setTo(List<String> to){
		this.to = to;
	}

	public List<String> getTo(){
		return to;
	}
}