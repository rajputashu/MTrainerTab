package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sisindia.ai.mtrainer.android.db.entities.MountedSpiPhotoEntity;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiImage;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public  interface  MountedSpiPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMountedPhoto(MountedSpiPhotoEntity mountedSpiPhotoEntity);
    @Query("SELECT imageUrl, position, uniqueId from mounted_photo_table WHERE spiId=:spiId")
    LiveData<List<SpiImage>> getMountedPhotoList(int spiId);

    @Query("SELECT position from mounted_photo_table WHERE spiId=:spiId AND postId=:postId ORDER BY position ASC")
    Single<List<Integer>> getOccupiedPositions(int spiId, int postId);

    @Query("SELECT * from mounted_photo_table WHERE spiId=:spiId AND isSynced = 0")
    Single<List<MountedSpiPhotoEntity>> getMountedPhotoForSync(int spiId);

    @Query("SELECT count(id) from mounted_photo_table WHERE spiId=:spiId AND isSynced = 0")
    int getPendingImageCount(int spiId);
    @Query("SELECT * from mounted_photo_table WHERE spiId=:spiId AND isSynced = 1")
    Single<List<MountedSpiPhotoEntity>> getSyncedImageData(int spiId);

    @Query("DELETE from mounted_photo_table WHERE spiId = :spiId")
    Completable deleteMountedPhoto(int spiId);

    @Query("UPDATE mounted_photo_table SET isCompress = 1, imageUrl =:compressedImage WHERE imageUrl = :draftSpiPhotoUri ")
    void imageCompressed(String draftSpiPhotoUri, String compressedImage);
    @Query("DELETE from mounted_photo_table")
    Completable flushMountedPhotoTable();
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMountedSpiPhoto(MountedSpiPhotoEntity draftSpiPhotoEntity);


}
