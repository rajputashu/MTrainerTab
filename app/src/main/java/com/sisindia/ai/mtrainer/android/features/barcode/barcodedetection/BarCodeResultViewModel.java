package com.sisindia.ai.mtrainer.android.features.barcode.barcodedetection;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceViewModel;
import com.sisindia.ai.mtrainer.android.models.EmployeeSearchRequest;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sisindia.ai.mtrainer.android.constants.PrefsConstants.CURRENT_POST_ID;
import static com.sisindia.ai.mtrainer.android.constants.PrefsConstants.CURRENT_POST_NAME;

public class BarCodeResultViewModel extends MTrainerViewModel {
    private MtrainerDataBase dataBase;
    String empcode = "";
    public ObservableField<Boolean> addEmpLoading = new ObservableField<>(false);
    @Inject
    DashBoardApi dashBoardApi;
    @Inject
    public TrainingAttendanceViewModel viewModel;
    private  BarcodeResultFragment barcodeResultFragment;

    @Inject
    public BarCodeResultViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
        //viewModel= new TrainingAttendanceViewModel(getApplication());
        barcodeResultFragment= new BarcodeResultFragment();

    }

    public void initEmployeeSaving(View view) {
        if (empcode.isEmpty())
            showToast("Employee ID not found");
        else {
            addEmpLoading.set(true);
           addDisposable( dataBase.getMasterAttendanceDao().getName(empcode.toUpperCase())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(name -> addEmployee(empcode),
                            throwable -> searchOnline(empcode.toUpperCase())));
        }
    }

    private void employeeAdded(int dummy) {
        showToast("Employee Added");
        addEmpLoading.set(false);
        message.what = NavigationConstants.CLOSE_BARCODE_DETAIL_FRAGMENT;
        liveData.setValue(message);
    }

    private void errorWhileAddingEmployee(Throwable th) {
        showToast("Error while adding employee");
        th.printStackTrace();
        addEmpLoading.set(false);
    }

    private void searchOnline(String searchText) {
        EmployeeSearchRequest request = new EmployeeSearchRequest();
        request.setCompanyId(Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)));
        request.setEmployeeCode(searchText);

        dashBoardApi.searchEmployee(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.attendanceResponse != null) {
                        dataBase.getMasterAttendanceDao().insertMasterEmployee(response.attendanceResponse)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            addEmployee(searchText);
                                            addEmpLoading.set(false);
                                        },
                                        th -> {
                                            logger.log("Error while inserting BarCode (insertMasterEmployee) - E3", th);
                                    showToast("Unexpected Error Occurred - E3");
                                            addEmpLoading.set(false);
                                });
                    } else {
                        showToast("No Employee Found");
                        addEmpLoading.set(false);
                    }
                }, th -> {
                    logger.log("Error from API BarCode (insertMasterEmployee) - E4", th);
                    showToast("Unexpected Error Occurred - E4");
                    addEmpLoading.set(false);});
    }

    private void addEmployee(String searchText) {
      /*  addDisposable(dataBase.getMasterAttendanceDao().addEmployeeToCurrentSite(searchText.toUpperCase(), String.valueOf(Prefs.getInt(PrefsConstants.UNIT_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::employeeAdded, this::errorWhileAddingEmployee));*/
        addDisposable(dataBase.getMasterAttendanceDao().addEmployeeToCurrentSite(searchText.toUpperCase(), String.valueOf(Prefs.getInt(PrefsConstants.UNIT_ID))).toSingleDefault(1)
                               .flatMap(baseApiResponse -> dataBase.getMasterAttendanceDao().getEmployee(searchText.toUpperCase()))
                                .map(data -> {
                                saveManualData(data.employeeName, data.employeeId, data.employeeCode, data.score);
                                return 1;})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::employeeAdded, this::errorWhileAddingEmployee));

    }

    private void saveManualData(String employeeName, int employeeID, String empCode, float score) {
        AttendanceEntity attendanceEntity = new AttendanceEntity();
        attendanceEntity.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
        attendanceEntity.employeeId = employeeID;
        attendanceEntity.employeeName = employeeName;
        // Need Post Details
        attendanceEntity.postName =Prefs.getString(CURRENT_POST_NAME);
        attendanceEntity.postId =Prefs.getInt(CURRENT_POST_ID);
        attendanceEntity.photoId = "NA";
        attendanceEntity.signatureId = "NA";
        attendanceEntity.empCode = empCode;
        attendanceEntity.score = score;

        viewModel.selectedEmployeeSet.add(empCode);
        viewModel.selectedPostSet.add(Prefs.getInt(CURRENT_POST_ID));

      //  barcodeResultFragment.model.selectedEmployeeSet.add(empCode);
     //   barcodeResultFragment.model.selectedPostSet.add(Prefs.getInt(CURRENT_POST_ID));


        // viewModel.selectedPostSet.add(Integer.parseInt(String.valueOf(barcodeScanningActivity.getPostItem())));
        //  barcodeScanningActivity.setSelectedPostSet.add(String.valueOf(barcodeScanningActivity.getPostItem()));

        //viewModel.selectedEmployeeSet.add(empCode);
        // viewModel.selectedPostSet.add(postItem.postId);


        addDisposable(dataBase.getAttendanceDao().insertAttendance(attendanceEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }
}


