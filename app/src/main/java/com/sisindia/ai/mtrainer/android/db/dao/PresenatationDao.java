package com.sisindia.ai.mtrainer.android.db.dao;


import androidx.room.Dao;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.Presenatation;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface PresenatationDao {
    @Query("SELECT id ,name,videoId from presentation_table")
    List<Presenatation> getTrainingPresentation();

    @Query("SELECT * from presentation_table ")
    List<Presenatation> getTrainingVideo();

    @Query("DELETE from presentation_table")
    Completable flushDb();
}

