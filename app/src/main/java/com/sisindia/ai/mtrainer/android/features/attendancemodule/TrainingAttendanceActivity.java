package com.sisindia.ai.mtrainer.android.features.attendancemodule;

import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.BOTH_REQUEST;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.PICTURE_REQUEST;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_CODE_RESOLUTION_REQUIRED;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_LOCATION_PERMISSION;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.SIGNATURE_REQUEST;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ATTENDENCE_ERROR;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_BARCODE;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TAKE_BOTH;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TAKE_EMPLOYEE_IMAGE;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TAKE_EMPLOYEE_SIGH;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.UPDATE_ATTENDENCE_VIEW;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingAttendanceBinding;
import com.sisindia.ai.mtrainer.android.databinding.AddEmployeeDialogBinding;
import com.sisindia.ai.mtrainer.android.databinding.ClientEmployeeDialogBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.barcode.LiveBarcodeScanningActivity;
import com.sisindia.ai.mtrainer.android.models.EmployeeDetail;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class TrainingAttendanceActivity extends MTrainerBaseActivity {
    private TrainingAttendanceViewModel viewModel;
    private ActivityTrainingAttendanceBinding binding;
    private boolean canClick = true;
    private int selectedView;
    private EmployeeDetail employeeDetail;

    //    private TrainingAttendanceRecyclerAdapter trainingAttendanceRecyclerAdapter;
//    int typeid = 2;
//    private ArrayList<TrainingAttendanceResponse.AttendanceResponse> response;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    private Dialog dialog;
    TextView textempCount;
    private MtrainerDataBase dataBase;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, TrainingAttendanceActivity.class);
        return intent;
    }


    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewState() {
/*
        liveTimerEvent.observe(this, message -> {
            switch (message.what) {
                *//*case REVIEW_INFORMATION_TIME_SPENT:
                    bindReviewInformationTime(message.arg1);
                    break;*//*
         *//*case ON_OPEN_TRAINING_PHOTO:
                   openTrainingPhotoScreen();
                    break;*//*
                case NavigationConstants.ON_START_TRAINING_CLICK:
                    openStartTrainingScreen();
                    break;
            }
        });*/

        liveData.observe(this, message -> {
            switch (message.what) {
                case TAKE_EMPLOYEE_IMAGE:
                    if (message.obj != null) {
                        EmployeeDetail employeeDetail = (EmployeeDetail) message.obj;
                        if (canClick) {
                            canClick = false;
                            selectedView = TAKE_EMPLOYEE_IMAGE;
                            this.employeeDetail = employeeDetail;
                            onLocationPermissionRequest();
                        }
                    }
                    break;

                case TAKE_EMPLOYEE_SIGH:
                    if (message.obj != null) {
                        EmployeeDetail employeeDetail = (EmployeeDetail) message.obj;
                        if (canClick) {
                            canClick = false;
                            openSignatureScreen(employeeDetail);
                        }
                    }
                    break;

                case TAKE_BOTH:
                    if (message.obj != null) {
                        EmployeeDetail employeeDetail = (EmployeeDetail) message.obj;
                        if (canClick) {
                            canClick = false;
                            selectedView = TAKE_BOTH;
                            this.employeeDetail = employeeDetail;
                            onLocationPermissionRequest();
                        }
                    }
                    break;
                case UPDATE_ATTENDENCE_VIEW:
                    updateFab();
                    if (dialogBinding != null)
                        dialogBinding.etEmployeeId.setText("");
                    break;

                case ATTENDENCE_ERROR:
                    Toast.makeText(TrainingAttendanceActivity.this, "Please Select Post", Toast.LENGTH_SHORT).show();
                    break;
                case OPEN_BARCODE:
                    Intent i = new Intent(this, LiveBarcodeScanningActivity.class);
                    // below code added for bar code
                    i.putExtra("EMPLOYEE_SET", viewModel.selectedEmployeeSet);
                    i.putExtra("POST_SET", viewModel.selectedPostSet);
                    i.putExtra("POST_Name", viewModel.postItem.postName);
                    i.putExtra("SELECTED_POST", viewModel.postItem.postId);

                    Prefs.putIntOnMainThread(PrefsConstants.CURRENT_POST_ID, viewModel.postItem.postId);
                    Prefs.putStringOnMainThread(PrefsConstants.CURRENT_POST_NAME, viewModel.postItem.postName);
                    startActivity(i);
                    break;
            }
        });


        viewModel.getSavedattendanceList().observe(this, new Observer<>() {
            @Override
            public void onChanged(Integer attendanceCount) {
                binding.totalCount.setText("Total:" + attendanceCount);
            }
        });

        viewModel.getEmployeeList().observe(this, attendanceResponses -> {
            Log.v("Employee Size", "" + attendanceResponses.size());
            viewModel.setEmployee(attendanceResponses);
        });

        viewModel.getPostList().observe(this, postItems -> {
            if (!(Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8")))
                inflatePostChip(postItems);
            else {
                inflatePostChip(new ArrayList<>());
            }
        });
    }
   /* private void openStartTrainingScreen() {
        startActivityForResult(StartTraningActivity.newIntent(this), START_TRAINING);
    }*/

    private void openPhotoScreen(EmployeeDetail employeeDetail) {
        Intent i = new Intent(this, TrainingAttendancePictureActivity.class);
        i.putExtra("NAME", employeeDetail.name);
        i.putExtra("ID", employeeDetail.id);
        i.putExtra("CODE", employeeDetail.empCode);
        i.putExtra("SCORE", employeeDetail.score);
        i.putExtra("POST_NAME", viewModel.postItem.postName);
        i.putExtra("POST_ID", viewModel.postItem.postId);
        startActivityForResult(i, PICTURE_REQUEST);
    }

    private void openBothScreen(EmployeeDetail employeeDetail) {
        Intent i = new Intent(this, TrainingPicAndSignActivity.class);
        i.putExtra("NAME", employeeDetail.name);
        i.putExtra("ID", employeeDetail.id);
        i.putExtra("CODE", employeeDetail.empCode);
        i.putExtra("SCORE", employeeDetail.score);
        i.putExtra("POST_NAME", viewModel.postItem.postName);
        i.putExtra("POST_ID", viewModel.postItem.postId);
        startActivityForResult(i, BOTH_REQUEST);
    }

    private void openSignatureScreen(EmployeeDetail employeeDetail) {
        Intent i = new Intent(this, TrainingAttendanceSignatureActvity.class);
        i.putExtra("NAME", employeeDetail.name);
        i.putExtra("ID", employeeDetail.id);
        i.putExtra("CODE", employeeDetail.empCode);
        i.putExtra("SCORE", employeeDetail.score);
        i.putExtra("POST_NAME", viewModel.postItem.postName);
        i.putExtra("POST_ID", viewModel.postItem.postId);
        startActivityForResult(i, SIGNATURE_REQUEST);
    }

    private void bindReviewInformationTime(int seconds) {
        binding.includeTimeSpent.tvTimeSpent.setText(TimeUtils.convertIntSecondsToHHMMSS(seconds));
    }

    @Override
    protected void onCreated() {
        setupToolBarForBackArrow(binding.tbTakeAttendance);
        dataBase = MtrainerDataBase.getDatabase(getApplicationContext());
        viewModel.getTrainingAttendance();

        //viewModel.getPostDeatils();
//        response = new ArrayList<>();
        updateFab();
        binding.flButtonTraining.setOnClickListener(v -> {
            if (viewModel.selectedEmployeeSet.isEmpty()) {
                showToast("Please Take Attendance");
            } else
                finish();
        });
        binding.includeTimeSpent.tvTimeSpent.setText(Prefs.getString(PrefsConstants.STARTED_TIME));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_take_attendance, menu);
        final MenuItem menuItem = menu.findItem(R.id.add_client_employee);

        View actionView = menuItem.getActionView();
        textempCount = (TextView) actionView.findViewById(R.id.client_emp_number);
        if ((Prefs.getString(PrefsConstants.CLIENT_EMPLOYEE_COUNT, "-1") == "-1") || (Prefs.getString(PrefsConstants.CLIENT_EMPLOYEE_COUNT, "-1").equals("0"))) {
            //  Prefs.putString(PrefsConstants.CLIENT_EMPLOYEE_COUNT,"0");
            //  textempCount.setText(Prefs.getString(PrefsConstants.CLIENT_EMPLOYEE_COUNT));
            textempCount.setText(Prefs.getString(PrefsConstants.CLIENT_EMPLOYEE_COUNT));
        } else {
            textempCount.setText(Prefs.getString(PrefsConstants.CLIENT_EMPLOYEE_COUNT));
        }

        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_employee:
                openAddEmpScreen();
                break;

            case R.id.add_client_employee:
                openClientEmployeeScreen();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingAttendanceViewModel) getAndroidViewModel(TrainingAttendanceViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_training_attendance;
    }

    private void inflatePostChip(List<PostItem> postList) {

        View view = binding.getRoot();
        ChipGroup chipGroup = view.findViewById(R.id.post_container);
        chipGroup.removeAllViews();
        SparseArray<PostItem> map = new SparseArray<>();
        PostItem commonItem = new PostItem();
        commonItem.postId = 0;
        commonItem.postName = "Group Training";

        postList.add(commonItem);

        for (PostItem s : postList) {
            Chip chip =
                    (Chip) getLayoutInflater().inflate(R.layout.single_post_item_chip, chipGroup, false);
            chip.setText(s.postName);

            chipGroup.addView(chip);
            map.append(chip.getId(), s);


            if (s.postId == 0) {
                chipGroup.check(chip.getId());
                viewModel.updatePost(s);
                viewModel.postItem = s;
                //chip.setTextColor(getResources().getColor(R.color.colorWhite));

            }
        }

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                //Toast.makeText(TrainingAttendanceActivity.this, " Selected : " + map.get(checkedId) + " ID :" + checkedId, Toast.LENGTH_SHORT).show();
                viewModel.updatePost(map.get(checkedId));
                viewModel.postItem = map.get(checkedId);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        canClick = true;
        if (requestCode == REQUEST_CODE_RESOLUTION_REQUIRED) {
            if (resultCode == Activity.RESULT_OK) {
                canClick = false;
                onLocationRequest();
            } else {
                showToast("GPS is needed");
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == PICTURE_REQUEST) {
                if (data != null) {
                    viewModel.selectedEmployeeSet.add(data.getStringExtra("EMP_CODE"));
                    viewModel.selectedPostSet.add(viewModel.postItem.postId);
                    viewModel.refreshRecylerView();
                    updateFab();
                }
            } else if (requestCode == BOTH_REQUEST) {
                if (data != null) {
                    viewModel.selectedEmployeeSet.add(data.getStringExtra("EMP_CODE"));
                    viewModel.selectedPostSet.add(viewModel.postItem.postId);
                    viewModel.refreshRecylerView();
                    updateFab();
                }
            } else if (requestCode == SIGNATURE_REQUEST) {
                if (data != null) {
                    viewModel.selectedEmployeeSet.add(data.getStringExtra("EMP_CODE"));
                    viewModel.selectedPostSet.add(viewModel.postItem.postId);
                    viewModel.refreshRecylerView();
                    updateFab();
                }
            }
        }
    }

    private void updateFab() {
        if (viewModel.selectedEmployeeSet.isEmpty()) {
            binding.flButtonTraining.setAlpha(0.3f);
        } else {
            binding.flButtonTraining.setAlpha(1f);
        }

        //Commenting as per requirement to avoid auto close of dialog after one attendance
        /*if (dialog != null && dialog.isShowing())
            dialog.dismiss();*/
    }

    public void onLocationPermissionRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            checkLocationSetting();
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
                    if (selectedView == BOTH_REQUEST)
                        openBothScreen(employeeDetail);
                    else
                        openPhotoScreen(employeeDetail);
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
                                rae.startResolutionForResult(TrainingAttendanceActivity.this, REQUEST_CODE_RESOLUTION_REQUIRED);
                            } catch (IntentSender.SendIntentException sie) {
                                Timber.i("PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            Timber.e(errorMessage);
                            Toast.makeText(TrainingAttendanceActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationSetting();
                } else {
                    canClick = true;
                    Toast.makeText(this,
                            "Location Permission denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void openClientEmployeeScreen() {
        dialog = new Dialog(TrainingAttendanceActivity.this);
        ClientEmployeeDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(TrainingAttendanceActivity.this), R.layout.client_employee_dialog, null, false);
        dialogBinding.setVm(viewModel);


        dialogBinding.closeBtn.setOnClickListener(v -> {
            if (dialog.isShowing())
                dialog.dismiss();
        });


        dialogBinding.btnAddClientEmp.setOnClickListener(v -> {
            if (dialogBinding.etClientEmployeeNo == null || dialogBinding.etClientEmployeeNo.getText().toString().isEmpty()) {
                showToast("Please enter client employee  number");
            } else {
                String clientEmpCount = dialogBinding.etClientEmployeeNo.getText().toString();

                Prefs.putString(PrefsConstants.CLIENT_EMPLOYEE_COUNT, clientEmpCount);
                //textempCount.setText(clientEmpCount);
                textempCount.setText(Prefs.getString(PrefsConstants.CLIENT_EMPLOYEE_COUNT));
                showToast("Added client employees number successfully");
                dialogBinding.etClientEmployeeNo.setText("");
                dialog.dismiss();

                //  ActionLayoutBinding dialogBinding1 = DataBindingUtil.inflate(LayoutInflater.from(TrainingAttendanceActivity.this),R.layout.action_layout,null,false);
                //   dialogBinding1.clientEmpNumber.setText(s);
            }

        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    AddEmployeeDialogBinding dialogBinding;

    private void openAddEmpScreen() {
        dialog = new Dialog(TrainingAttendanceActivity.this);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(TrainingAttendanceActivity.this), R.layout.add_employee_dialog, null, false);
        dialogBinding.setVm(viewModel);
        dialogBinding.setLifecycleOwner(this);

        dialogBinding.etEmployeeId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.empRegNo.set(s.toString());
            }
        });

        /*dialogBinding.etEmployeeId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.searchText = s.toString();
                viewModel.isAddVisual.set(false);
                viewModel.empName.set("");
                dialogBinding.etEmployeeName.setText("");
                viewModel.canAddEmployeeName.set(false);
            }
        });*/

        dialogBinding.etEmployeeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.employeeName = s.toString();
            }
        });

        dialogBinding.closeBtn.setOnClickListener(v -> {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        });

        dialog.setCanceledOnTouchOutside(false);
//        viewModel.searchText = "";
        viewModel.isAddVisual.set(false);
        viewModel.addEmpLoading.set(false);
        //dialogBinding.efabClick.bringToFront();
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Prefs.putIntOnMainThread(PrefsConstants.SELECTED_EMP_COUNT, viewModel.selectedEmployeeSet.size());
        Prefs.putIntOnMainThread(PrefsConstants.SELECTED_POST_COUNT, viewModel.selectedPostSet.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.recoverAttendanceState();
    }
}
