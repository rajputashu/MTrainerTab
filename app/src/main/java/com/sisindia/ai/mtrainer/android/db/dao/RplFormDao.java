package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.RplFormEntity;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface RplFormDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertRplFormValues(RplFormEntity rplFormEntity);
    @Query("SELECT * from rpl_form_table")
    List<RplFormEntity> getRplForm();

    @Query("DELETE from rpl_form_table WHERE rotaId = :rotaId")
    Completable deleteRplForm(int rotaId);

}
