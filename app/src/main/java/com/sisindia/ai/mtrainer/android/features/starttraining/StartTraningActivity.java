package com.sisindia.ai.mtrainer.android.features.starttraining;

import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_CODE_RESOLUTION_REQUIRED;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_LOCATION_PERMISSION;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_TRAINING_PHOTO;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.TRAINING_KIT;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.CLOSE_REMARKS_DIALOG;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.GET_END_LOCATION;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_OPEN_AGAIN_DASHBOARD_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_TAKE_TRAINING_PHOTO;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_ONLINE_TRAINING_COURSES;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.REMARKS;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.droidcommons.preference.Prefs;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PreTrainingConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityStartTraningBinding;
import com.sisindia.ai.mtrainer.android.databinding.DialogRemarksBinding;
import com.sisindia.ai.mtrainer.android.db.SavedTopic;
import com.sisindia.ai.mtrainer.android.features.assessment.AssessmentReportActivity;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceActivity;
import com.sisindia.ai.mtrainer.android.features.choosetopics.ChooseTopicsActivity;
import com.sisindia.ai.mtrainer.android.features.clientreport.ClientReportActivity;
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackActivity;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.features.rplform.RplFormActivity;
import com.sisindia.ai.mtrainer.android.features.syncadapter.GpsTracker;
import com.sisindia.ai.mtrainer.android.features.syncadapter.SyncWorker;
import com.sisindia.ai.mtrainer.android.features.takeassessment.AssessmentActivity;
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCoursesActivity;
import com.sisindia.ai.mtrainer.android.features.trainingimages.TrainingImageActivity;
import com.sisindia.ai.mtrainer.android.features.trainingkit.TrainingKitActivity;
import com.sisindia.ai.mtrainer.android.features.trainingprogram.TrainingProgramActivity;
import com.sisindia.ai.mtrainer.android.utils.TimeUtils;

import java.util.List;

import timber.log.Timber;

public class StartTraningActivity extends MTrainerBaseActivity implements OnMapReadyCallback {
    private ActivityStartTraningBinding binding;
    private StartTrainingViewModel viewModel;
    //    private MtrainerDataBase dataBase;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    private Dialog dialog;
    GpsTracker gpsTracker;
    private StartTrainingNotificationService notificationService;
    private int trainingImageCount;
    private int empCount;
    private int topicCount;
    private int adhocTopicCount;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_REQUEST_CODE = 210;

    public static Intent newIntent(Activity activity) {
        return new Intent(activity, StartTraningActivity.class);
    }

