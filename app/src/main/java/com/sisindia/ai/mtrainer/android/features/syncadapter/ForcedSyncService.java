package com.sisindia.ai.mtrainer.android.features.syncadapter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.droidcommons.base.BaseService;
import com.droidcommons.preference.Prefs;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ktx.Firebase;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AssementEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedback;
import com.sisindia.ai.mtrainer.android.db.entities.TrainingPhotoAttachmentEntity;
import com.sisindia.ai.mtrainer.android.models.DashboardRequest;
import com.sisindia.ai.mtrainer.android.models.ImageDataUploadResponse;
import com.sisindia.ai.mtrainer.android.models.ImageUploadRequest;
import com.sisindia.ai.mtrainer.android.models.ImageUploadResponse;
import com.sisindia.ai.mtrainer.android.models.SyncApiResponse;
import com.sisindia.ai.mtrainer.android.models.TrainerPerformanceResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingFinalSubmitRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingFinalSubmitResponse;
import com.sisindia.ai.mtrainer.android.models.sync.AdhocAssessmentItem;
import com.sisindia.ai.mtrainer.android.models.sync.AdhocAttendanceItem;
import com.sisindia.ai.mtrainer.android.models.sync.AssessmentReport;
import com.sisindia.ai.mtrainer.android.models.sync.ClientHandshakeDetails;
import com.sisindia.ai.mtrainer.android.models.sync.ClientReport;
import com.sisindia.ai.mtrainer.android.models.sync.RotaDetails;
import com.sisindia.ai.mtrainer.android.models.sync.TrainingAttendanceItem;
import com.sisindia.ai.mtrainer.android.models.sync.TrainingPicturesItem;
import com.sisindia.ai.mtrainer.android.models.sync.UserData;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.SyncApi;
import com.sisindia.ai.mtrainer.android.utils.StringUtils;
import com.sisindia.ai.mtrainer.android.utils.WaterMarkUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class ForcedSyncService extends BaseService {
    private static final String TAG = "ForcedSyncService";
    private static final String CHANNEL_ID = "mtrainer_force_sync_channel";
    private static final int NOTIFICATION_ID = 1011;
    private NotificationManager mNotificationManager;
    private MtrainerDataBase dataBase;
    private SyncApi syncApi;
    private DashBoardApi dashBoardApi;
   /* @Inject
    DashBoardApi dashBoardApi;
    @Inject
    SyncApi syncApi;*/
   private Logger logger;
    private NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        FirebaseApp.initializeApp(this);
        dataBase = MtrainerDataBase.getDatabase(this);
        logger = new Logger(TAG);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Toast.makeText(this, "Force Sync Started", Toast.LENGTH_SHORT).show();
        initRetrofit();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, getNotification());
        uploadData();
        return START_NOT_STICKY;
    }


    private void uploadData() {
        //logger.log("uploadData: Start "+dashBoardApi);
        Log.d(TAG, "uploadData: Start " + dashBoardApi);
        addDisposable(dataBase.getTrainingFinalSubmitDao().getSavedRota()
                .subscribeOn(Schedulers.io())
                .flatMap(data -> {
                    if (data.isEmpty()) {
                        //logger.log("uploadData: Start data is empty");
                        return dataBase.getAttendancePhotoDao().getSavedAttendancePicForForced();
                    }else {
                        try {
                            //logger.log("uploadRotaData: Start data");
                            uploadRotaData(data);
                        }catch (Exception e){
                            logger.log("uploadRotaData: Start data exception"+e.getMessage());
                            Log.d(TAG,"Exception calling uploadRotaData method",e.getCause());
                        }
                        return dataBase.getAttendancePhotoDao().getSavedAttendancePicForForced();
                    }
                }).flatMap(attendancePhotoEntities -> {
                    if (attendancePhotoEntities.isEmpty()) {
                        //logger.log("Force sync attendancePhotoEntities is empty");
                        Log.d(TAG,"Force sync attendancePhotoEntities is empty");
                        return dataBase.getPhotoAttachmentDao().getTrainingPhotoForForceSync();
                    }else {
                        //logger.log("Force sync calling uploadAttendanceImage method "+attendancePhotoEntities.size());
                        try {
                            //logger.log("Force sync calling uploadAttendanceImage try method "+attendancePhotoEntities.size());
                            uploadAttendanceImage(attendancePhotoEntities);
                        }catch (Exception e){
                            logger.log("Force sync calling uploadAttendanceImage method exception"+e.getMessage());
                            Log.d(TAG,"Force sync attendancePhotoEntities exception",e.getCause());
                        }
                        return dataBase.getPhotoAttachmentDao().getTrainingPhotoForForceSync();
                    }
                }).flatMap(trainingPhotoAttachmentEntities -> {
                    if (trainingPhotoAttachmentEntities.isEmpty()) {
                       // logger.log("Force sync trainingPhotoAttachmentEntities is empty");
                        Log.d(TAG,"Force sync trainingPhotoAttachmentEntities is empty");
                        List<AssementEntity> assementEntityList = dataBase.getAssessmentDao().getAssessmentVideoForSync();
                                               //logger.logDb(assementEntityList, "Assessement Table");
                                               return Single.just(assementEntityList);
                    } else {
                       // logger.log("Force sync calling uploadTrainingImage method "+trainingPhotoAttachmentEntities.size());
                        try {
                            //logger.log("Force sync calling uploadTrainingImage try method "+trainingPhotoAttachmentEntities.size());
                            uploadTrainingImage(trainingPhotoAttachmentEntities);
                        }catch (Exception e){
                            logger.log("Force sync calling uploadTrainingImage method exception"+e.getMessage());
                            Log.d(TAG,"Force sync trainingPhotoAttachmentEntities exception "+e.getMessage(),e.getCause());
                        }
                        return dataBase.getAssessmentDao().getAssessmentVideoForForceSync();
                    }
                }).map(assementEntityList -> {
                    if (assementEntityList.isEmpty()) {
                        //logger.log("Force sync assementEntityList is empty");
                        Log.d(TAG,"Force sync assementEntityList is empty");
                        stopSelf();
                    }else {
                        //logger.log("Force sync calling uploadAssessmentVideo method "+assementEntityList.size());
                        Log.d(TAG,"Force sync calling uploadAssessmentVideo method "+assementEntityList.size());
                        try {
                            //logger.log("Force sync calling uploadAssessmentVideo try method "+assementEntityList.size());
                            uploadAssessmentVideo(assementEntityList);
                        }catch (Exception e){
                            logger.log("Force sync assementEntityList exception"+e.getMessage());
                            Log.d(TAG,"Force sync assementEntityList exception",e.getCause());
                        }
                    }
                        return true;
                        }
                )
                .subscribe(finalResult ->{
                    //logger.log("Force sync finalResult "+finalResult);
                    Log.d(TAG,"Force sync finalResult "+finalResult);
                    stopSelf();
                } , th ->{
                    logger.log("Exception in force sync finalResult"+th.getMessage());
                    Log.d(TAG,"Exception in force sync finalResult",th);
                    stopSelf();
                }));
    }

    private void uploadRotaData(List<TrainingFinalSubmitResponse.TrainingSubmitResponse> responseList) {
        Log.d(TAG, "getSavedRota: List Size : " + responseList.size());
        int PROGRESS_CURRENT = 1;
        int total = responseList.size();
        for (TrainingFinalSubmitResponse.TrainingSubmitResponse response : responseList) {
            builder.setContentText("Syncing Rota Data (" + PROGRESS_CURRENT + "/" + total + ")");
            builder.setProgress(total, PROGRESS_CURRENT, false);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
            List<AttendanceEntity> attendanceEntityList = dataBase.getAttendanceDao().getAttendanceSubList(response.rotaid);

            List<AttendanceEntity> AdhocDirtyAttendance = dataBase.getAttendanceDao().getAdhocAttendanceList(response.rotaid);

            List<String> trainingPhotoAttachmentPostList = dataBase.getPhotoAttachmentDao().getTrainingPostList(response.rotaid);
            List<TrainingPicturesItem> syncTrainingPictureList = new ArrayList<>();
            syncTrainingPictureList = getTrainingPicture(trainingPhotoAttachmentPostList, response.rotaid);

            List<AssessmentReport> syncAssessmentReport = new ArrayList<>();
            syncAssessmentReport = dataBase.getAssessmentDao().getAssessmentReportForSync(response.rotaid);

            List<AssementEntity> dirtyAssessment = dataBase.getAssessmentDao().getAdHocAssessmentReportForSync(response.rotaid);
            List<AdhocAssessmentItem> adhocAssessmentItemList = getAdhocAssesmentList(dirtyAssessment);

            SavedFeedback savedFeedback = dataBase.getSavedFeedbackDao().getFeedback(String.valueOf(response.rotaid));
            ClientHandshakeDetails syncClientHandshakeDetails = new ClientHandshakeDetails();
            if (savedFeedback == null) {
                List<String> reason = new ArrayList<>();
                syncClientHandshakeDetails.setFeedback(reason);
            } else {
                syncClientHandshakeDetails = getClientHandshakeDetails(savedFeedback, String.valueOf(response.rotaid));
            }

            ClientReport clientReport = new ClientReport();
            clientReport.setCC(dataBase.getSavedClientReportCcDao().getClientCcList(response.rotaid));
            clientReport.setTo(dataBase.getSavedClientReportToDao().getClientEmailList(response.rotaid));

            RotaDetails syncRotaDetails = new RotaDetails();
            syncRotaDetails.setCompanyId(Prefs.getString(PrefsConstants.COMPANY_ID));
            syncRotaDetails.setRotaId(String.valueOf(response.rotaid));
            List<TrainingAttendanceItem> syncAttendance = new ArrayList<>();
            List<AdhocAttendanceItem> adhocAttendanceItemList = new ArrayList<>();
            try {
                adhocAttendanceItemList = getAdhocAttendanceList(AdhocDirtyAttendance);
            }catch (Exception e){

            }
            try {
                syncAttendance = getAttendenceData(attendanceEntityList);
            }catch (Exception e){

            }
            try {
                if (syncAttendance!=null && adhocAttendanceItemList!=null) {
                    int totallength = syncAttendance.size() + adhocAttendanceItemList.size();
                    syncRotaDetails.setActualTraineeCount("" + totallength);
                }else {
                    syncRotaDetails.setActualTraineeCount("0");
                }
            }catch (Exception e){

            }
           // syncRotaDetails.setMultipleSession(response.isMultipleSession ? "1" : "0");
            UserData syncUserData = new UserData();
            syncUserData.setTrainingAttendance(syncAttendance);
            syncUserData.setAdhocAttendance(adhocAttendanceItemList);
            syncUserData.setClientHandshakeDetails(syncClientHandshakeDetails);
            syncUserData.setRotaDetails(syncRotaDetails);
            syncUserData.setTrainingPictures(syncTrainingPictureList);
            syncUserData.setAssessment(syncAssessmentReport);
            syncUserData.setAdhocAssessment(adhocAssessmentItemList);
            syncUserData.setadhocSavedTopics(getAdhocFinalList(response.rotaid));
            syncUserData.setStartLatitude(response.startLat);
            syncUserData.setStartLongitude(response.startLong);
            syncUserData.setEndLatitude(response.endLat);
            syncUserData.setEndLongitude(response.endLong);
            syncUserData.setSelectedTopicsID(getTopicIdList(response.rotaid));
            syncUserData.setClientReport(clientReport);
            TrainingFinalSubmitRequest trainingFinalSubmitRequest = new TrainingFinalSubmitRequest();
            trainingFinalSubmitRequest.rotaid = response.rotaid;
            trainingFinalSubmitRequest.taskEndTime = response.taskEndTime;
            trainingFinalSubmitRequest.startTime = response.startTime;
            trainingFinalSubmitRequest.remarks = response.remarks;
            trainingFinalSubmitRequest.taskExecutionResult = syncUserData;
            /*builder.setContentText("Syncing Rota Data");
            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());*/
            submitRecords(trainingFinalSubmitRequest);
            PROGRESS_CURRENT++;
        }
    }

    private List<String> getAdhocFinalList(int rotaId) {
        return dataBase.getAdhocSavedTopicsDao().getAdhocFinalTopicList(rotaId);
    }

    private List<TrainingAttendanceItem> getAttendenceData(List<AttendanceEntity> attendanceEntityList) {
        List<TrainingAttendanceItem> attendanceItemList = new ArrayList<>();
        for (AttendanceEntity entity : attendanceEntityList) {
            TrainingAttendanceItem attendanceItem = new TrainingAttendanceItem();
            attendanceItem.setEmpID(String.valueOf(entity.employeeId));
            attendanceItem.setEmpName(entity.employeeName);
            attendanceItem.setPhotoId(entity.photoId);
            attendanceItem.setSignatureId(entity.signatureId);
            attendanceItem.setPost(String.valueOf(entity.postId));
            attendanceItemList.add(attendanceItem);
        }
        return attendanceItemList;
    }

    private List<AdhocAttendanceItem> getAdhocAttendanceList(List<AttendanceEntity> attendanceEntityList) {
        List<AdhocAttendanceItem> result = new ArrayList<>();
        for (AttendanceEntity entity : attendanceEntityList) {
            AdhocAttendanceItem item = new AdhocAttendanceItem();
            item.setEmpCode(StringUtils.getEmployeeCode(entity.empCode));
            item.setEmpName(entity.employeeName);
            item.setPhotoId(entity.photoId);
            item.setSignatureId(entity.signatureId);
            item.setPost(String.valueOf(entity.postId));
           // item.setDesignationId(entity.designationId);
            result.add(item);
        }
        return result;
    }

    private List<AdhocAssessmentItem> getAdhocAssesmentList(List<AssementEntity> assementEntityList) {
        List<AdhocAssessmentItem> result = new ArrayList<>();
        for (AssementEntity entity : assementEntityList) {
            AdhocAssessmentItem item = new AdhocAssessmentItem();
            item.setEmpCode(StringUtils.getEmployeeCode(entity.getEmpCode()));
            item.setEmpName(entity.getEmpName());
            item.setVideoId(entity.getId());
            result.add(item);
        }
        return result;
    }

    private List<TrainingPicturesItem> getTrainingPicture(List<String> postList, int rotaId) {
        List<TrainingPicturesItem> trainingPicturesItemList = new ArrayList<>();
        for (String post : postList) {
            List<String> trainingPhotoList = dataBase.getPhotoAttachmentDao().getTrainingPhotoList(post, rotaId);
            TrainingPicturesItem picturesItem = new TrainingPicturesItem();
            picturesItem.setPictureId(trainingPhotoList);
            picturesItem.setPost(post);
            trainingPicturesItemList.add(picturesItem);
        }
        return trainingPicturesItemList;
    }

    private ClientHandshakeDetails getClientHandshakeDetails(SavedFeedback savedFeedback, String rotaId) {
        ClientHandshakeDetails clientHandshakeDetails = new ClientHandshakeDetails();
        clientHandshakeDetails.setClientAvailable(savedFeedback.clientAvailable);
        clientHandshakeDetails.setClientEmailId(savedFeedback.emailId);
        clientHandshakeDetails.setClientID(savedFeedback.clientId);
        clientHandshakeDetails.setClientName(savedFeedback.name);
        clientHandshakeDetails.setClientMobNo(savedFeedback.clientMobNumber);
        clientHandshakeDetails.setClientNotAvailableReason(savedFeedback.clientNotAvailableReason);
        clientHandshakeDetails.setRating(String.valueOf(savedFeedback.rating));
        clientHandshakeDetails.setClientOtpVerify(savedFeedback.clientOtpVerify);
        clientHandshakeDetails.setRemarks(savedFeedback.remarks);
        List<String> reason = new ArrayList<>();
        List<Integer> reasonIdList = dataBase.getSavedFeedbackReasonDao().getReasonList(rotaId);
        for (int e : reasonIdList)
            reason.add(String.valueOf(e));

        //reason = dataBase.getSavedFeedbackReasonDao().getReasonList(rotaId).stream().map(String::valueOf).collect(Collectors.toList());;
        clientHandshakeDetails.setFeedback(reason);
        return clientHandshakeDetails;
    }

    private List<Integer> getTopicIdList(int rotaId) {
        return dataBase.getSavedTopicDao().getSavedTopicSubList(rotaId);
    }

    private void submitRecords(TrainingFinalSubmitRequest request) {
        Log.d(TAG, "submitRecords: rotaId : " + request.rotaid);
        try {
            Response<SyncApiResponse> response = syncApi.SubmitTrainingReport(request).execute();
            //logger.log("submitrecordsresponse"+response.body().toString());
            if (response.body() != null && response.body().statusCode == 200) {
                Log.d(TAG, "submitRecords: Rota Submitted ID - " + response.body().rotaID);
                // Can Upload Training Images
                dataBase.getPhotoAttachmentDao().updateTrainingImageStatus(response.body().rotaID);
                Log.d(TAG, "submitRecords: Upload Training Images Updated");
                // Can Upload Video
                dataBase.getAssessmentDao().updateVideoStatus(response.body().rotaID);
                Log.d(TAG, "submitRecords: Upload Video Updated");
                // Can Upload Attendance Training
                dataBase.getAttendancePhotoDao().updateAttendanceImageStatus(response.body().rotaID);
                Log.d(TAG, "submitRecords: Attendance Training iMAGES uPDATED");

                // Update Status to Completed
                dataBase.getTrainingCalenderDao().updateInProgressStatus(response.body().rotaID)
                        .subscribe();
                Log.d(TAG, "submitRecords: Update Status to Completed");


                // Delete Adhoc Master Attendence
                dataBase.getMasterAttendanceDao().deleteAdhocAttendance(response.body().rotaID);
                Log.d(TAG, "submitRecords: Adhoc Master Attendence Deleted");

                // Attendance Deleted
                dataBase.getAttendanceDao().deleteSyncedAttendance(response.body().rotaID)
                        .subscribe();
                Log.d(TAG, "submitRecords: Attendance Deleted");

                // Rota Deleted
                dataBase.getTrainingFinalSubmitDao().deleteSyncedRota(response.body().rotaID)
                        .subscribe();
                Log.d(TAG, "submitRecords: Rota Deleted");

                // Feedback Deleted
                dataBase.getSavedFeedbackDao().deleteFeedback(String.valueOf(response.body().rotaID))
                        .subscribe();
                Log.d(TAG, "submitRecords:Feedback Deleted");

                // Feedback Reason Deleted
                dataBase.getSavedFeedbackReasonDao().deleteReason(String.valueOf(response.body().rotaID))
                        .subscribe();
                Log.d(TAG, "submitRecords: Feedback Reason Deleted");

                // Topics Deleted
                dataBase.getSavedTopicDao().deleteSavedTopicByRotaId(response.body().rotaID)
                        .subscribe();
                Log.d(TAG, "submitRecords: Topics Deleted");

                getDashboardPerformance();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*.enqueue(new Callback<SyncApiResponse>() {
            @Override
            public void onResponse(Call<SyncApiResponse> call, Response<SyncApiResponse> response) {
                if (response.body() != null && response.body().statusCode == 200) {

                    // Can Upload Training Images
                    dataBase.getPhotoAttachmentDao().updateTrainingImageStatus(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    // Can Upload Video
                    dataBase.getAssessmentDao().updateVideoStatus(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    // Can Upload Attendance Training
                    dataBase.getAttendancePhotoDao().updateAttendanceImageStatus(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Update Status to Completed
                    dataBase.getTrainingCalenderDao().updateInProgressStatus(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Delete Adhoc Master Attendence
                    dataBase.getMasterAttendanceDao().deleteAdhocAttendance(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Attendance Deleted
                    dataBase.getAttendanceDao().deleteSyncedAttendance(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            //.observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Rota Deleted
                    dataBase.getTrainingFinalSubmitDao().deleteSyncedRota(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            //.observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Feedback Deleted
                    dataBase.getSavedFeedbackDao().deleteFeedback(String.valueOf(response.body().rotaID))
                            .subscribeOn(Schedulers.io())
                            //.observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Feedback Reason Deleted
                    dataBase.getSavedFeedbackReasonDao().deleteReason(String.valueOf(response.body().rotaID))
                            .subscribeOn(Schedulers.io())
                            //.observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Topics Deleted
                    dataBase.getSavedTopicDao().deleteSavedTopicByRotaId(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            //.observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                }
                getDashboardPerformance();
                //Toast.makeText(context, "Data Synced successfully ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SyncApiResponse> call, Throwable t) {

            }
        });*/
    }

    private void getDashboardPerformance() {

        String empId = Prefs.getString(PrefsConstants.EMPLOYEE_ID);
        DashboardRequest request = new DashboardRequest(empId);

        Log.d(TAG, "getDashboardPerformance: ");

        dashBoardApi.getTrainerPerformance(request)
                .subscribe(this::onTrainingPerformanceSuccess, this::onError);
    }

    private void onTrainingPerformanceSuccess(TrainerPerformanceResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {
            //performanceAdapter.clearAndSetItems(response.performance);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
            String currentDate = dateFormat.format(new Date());
            TrainerPerformanceResponse.PerformanceResponse daily = new TrainerPerformanceResponse.PerformanceResponse();
            TrainerPerformanceResponse.PerformanceResponse weekly = new TrainerPerformanceResponse.PerformanceResponse();
            TrainerPerformanceResponse.PerformanceResponse monthly = new TrainerPerformanceResponse.PerformanceResponse();
            List<TrainerPerformanceResponse.PerformanceResponse> performanceResponseList = new ArrayList<>(3);
            switch (response.performance.size()) {
                case 0:
                    daily = new TrainerPerformanceResponse.PerformanceResponse();
                    weekly = new TrainerPerformanceResponse.PerformanceResponse();
                    monthly = new TrainerPerformanceResponse.PerformanceResponse();
                    daily.typeId = 1;
                    weekly.typeId = 2;
                    monthly.typeId = 3;

                    performanceResponseList.add(daily);
                    performanceResponseList.add(weekly);
                    performanceResponseList.add(monthly);
                    dataBase.getPerformanceDao().insertPerformance(performanceResponseList)
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                    break;
                case 1:
                    daily = new TrainerPerformanceResponse.PerformanceResponse();
                    weekly = new TrainerPerformanceResponse.PerformanceResponse();
                    daily.typeId = 1;
                    weekly.typeId = 2;
                    daily.currentDate = currentDate;
                    performanceResponseList.add(daily);
                    performanceResponseList.add(weekly);
                    performanceResponseList.add(response.performance.get(0));
                    dataBase.getPerformanceDao().insertPerformance(performanceResponseList)
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                    break;
                case 2:
                    daily = new TrainerPerformanceResponse.PerformanceResponse();
                    daily.typeId = 1;
                    daily.currentDate = currentDate;
                    performanceResponseList.add(daily);
                    performanceResponseList.add(response.performance.get(1));
                    performanceResponseList.add(response.performance.get(0));
                    dataBase.getPerformanceDao().insertPerformance(performanceResponseList)
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                    break;
                case 3:
                    response.performance.get(0).currentDate = currentDate;
                    dataBase.getPerformanceDao().insertPerformance(response.performance)
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                    break;

            }
        }
    }

    private void onError(Throwable throwable) {

    }

    private synchronized void uploadAttendanceImage(List<AttendancePhotoEntity> attendancePhotoEntityList) {
        int PROGRESS_CURRENT = 1;
        int total = attendancePhotoEntityList.size();
        for (AttendancePhotoEntity entity : attendancePhotoEntityList) {
            builder.setContentText("Syncing Attendance Images (" + PROGRESS_CURRENT + "/" + total + ")");
            builder.setProgress(total, PROGRESS_CURRENT, false);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
            //Create a file object using file path
            if (entity.isMark == 0) {
                try {
                    mark(entity.waterMark, entity.attendancePhotoURI);
                }catch (Exception e){

                }
                dataBase.getAttendancePhotoDao().imageMarked(entity.attendancePhotoURI);
            }

            Log.d(TAG, "uploadAttendanceImage: Is Compress -> " + entity.isCompress + " Image File :-> "  + entity.attendancePhotoURI);
            if(entity.isCompress == 0) {
               try {
                   Log.d(TAG, "uploadAttendanceImage: Inside Compress");
                   File originalFile = new File(entity.attendancePhotoURI);
                   File imageDir = new File(entity.attendancePhotoURI.substring(0, entity.attendancePhotoURI.lastIndexOf("/")));
                   Log.d(TAG, "uploadAttendanceImage: Inside Compress   If " + imageDir + " exist -> " + imageDir.exists() + " If file " + originalFile + " Exist --> " + originalFile.exists());
                   if (imageDir.exists() && originalFile.exists()) {
                       Log.d(TAG, "uploadAttendanceImage: Inside Compress Calling Compresss");
                       Compressor compressor = new Compressor(this);
                       compressor.setDestinationDirectoryPath(imageDir.getAbsolutePath());
                       try {
                           File compressedFile = compressor.compressToFile(originalFile, "min_" + originalFile.getName());
                           Log.d(TAG, "uploadAttendanceImage: Compressed File : " + compressedFile.getAbsolutePath());
                           dataBase.getAttendancePhotoDao().imageCompressed(entity.attendancePhotoURI, compressedFile.getAbsolutePath());
                           entity.attendancePhotoURI = compressedFile.getAbsolutePath();
                           if (originalFile.exists())
                               originalFile.delete();
                       } catch (IOException e) {
                           logger.log("uploadAttendanceImage: Compressed File in uploadAttendanceImage method exception"+e.getMessage());
                           e.printStackTrace();
                       }
                   }
               }catch (Exception e){
                   logger.log("uploadAttendanceImage: Inside Compress exception"+e.getMessage());

               }
            }


            File file = new File(entity.attendancePhotoURI);
            // Create a request body with file and image media type
            Log.v("MIME : ", " uploadAttendanceImage");
            String str = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str);
            /*try {
                logger.log("uploadAttendanceImage: Inside fileupload mime type"+type+","+file.getPath());
            }catch (Exception e){
                logger.log("uploadAttendanceImage: Inside fileupload mime type exception1"+e.getMessage());
            }*/
            if (type == null||type.equals("")||type.isEmpty()) {
                type = "*/*";
            }
            Log.v("MIME TYPE : ", " " + type);
            try {
                logger.log("uploadAttendanceImage: Inside fileupload mime type"+type+","+file.getPath());
            }catch (Exception e){
                logger.log("uploadAttendanceImage: Inside fileupload mime type exception"+e.getMessage());
            }
            RequestBody fileReqBody = RequestBody.create(file,MediaType.parse(type));
            // Create MultipartBody.Part using file request-body,file name and part name
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

            /*RequestBody companyId =
                    RequestBody.create(
                            okhttp3.MultipartBody.FORM, Prefs.getString(PrefsConstants.COMPANY_ID));*/

            try {
                Response<ImageUploadResponse> response = syncApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))).execute();
                //Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                if (response.body() != null && response.body().statusCode == 200) {
                    if (file.exists())
                        file.delete();
                    entity.attendancePhotoURI = response.body().data;
                    dataBase.getAttendancePhotoDao().updateAttendancePhoto(entity);
                    ImageUploadRequest uploadRequest = new ImageUploadRequest();
                    uploadRequest.pictureid = entity.attendancePhotoId;
                    uploadRequest.picturetypeid = String.valueOf(entity.pictureTypeId);
                    uploadRequest.pictureUrl = response.body().data;
                    uploadRequest.postId = entity.postId;
                    uploadRequest.trainingid = String.valueOf(entity.rotaId);
                    Response<ImageDataUploadResponse> dataUploadResponse = syncApi.uploadImageData(uploadRequest).execute();
                    if (dataUploadResponse.body() != null && dataUploadResponse.body().statusCode == 200) {
                        dataBase.getAttendancePhotoDao().deleteAttendancePhoto(entity.id)
                                .subscribe();
                        Log.v("Attendance Photo", " Work Manager Deleted : " + entity.id);
                    }
                }
            } catch (Exception e) {
                logger.log("calling uploadImages api in uploadAttendanceImage method exception"+e.getMessage());
                e.printStackTrace();
            }
            PROGRESS_CURRENT++;
        }
    }

    private synchronized void uploadTrainingImage(List<TrainingPhotoAttachmentEntity> trainingPhotoAttachmentEntityList) {
        int PROGRESS_CURRENT = 1;
        int total = trainingPhotoAttachmentEntityList.size();
        for (TrainingPhotoAttachmentEntity entity : trainingPhotoAttachmentEntityList) {
            builder.setContentText("Syncing Training Images (" + PROGRESS_CURRENT + "/" + total + ")");
            builder.setProgress(total, PROGRESS_CURRENT, false);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());

            //Create a file object using file path
            if (entity.isMark == 0) {
                try {
                    mark(entity.waterMark, entity.trainingPhotoURI);
                }catch (Exception e){

                }
                dataBase.getPhotoAttachmentDao().imageMarked(entity.trainingPhotoURI);
            }

            Log.d(TAG, "uploadTrainingImage: Is Compress -> " + entity.isCompress + " Image File :-> "  + entity.trainingPhotoURI);
            if(entity.isCompress == 0) {
                try {
                    Log.d(TAG, "uploadTrainingImage: Inside Compress");
                    File originalFile = new File(entity.trainingPhotoURI);
                    File imageDir = new File(entity.trainingPhotoURI.substring(0, entity.trainingPhotoURI.lastIndexOf("/")));
                    Log.d(TAG, "uploadTrainingImage: Inside Compress   If " + imageDir + " exist -> " + imageDir.exists() + " If file "+ originalFile +" Exist --> " + originalFile.exists());
                    if(imageDir.exists() && originalFile.exists()) {
                        Log.d(TAG, "uploadTrainingImage: Inside Compress Calling Compresss");
                        Compressor compressor = new Compressor(this);
                        compressor.setDestinationDirectoryPath(imageDir.getAbsolutePath());
                        try {
                            File compressedFile = compressor.compressToFile(originalFile, "min_" + originalFile.getName());
                            Log.d(TAG, "uploadTrainingImage: Compressed File : " + compressedFile.getAbsolutePath());
                            dataBase.getPhotoAttachmentDao().imageCompressed(entity.trainingPhotoURI, compressedFile.getAbsolutePath());
                            entity.trainingPhotoURI = compressedFile.getAbsolutePath();
                            if(originalFile.exists())
                                originalFile.delete();
                        } catch (IOException e) {
                            logger.log("uploadTrainingImage: Compressed File Exception"+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    logger.log("calling uploadTrainingImage method file error exception"+e.getMessage());
                    Log.d(TAG,"calling uploadTrainingImage method file error exception",e.getCause());
                }
            }


            File file = new File(entity.trainingPhotoURI);
            // Create a request body with file and image media type
            if (file.exists()) {
                Log.v("MIME : ", " uploadTrainingImage");
                String type = null;
                String str = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                if (str!=null) {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str);
                }
                if (type == null) {
                    type = "*/*";
                }
                Log.d("MIME TYPE : ", " " + type);
                try {
                    logger.log("uploadTrainingImage: Inside fileupload mime type"+type+","+file.getPath());
                }catch (Exception e){
                    logger.log("uploadTrainingImage: Inside fileupload mime type exception"+e.getMessage());
                }
                RequestBody fileReqBody = RequestBody.create(file,MediaType.parse(type));
                // Create MultipartBody.Part using file request-body,file name and part name
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                /*RequestBody companyId =
                        RequestBody.create(
                                okhttp3.MultipartBody.FORM, Prefs.getString(PrefsConstants.COMPANY_ID));*/

                try {
                    Response<ImageUploadResponse> response = syncApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))).execute();
                    //Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                    if (response.body() != null && response.body().statusCode == 200) {
                        if (file.exists())
                            file.delete();
                        entity.trainingPhotoURI = response.body().data;
                        dataBase.getPhotoAttachmentDao().updateTrainingPhoto(entity);
                        ImageUploadRequest uploadRequest = new ImageUploadRequest();
                        uploadRequest.pictureid = entity.trainingPhotoId;
                        uploadRequest.picturetypeid = String.valueOf(entity.pictureTypeId);
                        uploadRequest.pictureUrl = response.body().data;
                        uploadRequest.postId = entity.postId;
                        uploadRequest.trainingid = String.valueOf(entity.rotaId);
                        Response<ImageDataUploadResponse> dataUploadResponse = syncApi.uploadImageData(uploadRequest).execute();
                        if (dataUploadResponse.body() != null && dataUploadResponse.body().statusCode == 200) {
                            dataBase.getPhotoAttachmentDao().deletePhotoAttchment(entity.trainingPhotoId);
                            Log.v("Training Photo", " Deleted : " + entity.trainingPhotoId);
                        }
                    }
                } catch (IOException e) {
                    logger.log("Exception in uploadImages api in uploadTrainingImage method " + e.getMessage());
                    e.printStackTrace();
                }
            }
            PROGRESS_CURRENT++;
        }
    }

    private synchronized void uploadAssessmentVideo(List<AssementEntity> assementEntityList) {
        //logger.logDb(assementEntityList, "Assessment Table from Force");
        int PROGRESS_CURRENT = 1;
        int total = assementEntityList.size();
        for (AssementEntity entity : assementEntityList) {
            builder.setContentText("Syncing Assessment Videos (" + PROGRESS_CURRENT + "/" + total + ")");
            builder.setProgress(total, PROGRESS_CURRENT, false);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
            //Create a file object using file path
            File file = new File(entity.getVideoPath());
            // Create a request body with file and image media type
            if (file.exists()) {
                //logger.log("File Exist -> " + file.getAbsolutePath());
                //logger.log("File Exist -> " + file.getPath());
               // Log.v("MIME : ", " uploadTrainingImage");
                String type = null;
                String str = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                if (str!=null) {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str);
                }
                /*try {
                    logger.log("uploadAssessmentVideo: Inside fileupload mime type"+type+","+file.getPath());
                }catch (Exception e){
                    logger.log("uploadAssessmentVideo: Inside fileupload mime type exception1"+e.getMessage());
                }*/

                if (type == null) {
                    type = "*/*";
                }
                try {
                    logger.log("uploadAssessmentVideo: Inside fileupload mime type"+type+","+file.getPath());
                }catch (Exception e){
                    logger.log("uploadAssessmentVideo: Inside fileupload mime type exception"+e.getMessage());
                }
               // Log.v("MIME TYPE : ", " " + type);
                Log.d("MIME TYPE : ", " " + type);
                RequestBody fileReqBody = RequestBody.create(file,MediaType.parse(type));
                // Create MultipartBody.Part using file request-body,file name and part name
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
                /*RequestBody companyId =
                        RequestBody.create(
                                okhttp3.MultipartBody.FORM, Prefs.getString(PrefsConstants.COMPANY_ID));*/
                try {
                    Response<ImageUploadResponse> response = syncApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))).execute();
                    //Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                    if (response.body() != null && response.body().statusCode == 200) {
                        //logger.log("file uploaded sucessfully -> " + file.getAbsolutePath());
                        if (file.exists())
                            file.delete();
                        entity.setVideoPath(response.body().data);
                        dataBase.getAssessmentDao().updateAssessmentVideo(entity);
                        ImageUploadRequest uploadRequest = new ImageUploadRequest();
                        uploadRequest.pictureid = entity.getId();
                        uploadRequest.picturetypeid = "4";
                        uploadRequest.pictureUrl = response.body().data;
                        uploadRequest.postId = entity.getPostId();
                        uploadRequest.trainingid = String.valueOf(entity.getRotaId());
                        Response<ImageDataUploadResponse> dataUploadResponse = syncApi.uploadImageData(uploadRequest).execute();
                        if (dataUploadResponse.body() != null && dataUploadResponse.body().statusCode == 200) {
                            dataBase.getAssessmentDao().deleteSingleAssessment(entity.getId());
                            Log.d(TAG,"Video File Entry Deleted Sucessfully -->");
                            //logger.log("Video File Entry Deleted Sucessfully --> ");
                           //List<AssementEntity> assementEntityList = dataBase.getAssessmentDao().getAssessmentVideoForSync();
                           //logger.logDb(dataBase.getAssessmentDao().getAssessmentVideoForSync(),"Assessement Table after deleting");
                            //Log.v("Training Photo", " Deleted : " + entity.trainingPhotoId);
                        }
                    }
                } catch (IOException e) {
                    logger.log("Exception in uploadImages api in uploadAssessmentVideo method " + e.getMessage());
                    Log.d(TAG,"Exception in uploadImages api in uploadAssessmentVideo method "+e.getCause());
                    e.printStackTrace();
                }
            }else {
                logger.log("uploadAssessmentVideo method file not exist error ");
                Log.d(TAG,"uploadAssessmentVideo method file not exist error ");
            }
            PROGRESS_CURRENT++;
        }
    }

    private void mark(String watermark, String path) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

            Bitmap bmp1 = WaterMarkUtil.addWatermark(bitmap, watermark);

            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path);
                bmp1.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger.log("mark method exception inner"+e.getMessage());
            }
        }catch (Exception e){
            logger.log("mark method exception outer"+e.getMessage());
        }
    }

    private Notification getNotification() {
        builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentText("Preparing to Upload")
                .setContentTitle("Data Syncing")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_mtrainer_white);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    private void initRetrofit() {
        Log.d(TAG, "initRetrofit: ");
        final int CONNECT_TIMEOUT_MILLIS = 1500 * 1000; // 15s
        final int READ_TIMEOUT_MILLIS = 2000 * 1000; // 20s

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Timber.d(message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(0, 5,TimeUnit.MINUTES));


        RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        MtrainerLogIntercepter mtrainerLogIntercepter = new MtrainerLogIntercepter();
        builder.addInterceptor(headerInterceptor);
        builder.addInterceptor(mtrainerLogIntercepter);
        builder.addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.MTRAINER_HOST).client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        syncApi = retrofit.create(SyncApi.class);
        dashBoardApi = retrofit.create(DashBoardApi.class);
        Log.d(TAG, "initRetrofit: ");
    }

    static class ForceSyncHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    }
}





