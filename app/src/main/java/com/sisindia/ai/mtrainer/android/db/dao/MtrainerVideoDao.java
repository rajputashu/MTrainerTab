package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.VideoClick;
import com.sisindia.ai.mtrainer.android.features.trainingkit.VideoDetailsModel;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface MtrainerVideoDao {
/*

    @Query("SELECT * from `video_model_table ` ")
    List<VideoDetailsModel> getVideoModuleCount();
*/

    @Query("SELECT COUNT(id) FROM video_model_table ")
     int   getVideoModuleCount();

    @Query("SELECT * from video_model_table")
    List<VideoDetailsModel.VideoDetailsModellist> getTrainingVideo();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertVideoModule(List<VideoDetailsModel.VideoDetailsModellist> videoDetailsModels);

    @Query("SELECT * from video_model_table WHERE SubModuleNo=:moduleno and ModuleNo=:id")
    List<VideoDetailsModel.VideoDetailsModellist> getVideos(String moduleno, String id);

/*
 @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertModule (List<MyModule> videoDetailsModels);
*/

/*

    @Query("SELECT * from sub_modules_table ")
    List<SubModules> getSubModuleCount();
*/

/*
    @Query("SELECT * FROM modu_table WHERE id =id")
    List<ModuleEntity> getModuleCount();
    */
  /* @Query("SELECT * FROM modu_table")
    List<MyModule> getModuleCount();
*/
/*
    @Query("SELECT  COUNT (*) FROM `module_table`")
    public  int  getModuleCount();
    */

    @Query("DELETE from video_model_table WHERE id = :id")
    Completable deleteVideoModule(String id);

    @Query("SELECT * from video_model_table WHERE id=:id")
    List<VideoDetailsModel.VideoDetailsModellist> getSubModules(String id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertVideoClick (VideoClick videoClick);

}
