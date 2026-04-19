package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.SlideModuleEntity;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface SlideModuleDao {

    @Query("SELECT * from slide_module_table")
    int  getSlideModuleCount();

    @Query("DELETE from slide_module_table")
    public void deleteSlideModule();

    @Query("DELETE  from slide_module_table " +"WHERE id=:module and SubModuleNo=:sub")
    Completable deleteSlideModule(String module, String sub);

    @Query("SELECT * from slide_module_table WHERE  SubModuleNo=:submodelno and moduleNo=:moduleno" )
    List<SlideModuleEntity.SlideModuleEntityList> getSlides(String submodelno, String moduleno);

    @Query("SELECT Count(*) from slide_module_table WHERE moduleNo=:moduleNo and SubModuleNo=:submodule")
    public int getSlideModuleCount(String moduleNo, String submodule);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertSlideModule(List<SlideModuleEntity.SlideModuleEntityList> slideModuleEntityLists);

}
