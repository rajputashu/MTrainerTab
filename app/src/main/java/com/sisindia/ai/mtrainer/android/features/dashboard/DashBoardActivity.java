package com.sisindia.ai.mtrainer.android.features.dashboard;

import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.IRQ_ON_START_TRAINING;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_CODE_RESOLUTION_REQUIRED;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_LOCATION_PERMISSION;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.START_ACTIVITY_FOR_RESULT;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_ROTA_ITEM_CLICK_TMP;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.SHOW_SIS_MAIN_DIALOG;
import static com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendancePictureActivity.decodeSampledBitmapFromFile;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.droidcommons.base.BaseActivity;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.commons.FolderNames;
import com.sisindia.ai.mtrainer.android.constants.Constant;
import com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PreTrainingConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityDashBoardBinding;
import com.sisindia.ai.mtrainer.android.databinding.ActivityImageProfileDailogBinding;
import com.sisindia.ai.mtrainer.android.databinding.AddRotaDialogBinding;
import com.sisindia.ai.mtrainer.android.databinding.SisStatusDialogBinding;
import com.sisindia.ai.mtrainer.android.databinding.SyncStatusViewBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.addrota.AddRotaActivity;
import com.sisindia.ai.mtrainer.android.features.addrota.AddRotaViewmodel;
import com.sisindia.ai.mtrainer.android.features.checkin.CheckInFragment;
import com.sisindia.ai.mtrainer.android.features.myattendance.MyAttendanceFragment;
import com.sisindia.ai.mtrainer.android.features.myconveyance.MyconvenceActivity;
import com.sisindia.ai.mtrainer.android.features.myunits.MyUnitstabFragment;
import com.sisindia.ai.mtrainer.android.features.notifications.NotificationsFragment;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.features.report.ReportActivity;
import com.sisindia.ai.mtrainer.android.features.reports.ReportsActivity;
import com.sisindia.ai.mtrainer.android.features.rota.DashBoardRotaFragment;
import com.sisindia.ai.mtrainer.android.features.rota.WebviewActivity;
import com.sisindia.ai.mtrainer.android.features.rplform.RplFormFragment;
import com.sisindia.ai.mtrainer.android.features.starttraining.StartTraningActivity;
import com.sisindia.ai.mtrainer.android.features.syncadapter.ForcedSyncService;
import com.sisindia.ai.mtrainer.android.features.syncadapter.GpsTracker;
import com.sisindia.ai.mtrainer.android.features.syncadapter.SyncWorker;
import com.sisindia.ai.mtrainer.android.features.timeline.TimeLineFragment;
import com.sisindia.ai.mtrainer.android.features.timeline.TimeLineTableDelete;
import com.sisindia.ai.mtrainer.android.features.trainingcalendar.TrainingCalendarFragment;
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCoursesActivity;
import com.sisindia.ai.mtrainer.android.features.trainingprogram.TrainingProgramActivity;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.UmbrellaReportBaseActivity;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.UmbrellaSPIReportBaseActivity;
import com.sisindia.ai.mtrainer.android.models.SaveTokenRequest;
import com.sisindia.ai.mtrainer.android.models.SaveTokenResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingFinalSubmitResponse;
import com.sisindia.ai.mtrainer.android.models.VanRunningStatusRequest;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

public class DashBoardActivity extends MTrainerBaseActivity {
    //    private static final String TAG = "DashBoardActivity";
    private ActivityDashBoardBinding binding;
    public DashBoardViewModel viewModel;
    private boolean doubleBackToExitPressedOnce = false;
    private Dialog dialog;
    /*final int SELECT_PHOTO = 1;
    private int mReturnCode;
    private int mResultCode;
    private Intent mResultIntent;
    private final boolean mUploadFileOnLoad = false;*/
    private Dialog addRotaDialog;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int PICK_FROM_FILE = 3;
    //    private static final int CAN_DRAW_OVERLAY_REQUEST = 5341;
//    private static final int PENDING_INTENT_REQUEST_CODE = 1213;
    private Dialog syncDialog;
    AuthApi authApi;
    ProgressDialog progressDialog;
    final int CAMERA_CAPTURE = 1;
    GpsTracker gpsTracker;
    //    private Dialog employeeListDialog, profileDialog;
    private Dialog profileDialog;
    //    public static final String GridViewDemo_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ChoosePhoto/";
    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    String _path;
    @Inject
    Picasso picasso;
    SisStatusDialogBinding dialogBinding;
    String road = "";
    String reason = "";
    private int clickedView;
    private boolean canSyncData = true;
    @Inject
    DashBoardApi dashBoardApi;

//    private WorkManager workManager;
//    private AlarmManager alarmManager;

    String[] trainingThrough, onRoad, selectReason;
    ArrayAdapter<String> trainingTypeadapter, OnRoadAdapter, selectReasonAdapter;

    public static Intent newInstance(BaseActivity activity) {
        return new Intent(activity, DashBoardActivity.class);
    }

