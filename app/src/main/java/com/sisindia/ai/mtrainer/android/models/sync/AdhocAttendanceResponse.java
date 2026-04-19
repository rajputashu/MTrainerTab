package com.sisindia.ai.mtrainer.android.models.sync;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdhocAttendanceResponse{

	@SerializedName("AdhocAttendance")
	private List<AdhocAttendanceItem> adhocAttendance;

	public void setAdhocAttendance(List<AdhocAttendanceItem> adhocAttendance){
		this.adhocAttendance = adhocAttendance;
	}

	public List<AdhocAttendanceItem> getAdhocAttendance(){
		return adhocAttendance;
	}

	@Override
 	public String toString(){
		return 
			"AdhocAttendanceResponse{" + 
			"adhocAttendance = '" + adhocAttendance + '\'' + 
			"}";
		}
}