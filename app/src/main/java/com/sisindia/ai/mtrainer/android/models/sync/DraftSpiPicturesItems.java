package com.sisindia.ai.mtrainer.android.models.sync;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DraftSpiPicturesItems {

    @SerializedName("ImageUrl")
    public List<String> imageUrl;

    @SerializedName("PostId")
    public int postId;

    public void setImageUrl(List<String> imageUrl){
        this.imageUrl = imageUrl;
    }

    public void setPostId(int postid){
        this.postId = postid;
    }
}
