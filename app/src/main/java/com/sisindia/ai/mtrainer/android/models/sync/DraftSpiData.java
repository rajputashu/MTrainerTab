package com.sisindia.ai.mtrainer.android.models.sync;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DraftSpiData {
    @SerializedName("SpiId")
    public int spiId;

    @SerializedName("TrainerId")
    public int trainerId;
    @SerializedName("Details")
    public List<DraftSpiPicturesItems> draftSpiPicturesItems;

    public int getSpiId() {
        return spiId;
    }

    public void setSpiId(int spiId) {
        this.spiId = spiId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public List<DraftSpiPicturesItems> getDraftSpiPicturesItems() {
        return draftSpiPicturesItems;
    }

    public void setDraftSpiPicturesItems(List<DraftSpiPicturesItems> draftSpiPicturesItems) {
        this.draftSpiPicturesItems = draftSpiPicturesItems;
    }


}