    @Override
    protected void extractBundle() {
        Intent intent = getIntent();
//        workManager = WorkManager.getInstance(this);
//        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (intent != null && intent.getExtras() != null) {
            String IS_DEBUG = "isDebug";
            String isDebug = intent.getStringExtra(IS_DEBUG);
            if (isDebug != null && isDebug.equals("1"))
                Prefs.putBooleanOnMainThread(PrefsConstants.IS_DEBUG, true);
            else
                Prefs.putBooleanOnMainThread(PrefsConstants.IS_DEBUG, false);
        }
    }

    @Override
    protected void initViewState() {

        liveData.observe(this, message -> {
            switch (message.what) {

                case NavigationConstants.ON_MENU_ITEM_DASHBOARD_ROTA_CLICK:
                    openDashboardRotaScreen();
                    break;

                case NavigationConstants.OPEN_REPORT_PAGE:
                    String url = Constant.BASE_REPORT_PAGE_URL + Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
                    openWebViewPage(url);
//                    new Handler().postDelayed(this::openWebViewPage(url), 250)
                    break;

                case NavigationConstants.OPEN_RAISING_PAGE:
                    binding.dlDashBoard.closeDrawer(GravityCompat.START);
                    new Handler().postDelayed(this::openRaisingPage, 250);
                    break;

                case NavigationConstants.TRAINING_COURSES:
                    openTrainingCoursesScreen();
                    break;

                case NavigationConstants.OPEN_ONLINE_REPORT_WEB_VIEW:
                    String url1 = Constant.WEB_VIEW_SIS_REPORT_URL + Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
                    openWebViewPage(url1);
                    break;

                case NavigationConstants.ON_MENU_ITEM_MY_UNITS_CLICK:
                    openMyUnitsScreen();
                    break;

                case NavigationConstants.ON_MENU_ITEM_TRAINING_CALENDAR:
                    openTrainingCalendarScreen();
                    break;
                case NavigationConstants.ACTION_FORCE_SYNC:
                    viewModel.getPendingDataCount();
                    syncDialog = new Dialog(this);
                    SyncStatusViewBinding syncStatusViewBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.sync_status_view, null, false);
                    syncStatusViewBinding.btnCancel.setOnClickListener(v -> {
                        if (syncDialog != null && syncDialog.isShowing())
                            syncDialog.dismiss();
                    });
                    syncStatusViewBinding.setVm(viewModel);
                    syncDialog.setContentView(syncStatusViewBinding.getRoot());
                    syncDialog.setCanceledOnTouchOutside(false);
                    binding.dlDashBoard.closeDrawer(GravityCompat.START);
                    syncDialog.show();
                    Window window = syncDialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    //Intent forceSyncIntent = new Intent(this, ForcedSyncService.class);
                    //forceSyncIntent.putExtra("LAST_ACCESSED", )
                    //startService(forceSyncIntent);
                    break;

                case NavigationConstants.START_FORCE_SYNC:
                    try {
                        if (syncDialog != null && syncDialog.isShowing())
                            syncDialog.dismiss();
                        startForcedSyncing();
                    } catch (Exception e) {
                        logger.log("NavigationConstants.START_FORCE_SYNC", e);
                    }
                    break;

                case NavigationConstants.REFRESH_SITE_DATA:
                    binding.dlDashBoard.closeDrawer(GravityCompat.START);
                    viewModel.clearAllTable();
                    viewModel.refreshSiteData();
                    break;

                case NavigationConstants.ON_MENU_ITEM_NOTIFICATIONS_CLICK:
                    openNotificationScreen();
                    break;

                case NavigationConstants.ON_MENU_ITEM_TODAY_TIMELINE:
                    openTodayTimelineScreen();
                    break;

                case NavigationConstants.ON_MENU_ITEM_CHECK_IN:
                    openCheckInScreen();
                    break;

                case NavigationConstants.ON_MENU_ITEM_MY_CONVEYANCE:
                    openMyConveyanceScreen();
                    break;

                case NavigationConstants.ON_MENU_ITEM_REPORT_EMPLOYEE:
                    openEmployeeReportScreen();
                    break;

                case NavigationConstants.ON_MENU_ITEM_TRAINING_KIT:
                    //openTrainingKitScreen();
                    /*Intent in = new Intent(this, TrainingkitwebActivity.class);
                    startActivity(in);*/
                    openTrainingKit();
                    break;

                case NavigationConstants.ON_MENU_ITEM_MY_ATTENDANCE:
                    openMyAttendanceScreen();
                    break;

                case NavigationConstants.ON_MENU_ITEM_RPL_FORM:
                    openRplFormScreen();
                    break;

                case NavigationConstants.ON_MENU_ITEM_TICKETS:
                    openTicketsScreen();
                    break;

                case NavigationConstants.CLOSE_DIALOG:
                    closeDialog();
                    break;

                case NavigationConstants.CLOSE_SIS_MAIN_DIALOG:
                    closeSisMainDialog();
                    break;

                case NavigationConstants.START_UMBRELLA_REPORT:
                    startUmbrellaScreen();
                    break;

                case ON_ROTA_ITEM_CLICK_TMP:
                    Log.d("rotaid143 : ", Prefs.getInt(PrefsConstants.ROTA_ID) + "");
                    if (Prefs.getString(PrefsConstants.ROLE).equals("Training Champ")
                            || Prefs.getString(PrefsConstants.ROLE).equals("Unit Commander")) {
                        viewModel.canDelete = false;
                        //  openStartTrainingScreen();
                        initStartTrainingScreen();
                    } else {
                        viewModel.setIsLoading(false);
                        openPreTrainingReviewScreen();
                    }
                    break;

                case NavigationConstants.TRAINING_CHAMP_START_TRAINING_SCREEN:
                    initStartTrainingScreen();
                    break;
                case NavigationConstants.DUTY_STATUS_CHANGED:
                    clickedView = NavigationConstants.DUTY_STATUS_CHANGED;
                    initStartTrainingScreen();
                    break;

                case SHOW_SIS_MAIN_DIALOG:
                    openStatusDialog();
                    break;

                case NavigationConstants.ON_MENU_ITEM_SPI:
                    //openSpiScreen();
                    startSPIUmbrellaScreen();
                    break;

//                case NavigationConstants.CHANGE_LOCATION_SERVICE_STATE:
//                    changeLocationServiceStatus(message.arg1);
//                    break;
            }
        });

        viewModel.getUnitListFromDb().observe(this, unitList -> {
            if (!unitList.isEmpty())
                viewModel.setUnitList(unitList);
        });
    }

    private void openTrainingCoursesScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
