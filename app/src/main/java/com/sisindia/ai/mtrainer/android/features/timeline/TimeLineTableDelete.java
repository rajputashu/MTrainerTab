package com.sisindia.ai.mtrainer.android.features.timeline;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class TimeLineTableDelete extends Worker {

    MtrainerDataBase dataBase;


    private static final String TAG = "MyWorker";

    public TimeLineTableDelete(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        dataBase= MtrainerDataBase.getDatabase(getApplicationContext());

    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG,"do work");



        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
       /* if(timeOfDay >= 21 && timeOfDay < 23){
            // this condition for evening time and call return here
            dataBase.getTimeLineDao().flushTimelinet()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            return Result.success();
        }*/
        if(timeOfDay >= 21 && timeOfDay < 24){
            // this condition for night time and return success
            dataBase.getTimeLineDao().flushTimelinet()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            return Result.success();
        }
        else{

        }
        return Result.success();



    }


}
