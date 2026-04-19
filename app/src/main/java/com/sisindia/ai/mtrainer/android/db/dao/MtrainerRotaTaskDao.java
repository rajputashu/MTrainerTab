package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.MtrainerRotaTaskEntity;

import java.util.List;

import io.reactivex.Completable;
@Dao
public interface MtrainerRotaTaskDao  {
    @Query("SELECT * from rota_task_table")
    LiveData<List<MtrainerRotaTaskEntity>> getRotaTaskEntity();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertRotaTask(MtrainerRotaTaskEntity rotaTaskEntity);

    @Query("DELETE from rota_task_table WHERE id = :id")
    Completable deleteRotaTask(int id);

    @Query("DELETE from rota_task_table")
    Completable flushRotaTask();


}
