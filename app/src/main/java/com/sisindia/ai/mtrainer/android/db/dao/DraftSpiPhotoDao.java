
package com.sisindia.ai.mtrainer.android.db.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sisindia.ai.mtrainer.android.db.entities.DraftSpiPhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.TempDraftSpiPhotoList;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiImage;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface DraftSpiPhotoDao {


    @Query("SELECT * from draft_spi_photos_table WHERE spiId=:spiId")
    Single<List<DraftSpiPhotoEntity>> getDraftSpiPhotoList1(int spiId);

    @Query("SELECT imageUrl, position, uniqueId from draft_spi_photos_table WHERE spiId=:spiId")
    LiveData<List<SpiImage>> getSpiPhotoList(int spiId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertDraftPhoto(DraftSpiPhotoEntity draftSpiPhotoEntity);
    /*@Query("SELECT draftPhotoURI, pictureTypeId from draft_spi_photos_table WHERE postId = :postid")
    LiveData<List<TempDraftSpiPhotoList>> getDraftSpiPhotoList(int postid);*/
    @Query("SELECT position from draft_spi_photos_table WHERE spiId=:spiId AND postId=:postId ORDER BY position ASC")
    Single<List<Integer>> getOccupiedPositions(int spiId, int postId);

    @Query("DELETE from draft_spi_photos_table")
    Completable flushDraftSpiPhotoTable();

    @Query("DELETE from draft_spi_photos_table WHERE position=:position AND uniqueId=:uniqueid ")
    Completable deleteDraftImage(int position,String uniqueid);


    @Query("SELECT * from draft_spi_photos_table WHERE spiId=:spiId AND isSynced = 0")
    Single<List<DraftSpiPhotoEntity>> getDraftSpiPhotoForSync(int spiId);

    @Query("SELECT * from draft_spi_photos_table WHERE spiId=:spiId AND isSynced = 1")
    Single<List<DraftSpiPhotoEntity>> getSyncedImageData(int spiId);

    @Query("SELECT count(id) from draft_spi_photos_table WHERE spiId=:spiId AND isSynced = 0")
    int getPendingImageCount(int spiId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDraftSpiPhoto(DraftSpiPhotoEntity draftSpiPhotoEntity);

    @Query("SELECT imageUrl from draft_spi_photos_table WHERE spiId = :spiid")
    List<String> getDraftSpiPhotoList(String spiid);


    @Query("UPDATE draft_spi_photos_table SET isCompress = 1, imageUrl =:compressedImage WHERE imageUrl = :draftSpiPhotoUri ")
    void imageCompressed(String draftSpiPhotoUri, String compressedImage);



    @Query("DELETE from draft_spi_photos_table WHERE spiId = :spiId")
    Completable deleteDraftPhoto(int spiId);


}

