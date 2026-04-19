package com.sisindia.ai.mtrainer.android.features.attendancemodule;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TrainingAttendancePictureViewModel extends MTrainerViewModel {
    private final MtrainerDataBase dataBase;

    @Inject
    public TrainingAttendancePictureViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }

    public void saveTrainingAttendanceDataToDB(AttendancePhotoEntity photoEntity,
                                               AttendanceEntity attendanceEntity) {

        addDisposable(dataBase.getAttendancePhotoDao()
                .insertAttendancePhoto(photoEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

        addDisposable(dataBase.getAttendanceDao()
                .insertAttendance(attendanceEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }
}
