package com.sisindia.ai.mtrainer.android.features.spi.model;

import com.google.gson.annotations.SerializedName;

public class DataItem{

	@SerializedName("TrainerId")
	private int trainerId;

	@SerializedName("Type")
	private int type;

	@SerializedName("UnitId")
	private int unitId;

	@SerializedName("CompanyId")
	private int companyId;

	@SerializedName("AllPostsVisited")
	private int allPostsVisited;

	@SerializedName("PostCount")
	private int postCount;

	@SerializedName("VerifiedPostCountWithAreaOfficer")
	private int verifiedPostCountWithAreaOfficer;

	@SerializedName("KeyId")
	private int keyId;

	@SerializedName("CustomerId")
	private int customerId;

	@SerializedName("BranchId")
	private int branchId;

	@SerializedName("MissingPostsCreatedbyTrainer")
	private int missingPostsCreatedbyTrainer;

	@SerializedName("AllPostsWereAvailableInIOPS")
	private int allPostsWereAvailableInIOPS;

	public void setTrainerId(int trainerId){
		this.trainerId = trainerId;
	}

	public int getTrainerId(){
		return trainerId;
	}

	public void setType(int type){
		this.type = type;
	}

	public int getType(){
		return type;
	}

	public void setUnitId(int unitId){
		this.unitId = unitId;
	}

	public int getUnitId(){
		return unitId;
	}

	public void setCompanyId(int companyId){
		this.companyId = companyId;
	}

	public int getCompanyId(){
		return companyId;
	}

	public void setAllPostsVisited(int allPostsVisited){
		this.allPostsVisited = allPostsVisited;
	}

	public int getAllPostsVisited(){
		return allPostsVisited;
	}

	public void setPostCount(int postCount){
		this.postCount = postCount;
	}

	public int getPostCount(){
		return postCount;
	}

	public void setVerifiedPostCountWithAreaOfficer(int verifiedPostCountWithAreaOfficer){
		this.verifiedPostCountWithAreaOfficer = verifiedPostCountWithAreaOfficer;
	}

	public int getVerifiedPostCountWithAreaOfficer(){
		return verifiedPostCountWithAreaOfficer;
	}

	public void setKeyId(int keyId){
		this.keyId = keyId;
	}

	public int getKeyId(){
		return keyId;
	}

	public void setCustomerId(int customerId){
		this.customerId = customerId;
	}

	public int getCustomerId(){
		return customerId;
	}

	public void setBranchId(int branchId){
		this.branchId = branchId;
	}

	public int getBranchId(){
		return branchId;
	}

	public void setMissingPostsCreatedbyTrainer(int missingPostsCreatedbyTrainer){
		this.missingPostsCreatedbyTrainer = missingPostsCreatedbyTrainer;
	}

	public int getMissingPostsCreatedbyTrainer(){
		return missingPostsCreatedbyTrainer;
	}

	public void setAllPostsWereAvailableInIOPS(int allPostsWereAvailableInIOPS){
		this.allPostsWereAvailableInIOPS = allPostsWereAvailableInIOPS;
	}

	public int getAllPostsWereAvailableInIOPS(){
		return allPostsWereAvailableInIOPS;
	}

	@Override
 	public String toString(){
		return 
			"DataItem{" + 
			"trainerId = '" + trainerId + '\'' + 
			",type = '" + type + '\'' + 
			",unitId = '" + unitId + '\'' + 
			",companyId = '" + companyId + '\'' + 
			",allPostsVisited = '" + allPostsVisited + '\'' + 
			",postCount = '" + postCount + '\'' + 
			",verifiedPostCountWithAreaOfficer = '" + verifiedPostCountWithAreaOfficer + '\'' + 
			",keyId = '" + keyId + '\'' + 
			",customerId = '" + customerId + '\'' + 
			",branchId = '" + branchId + '\'' + 
			",missingPostsCreatedbyTrainer = '" + missingPostsCreatedbyTrainer + '\'' + 
			",allPostsWereAvailableInIOPS = '" + allPostsWereAvailableInIOPS + '\'' + 
			"}";
		}
}