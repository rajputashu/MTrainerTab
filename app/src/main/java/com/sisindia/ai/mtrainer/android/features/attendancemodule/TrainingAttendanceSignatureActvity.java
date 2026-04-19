package com.sisindia.ai.mtrainer.android.features.attendancemodule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.databinding.DataBindingUtil;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.AttendanceConstants;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingAttendanceSignarureBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TrainingAttendanceSignatureActvity extends MTrainerBaseActivity {

    private TrainingAttendanceSignatureViewModel viewModel;
    private ActivityTrainingAttendanceSignarureBinding binding;
    private String employeeName;
    private int employeeID;
    private MtrainerDataBase dataBase = MtrainerDataBase.getDatabase(this);
    private String empCode;
    private float score;
    private int postId;
    private String postName;




    public static Intent newIntent(Activity activity) {
        Intent intent= new Intent(activity, TrainingAttendanceSignatureActvity.class);
        return intent;
    }

    @Override
    protected void extractBundle() {
        employeeID = getIntent().getIntExtra("ID", -1);
        employeeName = getIntent().getStringExtra("NAME");
        empCode = getIntent().getStringExtra("CODE");
        score = getIntent().getFloatExtra("SCORE", -1);
        postId = getIntent().getIntExtra("POST_ID", -1);
        postName = getIntent().getStringExtra("POST_NAME");
        viewModel.setEmployeeDetails(employeeID, employeeName);
    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            if(message.what == NavigationConstants.SAVE_SIGNATURE)
                saveData((String)message.obj);

        });
    }

    @Override
    protected void onCreated() {
        binding.userName.setText(employeeName);
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingAttendanceSignatureViewModel) getAndroidViewModel(TrainingAttendanceSignatureViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_training_attendance_signarure;
    }

    private void saveData(String imagePath) {

        AttendancePhotoEntity attendanceSignatureEntity = new AttendancePhotoEntity();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
        String signatureId = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + empCode + "_"+ AttendanceConstants.ATTENDANCE_SIGNATURE + "_"+ sdf.format(new Date());
        attendanceSignatureEntity.id = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + empCode + "_"+ AttendanceConstants.ATTENDANCE_SIGNATURE;
        attendanceSignatureEntity.setAttendancePhotoURI(imagePath);
        attendanceSignatureEntity.pictureTypeId = 3;
        attendanceSignatureEntity.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
        attendanceSignatureEntity.postId = postId;
        attendanceSignatureEntity.setAttendancePhotoId(signatureId);
        attendanceSignatureEntity.setStatus(AttendanceConstants.CANT_SYNCED);
        showToast("Successfully added");

        dataBase.getAttendancePhotoDao().insertAttendancePhoto(attendanceSignatureEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        AttendanceEntity attendanceEntity = new AttendanceEntity();
        attendanceEntity.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
        attendanceEntity.employeeId = employeeID;
        attendanceEntity.employeeName = employeeName;
        attendanceEntity.postName = postName;
        attendanceEntity.postId = postId;
        attendanceEntity.signatureId = signatureId;
        attendanceEntity.empCode = empCode;
        attendanceEntity.score = score;


        dataBase.getAttendanceDao().insertAttendance(attendanceEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        Intent data = new Intent();
        data.putExtra("EMP_CODE", empCode);
        setResult(RESULT_OK, data);
        finish();
    }
}