package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class UnitsData {

    @SerializedName("UnitName")
    String unitName;

    @SerializedName("UnitCode")
    String unitCode;

    @SerializedName("UnitId")
    int unitId;

    @SerializedName("SPI")
    int spi;

    @SerializedName("IsUmbrellaInstalled")
    int isUmbrellaInstalled;

    @SerializedName("Umbrella")
    int umbrella;

    @SerializedName("SPICount")
    int spiCount;

    @SerializedName("SPIMountTarget")
    int spiMountTarget;

    @SerializedName("SPIPostCovered")
    int spiPostCovered;

    @SerializedName("UmbrellaInstallTarget")
    int umbrellaInstallTarget;

    @SerializedName("UmbrellaPostCovered")
    int umbrellaPostCovered;

    @SerializedName("UmbrellaReceived")
    int umbrellaReceived;

    @SerializedName("IsUmbrellDamage")
    int isUmbrellaDamage;

    @SerializedName("UmbrellDamageCount")
    int umbrellaDamageCount;

    @SerializedName("UnitType")
    String unitType;


    @SerializedName("SectorId")
    int sectorId;

    @SerializedName("SectorName")
    String sectorName;

    @SerializedName("IsDisbanded")
    String isDisbanded;

    public UnitsData() {
    }

    public String getIsDisbanded() {
        return isDisbanded;
    }

    public void setIsDisbanded(String isDisbanded) {
        this.isDisbanded = isDisbanded;
    }

    public int getUmbrellaReceived() {
        return umbrellaReceived;
    }

    public void setUmbrellaReceived(int umbrellaReceived) {
        this.umbrellaReceived = umbrellaReceived;
    }

    public int getIsUmbrellaDamage() {
        return isUmbrellaDamage;
    }

    public void setIsUmbrellaDamage(int isUmbrellaDamage) {
        this.isUmbrellaDamage = isUmbrellaDamage;
    }

    public int getUmbrellaDamageCount() {
        return umbrellaDamageCount;
    }

    public void setUmbrellaDamageCount(int umbrellaDamageCount) {
        this.umbrellaDamageCount = umbrellaDamageCount;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getSpi() {
        return spi;
    }

    public int getUmbrellaPostCovered() {
        return umbrellaPostCovered;
    }

    public void setUmbrellaPostCovered(int umbrellaPostCovered) {
        this.umbrellaPostCovered = umbrellaPostCovered;
    }

    public int getIsUmbrellaInstalled() {
        return isUmbrellaInstalled;
    }

    public void setIsUmbrellaInstalled(int isUmbrellaInstalled) {
        this.isUmbrellaInstalled = isUmbrellaInstalled;
    }

    public int getSpiMountTarget() {
        return spiMountTarget;
    }

    public void setSpiMountTarget(int spiMountTarget) {
        this.spiMountTarget = spiMountTarget;
    }

    public int getSpiPostCovered() {
        return spiPostCovered;
    }

    public void setSpiPostCovered(int spiPostCovered) {
        this.spiPostCovered = spiPostCovered;
    }

    public int getUmbrellaInstallTarget() {
        return umbrellaInstallTarget;
    }

    public void setUmbrellaInstallTarget(int umbrellaInstallTarget) {
        this.umbrellaInstallTarget = umbrellaInstallTarget;
    }

    public void setSpi(int spi) {
        this.spi = spi;
    }

    public int getUmbrella() {
        return umbrella;
    }

    public void setUmbrella(int umbrella) {
        this.umbrella = umbrella;
    }

    public int getSpiCount() {
        return spiCount;
    }

    public void setSpiCount(int spiCount) {
        this.spiCount = spiCount;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }


    public int getSectorId() {
        return sectorId;
    }

    public void setSectorId(int sectorId) {
        this.sectorId = sectorId;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }
}
