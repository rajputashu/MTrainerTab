package com.sisindia.ai.mtrainer.android.models.assesments;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class ProgramResponseV2 extends BaseApiResponse {

    @SerializedName("Data")
    public List<ProgramDataV2> programDataList;

    @Parcel
    public static class ProgramDataV2 {

        @SerializedName("ProgramId")
        public int programId;

        @SerializedName("ProgramName")
        public String programName;
    }
}