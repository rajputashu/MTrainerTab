package com.sisindia.ai.mtrainer.android.features.syncadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AdhocSavedTopics;
import com.sisindia.ai.mtrainer.android.db.entities.AssementEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.DraftSpiPhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedback;
import com.sisindia.ai.mtrainer.android.db.entities.TrainingPhotoAttachmentEntity;
import com.sisindia.ai.mtrainer.android.models.AdhocEmployeesSaved;
import com.sisindia.ai.mtrainer.android.models.AdhoctopicItem;
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
import com.sisindia.ai.mtrainer.android.models.sync.DraftSpiData;
import com.sisindia.ai.mtrainer.android.models.sync.DraftSpiPicturesItems;
import com.sisindia.ai.mtrainer.android.models.sync.RotaDetails;
import com.sisindia.ai.mtrainer.android.models.sync.TrainingAttendanceItem;
import com.sisindia.ai.mtrainer.android.models.sync.TrainingPicturesItem;
import com.sisindia.ai.mtrainer.android.models.sync.UserData;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.SyncApi;
import com.sisindia.ai.mtrainer.android.utils.StringUtils;
import com.sisindia.ai.mtrainer.android.utils.WaterMarkUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class SyncWorker extends Worker {
    private MtrainerDataBase dataBase;
    private SyncApi syncApi;
    private DashBoardApi dashBoardApi;
    private Context context;
    private int companyId;
    private final Logger logger = new Logger(this.getClass().getSimpleName());


    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dataBase = MtrainerDataBase.getDatabase(context);
        companyId = Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID, "-1"));
        initRetrofit();
        this.context = context;
        //Log.d(TAG, "SyncWorker: Created");
        //logger.log("SyncWorker: Created");


    }

    @NonNull
    @Override
    public Result doWork() {
        /*Bundle b = new Bundle();
        SyncAdapterInitialization syncAdapterInitialization = new SyncAdapterInitialization(context);
        syncAdapterInitialization.startForceSyncing(b);*/
        //Log.d(TAG, "doWork: Start");
        if (companyId != -1) {
            //logger.log("doWork: Start");
            List<TrainingFinalSubmitResponse.TrainingSubmitResponse> responseList = dataBase.getTrainingFinalSubmitDao().getSavedRota().blockingGet();
            try {
                getSavedRota(responseList);
            } catch (Exception e) {
                logger.log("Exception in getSavedRota", e);
            }
            try {
                uploadAttendanceImage();
            } catch (Exception e) {
                logger.log("Exception in uploadAttendanceImage", e);
            }
            try {
                uploadTrainingImage();
            } catch (Exception e) {
                logger.log("Exception in uploadTrainingImage", e);
            }
            try {
                uploadAssessmentVideo();
            } catch (Exception e) {
                logger.log("Exception in uploadAssessmentVideo", e);
            }
        }
        //logger.log("doWork: about to return");
        return Result.success();
    }

    private void initRetrofit() {
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
    }

    private void getSavedRota(List<TrainingFinalSubmitResponse.TrainingSubmitResponse> responseList) {
        //logger.log("getSavedRota: List Size : " + responseList.size());
            for (TrainingFinalSubmitResponse.TrainingSubmitResponse response : responseList) {
                try {
                    List<AttendanceEntity> attendanceEntityList = dataBase.getAttendanceDao().getAttendanceSubList(response.rotaid);
                    List<TrainingAttendanceItem> syncAttendance = new ArrayList<>();
                    syncAttendance = getAttendenceData(attendanceEntityList);
                    List<AttendanceEntity> AdhocDirtyAttendance = dataBase.getAttendanceDao().getAdhocAttendanceList(response.rotaid);
                    List<AdhocAttendanceItem> adhocAttendanceItemList = getAdhocAttendanceList(AdhocDirtyAttendance);
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
                    syncRotaDetails.setCompanyId(String.valueOf(companyId));
                    syncRotaDetails.setRotaId(String.valueOf(response.rotaid));
                    syncRotaDetails.setActualTraineeCount(Prefs.getString(PrefsConstants.CLIENT_EMPLOYEE_COUNT));
                    UserData syncUserData = new UserData();
                    syncUserData.setTrainingAttendance(syncAttendance);
                    syncUserData.setAdhocAttendance(adhocAttendanceItemList);
                    syncUserData.setClientHandshakeDetails(syncClientHandshakeDetails);
                    syncUserData.setRotaDetails(syncRotaDetails);
                    syncUserData.setTrainingPictures(syncTrainingPictureList);
                    syncUserData.setAssessment(syncAssessmentReport);
                    syncUserData.setAdhocAssessment(adhocAssessmentItemList);

                    //below one for adhoc topic
                    // syncUserData.setadhocSavedTopics(syncAdhocReport);
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
                    submitRecords(trainingFinalSubmitRequest);
                    Log.d("Finally synced json", "data" + trainingFinalSubmitRequest);
                } catch (Exception e) {
                    logger.log("Exception in getSavedRota", e);
                }
            }
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
        // clientHandshakeDetails.setRemarks(savedFeedback.remarks);
        List<String> reason = new ArrayList<>();
        List<Integer> reasonIdList = dataBase.getSavedFeedbackReasonDao().getReasonList(rotaId);
        for (int e : reasonIdList)
            reason.add(String.valueOf(e));

        clientHandshakeDetails.setFeedback(reason);
        return clientHandshakeDetails;
    }

    private List<Integer> getTopicIdList(int rotaId) {
        return dataBase.getSavedTopicDao().getSavedTopicSubList(rotaId);
    }


    private List<String> getAdhocFinalList(int rotaId) {
        return dataBase.getAdhocSavedTopicsDao().getAdhocFinalTopicList(rotaId);
    }


    private void onError(Throwable throwable) {

    }

    private void submitRecords(TrainingFinalSubmitRequest request) {
        //logger.log("submitRecords: rotaId : " + request.rotaid);
        try {
            Response<SyncApiResponse> response = syncApi.SubmitTrainingReport(request).execute();
            if (response.body() != null && response.body().statusCode == 200) {

                //logger.log("submitRecords: Rota Submitted ID - " + response.body().rotaID);
                // Can Upload Training Images
                dataBase.getPhotoAttachmentDao().updateTrainingImageStatus(response.body().rotaID);
                //logger.log("submitRecords: Upload Training Images Updated");
                // Can Upload Video
                dataBase.getAssessmentDao().updateVideoStatus(response.body().rotaID);
                //logger.log("submitRecords: Upload Video Updated");
                // Can Upload Attendance Training
                dataBase.getAttendancePhotoDao().updateAttendanceImageStatus(response.body().rotaID);
                //logger.log("submitRecords: Attendance Training iMAGES uPDATED");

                // Update Status to Completed
                dataBase.getTrainingCalenderDao().updateInProgressStatus(response.body().rotaID)
                        .subscribe();
                //logger.log("submitRecords: Update Status to Completed");


                // Delete Adhoc Master Attendence
                dataBase.getMasterAttendanceDao().deleteAdhocAttendance(response.body().rotaID);
                //logger.log("submitRecords: Adhoc Master Attendence Deleted");

                // Attendance Deleted
                dataBase.getAttendanceDao().deleteSyncedAttendance(response.body().rotaID)
                        .subscribe();
                //logger.log("submitRecords: Attendance Deleted");

                // Rota Deleted
                dataBase.getTrainingFinalSubmitDao().deleteSyncedRota(response.body().rotaID)
                        .subscribe();
                //logger.log("submitRecords: Rota Deleted");

                // Feedback Deleted
                dataBase.getSavedFeedbackDao().deleteFeedback(String.valueOf(response.body().rotaID))
                        .subscribe();
                //logger.log("submitRecords:Feedback Deleted");

                // Feedback Reason Deleted
                dataBase.getSavedFeedbackReasonDao().deleteReason(String.valueOf(response.body().rotaID))
                        .subscribe();
                //logger.log("submitRecords: Feedback Reason Deleted");

                // Topics Deleted
                dataBase.getSavedTopicDao().deleteSavedTopicByRotaId(response.body().rotaID)
                        .subscribe();
                //logger.log("submitRecords: Topics Deleted");


                //  adhoc topic deleted
                dataBase.getAdhocTopicsDao().deleteadhocTopicByRotaId(response.body().rotaID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();


                // saved adhoc topic deleted
                dataBase.getAdhocSavedTopicsDao().deleteAdhocSavedTopicByRotaId(response.body().rotaID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                // deleting client employee count
                Prefs.edit().remove(PrefsConstants.CLIENT_EMPLOYEE_COUNT).apply();
                // in progress preference removed
                Prefs.edit().remove(PrefsConstants.IS_PROGRESS_ROTAID).apply();
                Prefs.edit().remove(PrefsConstants.PRE_TIME_SAVE).apply();


                getDashboardPerformance();
            }

        } catch (IOException e) {
            logger.log("submitRecords", e);
        }






        /*syncApi.SubmitTrainingReport(request).enqueue(new Callback<SyncApiResponse>() {
            @Override
            public void onResponse(Call<SyncApiResponse> call, Response<SyncApiResponse> response) {
                if (response.body() != null && response.body().statusCode == 200) {


                    dataBase.getPhotoAttachmentDao().updateTrainingImageStatus(Prefs.getInt(PrefsConstants.ROTA_ID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    dataBase.getAssessmentDao().updateVideoStatus(Prefs.getInt(PrefsConstants.ROTA_ID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    dataBase.getAttendancePhotoDao().updateAttendanceImageStatus(Prefs.getInt(PrefsConstants.ROTA_ID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Update Status to Completed
                    dataBase.getTrainingCalenderDao().updateInProgressStatus(response.body().rotaID)
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

                    //  adhoc topic deleted
                    dataBase.getAdhocTopicsDao().deleteadhocTopicByRotaId(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();


                    // saved adhoc topic deleted
                    dataBase.getAdhocSavedTopicsDao().deleteAdhocSavedTopicByRotaId(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    // deleting client employee count
                    Prefs.edit().remove(PrefsConstants.CLIENT_EMPLOYEE_COUNT).apply();
                    // in progress preference removed
                    Prefs.edit().remove(PrefsConstants.IS_PROGRESS_ROTAID).apply();
                    Prefs.edit().remove(PrefsConstants.PRE_TIME_SAVE).apply();


                    // Delete Adhoc Master Attendence
                    dataBase.getMasterAttendanceDao().deleteAdhocAttendance(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // saved adhoc employee deleted
                    dataBase.getAdhocSavedEmployeeDao().deleteAdhocSavedEmployeeByRotaId(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    //  adhoc employee deleted
                    dataBase.getAdhocEmployeeDao().deleteadhocEmployeeByRotaId(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
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

    private synchronized void uploadAttendanceImage() {
        //String tocken = Prefs.getString(PrefsConstants.AUTH_TOKEN_KEY);
        List<AttendancePhotoEntity> attendancePhotoEntityList = dataBase.getAttendancePhotoDao().getSavedAttendancePic();
        for (AttendancePhotoEntity entity : attendancePhotoEntityList) {
            try {
                if (entity.isMark == 0) {
                    boolean isMarked = mark(entity.waterMark, entity.attendancePhotoURI);
                    if (!isMarked) {
                       // logger.log("Marking Error -> " + entity.attendancePhotoURI);
                        continue;
                    }
                    dataBase.getAttendancePhotoDao().imageMarked(entity.attendancePhotoURI);
                }

                //logger.log("uploadTrainingImage: Is Compress -> " + entity.isCompress + " Image File :-> " + entity.attendancePhotoURI);
                if (entity.isCompress == 0) {
                   try {
                       //logger.log("uploadTrainingImage: Inside Compress");
                       File originalFile = new File(entity.attendancePhotoURI);
                       File imageDir = new File(entity.attendancePhotoURI.substring(0, entity.attendancePhotoURI.lastIndexOf("/")));
                       //logger.log("uploadTrainingImage: Inside Compress   If " + imageDir + " exist -> " + imageDir.exists() + " If file " + originalFile + " Exist --> " + originalFile.exists());
                       if (imageDir.exists() && originalFile.exists()) {
                           //logger.log("uploadTrainingImage: Inside Compress Calling Compresss");
                           Compressor compressor = new Compressor(context);
                           compressor.setDestinationDirectoryPath(imageDir.getAbsolutePath());
                           try {
                               File compressedFile = compressor.compressToFile(originalFile, "min_" + originalFile.getName());
                              // logger.log("uploadTrainingImage: Compressed File : " + compressedFile.getAbsolutePath());
                               dataBase.getAttendancePhotoDao().imageCompressed(entity.attendancePhotoURI, compressedFile.getAbsolutePath());
                               entity.attendancePhotoURI = compressedFile.getAbsolutePath();
                               if (originalFile.exists())
                                   originalFile.delete();
                           } catch (IOException e) {
                               logger.log("Compression Error " + "[" + originalFile + "] -> ", e);
                               continue;
                           }
                       }
                   }catch (Exception e){

                   }
                }

                //Create a file object using file path
                File file = new File(entity.attendancePhotoURI);
                // Create a request body with file and image media type
                String str = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str);
                /*try {
                    logger.log("syncworker uploadAttendanceImage method:"+type+","+companyId+","+file.getPath());
                }catch (Exception e){
                    logger.log("syncworker uploadAttendanceImage method: exception"+e.getMessage());
                }*/
                if (type == null) {
                    type = "*/*";
                }
                try {
                    logger.log("syncworker uploadAttendanceImage method1:"+type+","+companyId+","+file.getPath());
                }catch (Exception e){
                    logger.log("syncworker uploadAttendanceImage method1: exception"+e.getMessage());
                }
                RequestBody fileReqBody = RequestBody.create(file,MediaType.parse(type));
                // Create MultipartBody.Part using file request-body,file name and part name
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

            /*RequestBody companyId =
                    RequestBody.create(
                            okhttp3.MultipartBody.FORM, Prefs.getString(PrefsConstants.COMPANY_ID));*/

                //Create request body with text description and text media type

                try {
                    Response<ImageUploadResponse> response = syncApi.uploadImages(part, companyId).execute();
                    // Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
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
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                logger.log("Exception in uploadAttendanceImage", e);
            }
        }
    }


    private synchronized void uploadTrainingImage() {
        List<TrainingPhotoAttachmentEntity> trainingPhotoAttachmentEntityList = dataBase.getPhotoAttachmentDao().getTrainingPhotoForSync();
        //logger.log("uploadTrainingImage -> " + trainingPhotoAttachmentEntityList.size());
        for (TrainingPhotoAttachmentEntity entity : trainingPhotoAttachmentEntityList) {
            try {
                // below code water mark on image .........
                if (entity.isMark == 0) {
                    boolean isMarked = mark(entity.waterMark, entity.trainingPhotoURI);
                    if (!isMarked) {
                        //logger.log("Marking Error -> " + entity.trainingPhotoURI);
                        continue;
                    }
                    dataBase.getPhotoAttachmentDao().imageMarked(entity.trainingPhotoURI);
                }

                //logger.log("uploadTrainingImage: Is Compress -> " + entity.isCompress + " Image File :-> " + entity.trainingPhotoURI);
                if (entity.isCompress == 0) {
                    try {
                       // logger.log("uploadTrainingImage: Inside Compress");
                        File originalFile = new File(entity.trainingPhotoURI);
                        File imageDir = new File(entity.trainingPhotoURI.substring(0, entity.trainingPhotoURI.lastIndexOf("/")));
                       // logger.log("uploadTrainingImage: Inside Compress   If " + imageDir + " exist -> " + imageDir.exists() + " If file " + originalFile + " Exist --> " + originalFile.exists());
                        if (imageDir.exists() && originalFile.exists()) {
                           // logger.log("uploadTrainingImage: Inside Compress Calling Compresss");
                            Compressor compressor = new Compressor(context);
                            compressor.setDestinationDirectoryPath(imageDir.getAbsolutePath());
                            try {
                                File compressedFile = compressor.compressToFile(originalFile, "min_" + originalFile.getName());
                               // logger.log("uploadTrainingImage: Compressed File : " + compressedFile.getAbsolutePath());
                                dataBase.getPhotoAttachmentDao().imageCompressed(entity.trainingPhotoURI, compressedFile.getAbsolutePath());
                                entity.trainingPhotoURI = compressedFile.getAbsolutePath();
                                if (originalFile.exists())
                                    originalFile.delete();
                            } catch (IOException e) {
                                logger.log("Compression Error " + "[" + originalFile + "] -> ", e);
                                continue;
                            }
                        } else
                            logger.log("File Doesn't Exist(Compressing) -> " + originalFile);
                    }catch (Exception e){

                    }
                }

                //...........................
                //Create a file object using file path
                File file = new File(entity.trainingPhotoURI);
                // Create a request body with file and image media type

                if (file.exists()) {
                    String str = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                    String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str);
                    /*try {
                        logger.log("syncworker uploadTrainingImage method:"+type+","+companyId+","+file.getPath());
                    }catch (Exception e){
                        logger.log("syncworker uploadTrainingImage method: exception"+e.getMessage());
                    }*/
                    if (type == null) {
                        type = "*/*";
                    }

                    try {
                        logger.log("syncworker uploadTrainingImage method1:"+type+","+companyId+","+file.getPath());
                    }catch (Exception e){
                        logger.log("syncworker uploadTrainingImage method1: exception"+e.getMessage());
                    }
                    RequestBody fileReqBody = RequestBody.create(file,MediaType.parse(type));
                    // Create MultipartBody.Part using file request-body,file name and part name
                    MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                /*RequestBody companyId =
                        RequestBody.create(
                                okhttp3.MultipartBody.FORM, Prefs.getString(PrefsConstants.COMPANY_ID));*/

                    //Create request body with text description and text media type

                    try {
                        Response<ImageUploadResponse> response = syncApi.uploadImages(part, companyId).execute();
                        //  Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
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
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    logger.log("File Doesn't Exist(Uploading) -> " + file);
            } catch (Exception e) {
                logger.log("Exception in uploadTrainingImage", e);
            }
        }
    }


    private synchronized void uploadAssessmentVideo() {
        //String tocken = Prefs.getString(PrefsConstants.AUTH_TOKEN_KEY);
        List<AssementEntity> assementEntityList = dataBase.getAssessmentDao().getAssessmentVideoForSync();
        //logger.logDb(assementEntityList, "Assessement Table");
        //logger.log("uploadAssessmentVideo -> " + assementEntityList.size());
        for (AssementEntity entity : assementEntityList) {
            try {
                //Create a file object using file path
                File file = new File(entity.getVideoPath());
                // Create a request body with file and video media type
                if (file.exists()) {
                    //logger.log("File Exists -> " + file.getAbsolutePath());
                    //  Log.v("MIME : ", " uploadTrainingImage");
                    String str = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                    String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str);
                    /*try {
                        logger.log("syncworker uploadAssessmentVideo method:"+type+","+companyId+","+file.getPath());
                    }catch (Exception e){
                        logger.log("syncworker uploadAssessmentVideo method: exception"+e.getMessage());
                    }*/

                    if (type == null) {
                        type = "*/*";
                    }
                    try {
                        logger.log("syncworker uploadAssessmentVideo method1:"+type+","+companyId+","+file.getPath());
                    }catch (Exception e){
                        logger.log("syncworker uploadAssessmentVideo method1: exception"+e.getMessage());
                    }

                    //   Log.v("MIME TYPE : ", " " + type);
                    RequestBody fileReqBody = RequestBody.create(MediaType.parse(type), file);
                    // Create MultipartBody.Part using file request-body,file name and part name
                    MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                /*RequestBody companyId =
                        RequestBody.create(
                                okhttp3.MultipartBody.FORM, Prefs.getString(PrefsConstants.COMPANY_ID));*/

                    //Create request body with text description and text media type

                    try {
                        Response<ImageUploadResponse> response = syncApi.uploadImages(part, companyId).execute();
                        //  Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                        if (response.body() != null && response.body().statusCode == 200) {
                           // logger.log("Video Uploaded Sucessfully");
                            if (file.exists())
                                //logger.log("Video File Deleted Sucessfully --> " + file.getAbsolutePath());
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
                                //List<AssementEntity> assementEntityList = dataBase.getAssessmentDao().getAssessmentVideoForSync();
                                //Log.v("Training Photo", " Deleted : " + entity.trainingPhotoId);
                            }
                        }
                    } catch (IOException e) {
                        logger.log("Exception thrown -> " + e.getMessage() + "  ->  " + e.toString());
                    }
                }
            } catch (Exception e) {
                logger.log("Exception in uploadAssessmentVideo", e);
            }
        }
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

    public void getDashboardPerformance() {

        String empId = Prefs.getString(PrefsConstants.EMPLOYEE_ID);
        DashboardRequest request = new DashboardRequest(empId);

        //logger.log("getDashboardPerformance: ");

        dashBoardApi.getTrainerPerformance(request)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onTrainingPerformanceSuccess, this::onError);
    }

    private synchronized boolean mark(String watermark, String path) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        if (bitmap == null) {
            //logger.log("Bitmap null [" + path + "]");
            return false;
        }
        if (watermark == null) {
            //logger.log("Watermark null [" + path + "] -> " + watermark);
            return false;
        }
        String duplicateFileName = path.substring(0, path.lastIndexOf(".")) + "_old_" + path.substring(path.lastIndexOf("."));
        if (!copyAndDeleteFile(path, duplicateFileName))
            return false;
        Bitmap watermarkedBitmap = null;
        try {
            watermarkedBitmap = WaterMarkUtil.addWatermark(bitmap, watermark);
        } catch (Exception e) {
            copyAndDeleteFile(duplicateFileName, path);
            //logger.log("Watermarked Bitmap null -> ", e);
            return false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            watermarkedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        } catch (IOException e) {
            copyAndDeleteFile(duplicateFileName, path);
            //logger.log("mark", e);
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //logger.log("Mark Closeing Stream Error", e);
                }
            }
        }
        return true;
    }

    private boolean copyAndDeleteFile(String src, String dest) {
        File file = new File(src);
        Log.d(TAG, "copyAndDeleteFile: new File -> " + dest);
        File dupFile = new File(dest);
        FileInputStream in = null;
        FileOutputStream dupFileStream = null;
        int level = 0;
        try {
            in = new FileInputStream(file);
            level = 1;
            dupFileStream = new FileOutputStream(dupFile);
            level = 2;
            byte[] buffer = new byte[1024];
            int len;
            while (!((len = in.read(buffer)) > 0)) {
                if (level == 2)
                    level = 3;
                dupFileStream.write(buffer, 0, len);
            }

        } catch (IOException e) {
            if (dupFile.exists())
                dupFile.delete();
            if (level == 1) {
                logger.log("copyAndDeleteFile [" + file.getAbsolutePath() + "] InputStream Issue", e);
                return false;
            } else if (level == 2) {
                logger.log("copyAndDeleteFile [" + dest + "] OutputStream Issue", e);
                return false;
            } else if (level == 3) {
                logger.log("copyAndDeleteFile [" + dest + "] OutputStream Inserting Issue", e);
            } else {
                logger.log("copyAndDeleteFile [" + file.getAbsolutePath() + "] Unknown Issue", e);
                return false;
            }
        } finally {
            try {
                if (in != null)
                    in.close();
                if (dupFileStream != null)
                    dupFileStream.close();
            } catch (IOException e) {
                logger.log("copyAndDeleteFile Closeing Stream Error", e);
            }
        }
        if (file.exists()) {
            file.delete();
        }
        return true;
    }
}