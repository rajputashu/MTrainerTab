package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.MyModule;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface MyModuleDao {

   /* @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertModule (List<MyModule.MyModuleslist> myModuleslistList);
*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     long[] insertModule(List<MyModule.MyModuleslist> myModuleslistList);

    @Query("SELECT COUNT(id) FROM modu_table WHERE id >= 1")
    int getModuleCount();

    @Query("SELECT * from modu_table ")
    List<MyModule.MyModuleslist> getModules();


    @Query("DELETE from modu_table WHERE id = :id")
    Completable deleteModule(String id);



}