//        Intent in = new Intent(this, TrainingCoursesActivity.class);
        Intent in = new Intent(this, TrainingProgramActivity.class);
        startActivity(in);
    }

    private void openTrainingKit() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(/*message -> Timber.d(message)*/);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        builder.addInterceptor(loggingInterceptor);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://mtrainer.sisindia.com/").client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        authApi = retrofit.create(AuthApi.class);
        Date date = new Date();
        long currentdate = date.getTime();
//        Log.d("sadfsadf", Prefs.getLong("TokenTime") + " , " + currentdate);
//        Log.d("sadfsadf", Prefs.getLong("ExpiryTokenTime") + " , " + currentdate);
        if (Prefs.getLong("ExpiryTokenTime") < currentdate) {
            progressDialog = new ProgressDialog(this);
            progressDialog.show();
            SaveTokenRequest saveTokenRequest = new SaveTokenRequest();
            saveTokenRequest.setUserName("ApiAdmin");
            saveTokenRequest.setPassword("India@123");
            saveTokenRequest.setLoginCode(PrefsConstants.EMPLOYEE_REG_NO);

            authApi.gettoken(saveTokenRequest).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<SaveTokenResponse> call, Response<SaveTokenResponse> response) {
                    Prefs.putString("LoginToken", Objects.requireNonNull(response.body()).getLoginCodeToken());
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.HOUR, 8);
                    long formatdate = date.getTime();
                    Date date1 = calendar.getTime();
                    long expirydate = date1.getTime();
                    Prefs.putLong("TokenTime", formatdate);
                    Prefs.putLong("ExpiryTokenTime", expirydate);
                    progressDialog.dismiss();
                    String url = "https://mtrainer.sisindia.com/app/index.html?token=" + response.body().getLoginCodeToken();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    /*Intent in = new Intent(this, TrainingkitwebActivity.class);
                    startActivity(in);*/
                }

                @Override
                public void onFailure(Call<SaveTokenResponse> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        } else {
            String url = "https://mtrainer.sisindia.com/app/index.html?token=" + Prefs.getString("LoginToken", "");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
           /* Intent in = new Intent(this, TrainingkitwebActivity.class);
            startActivity(in);*/
        }
    }

    private void initStartTrainingScreen() {
        onLocationPermissionRequest();
    }

