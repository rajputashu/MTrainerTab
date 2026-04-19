package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sisindia.ai.mtrainer.android.db.entities.RplFormPhotoEntity;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface RplFormPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertRplFormPhoto(RplFormPhotoEntity rplFormPhotoEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertRplFormPhotoList(List<RplFormPhotoEntity> list);

    @Query("SELECT * from rplform_photo_table")
    List<RplFormPhotoEntity> getSavedRplFormPic();

    @Query("DELETE from rplform_photo_table WHERE rplFormPhotoId = :id")
    Completable deleteRplPhoto(String id);

    @Query("SELECT * from rplform_photo_table")
    List<RplFormPhotoEntity> getRplPhotoForSync();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRplPhoto(RplFormPhotoEntity rplFormPhotoEntity);


}
