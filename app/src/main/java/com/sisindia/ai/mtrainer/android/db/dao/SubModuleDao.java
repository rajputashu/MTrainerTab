package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.SubModules;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface SubModuleDao {

/*
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertSubModule (List<SubModules> videoDetailsModels);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertSubModule(List<SubModules.MySubModuleslist> videoDetailsModels);


    @Query("SELECT COUNT(id) FROM sub_modules_table")
    public int getSubModuleCount();

    @Query("DELETE from sub_modules_table WHERE id = :id")
    Completable deleteSubModule(String id);

    @Query("SELECT * from sub_modules_table WHERE id=:id and ModuleNo=:moduleno ")
    List<SubModules.MySubModuleslist> getVideos(String id, String moduleno);

    @Query("SELECT * from sub_modules_table")
    List<SubModules.MySubModuleslist> getSubModules();


    @Query("SELECT * from sub_modules_table WHERE ModuleNo=:id")
    List<SubModules.MySubModuleslist> getSubModules1(String id);

}
