package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class SaveTokenRequest {

    @SerializedName("LoginCode")
    String loginCode;

    @SerializedName("UserName")
    String userName;

    @SerializedName("Password")
    String password;

    @Override
    public String toString() {
        return "SaveTokenRequest{" +
                "loginCode='" + loginCode + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
