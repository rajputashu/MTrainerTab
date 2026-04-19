package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.features.trainingkit.VideoDetailsModel;
import com.sisindia.ai.mtrainer.android.models.MLCVideoDetailsModel;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface MtrainerMLCVideoDao {
    @Query("SELECT COUNT(id) FROM mlc_video_model_table ")
    int   getVideoModuleCount();

    @Query("SELECT * from mlc_video_model_table")
    List<MLCVideoDetailsModel.MlcVideoDetailsModellist> getTrainingVideo();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertVideoModule(List<MLCVideoDetailsModel.MlcVideoDetailsModellist> videoDetailsModels);

    @Query("SELECT * from video_model_table WHERE ModuleNo=:id")
    List<MLCVideoDetailsModel.MlcVideoDetailsModellist> getMlcVideos(String id);

    @Query("DELETE from video_model_table")
    Completable deleteVideoModule();
}