//    private void onDutyChanged(Boolean isOnDuty) {
//
//        startActivityForResult(UserLocationActivity.newIntent(this, isOnDuty), IntentRequestCodes.REQUEST_CODE_IS_DUTY_ON);
//    }

    private void openPreTrainingReviewScreen() {
        startActivityForResult(PreTrainingReviewActivity.newIntent(this), IntentRequestCodes.IRQ_PRE_TRAINING_REVIEW);
    }

    private void openStartTrainingScreen() {
        PreTrainingReviewActivity.selectedAssessmentEmpId.clear();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String startTime = sdf.format(date);
        TrainingFinalSubmitResponse.TrainingSubmitResponse trainingFinalSubmitResponse = new TrainingFinalSubmitResponse.TrainingSubmitResponse();
        trainingFinalSubmitResponse.setRotaid(Prefs.getInt(PrefsConstants.ROTA_ID));
        // trainingFinalSubmitResponse.startTime = startTime;

        // TODO: Its a Dirty Fix need concreate solution
        if (Prefs.getString(PrefsConstants.STARTED_TIME_FOR_SERVER, "-1").equals("-1")) {
            trainingFinalSubmitResponse.startTime = startTime;
            Prefs.putStringOnMainThread(PrefsConstants.STARTED_TIME_FOR_SERVER, startTime);
        } else
            trainingFinalSubmitResponse.startTime = Prefs.getString(PrefsConstants.STARTED_TIME_FOR_SERVER);

        trainingFinalSubmitResponse.startLat = String.valueOf(Prefs.getDouble(PrefsConstants.LAT));
        trainingFinalSubmitResponse.startLong = String.valueOf(Prefs.getDouble(PrefsConstants.LONGI));

        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String time1 = sdf2.format(date);
        // TODO: Its a Dirty Fix need concreate solution
        if (Prefs.getString(PrefsConstants.STARTED_TIME, "-1").equals("-1"))
            Prefs.putStringOnMainThread(PrefsConstants.STARTED_TIME, time1);
        else
            time1 = Prefs.getString(PrefsConstants.STARTED_TIME);

//        Log.d(TAG, "openStartTrainingScreen: New Time : " + time1);
        viewModel.updateTaskStartDateTime(time1);
//        Log.d(TAG, "openStartTrainingScreen: Time Saved in ShardPref : " + time1);

        MtrainerDataBase.getDatabase(this)
                .getTrainingFinalSubmitDao()
                .insertTrainingFinalSubmit(trainingFinalSubmitResponse)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSucessDBSaveTest);
    }

    public void hideRotaButton() {
        binding.fab.setVisibility(View.INVISIBLE);
    }

    public void showRotaButton() {
        binding.fab.setVisibility(View.VISIBLE);
    }

    private void onSucessDBSaveTest() {
        viewModel.setIsLoading(false);
        if (viewModel.canDelete) {
            Intent i = new Intent(this, StartTraningActivity.class);
            Prefs.putBooleanOnMainThread(PrefsConstants.CAN_DELETE_ROTA, true);
            startActivityForResult(i, START_ACTIVITY_FOR_RESULT);
        } else
            startActivityForResult(StartTraningActivity.newIntent(this), IRQ_ON_START_TRAINING);
    }

    private void openDashboardRotaScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        loadFragment(R.id.flDashBoard, DashBoardRotaFragment.newInstance(), FRAGMENT_REPLACE, false);
        binding.fab.setVisibility(View.VISIBLE);
    }

    private void openMyUnitsScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        loadFragment(R.id.flDashBoard, MyUnitstabFragment.newInstance(), FRAGMENT_REPLACE, true);
        binding.fab.setVisibility(View.GONE);
    }

    private void openTrainingCalendarScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        loadFragment(R.id.flDashBoard, TrainingCalendarFragment.newInstance(), FRAGMENT_REPLACE, true);
        binding.fab.setVisibility(View.GONE);
    }

    private void openNotificationScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        loadFragment(R.id.flDashBoard, NotificationsFragment.newInstance(), FRAGMENT_REPLACE, true);
    }

    private void openTodayTimelineScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        loadFragment(R.id.flDashBoard, TimeLineFragment.newInstance(), FRAGMENT_REPLACE, true);
    }

    private void openCheckInScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        loadFragment(R.id.flDashBoard, CheckInFragment.newInstance(), FRAGMENT_REPLACE, true);
        binding.fab.setVisibility(View.GONE);
    }

    private void openMyConveyanceScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(this, MyconvenceActivity.class);
        startActivity(intent);
        //loadFragment(R.id.flDashBoard, MyConveyanceFragment.newInstance(), FRAGMENT_REPLACE, true);
    }

    private void openEmployeeReportScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(this, ReportsActivity.class);
        startActivity(intent);
        //loadFragment(R.id.flDashBoard, MyConveyanceFragment.newInstance(), FRAGMENT_REPLACE, true);
    }

    /*private void openTrainingKitScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        //loadFragment(R.id.flDashBoard, TrainingKitFragment.newInstance(), FRAGMENT_REPLACE, false);
        // Use package name which we want to check
        boolean isAppInstalled = appInstalledOrNot(Constant.LEARNING_APP_PACKAGE_NAME);

        if (isAppInstalled) {
            //This intent will help you to launch if the package is already installed
            Intent LaunchIntent = getPackageManager()
                    .getLaunchIntentForPackage(Constant.LEARNING_APP_PACKAGE_NAME);

            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(LaunchIntent);
        } else {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Constant.LEARNING_APP_PACKAGE_NAME)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Constant.LEARNING_APP_PACKAGE_NAME)));
            }
        }
    }*/

    private void openMyAttendanceScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        loadFragment(R.id.flDashBoard, MyAttendanceFragment.newInstance(), FRAGMENT_REPLACE, true);
    }

    private void openRplFormScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        loadFragment(R.id.flDashBoard, RplFormFragment.newInstance(), FRAGMENT_REPLACE, true);
        binding.fab.setVisibility(View.GONE);
    }

    private void openTicketsScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        // loadFragment(R.id.flDashBoard, QRScannerFragment.newInstance(), FRAGMENT_REPLACE, false);
    }

    /*private void openSpiScreen() {
        AddRotaViewmodel.selectedTopic.clear();
        startActivityForResult(SpiMainActivity.newIntent(this), IntentRequestCodes.IRQ_SPI_DETAIL);
    }*/

    @Override
    protected void onCreated() {

        setupToolBarForDrawer(binding.tbDashBoard, R.drawable.ic_drawer_menu, false);
        openDashboardRotaScreen();
//        viewModel.fetchMasterUnitList();

        if (binding.refreshDashboard != null) {
            binding.refreshDashboard.setOnClickListener(v -> {
                DashBoardRotaFragment.refreshDashboardApi();
                // dashBoardRotaViewModel.getDashboardPerformance();
                // dashBoardRotaViewModel.getTrainingCalendar();
                showToast("Data refreshed successfully");
            });
        }

       /* // below code for logo changes according to company ids
        if(Prefs.getString(PrefsConstants.COMPANY_ID).equals("9"))
        {
            binding.mtrainerLogo.setImageResource(R.drawable.ic_uniq_logo);
           // binding.headerId.mtrainerLogo.setImageResource(R.drawable.ic_uniq_logo);
        }
        else if(Prefs.getString(PrefsConstants.COMPANY_ID).equals("7")){
            binding.mtrainerLogo.setImageResource(R.drawable.ic_slv_logo);
           // binding.headerId.mtrainerLogo.setImageResource(R.drawable.ic_slv_logo);
        }
        else {
            binding.mtrainerLogo.setImageResource(R.drawable.ic_sis_bg_logo);
          //  binding.headerId.mtrainerLogo.setImageResource(R.drawable.ic_sis_bg_logo);
        }*/

        gpsTracker = new GpsTracker(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    mLastLocation = locationResult.getLastLocation();
                    Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                    Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                    if (clickedView == NavigationConstants.DUTY_STATUS_CHANGED)
                        viewModel.updateDutyStatus();
                    else
                        openStartTrainingScreen();
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                } else {
                    viewModel.setIsLoading(false);
                    showToast("Error fetching location");
                }
            }
        };

        /*Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();*/

      /*  WorkRequest immediateSync = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(constraints)
                .build();
        Log.d(TAG, "onCreated: WorkManager Work Enqueue");
        WorkManager.getInstance(this).enqueue(immediateSync);
*/

        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(TimeLineTableDelete.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance().enqueue(periodicWork);

     /*   // Syncing If saved offline
        Bundle b = new Bundle();
        SyncAdapterInitialization syncAdapterInitialization = new SyncAdapterInitialization(this);
        syncAdapterInitialization.startForceSyncing(b);
*/
        binding.fab.setOnClickListener(view -> {
            if (Prefs.getString(PrefsConstants.COMPANY_ID).equals("1")
                    || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4")
                    || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8")) {
                if (!Prefs.getString(PrefsConstants.ROLE).equals("Training Champ"))
                    openAddRotaScreen();
                else {
                    if (Prefs.getBoolean(PrefsConstants.IS_ON_DUTY)) {
                        viewModel.trainingConductType = 0;
                        viewModel.fetchUnitList();
                        addRotaDialog = new Dialog(DashBoardActivity.this);
                        AddRotaDialogBinding addRotaDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(DashBoardActivity.this), R.layout.add_rota_dialog, null, false);
                        addRotaDialogBinding.setVm(viewModel);
                        addRotaDialog.setContentView(addRotaDialogBinding.getRoot());
                        addRotaDialog.setCanceledOnTouchOutside(false);
                        addRotaDialog.show();
                        Window window = addRotaDialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    //viewModel.createImmediateRota();
                    else
                        showToast("Please Start Duty");
                }
            } else {
                if (!Prefs.getString(PrefsConstants.ROLE).equals("Unit Commander"))
                    openAddRotaScreen();
                else {
                    if (Prefs.getBoolean(PrefsConstants.IS_ON_DUTY))
                        viewModel.createImmediateRota();
                    else
                        showToast("Please Start Duty");
                }
            }
        });


       /* if (isExternalStorageWritable())
        {
            File appDirectory = new File( Environment.getExternalStorageDirectory() + "/Mtrainertab" );
            File logDirectory = new File( appDirectory + "/log" );
            File logFile = new File( logDirectory, "logcat" + System.currentTimeMillis() + ".txt" );

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
        } else {
            // not accessible
        }
*/

        binding.headerId.ivUserPic.setOnClickListener(view -> {
            //onLocationPermissionRequest();
            openBottomUserImageDailog();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_dash_board, menu);
//        return super.onCreateOptionsMenu(menu);
        return false;
    }

    private void openAddRotaScreen() {
        AddRotaViewmodel.selectedTopic.clear();
        startActivityForResult(AddRotaActivity.newIntent(this), IntentRequestCodes.IRQ_ON_ROTA_DETAIL);
    }

    private void startUmbrellaScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(this, UmbrellaReportBaseActivity.class);
        startActivity(intent);
    }

    private void startSPIUmbrellaScreen() {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(this, UmbrellaSPIReportBaseActivity.class);
        startActivity(intent);
    }


    @Override
    protected void initViewBinding() {
        binding = (ActivityDashBoardBinding) bindActivityView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            binding.dlDashBoard.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModel() {
        viewModel = (DashBoardViewModel) getAndroidViewModel(DashBoardViewModel.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        canSyncData = false;
        if (resultCode != RESULT_OK) return;
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentRequestCodes.REQUEST_CODE_IS_DUTY_ON:
                viewModel.isOnDuty.set(true);
                Prefs.putBoolean(PrefsConstants.IS_ON_DUTY, true);
                break;
            case REQUEST_CODE_RESOLUTION_REQUIRED:
                if (resultCode == Activity.RESULT_OK) {
                    onLocationRequest();
                } else {
                    viewModel.setIsLoading(false);
                    showToast("GPS is needed");
                }
                break;

            case PICK_FROM_FILE:
                //profileCaptureUri = data.getData();
                // doCrop();
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    File photos = new File(picturePath);

                    Bitmap bitmapvalue2 = decodeSampledBitmapFromFile(photos.getAbsolutePath(), 640, 480);

                    if (bitmapvalue2 != null) {
                        FileOutputStream fos = new FileOutputStream(picturePath);
                        bitmapvalue2.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.close();
                    }

                    binding.headerId.ivUserPic.setImageBitmap(bitmapvalue2);
                    Prefs.putString(PrefsConstants.IMAGE_PATH, photos.getAbsolutePath());
                    //performCrop(selectedImage);
                } catch (Exception ignored) {
                }
                break;
            case CAMERA_CAPTURE:
//                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                String watermark = Prefs.getString(PrefsConstants.EMPPLOYEE_NAME) + " " + sdf.format(new Date()) + " " + "Lat : " + Prefs.getDouble(PrefsConstants.LAT) + " " + "Long : " + Prefs.getDouble(PrefsConstants.LONGI);
                // viewModel.saveImage(_path);
                setPreview(_path);

                break;
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_dash_board;
    }

    /*private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }*/

    private void closeDialog() {
        if (addRotaDialog != null)
            addRotaDialog.dismiss();
    }

    private void closeSisMainDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    private void openWebViewPage(String url) {
        binding.dlDashBoard.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(Constant.WEB_VIEW_URL_KEY, url);
        startActivity(intent);

        /*final String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36";
        new FinestWebView.Builder(this)
                .webViewUserAgentString(DESKTOP_USER_AGENT)
                .webViewBuiltInZoomControls(true)
                .webViewAppCacheEnabled(true)
                .webViewJavaScriptEnabled(true)
                .webViewJavaScriptCanOpenWindowsAutomatically(true)
                .webViewDomStorageEnabled(true)
                .titleDefault("Report")
                .showUrl(false)
                .webViewLoadsImagesAutomatically(true)
                .showIconMenu(false)
                .webViewAllowFileAccessFromFileURLs(true)
                .webViewAllowUniversalAccessFromFileURLs(true)
                .updateTitleFromHtml(false)
                .webViewMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
                .show(url);*/
    }

    private void openRaisingPage() {

        Intent intent = new Intent(this, WebviewActivity.class);
        startActivity(intent);

        /*final String url = Constant.BASE_RAISING_PAGE_URL + Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);

        final String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36";

        new FinestWebView.Builder(this)
                .webViewUserAgentString(DESKTOP_USER_AGENT)
                .webViewBuiltInZoomControls(true)
                .webViewCacheMode(WebSettings.LOAD_NO_CACHE)
                .webViewJavaScriptEnabled(true)
                .webViewUseWideViewPort(true)
                .webViewLoadWithOverviewMode(true)
                .webViewAllowContentAccess(true)
                .webViewJavaScriptCanOpenWindowsAutomatically(true)
                .webViewDomStorageEnabled(true)
                .titleDefault("Report")
                .showUrl(false)
                .showIconMenu(false)
                .webViewAllowFileAccessFromFileURLs(true)
                .webViewAllowUniversalAccessFromFileURLs(true)
                .updateTitleFromHtml(false)
                .webViewLoadsImagesAutomatically(true)
                .webViewMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
                .setWebViewListener(new WebViewListener() {
                    @Override
                    public void onLoadResource(String url) {
                        if (url.contains("file1")){
                            Log.d("file","file1");
                        }
                    }
                })
                .webViewAllowFileAccess(true)
                .show(url);*/

        /*WebView theWebPage = new WebView(this);
        theWebPage.getSettings().setBuiltInZoomControls(true);
        theWebPage.getSettings().setUserAgentString(DESKTOP_USER_AGENT);
        theWebPage.getSettings().setAppCacheEnabled(true);
        theWebPage.getSettings().setJavaScriptEnabled(true);
        theWebPage.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        theWebPage.getSettings().setDomStorageEnabled(true);
        theWebPage.getSettings().setAllowFileAccessFromFileURLs(true);
        theWebPage.getSettings().setAllowUniversalAccessFromFileURLs(true);
        theWebPage.getSettings().setLoadsImagesAutomatically(true);
        theWebPage.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        theWebPage.getSettings().setAllowFileAccess(true);
        theWebPage.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);
            }

        });
        theWebPage.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return false;
            }
        });*/
        //theWebPage.loadUrl(url);

    }

    /*@Override
    public void onBackPressed() {
        OnBoardViewModel.apiCalled = false;
        super.onBackPressed();
    }*/

    public void onLocationPermissionRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            //checkLocationSetting();
            if (gpsTracker.canGetLocation()) {
                if (gpsTracker.getLatitude() != 0.0 && gpsTracker.getLongitude() != 0.0) {
                    if (gpsTracker.getLocation() != null && (new Date().getTime() - gpsTracker.getLocation().getTime()) <= 120000) {
                        Prefs.putDouble(PrefsConstants.LAT, gpsTracker.getLocation().getLatitude());
                        Prefs.putDouble(PrefsConstants.LONGI, gpsTracker.getLocation().getLongitude());
                        Log.v("Location", "Lat : " + gpsTracker.getLocation().getLatitude() + "  Long : " + gpsTracker.getLocation().getLongitude() + "  Time : " + gpsTracker.getLocation().getTime() + " Time Diff : " + (new Date().getTime() - gpsTracker.getLocation().getTime()));
                        if (clickedView == NavigationConstants.DUTY_STATUS_CHANGED)
                            viewModel.updateDutyStatus();
                        else
                            openStartTrainingScreen();
                        //mFusedLocationClient.requestLocationUpdates(getLocationRequest(), new LocationCallback(), null);
                    } else {
                        checkLocationSetting();
                    }
                } else
                    checkLocationSetting();
                //mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
            }
            Log.d("Pre Training", "getLocation: permissions granted");
        }
    }

    private void onLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && (new Date().getTime() - location.getTime()) <= 120000) {
                Prefs.putDouble(PrefsConstants.LAT, location.getLatitude());
                Prefs.putDouble(PrefsConstants.LONGI, location.getLongitude());
//                Log.v("Location", "Lat : " + location.getLatitude() + "  Long : " + location.getLongitude() + "  Time : " + location.getTime() + " Time Diff : " + (new Date().getTime() - location.getTime()));
                if (clickedView == NavigationConstants.DUTY_STATUS_CHANGED)
                    viewModel.updateDutyStatus();
                else
                    openStartTrainingScreen();
                mFusedLocationClient.requestLocationUpdates(getLocationRequest(), new LocationCallback(), null);
            } else
                mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
        });
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
                    onLocationRequest();
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(DashBoardActivity.this, REQUEST_CODE_RESOLUTION_REQUIRED);
                            } catch (IntentSender.SendIntentException sie) {
                                Timber.i("PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            viewModel.setIsLoading(false);
                            Timber.e(errorMessage);
                            Toast.makeText(DashBoardActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //checkLocationSetting();
                if (gpsTracker.getLocation() != null && (new Date().getTime() - gpsTracker.getLocation().getTime()) <= 120000) {
                    if (gpsTracker.getLatitude() != 0.0 && gpsTracker.getLongitude() != 0.0) {
                        Prefs.putDouble(PrefsConstants.LAT, gpsTracker.getLocation().getLatitude());
                        Prefs.putDouble(PrefsConstants.LONGI, gpsTracker.getLocation().getLongitude());
                        if (clickedView == NavigationConstants.DUTY_STATUS_CHANGED)
                            viewModel.updateDutyStatus();
                        else
                            openStartTrainingScreen();
                        //mFusedLocationClient.requestLocationUpdates(getLocationRequest(), new LocationCallback(), null);
                    } else {
                        checkLocationSetting();
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
                }
            } else {
                viewModel.setIsLoading(false);
                Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (canSyncData) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            WorkRequest immediateSync = new OneTimeWorkRequest.Builder(SyncWorker.class)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance(this).enqueue(immediateSync);
            canSyncData = false;
        }

        if (Prefs.getInt(PrefsConstants.PRE_TRAINING_STATE, PreTrainingConstants.STARTED_FOR_SITE) != PreTrainingConstants.STARTED_FOR_SITE) {
            // openPreTrainingReviewScreen();
        } else if (!Prefs.getString(PrefsConstants.STARTED_TIME, "-1").equals("-1")) {
            Intent i = new Intent(this, StartTraningActivity.class);
            i.putExtra("CAN_DELETE", Prefs.getBoolean(PrefsConstants.CAN_DELETE_ROTA, false));
            startActivityForResult(i, START_ACTIVITY_FOR_RESULT);
        }
    }

    // below code for image profile adding
    private void openBottomUserImageDailog() {
        profileDialog = new Dialog(this);
        ActivityImageProfileDailogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_image_profile_dailog, null, false);
        dialogBinding.setVm(viewModel);
        dialogBinding.cameraCancel.setOnClickListener(v -> profileDialog.dismiss());

        dialogBinding.galleryOpt.setOnClickListener(v -> {
            try {
                profileDialog.dismiss();
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_FROM_FILE);
            } catch (Exception e) {
                Log.e("gallery", e.toString());
            }
        });

        dialogBinding.cameraOpt.setOnClickListener(v -> {
                    profileDialog.dismiss();
                    takeImage();
                }
        );

        profileDialog.setCanceledOnTouchOutside(false);
        profileDialog.setContentView(dialogBinding.getRoot());
        Objects.requireNonNull(profileDialog.getWindow()).setGravity(Gravity.BOTTOM);
        profileDialog.show();
        Window window = profileDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void takeImage() {
        try {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUriV2());
            viewModel.setIsLoading(false);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Device doesn't support capturing images!";
            viewModel.setIsLoading(false);
            // isClicked = false;
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void setPreview(String imagePath) {
//        picasso.load(new File(imagePath))
        picasso.load(new File(FileUtils.getAbsolutePathFromString(imagePath)))
                .fit()
                .into(binding.headerId.ivUserPic);
    }

    /*private Uri getFileUri() {
        String imgcurTime = dateFormat.format(new Date());
        File imageDirectory = new File(GridViewDemo_ImagePath);
        imageDirectory.mkdirs();
        _path = GridViewDemo_ImagePath + "_" + Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + "_" + imgcurTime + ".jpg";
        File file = new File(_path);
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
    }*/

    private Uri getFileUriV2() {

        String rootPath = FileUtils.getRootPathV2(this, FolderNames.ChoosePhoto);
        if (FileUtils.createOrExistsDir(rootPath)) {
            _path = FileUtils.createFilePathV2(rootPath, "" + Prefs.getInt(PrefsConstants.ROTA_ID));
            File file = FileUtils.getFileByPath(_path);
            try {
                if (file != null && (file.exists() || file.createNewFile())) {
                    return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                }
            } catch (Throwable t) {
                Log.e("ERROR", "Error compressing file." + t);
            }
        }
        return null;
    }

    public void openStatusDialog() {
        final int[] positionselected = {0};
        final int[] onroadpositionselected = {0};
        final int[] selectedreason = {0};
        dialog = new Dialog(this);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.sis_status_dialog, null, false);
        dialogBinding.setVm(viewModel);

        trainingThrough = getResources().getStringArray(R.array.trainingType);
        trainingTypeadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trainingThrough);
        dialogBinding.spinnerTrainingType.setAdapter(trainingTypeadapter);
        onRoad = getResources().getStringArray(R.array.onRoadType);
        OnRoadAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, onRoad);
        dialogBinding.spinnerOnRoad.setAdapter(OnRoadAdapter);

        selectReason = getResources().getStringArray(R.array.selectReason);
        selectReasonAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectReason);
        dialogBinding.spinnerSelectReason.setAdapter(selectReasonAdapter);

        dialogBinding.closeBtn.setOnClickListener(v -> {
            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialogBinding.spinnerSelectReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedreason[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialogBinding.spinnerTrainingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

                switch (position) {
                    case 0:
                        dialogBinding.spinnerTrainingType.setSelection(((ArrayAdapter) trainingTypeadapter).getPosition(0));
                        dialogBinding.llOnRoad.setVisibility(View.GONE);
                        dialogBinding.llSelectReason.setVisibility(View.GONE);
                        positionselected[0] = position;
                        break;
                    case 1:
                        dialogBinding.spinnerTrainingType.setSelection(((ArrayAdapter) trainingTypeadapter).getPosition(1));
                        //reason="NA";
                        //road="NA";
                        dialogBinding.llOnRoad.setVisibility(View.GONE);
                        dialogBinding.llSelectReason.setVisibility(View.GONE);
                        positionselected[0] = position;
                        break;
                    case 2:
                        dialogBinding.spinnerTrainingType.setSelection(((ArrayAdapter) trainingTypeadapter).getPosition(2));
                        dialogBinding.llOnRoad.setVisibility(View.VISIBLE);
                        dialogBinding.llSelectReason.setVisibility(View.GONE);
                        positionselected[0] = position;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialogBinding.spinnerOnRoad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

                switch (position) {
                    case 0:
                        dialogBinding.spinnerOnRoad.setSelection(((ArrayAdapter) OnRoadAdapter).getPosition(0));

                        dialogBinding.llSelectReason.setVisibility(View.GONE);
                        onroadpositionselected[0] = position;
                        break;
                    case 1:
                        dialogBinding.spinnerOnRoad.setSelection(((ArrayAdapter) OnRoadAdapter).getPosition(1));
                        // reason="NA";
                      /*  if(position==1){
                            road="YES";

                        }*/
                        /// road= dialogBinding.spinnerOnRoad.getSelectedItem().toString();
                        dialogBinding.llSelectReason.setVisibility(View.GONE);
                        onroadpositionselected[0] = position;

                        break;
                    case 2:
                        dialogBinding.spinnerOnRoad.setSelection(((ArrayAdapter) OnRoadAdapter).getPosition(2));
                        dialogBinding.llSelectReason.setVisibility(View.VISIBLE);
                        //  road=dialogBinding.spinnerOnRoad.getSelectedItem().toString();
                        //  reason=dialogBinding.spinnerSelectReason.getSelectedItem().toString();
                       /* if(position==2){
                            road="NO";
                        }
                        reason="";*/
                        onroadpositionselected[0] = position;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Window window = dialog.getWindow();
        int width = getResources().getDimensionPixelSize(R.dimen._350sdp);
        //int height = getResources().getDimensionPixelSize(R.dimen._150sdp);

        window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogBinding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (!(trainingThrough[positionselected[0]].equals("Select")) || !onRoad[onroadpositionselected[0]].equals("Select")
                            || !selectReason[selectedreason[0]].equals("Select")
                            || dialogBinding.spinnerTrainingType.getSelectedItem().toString().equals("TAB")) {
                        // reason="NA";
                        // road="NA";

                        getVanSpinner();
                    } else if (dialogBinding.spinnerTrainingType.getSelectedItem().toString().equals("VAN") && dialogBinding.spinnerOnRoad.getSelectedItem().toString().equals("YES")) {

                        // road="NA";
                        getVanSpinner();
                    } else if (dialogBinding.spinnerTrainingType.getSelectedItem().toString().equals("VAN") && dialogBinding.spinnerOnRoad.getSelectedItem().toString().equals("Select")) {
                        showToast("Select OnRoad");

                    } else if (dialogBinding.spinnerOnRoad.getSelectedItem().toString().equals("NO") && dialogBinding.spinnerSelectReason.getSelectedItem().toString().equals("Select")) {
                        showToast("Select Reason");

                    } else {
                        showToast("Please Select Training type");
                    }
                } catch (Exception ignored) {

                }
            }

            public void getVanSpinner() {

                int trainerId = (Prefs.getInt(PrefsConstants.BASE_TRAINER_ID));
                int companyId = Integer.parseInt((Prefs.getString(PrefsConstants.COMPANY_ID)));

                if (dialogBinding.spinnerOnRoad.getSelectedItem().toString().equals("Select")) {
                    reason = "NA";
                    road = "NA";
                } else if (dialogBinding.spinnerOnRoad.getSelectedItem().toString().equals("YES")) {
                    road = "YES";
                    reason = "NA";
                } else {
                    road = dialogBinding.spinnerOnRoad.getSelectedItem().toString();
                    reason = dialogBinding.spinnerSelectReason.getSelectedItem().toString();
                }

                VanRunningStatusRequest request = new VanRunningStatusRequest(trainerId, companyId, dialogBinding.spinnerTrainingType.getSelectedItemPosition(), road, reason);
                viewModel.sendSisDialogDataToServer(request);
                dialogBinding.save.setEnabled(false);
            }
        });
    }

    private synchronized void startForcedSyncing() {
        long lastCalled = Prefs.getLong(PrefsConstants.LAST_SYNC_API_CALL_TIME, 0);
        long currentTimeStamp = System.currentTimeMillis();
        /*if ((currentTimeStamp - lastCalled) < (5 * 60 * 1000)) {
            final int waitTime = (5 - (int) Math.ceil((currentTimeStamp - lastCalled) / (60 * 1000.0)));
            Toast.makeText(this.getApplication(), "Syncing in process, try after " + (waitTime == 0 ? "sometime" : waitTime + " min"), Toast.LENGTH_SHORT).show();
        } else {*/
        Prefs.putLongOnMainThread(PrefsConstants.LAST_SYNC_API_CALL_TIME, currentTimeStamp);

        Intent forceSyncIntent = new Intent(this, ForcedSyncService.class);
        //forceSyncIntent.setPackage("com.sisindia.ai.mtrainer.android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, forceSyncIntent);
        } else {
            startService(forceSyncIntent);
        }
    }

    /*private void changeLocationServiceStatus(int state) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent =
                    new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CAN_DRAW_OVERLAY_REQUEST);
        }

        if (state == 1) {
            Intent intent = new Intent(this, LocationService.class);
            intent.putExtra("STATUS", 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            Intent receiverIntent = new Intent(this, AutoStartServiceReciver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, PENDING_INTENT_REQUEST_CODE, receiverIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            PeriodicWorkRequest request = new PeriodicWorkRequest
                    .Builder(LocationWorker.class, 15, TimeUnit.MINUTES)
                    .addTag(NavigationConstants.LOCATION_WORK_REQUEST_TAG)
                    .build();
            workManager.enqueue(request);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        System.currentTimeMillis() + 10 * 60 * 1000,
                        pendingIntent
                );
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent receiverIntent = new Intent(this, AutoStartServiceReciver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this, PENDING_INTENT_REQUEST_CODE, receiverIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
                alarmManager.cancel(pendingIntent);
            }
            workManager.cancelAllWorkByTag(NavigationConstants.LOCATION_WORK_REQUEST_TAG);
            Intent intent = new Intent(this, LocationService.class);
            intent.putExtra("STATUS", 0);
            startService(intent);
        }
    }*/

    private void showAppCloseSnackBar() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;

        // Find the root view of your layout
        View rootView = findViewById(android.R.id.content); // or use a specific root view if you have one

        Snackbar snackBar = Snackbar.make(rootView, "Press again to exit", Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightRed));
        snackBar.show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    public void showRefreshButton(boolean show) {
        viewModel.showSyncButton.set(show);
    }


    @Override
    public void onBackPressed() {
        showAppCloseSnackBar();
    }
}