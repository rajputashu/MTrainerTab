package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

public class ImageUploadRequest {


    @SerializedName("TraningId")
    public String trainingid;

    @SerializedName("PictureTypeId")
    public String picturetypeid;


    @SerializedName("PictureId")
    public String pictureid;

    @SerializedName("Post")
    public int postId;

    @SerializedName("PictureUrl")
    public String pictureUrl;

}
