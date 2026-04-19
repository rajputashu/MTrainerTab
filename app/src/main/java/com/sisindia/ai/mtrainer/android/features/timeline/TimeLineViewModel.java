package com.sisindia.ai.mtrainer.android.features.timeline;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;

import java.util.List;

import javax.inject.Inject;

public class TimeLineViewModel extends MTrainerViewModel {

    MtrainerDataBase dataBase;
    public TimeLineRecyclerAdapter1 recycleradapterTimeLine1 ;


    @Inject
    public TimeLineViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
        recycleradapterTimeLine1 = new TimeLineRecyclerAdapter1(dataBase);

    }

    void refreshPerfRecylerView1(List<TimeLineEntity> data) {
        recycleradapterTimeLine1.clearAndSetItems(data);
    }

    LiveData<List<TimeLineEntity>> getTimelineDetails1() {
        return dataBase.getTimeLineDao().getTimelineList();
    }
}
