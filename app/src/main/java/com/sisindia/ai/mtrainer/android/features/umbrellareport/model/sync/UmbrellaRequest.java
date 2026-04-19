package com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class UmbrellaRequest{

	@SerializedName("TrainerId")
	private int trainerId;

	@SerializedName("SiteId")
	private int siteId;

	@SerializedName("Details")
	private List<DetailsItem> details;

	@SerializedName("CompanyId")
	private int companyId;

	@SerializedName("TotalUmbrella")
	private int totalUmbrella;

	public void setTrainerId(int trainerId){
		this.trainerId = trainerId;
	}

	public int getTrainerId(){
		return trainerId;
	}

	public void setSiteId(int siteId){
		this.siteId = siteId;
	}

	public int getSiteId(){
		return siteId;
	}

	public void setDetails(List<DetailsItem> details){
		this.details = details;
	}

	public List<DetailsItem> getDetails(){
		return details;
	}

	public void setCompanyId(int companyId){
		this.companyId = companyId;
	}

	public int getCompanyId(){
		return companyId;
	}

	public void setTotalUmbrella(int totalUmbrella){
		this.totalUmbrella = totalUmbrella;
	}

	public int getTotalUmbrella(){
		return totalUmbrella;
	}

	@Override
 	public String toString(){
		return 
			"UmbrellaRequest{" + 
			"trainerId = '" + trainerId + '\'' + 
			",siteId = '" + siteId + '\'' + 
			",details = '" + details + '\'' + 
			",companyId = '" + companyId + '\'' + 
			",totalUmbrella = '" + totalUmbrella + '\'' + 
			"}";
		}
}