package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostItem  implements Serializable{
    @SerializedName("postId")
    public int postId;
    @SerializedName("postName")
    public String postName;
}