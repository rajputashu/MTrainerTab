package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

public class MyModule extends BaseApiResponse {
    @SerializedName("Data")
    public List<MyModuleslist> myModuleslists;

    public MyModule(){

    }


    @Parcel
    @Entity(tableName = "modu_table")
    public static class MyModuleslist  implements Serializable {
        @PrimaryKey(autoGenerate = true)
        @NonNull
        @ColumnInfo(name = "id")
        private int id;

        @ColumnInfo(name = "ModuleNo")
        private String ModuleNo;

        @ColumnInfo(name = "HindiName")
        private String HindiName;

        @ColumnInfo(name = "EnglishName")
        private String EnglishName;


        public int getId() {
            return id;
        }

        public void setId(@NonNull int id) {
            this.id = id;
        }

        public String getModuleNo() {
            return ModuleNo;
        }

        public void setModuleNo(String moduleNo) {
            ModuleNo = moduleNo;
        }

        public String getHindiName() {
            return HindiName;
        }

        public void setHindiName(String hindiName) {
            HindiName = hindiName;
        }

        public String getEnglishName() {
            return EnglishName;
        }

        public void setEnglishName(String englishName) {
            EnglishName = englishName;
        }
    }
}
