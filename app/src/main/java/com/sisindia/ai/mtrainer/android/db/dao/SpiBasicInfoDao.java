package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
        import androidx.room.Dao;
        import androidx.room.Insert;
        import androidx.room.OnConflictStrategy;
        import androidx.room.Query;

        import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;

        import java.util.List;

        import io.reactivex.Completable;
        import io.reactivex.Single;

@Dao
public interface SpiBasicInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSpiBasicinfo(List<SpiBasicInfoResponse.SpiBasicInfoDetailsData> spiBasicInfoDetailsData);

    @Query("SELECT * from spi_basic_info_table WHERE keyid =:spiId")
    Single<List<SpiBasicInfoResponse.SpiBasicInfoDetailsData>> getSpiBasicInfo(int spiId);

    @Query("SELECT * from spi_basic_info_table WHERE keyid =:spiId AND isDone = 0")
    Single<List<SpiBasicInfoResponse.SpiBasicInfoDetailsData>> getPendingSpiBasicInfo(int spiId);

    @Query("DELETE from spi_basic_info_table")
    Completable flushSpiBasicTable();

    @Query("DELETE from spi_basic_info_table WHERE keyid =:spiId")
    void removeSpiBasicTableData(int spiId);

    @Query("UPDATE spi_basic_info_table SET isDone = 1 WHERE postid = :postId")
    void markPostCompleted(int postId);
}
