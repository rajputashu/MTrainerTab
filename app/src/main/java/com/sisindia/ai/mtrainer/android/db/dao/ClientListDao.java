package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.ContactListResponse;
import com.sisindia.ai.mtrainer.android.models.RatingMasterResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface ClientListDao {
    @Query("SELECT * from master_client_contact_table WHERE unitId = :unitId")
    LiveData<List<ContactListResponse.ClientData>> getClientList(int unitId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertClientData(List<ContactListResponse.ClientData> clientDataList);

    @Query("SELECT * from master_client_contact_table WHERE unitId = :unitId")
    List<ContactListResponse.ClientData> getClientSubList(int unitId);
    @Query("DELETE from master_client_contact_table")
    Completable flushClientListMaster();

}

