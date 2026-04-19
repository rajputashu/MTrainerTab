package com.sisindia.ai.mtrainer.android.features.attendancemodule;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;

import com.droidcommons.views.ink.InkView;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;
import com.sisindia.ai.mtrainer.android.utils.FileUtils;
import com.sisindia.ai.mtrainer.android.utils.ImageUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TrainingPicAndSignatureViewModel extends MTrainerViewModel {

    private final MtrainerDataBase dataBase;
//    String signaturePath;
//    Canvas canvas;

    public ObservableInt isSigned = new ObservableInt(VISIBLE);

    public InkView.InkListener listener = new InkView.InkListener() {
        @Override
        public void onInkClear() {
            // isSigned.set(GONE);
        }

        @Override
        public void onInkDraw() {
            isSigned.set(VISIBLE);
        }
    };


    @Inject
    public TrainingPicAndSignatureViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }

    @MainThread
    public void onInkViewClear(InkView view) {
        view.clear();
    }

    @SuppressLint("ResourceAsColor")
    @MainThread
    public void onAddSignatureContinue(InkView view) {

        if (view.isViewEmpty()) {
            showToast("Signature and Image both are needed");
            return;
        }

        if (FileUtils.createOrExistsDir(FileUtils.DIR_ROOT)) {
            String imagePath = FileUtils.createFileForTrainerSignature();
            Bitmap bitmap = view.getBitmap(R.color.colorWhite);

            if (ImageUtils.save(bitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
                message.what = NavigationConstants.SAVE_SIGNATURE_AND_IMAGE;
                message.obj = imagePath;
                liveData.postValue(message);
                view.clear();
            } else {
                showToast("Unable to save ...");
            }
        }
    }

    public void saveTrainingPicAndSignToDB(List<AttendancePhotoEntity> photoList,
                                           AttendanceEntity attendanceEntity) {

        addDisposable(dataBase.getAttendancePhotoDao()
                .insertAllAttendancePhoto(photoList)
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
