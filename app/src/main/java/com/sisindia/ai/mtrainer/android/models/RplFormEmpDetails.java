package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class RplFormEmpDetails {

    @SerializedName("FirstName")
    public String firstName;

    @SerializedName("MiddleName")
    public String middleName;

    @SerializedName("LastName")
    public String lastName;

    @SerializedName("SiteId")
    public String SiteId;

    @SerializedName("BranchId")
    public String BranchId;

    @SerializedName("SiteName")
    public String siteName;

    @SerializedName("BranchName")
    public String branchName;




    public String getSiteId() {
        return SiteId;
    }

    public void setSiteId(String siteId) {
        SiteId = siteId;
    }

    public String getBranchId() {
        return BranchId;
    }

    public void setBranchId(String branchId) {
        BranchId = branchId;
    }




    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
