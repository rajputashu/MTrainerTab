package com.sisindia.ai.mtrainer.android.features.takeassessment;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_COMPLETING_ASSESSMENT;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_OPEN_CHOOSE_ASSESSMENT;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_ASSESSMENT_VIDEO_ACTIVITY;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_ASSESSMENT_VIDEO_VIEW;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTakeAssessmentBinding;
import com.sisindia.ai.mtrainer.android.utils.TimeUtils;

public class AssessmentActivity extends MTrainerBaseActivity {

    private ActivityTakeAssessmentBinding binding;
    private AssessmentViewModel viewModel;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private static final int REQUEST_REAR_WRITE = 102;
    private final int ASSESSMENT_VIDEO_ACTIVITY = 103;

    public static Intent newIntent(Activity activity) {
        return new Intent(activity, AssessmentActivity.class);
    }

    @Override
    protected void extractBundle() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_take_assessment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViewState() {
        /*liveTimerEvent.observe(this, message -> {
            switch (message.what) {
                case REVIEW_INFORMATION_TIME_SPENT:
                    bindReviewInformationTime(message.arg1);
                    break;
            }
        });*/

        liveData.observe(this, message -> {
            switch (message.what) {
                /*case ON_OPEN_CHOOSE_ASSESSMENT:
                    openChooseAssessmentScreen();
                    break;*/
                case ON_COMPLETING_ASSESSMENT:
                    this.finish();
                    break;

                case OPEN_ASSESSMENT_VIDEO_VIEW:
                    openAssessmentVideoScreen();
                    break;
                case OPEN_ASSESSMENT_VIDEO_ACTIVITY:
                    openAssessmentCamera();
                    break;

            }
        });
    }

    private void bindReviewInformationTime(int seconds) {
        binding.includeTimeSpent.tvTimeSpent.setText(TimeUtils.convertIntSecondsToHHMMSS(seconds));
    }

    @Override
    protected void onCreated() {
        openTakeAssessmentscreen();
        setupToolBarForBackArrow(binding.tbStartTraining);
        viewModel.initViewModel();
        binding.includeTimeSpent.tvTimeSpent.setText(Prefs.getString(PrefsConstants.STARTED_TIME));

    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    private void openChooseAssessmentScreen() {
        /*FragmentTransaction finalTransaction = fragmentManager.beginTransaction();
        Fragment fragment = ChooseAssessmentFragment.newInstance();
        finalTransaction.replace(R.id.flTakeAssessment, fragment, fragment.getClass().getSimpleName());
        finalTransaction.addToBackStack(null);
        finalTransaction.commitAllowingStateLoss();*/

        // Doing this because on back pressed recreates my previous
      /*  FragmentTransaction finalTransaction = fragmentManager.beginTransaction();
        Fragment fragment = TakeAssessmentFragment.newInstance();
        finalTransaction.replace(R.id.flTakeAssessment, fragment, fragment.getClass().getSimpleName());
        finalTransaction.addToBackStack("TakeAssessment"); // ADD THIS
        finalTransaction.commit();*/


        Intent i = new Intent(this, ChooseAssessmentFragment.class);
        startActivity(i);
    }

    private void openTakeAssessmentscreen() {
        FragmentTransaction finalTransaction = fragmentManager.beginTransaction();
        Fragment fragment = TakeAssessmentFragment.newInstance();
        finalTransaction.replace(R.id.flTakeAssessment, fragment, fragment.getClass().getSimpleName());
        finalTransaction.commitAllowingStateLoss();
    }

    private void openAssessmentVideoScreen() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (AssessmentViewModel) getAndroidViewModel(AssessmentViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_take_assessment;
    }

    private void openAssessmentCamera() {
        if (checkSelfPermission()) {
            startActivityForResult(new Intent(this, AssessmentQuestionActivity.class), ASSESSMENT_VIDEO_ACTIVITY);
        } else {
            requestPermission();
        }
    }

    private boolean checkSelfPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_REAR_WRITE);
            else
                showToast("Permission Needed");
        } else {
            Log.d("Wite permission ==", "PERMISSION GRANTED");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ASSESSMENT_VIDEO_ACTIVITY) {
                finish();
            }
        }
    }
}


