package com.sisindia.ai.mtrainer.android.models.sync;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ClientHandshakeDetails{

	@SerializedName("ClientName")
	private String clientName;

	@SerializedName("Feedback")
	private List<String> feedback;

	@SerializedName("ClientEmailId")
	private String clientEmailId;

	@SerializedName("Rating")
	private String rating;

	@SerializedName("ClientID")
	private String clientID;

	@SerializedName("ClientNotAvailableReason")
	private String clientNotAvailableReason;

	@SerializedName("ClientMobNo")
	private String clientMobNo;

	@SerializedName("ClientOtpVerify")
	private String clientOtpVerify;

	@SerializedName("ClientAvailable")
	private String clientAvailable;
	@SerializedName("Remarks")
	private String remarks;

	public void setClientName(String clientName){
		this.clientName = clientName;
	}

	public void setFeedback(List<String> feedback){
		this.feedback = feedback;
	}

	public void setClientEmailId(String clientEmailId){
		this.clientEmailId = clientEmailId;
	}

	public void setRating(String rating){
		this.rating = rating;
	}

	public void setClientID(String clientID){
		this.clientID = clientID;
	}

	public void setClientNotAvailableReason(String clientNotAvailableReason){
		this.clientNotAvailableReason = clientNotAvailableReason;
	}

	public void setClientMobNo(String clientMobNo){
		this.clientMobNo = clientMobNo;
	}

	public void setClientOtpVerify(String clientOtpVerify){
		this.clientOtpVerify = clientOtpVerify;
	}

	public void setClientAvailable(String clientAvailable){
		this.clientAvailable = clientAvailable;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}