    @Override
    protected void extractBundle() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_start_training, menu);
        // return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            // startActivityForResult(TrainingKitActivity.newIntent(this), TRAINING_KIT);
            // openTrainingKitScreen();
            // Intent launchNewIntent = new Intent(this,TrainingKitActivity.class);
            // startActivityForResult(launchNewIntent,TRAINING_KIT);
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void initViewState() {
        /*  liveTimerEvent.observe(this, message -> {
            switch (message.what) {
                case REVIEW_INFORMATION_TIME_SPENT:
                    bindReviewInformationTime(message.arg1);
                    break;
            }
        });*/
        gpsTracker = new GpsTracker(this);
        liveData.observe(this, message -> {
            switch (message.what) {

                case NavigationConstants.ON_TAKE_ATTENDANCE_CLICK:
                    openTakeAttendanceScreen();
                    break;
                case NavigationConstants.ON_OPEN_CHOOSE_TOPICS_TRAINED:
                    openChooseTopicsTrainedScreen();
                    break;
                case NavigationConstants.ON_OPEN_TAKE_ASSESSMENT:
                    openChooseTakeAssessment();
                    break;
                case NavigationConstants.ON_OPEN_ASSESSMENT_REPORT:
                    openAssessmentReports();
                    break;
                case NavigationConstants.CLIENT_REPORT:
                    openReportScreen();
                    break;
                case ON_TAKE_TRAINING_PHOTO:
                    openTrainingPhotos();
                    break;
                case NavigationConstants.ON_OPEN_RPL_FORM:
                    openRplFormScreen();
                    break;
                case ON_OPEN_AGAIN_DASHBOARD_SCREEN:
                    openDashBoardScreen();
                    break;

                case GET_END_LOCATION:
                    viewModel.setIsLoading(true);
                    onLocationPermissionRequest();
                    break;

                case REMARKS:
                    openremarksdialog();
                    break;

                case CLOSE_REMARKS_DIALOG:
                    dialog.dismiss();
                    break;

                case OPEN_ONLINE_TRAINING_COURSES:
//                    startActivity(new Intent(this, TrainingCoursesActivity.class));
                    startActivity(new Intent(this, TrainingProgramActivity.class));
                    break;
              /*  case ON_OPEN_TRAINING_KIT_SCREEN:
                    openTrainingKitScreen();
                    break;*/
            }
        });

        viewModel.getEmployeeCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer employeeCount) {
                viewModel.updateEmpCount(employeeCount);
            }
        });


        // for write this code in this activity saved topic counts from table
        viewModel.getSavedList().observe(this, new Observer<List<SavedTopic>>() {
            @Override
            public void onChanged(List<SavedTopic> savedTopics) {
                viewModel.selectedTopic.set(savedTopics.size());

            }
        });
    }

    private void openremarksdialog() {
        dialog = new Dialog(StartTraningActivity.this);
        DialogRemarksBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(StartTraningActivity.this), R.layout.dialog_remarks, null, false);
        dialogBinding.setVm(viewModel);
