package com.sisindia.ai.mtrainer.android.models.sync;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdhocAssessmentResponse{

	@SerializedName("AdhocAssessment")
	private List<AdhocAssessmentItem> adhocAssessment;

	public List<AdhocAssessmentItem> getAdhocAssessment(){
		return adhocAssessment;
	}

	public void setAdhocAssessment(List<AdhocAssessmentItem> adhocAssessment) {
		this.adhocAssessment = adhocAssessment;
	}
}