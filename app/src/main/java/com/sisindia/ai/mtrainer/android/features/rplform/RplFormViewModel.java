package com.sisindia.ai.mtrainer.android.features.rplform;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.RplFormEntity;
import com.sisindia.ai.mtrainer.android.db.entities.RplFormPhotoEntity;
import com.sisindia.ai.mtrainer.android.models.RplFormEmployeeResponse;
import com.sisindia.ai.mtrainer.android.models.RplFormEmplyoeeNoRequest;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RplFormViewModel extends MTrainerViewModel {
    @Inject
    DashBoardApi dashBoardApi;

    public ObservableField<String> employeeName = new ObservableField<>();
    public ObservableField<String> regno = new ObservableField<>();
    public ObservableField<String> branchNameEdit = new ObservableField<>();
    public ObservableField<String> unitNameEdit = new ObservableField<>();
    private final MtrainerDataBase dataBase;

    @Inject
    public RplFormViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }

    public void callAPIToGetEmployeeDetails() {
        String regnumber = regno.get();
        RplFormEmplyoeeNoRequest emplyoeeNoRequest = new RplFormEmplyoeeNoRequest(regnumber);
        setIsLoading(true);
        addDisposable(dashBoardApi.getEmployeeRegNo(emplyoeeNoRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRplSuccess, this::onApiError));
    }

    private void onRplSuccess(RplFormEmployeeResponse response) {
        try {
            if (response.getStatusCode() == SUCCESS_RESPONSE) {
                employeeName.set(response.getEmployeeDetails().firstName + response.getEmployeeDetails().middleName + response.getEmployeeDetails().lastName);
                branchNameEdit.set(response.getEmployeeDetails().branchName);
                unitNameEdit.set(response.getEmployeeDetails().siteName);
                // RplFormEntity rplFormEntity = new RplFormEntity();
                // rplFormEntity.setSiteId(response.getEmployeeDetails().SiteId);
                // rplFormEntity.setBranchId(response.getEmployeeDetails().BranchId);
                String siteid = response.getEmployeeDetails().SiteId;
                String branchid = response.getEmployeeDetails().getBranchId();
                Prefs.putString(PrefsConstants.SITE_ID, siteid);
                Prefs.putString(PrefsConstants.BRANCH_ID, branchid);

                showToast("Employee Name is " + response.getEmployeeDetails().firstName + response.getEmployeeDetails().lastName);
            }
        } catch (Exception ignored) {
        }
    }

    public void saveRPLDataToDB(List<RplFormPhotoEntity> photoList, RplFormEntity formEntity) {

        /*dataBase.getRplFormPhotoDao().insertRplFormPhoto(attendancePhotoEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        dataBase.getRplFormPhotoDao().insertRplFormPhoto(attendancePhotoEntity1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();*/

        addDisposable(dataBase.getRplFormPhotoDao()
                .insertRplFormPhotoList(photoList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

        addDisposable(dataBase.getRplFormDao()
                .insertRplFormValues(formEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }
}
