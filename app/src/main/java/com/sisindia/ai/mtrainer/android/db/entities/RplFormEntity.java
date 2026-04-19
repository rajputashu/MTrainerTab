package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "rpl_form_table")
public class RplFormEntity {

    @PrimaryKey
    @SerializedName("id")
    public int id;
    @SerializedName("rotaid")
    private String rotaid;

    @SerializedName("RegNo")
    private String regNo;
    @SerializedName("UnitName")
    private String unitName;
    @SerializedName("BranchName")
    private String branchName;
    @SerializedName("mobilebNo")
    private String mobilebNo;
    @SerializedName("Category")
    private String category;
    @SerializedName("EducationalQualification")
    private String educationalQualification;
    @SerializedName("Photoid1")
    private String photoid1;
    @SerializedName("Photoid2")
    private String photoid2;
    @SerializedName("Status")
    private String status;
    @SerializedName("Syncdatetime")
    private String syncdatetime;
    @SerializedName("imagepath1")
    private String imagepath1;
    @SerializedName("Imagepath2")
    private String imagepath2;
    @SerializedName("registrationNameET")
    private String registrationNameET;

    @SerializedName("SiteId")
    private String siteId;
    @SerializedName("BranchId")
    private String branchId;


    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getRotaid() {
        return rotaid;
    }

    public void setRotaid(String rotaid) {
        this.rotaid = rotaid;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getMobilebNo() {
        return mobilebNo;
    }

    public void setMobilebNo(String mobilebNo) {
        this.mobilebNo = mobilebNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEducationalQualification() {
        return educationalQualification;
    }

    public void setEducationalQualification(String educationalQualification) {
        this.educationalQualification = educationalQualification;
    }

    public String getPhotoid1() {
        return photoid1;
    }

    public void setPhotoid1(String photoid1) {
        this.photoid1 = photoid1;
    }

    public String getPhotoid2() {
        return photoid2;
    }

    public void setPhotoid2(String photoid2) {
        this.photoid2 = photoid2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSyncdatetime() {
        return syncdatetime;
    }

    public void setSyncdatetime(String syncdatetime) {
        this.syncdatetime = syncdatetime;
    }

    public String getImagepath1() {
        return imagepath1;
    }

    public void setImagepath1(String imagepath1) {
        this.imagepath1 = imagepath1;
    }

    public String getImagepath2() {
        return imagepath2;
    }

    public void setImagepath2(String imagepath2) {
        this.imagepath2 = imagepath2;
    }

    public String getRegistrationNameET() {
        return registrationNameET;
    }

    public void setRegistrationNameET(String registrationNameET) {
        this.registrationNameET = registrationNameET;
    }


}
