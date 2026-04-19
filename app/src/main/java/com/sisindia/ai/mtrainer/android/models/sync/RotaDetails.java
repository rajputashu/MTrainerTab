package com.sisindia.ai.mtrainer.android.models.sync;

import com.google.gson.annotations.SerializedName;

public class RotaDetails{

	@SerializedName("CompanyId")
	private String companyId;

	@SerializedName("RotaId")
	private String rotaId;

	@SerializedName("ActualTraineeCount")
	private String actualTraineeCount;

	public void setCompanyId(String companyId){
		this.companyId = companyId;
	}

	public void setRotaId(String rotaId){
		this.rotaId = rotaId;
	}
	public  void setActualTraineeCount(String actualTraineeCount)
	{
		this.actualTraineeCount=actualTraineeCount;
	}
}