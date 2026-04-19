package com.sisindia.ai.mtrainer.android.features.attendancemodule;

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
import com.sisindia.ai.mtrainer.android.utils.FileUtils;
import com.sisindia.ai.mtrainer.android.utils.ImageUtils;

import javax.inject.Inject;

import static android.view.View.VISIBLE;

public class TrainingAttendanceSignatureViewModel extends MTrainerViewModel {

    public ObservableInt isSigned = new ObservableInt(VISIBLE);
    String storesign;
    private MtrainerDataBase dataBase;
    private String employeeName;
    private int employeeID;

    public InkView.InkListener listener = new InkView.InkListener() {
        @Override
        public void onInkClear() {
         //   isSigned.set(GONE);
        }

        @Override
        public void onInkDraw() {
            isSigned.set(VISIBLE);
        }
    };


    @Inject
    public TrainingAttendanceSignatureViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }

    @MainThread
    public void onInkViewClear(InkView view) {
        view.clear();
    }

    @MainThread
    public void onAddSignatureContinue(InkView view) {

        if (view.isViewEmpty()) {
            showToast("Please add the signature to Continue..");
            return;
        }

        if (FileUtils.createOrExistsDir(FileUtils.DIR_ROOT)) {
            String imagePath = FileUtils.createFileForTrainerSignature();
            Bitmap bitmap = view.getBitmap(R.color.colorWhite);
            if (ImageUtils.save(bitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
                message.what = NavigationConstants.SAVE_SIGNATURE;
                message.obj = imagePath;
                liveData.postValue(message);
                view.clear();
            } else {
                showToast("Unable to add Signature..");
            }
        }
    }

    void setEmployeeDetails(int id, String name) {
        employeeID = id;
        employeeName = name;
    }
}