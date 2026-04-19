package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.SignatureAttachmentEntity;

import java.util.List;

import io.reactivex.Completable;
@Dao
public interface SignatureAttchmentDao {

    @Query("SELECT * from signature_attachment_table")
    LiveData<List<SignatureAttachmentEntity>> getSignatureAttachmentEntityList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSignatureAttachment(List<SignatureAttachmentEntity> SignatureAttachmentEntityList);

    @Query("DELETE from training_calender_table WHERE rotaId = :id")
    Completable deleteSignatureAttachment(int id);

    @Query("DELETE from signature_attachment_table")
    Completable flushSignatureAttachment();

}
