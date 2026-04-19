package com.sisindia.ai.mtrainer.android.features.umbrellareport.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.UmbrellaImageRvItem;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface UmbrellaPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUmbrellaPostList(List<UmbrellaPost> umbrellaPosts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUmbrellaPost(UmbrellaPost umbrellaPost);

    @Query("SELECT COUNT(id) from umbrella_post_table WHERE umbrellaId = :umbrellaId AND imagePath !=''")
    Single<Integer> getImageCount(String umbrellaId);

    @Query("SELECT COUNT(id) from umbrella_post_table WHERE umbrellaId = :umbrellaId AND siteId = :siteId")
    Single<Integer> getSiteCount(String umbrellaId, int siteId);

    @Query("SELECT * from umbrella_post_table WHERE umbrellaId = :umbrellaId AND imagePath !=''")
    List<UmbrellaPost> getUmbrellaImage(String umbrellaId);

    @Query("UPDATE umbrella_post_table SET imagePath=:imagePath, imageId = :imageId, waterMark = :watermark WHERE umbrellaId = :umbrellaId AND postId = :postId")
    Completable insertUmbrellaImage(int postId, String umbrellaId, String imagePath, String imageId, String watermark);

    @Query("SELECT * from umbrella_post_table WHERE umbrellaId = :umbrellaId")
    LiveData<List<UmbrellaImageRvItem>> getUmbrellaImageList(String umbrellaId);

    @Query("SELECT * from umbrella_post_table WHERE canSync = 1 AND imagePath !=''")
    List<UmbrellaPost> getUmbrellaImageForSync();

    @Query("SELECT * from umbrella_post_table WHERE canSync = 0 AND imagePath !=''")
    List<UmbrellaPost> getUmbrellaspiImageForSync();

    @Query("DELETE from umbrella_post_table WHERE umbrellaId = :umbrellaId")
    Completable clearCurrentUmbrellaData(String umbrellaId);

    @Query("DELETE from umbrella_post_table WHERE id = :id")
    void removeSingleUmbrellaImage(int id);

    @Query("UPDATE umbrella_post_table SET hasUrl=1, imagePath = :imageUrl WHERE id = :id")
    void updateUmbrellaImageUrl(int id, String imageUrl);

    @Query("UPDATE umbrella_post_table SET isCompress = 1, imagePath = :imagePath WHERE id = :id")
    void updateImageCompressState(int id, String imagePath);

    @Query("UPDATE umbrella_post_table SET isMark = 1 WHERE id = :id")
    void updateImageWatermarkState(int id);

    @Query("UPDATE umbrella_post_table SET canSync = 1 WHERE umbrellaId = :umbrellaId")
    void updateUmbrellaSpiImageState(String  umbrellaId);

    @Query("UPDATE umbrella_post_table SET canSync = 1, keyId = :keyId WHERE umbrellaId = :umbrellaId")
    void updateUmbrellaImageState(String  umbrellaId, int keyId);
}
