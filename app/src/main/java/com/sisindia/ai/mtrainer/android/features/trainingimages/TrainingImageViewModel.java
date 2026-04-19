package com.sisindia.ai.mtrainer.android.features.trainingimages;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.AttendanceConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.constants.StartActivityItemConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.TempImageData;
import com.sisindia.ai.mtrainer.android.db.entities.TrainingPhotoAttachmentEntity;
import com.sisindia.ai.mtrainer.android.models.PostItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TrainingImageViewModel extends MTrainerViewModel {

    public ObservableField<Uri> imageUri = new ObservableField<>();
    public ObservableInt pictureType = new ObservableInt(StartActivityItemConstants.TRAINING_PICTURE);
    private final MtrainerDataBase dataBase;

    @Inject
    public TrainingImageViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri.set(imageUri);
    }

    public ToggleListener toggleListener = isChecked -> {
        if (isChecked)
            pictureType.set(StartActivityItemConstants.POSTER_PICTURE);
        else
            pictureType.set(StartActivityItemConstants.TRAINING_PICTURE);
    };

    void saveImage(String imagePath, PostItem postItem, int pictureTypeId, String waterMark) {
        TrainingPhotoAttachmentEntity photoAttachmentEntity = new TrainingPhotoAttachmentEntity();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
        photoAttachmentEntity.setRotaId(Prefs.getInt(PrefsConstants.ROTA_ID));
        photoAttachmentEntity.postName = postItem.postName;
        photoAttachmentEntity.postId = postItem.postId;
        photoAttachmentEntity.setTrainingPhotoURI(imagePath);
        String photoId = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + postItem.postId + "_" + sdf.format(new Date());
        photoAttachmentEntity.setTrainingPhotoId(photoId);
        photoAttachmentEntity.pictureTypeId = String.valueOf(pictureTypeId);
        photoAttachmentEntity.setStatus(AttendanceConstants.CANT_SYNCED);
        photoAttachmentEntity.waterMark = waterMark;

        addDisposable(dataBase.getPhotoAttachmentDao()
                .insertPhotoAttechment(photoAttachmentEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    logger.log("TraingingImageViewModel saveImage " + throwable.getMessage());
                })
                .subscribe());
    }

    LiveData<List<TempImageData>> getSavedImageList() {
        return dataBase.getPhotoAttachmentDao().getTrainingPhoto(Prefs.getInt(PrefsConstants.ROTA_ID));
    }

    void removeImage(String imagePath) {
        setIsLoading(true);
        addDisposable(dataBase.getPhotoAttachmentDao().deleteTrainingImage(imagePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> setIsLoading(false), throwable -> showToast("Error removing image")));
    }
}