//        dialogBinding.etEmployeeId.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                viewModel.searchText = s.toString();
//                viewModel.isAddVisual.set(false);
//                viewModel.empName.set("");
//
//            }
//        });


        dialogBinding.etEmployeeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.remarkstext.set(s.toString());
            }
        });

        dialogBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.remarkstext.get().isEmpty()) {
                    showToast("Enter Remarks");
                    return;
                }
                dialog.dismiss();
            }
        });


        dialogBinding.closeBtn.setOnClickListener(v -> {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

        });

        dialog.setCanceledOnTouchOutside(false);
        //dialogBinding.efabClick.bringToFront();
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void openDashBoardScreen() {
      /*  Bundle b = new Bundle();
        SyncAdapterInitialization syncAdapterInitialization = new SyncAdapterInitialization(this);
        syncAdapterInitialization.startForceSyncing(b);*/
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkRequest immediateSync = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueue(immediateSync);

      /*  PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("mtrainer_sync", ExistingPeriodicWorkPolicy.KEEP, workRequest);
        */

        viewModel.setIsLoading(false);
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        Prefs.putStringOnMainThread(PrefsConstants.STARTED_TIME, "-1");
        Prefs.putStringOnMainThread(PrefsConstants.STARTED_TIME_FOR_SERVER, "-1");
        finish();
    }

    public void openTrainingKitScreen() {
        startActivityForResult(TrainingKitActivity.newIntent(this), TRAINING_KIT);

    }

    private void openTakeAttendanceScreen() {
        if (viewModel.isLoading.get() == View.GONE) {
            startActivity(TrainingAttendanceActivity.newIntent(this));
        }
    }


    private void openChooseTopicsTrainedScreen() {
        if (viewModel.isLoading.get() == View.GONE) {
            if (empCount != 0)
                startActivity(ChooseTopicsActivity.newIntent(this));
        }
    }

    private void openRplFormScreen() {
        if (viewModel.isLoading.get() == View.GONE) {
            if (empCount != 0)
                startActivity(RplFormActivity.newIntent(this));
            //loadFragment(R.id.rpl_fragment_container, RplFormFragment.newInstance(), FRAGMENT_REPLACE, true);
        }

      /*  FragmentTransaction finalTransaction = fragmentManager.beginTransaction();
        Fragment fragment = RplFormFragment.newInstance();
        finalTransaction.replace(R.id.flTakeAssessment, fragment, fragment.getClass().getSimpleName());
        finalTransaction.addToBackStack(null);
        finalTransaction.commitAllowingStateLoss();*/
    }

    private void openChooseTakeAssessment() {
        if (viewModel.isLoading.get() == View.GONE)
            if (empCount != 0)
                startActivity(AssessmentActivity.newIntent(this));
    }

    private void openAssessmentReports() {
        startActivity(new Intent(this, AssessmentReportActivity.class));
    }

    private void openTrainingPhotos() {
        if (viewModel.isLoading.get() == View.GONE) {
            if (empCount != 0)
                startActivityForResult(TrainingImageActivity.newIntentForPhotoEvaluation(this), REQUEST_TRAINING_PHOTO);
        }
    }

    private void bindReviewInformationTime(int seconds) {
        binding.includeTimeSpent.tvTimeSpent.setText(TimeUtils.convertIntSecondsToHHMMSS(seconds));
    }


    @Override
    protected void onCreated() {
        // Added for Pre-Training
        Prefs.putString(PrefsConstants.CLIENT_EMPLOYEE_COUNT, "0");
        Prefs.putInt(PrefsConstants.PRE_TRAINING_STATE, PreTrainingConstants.STARTED_FOR_SITE);
        setupToolBarForBackArrow(binding.tbStartTraining);
        // fetching master employee
        //viewModel.fetchMasterEmployee();
        viewModel.initDataCollection();
        if (Prefs.getInt(PrefsConstants.TRAINING_TYPE_ID) == 2) {
            binding.trainingType.setText("");
            binding.trainingType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_van, 0);
        } else {
            binding.trainingType.setText("");
            binding.trainingType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tab, 0);
        }
        binding.includeTimeSpent.tvTimeSpent.setText(Prefs.getString(PrefsConstants.STARTED_TIME));


        binding.videosIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTrainingKitScreen();

            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(StartTraningActivity.this);
        }

        Button refreshBtn = findViewById(R.id.btnRefresh);
        refreshBtn.setOnClickListener(v -> getCurrentLocation());
    }

    private void showLocationOnMap(Location location) {
        mMap.clear();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Prefs.putDouble(PrefsConstants.LAT, location.getLatitude());
        Prefs.putDouble(PrefsConstants.LONGI, location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLatLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f));
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        showLocationOnMap(location);
                    }
                });
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (StartTrainingViewModel) getAndroidViewModel(StartTrainingViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_start_traning;
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.initViewModel();
        viewModel.startRestoreSequence();
        trainingImageCount = Prefs.getInt(PrefsConstants.TRAINING_IMAGE_COUNT, 0);
        empCount = Prefs.getInt(PrefsConstants.SELECTED_EMP_COUNT, 0);
        viewModel.canDeleteRota = Prefs.getBoolean(PrefsConstants.CAN_DELETE_ROTA, false);
        viewModel.canShow = Prefs.getBoolean(PrefsConstants.CAN_SHOW, false);
        viewModel.rating = Prefs.getFloat(PrefsConstants.RATING, -1);

        topicCount = Prefs.getInt(PrefsConstants.SELECTED_TOPIC_COUNT, 0);
        adhocTopicCount = Prefs.getInt(PrefsConstants.SELECTED_ADHOC_TOPIC_COUNT, 0);
        updateFeedbackUI();

        if (empCount == 0) {
            binding.feedbackContainer.setAlpha(0.3f);
        } else {
            binding.feedbackContainer.setAlpha(1);
        }

        binding.feedbackContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.isLoading.get() == View.GONE) {
                    if (!(Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8")) && empCount != 0) {
                        Intent i = new Intent(StartTraningActivity.this, FeedbackActivity.class);
                        i.putExtra("TOTAL_TOPIC", viewModel.totalTopic.get());
                        i.putExtra("TOTAL_EMPLOYEE", viewModel.totalEmployee.get());
                        i.putExtra("TOTAL_POST", viewModel.totalPost.get());
                        startActivityForResult(i, IntentRequestCodes.FEEDBACK_REQUEST);
                    } else if (empCount != 0 && (Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8"))) {
                        Intent i = new Intent(StartTraningActivity.this, FeedbackActivity.class);
                        i.putExtra("TOTAL_TOPIC", viewModel.totalTopic.get());
                        i.putExtra("TOTAL_EMPLOYEE", viewModel.totalEmployee.get());
                        i.putExtra("TOTAL_POST", viewModel.totalPost.get());
                        i.putExtra("IS_DTSS", true);
                        startActivityForResult(i, IntentRequestCodes.DTSS_FEEDBACK_REQUEST);
                    }
                }
            }
        });

        viewModel.updateSummaryUI();
        if (!(Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8"))) {
            if (empCount == 0 || trainingImageCount == 0 || (topicCount == 0) && (adhocTopicCount == 0) || (!Prefs.getString(PrefsConstants.ROLE).equals("Unit Commander") && viewModel.rating == -1) /*|| (PreTrainingReviewActivity.selectedAssessmentEmpId.isEmpty() && !Prefs.getString(PrefsConstants.ROLE).equals("Unit Commander"))*/) {
                binding.trainingDoneBtn.setAlpha(0.4f);
                binding.trainingDoneBtn.setClickable(false);
            } else {
                binding.trainingDoneBtn.setAlpha(1);
                binding.trainingDoneBtn.setClickable(true);
            }
        } else {
            if (empCount == 0 || trainingImageCount == 0 || (topicCount == 0) && (adhocTopicCount == 0) || adhocTopicCount == 0 ||
                    (!Prefs.getString(PrefsConstants.ROLE).equals("Training Champ") && !viewModel.canShow) ||
                    (!Prefs.getString(PrefsConstants.ROLE).equals("Training Champ") && viewModel.rating == -1)) {
                binding.trainingDoneBtn.setAlpha(0.3f);
                binding.trainingDoneBtn.setClickable(false);
            } else {
                binding.trainingDoneBtn.setAlpha(1);
                binding.trainingDoneBtn.setClickable(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RESOLUTION_REQUIRED) {
            if (resultCode == Activity.RESULT_OK) {
                if (gpsTracker.canGetLocation()) {
                    if (gpsTracker.getLatitude() != 0.0 && gpsTracker.getLongitude() != 0.0) {
                        mLastLocation = gpsTracker.getLocation();
                        Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                        Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                        viewModel.setIsLoading(false);
                        viewModel.saveFinalData();
                    } else {
                        onLocationRequest();
                    }
                }
            } else {
                showToast("GPS is needed");
            }

        } else if (resultCode == RESULT_OK) {
            if (requestCode == IntentRequestCodes.FEEDBACK_REQUEST) {
                binding.feedbackContainer.setVisibility(View.GONE);
                binding.afterFeedbackContainer.setVisibility(View.VISIBLE);
                if (data != null && data.getStringExtra("RATING") != null) {
                    viewModel.rating = Float.parseFloat(data.getStringExtra("RATING"));
                    if (Float.parseFloat(data.getStringExtra("RATING")) > 0) {
                        String rating = data.getStringExtra("RATING") + "/5";
                        binding.afterFeedbackRating.setText(rating);
                    } else {
                        binding.afterFeedbackRating.setText("");
                        binding.afterFeedbackRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_completed1, 0);
                        binding.afterFeedbackText.setText("Client not present");
                    }
                }
            } else if (requestCode == IntentRequestCodes.CLIENT_REPORT_REQUEST) {
                if (data != null) {
                    viewModel.canShow = data.getBooleanExtra("CAN_SHOW", false);
                }
            } else if (requestCode == IntentRequestCodes.DTSS_FEEDBACK_REQUEST) {
                binding.feedbackContainer.setVisibility(View.GONE);
                binding.afterFeedbackContainer.setVisibility(View.VISIBLE);
                if (data != null && data.getStringExtra("RATING") != null) {
                    viewModel.rating = Float.parseFloat(data.getStringExtra("RATING"));
                    if (Float.parseFloat(data.getStringExtra("RATING")) > 0) {
                        String rating = data.getStringExtra("RATING") + "/5";
                        binding.afterFeedbackRating.setText(rating);
                    } else {
                        binding.afterFeedbackRating.setText("");
                        binding.afterFeedbackRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_completed1, 0);
                        binding.afterFeedbackText.setText("Client not present");
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!(Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8"))) {
            if (empCount != 0 || trainingImageCount != 0 || adhocTopicCount != 0 || topicCount != 0 || viewModel.rating != -1 || !PreTrainingReviewActivity.selectedAssessmentEmpId.isEmpty()) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle("Alert")
                        .setMessage("Data will be deleted")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewModel.flushData();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false)
                        .create();
                dialog.show();
            } else {
                viewModel.flushData();
            }

        } else {
            if (empCount != 0 || trainingImageCount != 0 || adhocTopicCount != 0 || topicCount != 0 || !PreTrainingReviewActivity.selectedAssessmentEmpId.isEmpty() || (!Prefs.getString(PrefsConstants.ROLE).equals("Training Champ") && viewModel.canShow)) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle("Alert")
                        .setMessage("Data will be deleted")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewModel.flushData();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false)
                        .create();
                dialog.show();
            } else {

                viewModel.flushData();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("StartTraining", "onPause: ");
        Prefs.putBooleanOnMainThread(PrefsConstants.CAN_DELETE_ROTA, viewModel.canDeleteRota);
    }

    public void onLocationPermissionRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {

            if (gpsTracker.canGetLocation()) {
                if (gpsTracker.getLatitude() != 0.0 && gpsTracker.getLongitude() != 0.0) {
                    mLastLocation = gpsTracker.getLocation();
                    Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                    Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                    viewModel.setIsLoading(false);
                    viewModel.saveFinalData();
                } else {
                    checkLocationSetting();
                }
            }
            Log.d("Pre Training", "getLocation: permissions granted");
        }
    }

    private void onLocationRequest() {
        final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    mLastLocation = locationResult.getLastLocation();
                    Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                    Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                    viewModel.setIsLoading(false);
                    viewModel.saveFinalData();
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    Log.v("got location", "Sucess");
                } else {
                    showToast("Error fetching location");
                }
            }
        };
        mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
    }

    // Setting the type of Request
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(200);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void checkLocationSetting() {
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(getLocationRequest());
        LocationSettingsRequest locationSettingsRequest = builder.build();
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Timber.i("All location settings are satisfied.");
                    onLocationRequest();
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Timber.i("Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(StartTraningActivity.this, REQUEST_CODE_RESOLUTION_REQUIRED);
                            } catch (IntentSender.SendIntentException sie) {
                                Timber.i("PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            Timber.e(errorMessage);
                            Toast.makeText(StartTraningActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationSetting();
                } else {
                    Toast.makeText(this,
                            "Location Permission denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            notificationService = ((StartTrainingNotificationService.ServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, StartTrainingNotificationService.class);
    }

    private void openReportScreen() {
        if (empCount != 0 && (Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8"))) {
            Intent i = new Intent(StartTraningActivity.this, ClientReportActivity.class);
            startActivityForResult(i, IntentRequestCodes.CLIENT_REPORT_REQUEST);
        }
    }

    private void updateFeedbackUI() {
        if (viewModel.rating == -1)
            return;
        else {
            binding.feedbackContainer.setVisibility(View.GONE);
            binding.afterFeedbackContainer.setVisibility(View.VISIBLE);
        }
        if (viewModel.rating > 0) {
            String rating = viewModel.rating + "/5";
            binding.afterFeedbackRating.setText(rating);
        } else {
            binding.afterFeedbackRating.setText("");
            binding.afterFeedbackRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_completed1, 0);
            binding.afterFeedbackText.setText("Client not present");
        }
    }


}