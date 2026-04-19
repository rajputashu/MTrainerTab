package com.sisindia.ai.mtrainer.android.models.sync;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.db.entities.AdhocSavedTopics;
import com.sisindia.ai.mtrainer.android.models.AdhoctopicItem;

public class UserData{

	@SerializedName("TrainingAttendance")
	private List<TrainingAttendanceItem> trainingAttendance;

	@SerializedName("Startlatitude")
	private String startLatitude;

	@SerializedName("Startlongitude")
	private String startLongitude;

	@SerializedName("Endlatitude")
	private String endLatitude;

	@SerializedName("Endlongitude")
	private String endLongitude;

	@SerializedName("TrainingPictures")
	private List<TrainingPicturesItem> trainingPictures;

	@SerializedName("RotaDetails")
	private RotaDetails rotaDetails;

	@SerializedName("SelectedTopicsID")
	private List<Integer> selectedTopicsID;

	@SerializedName("ClientHandshakeDetails")
	private ClientHandshakeDetails clientHandshakeDetails;

	@SerializedName("ClientReport")
	private ClientReport clientReport;

	@SerializedName("Assessment")
	private List<AssessmentReport> assessment;

	@SerializedName("AdhocTopics")
	private List<String> adhocSavedTopics;


	@SerializedName("TrainingAdhocEmployee")
	private List<AdhoctopicItem> trainingAdhocEmployee;

	@SerializedName("AdhocAttendance")
	private List<AdhocAttendanceItem> adhocAttendance;

	@SerializedName("AdhocAssessment")
	private List<AdhocAssessmentItem> adhocAssessment;

	public List<AdhocAssessmentItem> getAdhocAssessment(){
		return adhocAssessment;
	}

	public void setAdhocAssessment(List<AdhocAssessmentItem> adhocAssessment) {
		this.adhocAssessment = adhocAssessment;
	}

	public void setAdhocAttendance(List<AdhocAttendanceItem> adhocAttendance){
		this.adhocAttendance = adhocAttendance;
	}

	public List<AdhocAttendanceItem> getAdhocAttendance(){
		return adhocAttendance;
	}



	public void setTrainingAttendance(List<TrainingAttendanceItem> trainingAttendance){
		this.trainingAttendance = trainingAttendance;
	}

	public void setStartLatitude(String startLatitude){
		this.startLatitude = startLatitude;
	}

	public void setTrainingPictures(List<TrainingPicturesItem> trainingPictures){
		this.trainingPictures = trainingPictures;
	}

	public void setRotaDetails(RotaDetails rotaDetails){
		this.rotaDetails = rotaDetails;
	}

	public void setSelectedTopicsID(List<Integer> selectedTopicsID){
		this.selectedTopicsID = selectedTopicsID;
	}

	public void setStartLongitude(String startLongitude){
		this.startLongitude = startLongitude;
	}

	public void setClientHandshakeDetails(ClientHandshakeDetails clientHandshakeDetails){
		this.clientHandshakeDetails = clientHandshakeDetails;
	}

	public void setClientReport(ClientReport clientReport){
		this.clientReport = clientReport;
	}

	public void setEndLatitude(String endLatitude) {
		this.endLatitude = endLatitude;
	}

	public void setEndLongitude(String endLongitude) {
		this.endLongitude = endLongitude;
	}

	public void setAssessment(List<AssessmentReport> assessment) {
		this.assessment = assessment;
	}

	public List<AssessmentReport> getAssessment() {
		return assessment;
	}

	public void setadhocSavedTopics(List<String> adhocSavedTopics) {
		this.adhocSavedTopics = adhocSavedTopics;
	}

	public List<String> getAdhocSavedTopics() {
		return adhocSavedTopics;
	}

	public void setTrainingAdhocEmployee(List<AdhoctopicItem> adhocSavedEmp) {
		this.trainingAdhocEmployee = adhocSavedEmp;
	}

	/*public List<String> getTrainingAdhocEmployee() {
		return trainingAdhocEmployee;
	}*/
}