package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.GpsTokenEntity;

@Dao
public interface GpsTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGpsToken(GpsTokenEntity gpsTokenEntity);

    @Query("SELECT pairingKey from gps_token_table where id = :id")
    String getPairingKey(long id);

    @Query("DELETE from gps_token_table WHERE id =:id")
    void removeGpsToken(int id);
}
