package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class UpdateSite {


    @SerializedName("TrainerId")
    public int trainerId;

    @SerializedName("BranchId")
    public int branchId;

    @SerializedName("UnitId")
    public int unitId;

    @SerializedName("SPI")
    public int spi;

    @SerializedName("Umbrella")
    public int umbrella;

    @SerializedName("SPICount")
    public int spiCount;

    @SerializedName("UnitType")
    public String unitType;

    @SerializedName("IsUmbrellaInstalled")
    public int isUmbrellaInstalled;

    @SerializedName("SPIMountTarget")
    public int spiMountTarget;

    @SerializedName("SPIPostCovered")
    public int spiPostCovered;

    @SerializedName("UmbrellaInstallTarget")
    public int umbrellaInstallTarget;

    @SerializedName("UmbrellaPostCovered")
    public int umbrellaPostCovered;

    @SerializedName("UmbrellaReceived")
    public int umbrellaReceived;

    @SerializedName("IsUmbrellaDamage")
    public int isUmbrellaDamage;

    @SerializedName("UmbrellaDamageCount")
    public int umbrellaDamageCount;
    @SerializedName("SectorId")
    public int sectorId;

    @SerializedName("SectorName")
    public String sectorName;

    @SerializedName("IsDisbanded")
    public int isDisbanded;

    public UpdateSite() {
    }

    public UpdateSite(int trainerId, int branchId, int unitId, int spi, int umbrella, int spiCount, String unitType, int isUmbrellaInstalled, int spiMountTarget, int spiPostCovered, int umbrellaInstallTarget, int umbrellaPostCovered, int umbrellaReceived, int isUmbrellaDamage, int umbrellaDamageCount, int sectorId,String sectorName,int isDisbanded ) {
        this.trainerId = trainerId;
        this.branchId = branchId;
        this.unitId = unitId;
        this.spi = spi;
        this.umbrella = umbrella;
        this.spiCount = spiCount;
        this.unitType = unitType;
        this.isUmbrellaInstalled = isUmbrellaInstalled;
        this.spiMountTarget = spiMountTarget;
        this.spiPostCovered = spiPostCovered;
        this.umbrellaInstallTarget = umbrellaInstallTarget;
        this.umbrellaPostCovered = umbrellaPostCovered;
        this.umbrellaReceived = umbrellaReceived;
        this.isUmbrellaDamage = isUmbrellaDamage;
        this.umbrellaDamageCount = umbrellaDamageCount;
        this.sectorId = sectorId;
        this.sectorName = sectorName;
        this.isDisbanded = isDisbanded;
    }
}
