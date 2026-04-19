package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.TempImageData;
import com.sisindia.ai.mtrainer.android.db.entities.TrainingPhotoAttachmentEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TrainingPhotoAttachmentDao {

    @Query("SELECT * from training_photo_attachment_table")
    LiveData<List<TrainingPhotoAttachmentEntity>> getPhotoList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPhotoAttechment(TrainingPhotoAttachmentEntity photoAttachmentEntity);

    @Query("DELETE from training_photo_attachment_table WHERE trainingPhotoId = :trainingPhotoId")
    void deletePhotoAttchment(String trainingPhotoId);

    @Query("DELETE from training_photo_attachment_table WHERE trainingPhotoURI = :imagePath")
    Completable deleteTrainingImage(String imagePath);


    @Query("DELETE from training_photo_attachment_table WHERE rotaId = :rotaId")
    Completable flushPhotoAttachment(int rotaId);

    @Query("SELECT DISTINCT postName from training_photo_attachment_table WHERE rotaId = :rotaId")
    List<String> getTrainingPostList(int rotaId);

    @Query("SELECT trainingPhotoId from training_photo_attachment_table WHERE rotaId = :rotaId AND postName = :postName")
    List<String> getTrainingPhotoList(String postName, int rotaId);

    // Status 8 means Can upload image i.e. data has been synced
    @Query("SELECT * from training_photo_attachment_table WHERE status = 8")
    List<TrainingPhotoAttachmentEntity> getTrainingPhotoForSync();

    @Query("SELECT * from training_photo_attachment_table WHERE status = 8")
    Single<List<TrainingPhotoAttachmentEntity>> getTrainingPhotoForForceSync();

    @Query("SELECT trainingPhotoURI, pictureTypeId from training_photo_attachment_table WHERE rotaId = :rotaId")
    LiveData<List<TempImageData>> getTrainingPhoto(int rotaId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTrainingPhoto(TrainingPhotoAttachmentEntity trainingPhotoAttachmentEntity);

    @Query("UPDATE training_photo_attachment_table SET status = 8 WHERE rotaid = :rotaId ")
    void updateTrainingImageStatus(int rotaId);

  /*  @Query("UPDATE training_photo_attachment_table SET isMark = 1 WHERE rotaid = :rotaId ")
    void imageMarked(int rotaId);
*/
    @Query("UPDATE training_photo_attachment_table SET isMark = 1 WHERE trainingPhotoURI = :trainingPhotoUri")
    void imageMarked(String trainingPhotoUri);

    @Query("UPDATE training_photo_attachment_table SET isCompress = 1, trainingPhotoURI =:compressedImage WHERE trainingPhotoURI = :trainingPhotoUri ")
    void imageCompressed(String trainingPhotoUri, String compressedImage);

    @Query("SELECT count(trainingPhotoURI) from training_photo_attachment_table")
    Single<Integer> getPendingTrainingPhotoCount();

}
