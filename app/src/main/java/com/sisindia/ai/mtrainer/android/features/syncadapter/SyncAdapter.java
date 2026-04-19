package com.sisindia.ai.mtrainer.android.features.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.UiThread;

import com.droidcommons.preference.Prefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AdhocSavedTopics;
import com.sisindia.ai.mtrainer.android.db.entities.AssementEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.RplFormEntity;
import com.sisindia.ai.mtrainer.android.db.entities.RplFormPhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedback;
import com.sisindia.ai.mtrainer.android.db.entities.TrainingPhotoAttachmentEntity;
/*import com.sisindia.ai.mtrainer.android.di.component.DaggerMTrainerApplicationComponent;
import com.sisindia.ai.mtrainer.android.di.component.DaggerSyncComponent;
import com.sisindia.ai.mtrainer.android.di.component.SyncComponent;*/
import com.sisindia.ai.mtrainer.android.di.component.DaggerMTrainerSyncComponent;
import com.sisindia.ai.mtrainer.android.di.component.MTrainerSyncComponent;
import com.sisindia.ai.mtrainer.android.di.module.AppRetrofitModule;
import com.sisindia.ai.mtrainer.android.di.module.SyncModule;
import com.sisindia.ai.mtrainer.android.models.AdhocEmployeesSaved;
import com.sisindia.ai.mtrainer.android.models.AdhocTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.AdhoctopicItem;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.CancelTrainingResponse;
import com.sisindia.ai.mtrainer.android.models.DashboardRequest;
import com.sisindia.ai.mtrainer.android.models.ImageDataUploadResponse;
import com.sisindia.ai.mtrainer.android.models.ImageUploadRequest;
import com.sisindia.ai.mtrainer.android.models.ImageUploadResponse;
import com.sisindia.ai.mtrainer.android.models.RplFormImageRequest;
import com.sisindia.ai.mtrainer.android.models.RplFormUploadRequest;
import com.sisindia.ai.mtrainer.android.models.SyncApiResponse;
import com.sisindia.ai.mtrainer.android.models.TrainerPerformanceResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.Part;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private ContentResolver mContentResolver;
    private Context context;
    private final Logger logger = new Logger(this.getClass().getSimpleName());

    private DashBoardApi dashBoardApi;
    private SyncApi syncApi;
    private PrefsConstants utils;
    MtrainerDataBase dataBase;
    private static final String TAG = "Syncadapter";

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        this.context = context;
        //syncApi = (SyncApi) SyncApi.retrofit.create(SyncApi.class);
        //SyncComponent syncComponent = DaggerSyncComponent.builder().application(this)
        MTrainerSyncComponent component = DaggerMTrainerSyncComponent.builder()
                .syncModule(new SyncModule(context))
                .build();
        syncApi = component.getSyncApi();
        dashBoardApi = component.getDashBoardApi();
        dataBase = MtrainerDataBase.getDatabase(context);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
       syncLocalTableDataToSever();
       syncRplForm();
       uploadRplFormImage();
       //uploadRplImage();
    }

    private synchronized void syncLocalTableDataToSever() {
        List<TrainingFinalSubmitResponse.TrainingSubmitResponse> responseList = dataBase.getTrainingFinalSubmitDao().getSavedRota().blockingGet();
        getSavedRota(responseList);
        uploadAttendanceImage();
        uploadTrainingImage();
        uploadAssessmentVideo();



      //  syncRplForm();
      //  uploadRplImage();

    }

    private void getSavedRota(List<TrainingFinalSubmitResponse.TrainingSubmitResponse> responseList) {
        for (TrainingFinalSubmitResponse.TrainingSubmitResponse response : responseList) {
            List<AttendanceEntity> attendanceEntityList = dataBase.getAttendanceDao().getAttendanceSubList(response.rotaid);
            List<TrainingAttendanceItem> syncAttendance = new ArrayList<>();
            syncAttendance = getAttendenceData(attendanceEntityList);

            List<AttendanceEntity> AdhocDirtyAttendance = dataBase.getAttendanceDao().getAdhocAttendanceList(response.rotaid);
            List<AdhocAttendanceItem> adhocAttendanceItemList = getAdhocAttendanceList(AdhocDirtyAttendance);


            List<AssessmentReport> syncAssessmentReport = new ArrayList<>();
            syncAssessmentReport = dataBase.getAssessmentDao().getAssessmentReportForSync(response.rotaid);

            List<AssementEntity> dirtyAssessment = dataBase.getAssessmentDao().getAdHocAssessmentReportForSync(response.rotaid);
            List<AdhocAssessmentItem> adhocAssessmentItemList = getAdhocAssesmentList(dirtyAssessment);




            List<String> trainingPhotoAttachmentPostList = dataBase.getPhotoAttachmentDao().getTrainingPostList(response.rotaid);
            List<TrainingPicturesItem> syncTrainingPictureList = new ArrayList<>();
            syncTrainingPictureList = getTrainingPicture(trainingPhotoAttachmentPostList, response.rotaid);

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
            syncRotaDetails.setActualTraineeCount(Prefs.getString(PrefsConstants.CLIENT_EMPLOYEE_COUNT));
            //syncing task execution result
            UserData syncUserData = new UserData();
            syncUserData.setTrainingAttendance(syncAttendance);
            syncUserData.setAdhocAttendance(adhocAttendanceItemList);
            syncUserData.setClientHandshakeDetails(syncClientHandshakeDetails);
            syncUserData.setRotaDetails(syncRotaDetails);
            syncUserData.setTrainingPictures(syncTrainingPictureList);
            syncUserData.setAssessment(syncAssessmentReport);
            syncUserData.setAdhocAssessment(adhocAssessmentItemList);

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
        for(AttendanceEntity entity :  attendanceEntityList){
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
        for(AssementEntity entity :  assementEntityList){
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
        List<String> reason = new ArrayList<>();
        List<Integer> reasonIdList = dataBase.getSavedFeedbackReasonDao().getReasonList(rotaId);
        for(int e : reasonIdList)
            reason.add(String.valueOf(e));

        //reason = dataBase.getSavedFeedbackReasonDao().getReasonList(rotaId).stream().map(String::valueOf).collect(Collectors.toList());;
        clientHandshakeDetails.setFeedback(reason);
        return clientHandshakeDetails;
    }

    private List<Integer> getTopicIdList(int rotaId) {
        return dataBase.getSavedTopicDao().getSavedTopicSubList(rotaId);
    }

    private List<String> getAdhocFinalList(int rotaId) {
        return dataBase.getAdhocSavedTopicsDao(). getAdhocFinalTopicList(rotaId);
    }


    private void onError(Throwable throwable) {

    }


    private void submitRecords(TrainingFinalSubmitRequest request) {
        //String tocken = Prefs.getString(PrefsConstants.AUTH_TOKEN_KEY);
        syncApi.SubmitTrainingReport(request).enqueue(new Callback<SyncApiResponse>() {
            @Override
            public void onResponse(Call<SyncApiResponse> call, Response<SyncApiResponse> response) {
                if(response.body() != null && response.body().statusCode == 200) {

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



/*

                    // Update Status to Completed
                    dataBase.getTrainingCalenderDao().updateInProgressStatus(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Attendance Deleted
                    dataBase.getAttendanceDao().deleteSyncedAttendance(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Rota Deleted
                    dataBase.getTrainingFinalSubmitDao().deleteSyncedRota(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Feedback Deleted
                    dataBase.getSavedFeedbackDao().deleteFeedback(String.valueOf(response.body().rotaID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Feedback Reason Deleted
                    dataBase.getSavedFeedbackReasonDao().deleteReason(String.valueOf(response.body().rotaID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    // Topics Deleted
                    dataBase.getSavedTopicDao().deleteSavedTopicByRotaId(response.body().rotaID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
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
                            .subscribe();*/



                }
                getDashboardPerformance();
                Toast.makeText(context, "Data Synced successfully ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SyncApiResponse> call, Throwable t) {

            }
        });
    }

    //rpl form



    private synchronized void syncRplForm() {

        List<RplFormEntity> rplForms = dataBase.getRplFormDao().getRplForm();
        if (rplForms != null) {
            for (RplFormEntity rplForm : rplForms) {
               // rplFormUploadRequest.unitId = "1";//rplForm.getUnitName();
               // rplFormUploadRequest.branchid = "1"; //rplForm.getBranchName();

                RplFormUploadRequest rplFormUploadRequest = new RplFormUploadRequest();
                rplFormUploadRequest.rotaid = rplForm.getRotaid();
                rplFormUploadRequest.unitId = rplForm.getSiteId();
                rplFormUploadRequest.branchid = rplForm.getBranchId();
                rplFormUploadRequest.aadhaarfrontimageId = rplForm.getPhotoid1();
                rplFormUploadRequest.aadhaarbackimageId = rplForm.getPhotoid2();
                rplFormUploadRequest.category = rplForm.getCategory();
                rplFormUploadRequest.educationqualification = rplForm.getEducationalQualification();
                rplFormUploadRequest.syncdatetime =rplForm.getSyncdatetime();
                rplFormUploadRequest.mobileno =rplForm.getMobilebNo();
                rplFormUploadRequest.registrationNo =  rplForm.getRegNo();
                rplFormUploadRequest.name =rplForm.getRegistrationNameET();


              //  String tocken = Prefs.getString(PrefsConstants.AUTH_TOKEN_KEY);
                syncApi.rplform(rplFormUploadRequest).enqueue(new Callback<BaseApiResponse>() {
                    @Override
                    public void onResponse(Call<BaseApiResponse> call, Response<BaseApiResponse> response) {
                       if (response.body()!=null && response.body().statusCode == 200) {
                            Toast.makeText(context, "Rpl Form Data Synced successfully ", Toast.LENGTH_SHORT).show();
                            Log.v(" rpl form", " sent : " + rplForm);
                           Log.d(" rpl form", " sent : " + rplFormUploadRequest);
                           Log.e(" rpl form", " sent : " + rplFormUploadRequest);

                            // rplform Deleted
                            dataBase.getRplFormDao().deleteRplForm(Integer.parseInt(rplForm.getRotaid()))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
                       }
                    }

                    @Override
                    public void onFailure(Call<BaseApiResponse> call, Throwable t) {

                        Toast.makeText(context, "Rpl Form Failure ", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        }

    }




    private synchronized void uploadAttendanceImage() {
        //String tocken = Prefs.getString(PrefsConstants.AUTH_TOKEN_KEY);
        List<AttendancePhotoEntity> attendancePhotoEntityList = dataBase.getAttendancePhotoDao().getSavedAttendancePic();

        for (AttendancePhotoEntity entity : attendancePhotoEntityList) {
            //Create a file object using file path
            File file = new File(entity.attendancePhotoURI);
            // Create a request body with file and image media type
            Log.v("MIME : ", " uploadTrainingImage");
            String str = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.toLowerCase());
            if(type == null)
                type = "*/*";
            Log.v("MIME TYPE : ", " " + type);
            RequestBody fileReqBody = RequestBody.create(MediaType.parse(type), file);
            // Create MultipartBody.Part using file request-body,file name and part name
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

            try {
               Response<ImageUploadResponse> response = syncApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))).execute();
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
                        Log.v("Attendance Photo", " Deleted : " + entity.id);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void uploadTrainingImage() {
        //String tocken = Prefs.getString(PrefsConstants.AUTH_TOKEN_KEY);
        List<TrainingPhotoAttachmentEntity> trainingPhotoAttachmentEntityList = dataBase.getPhotoAttachmentDao().getTrainingPhotoForSync();

        for (TrainingPhotoAttachmentEntity entity : trainingPhotoAttachmentEntityList) {
            //Create a file object using file path
            File file = new File(entity.trainingPhotoURI);
            // Create a request body with file and image media type
            if (file.exists()) {
                Log.v("MIME : ", " uploadTrainingImage");
                String str = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.toLowerCase());
                if(type == null)
                    type = "*/*";
                Log.v("MIME TYPE : ", " " + type);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse(type), file);
                // Create MultipartBody.Part using file request-body,file name and part name
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                /*RequestBody companyId =
                        RequestBody.create(
                                okhttp3.MultipartBody.FORM, Prefs.getString(PrefsConstants.COMPANY_ID));
*/
                //Create request body with text description and text media type

                try {
                  Response<ImageUploadResponse> response = syncApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))).execute();
                  // Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                    Log.v("Training Photo", " Name : " + file.getName() + "  Path : " + file.getAbsolutePath());
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
                    e.printStackTrace();
                }
            }
        }
    }
    //..............................................................

    private synchronized void uploadRplFormImage() {

        List<RplFormPhotoEntity> rplPhotoAttachmentEntityList = dataBase.getRplFormPhotoDao().getRplPhotoForSync();

        for (RplFormPhotoEntity entity : rplPhotoAttachmentEntityList) {
            //Create a file object using file path
            File file = new File(entity.rplFormPhotoURI);
            // Create a request body with file and image media type
            if (file.exists()) {
                Log.v("MIME : ", " uploadTrainingImage");
                String str = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.toLowerCase());
                if(type == null)
                    type = "*/*";
                Log.v("MIME TYPE : ", " " + type);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse(type), file);
                // Create MultipartBody.Part using file request-body,file name and part name
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                //Create request body with text description and text media type

                try {
                    Response<ImageUploadResponse> response = syncApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))).execute();
                    // Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                    Log.v("Training Photo", " Name : " + file.getName() + "  Path : " + file.getAbsolutePath());
                    if (response.body() != null && response.body().statusCode == 200) {
                        if (file.exists())
                            file.delete();
                        entity.rplFormPhotoURI = response.body().data;
                        dataBase.getRplFormPhotoDao().updateRplPhoto(entity);

                        RplFormImageRequest uploadRequest = new RplFormImageRequest();
                        uploadRequest.pictureid = entity.rplFormPhotoId;
                        uploadRequest.picturetypeid = String.valueOf(entity.pictureTypeId);
                        uploadRequest.rotaid = String.valueOf(entity.rotaId);
                        uploadRequest.file=entity.rplFormPhotoURI;
                       // uploadRequest.file=response.body().data;
                        Response<BaseApiResponse> dataUploadResponse = syncApi.uploadImageRpl(uploadRequest).execute();

                        if (dataUploadResponse.body() != null && dataUploadResponse.body().statusCode == 200) {
                           // dataBase.getPhotoAttachmentDao().deletePhotoAttchment(entity.trainingPhotoId);

                            dataBase.getRplFormPhotoDao().deleteRplPhoto(entity.rplFormPhotoId);
                                   /* .subscribeOn(Schedulers.io())
                                    .subscribe();*/
                            Log.v("Training Photo", " Deleted : " + entity.rplFormPhotoId);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }





/*
      private synchronized void uploadRplImage() {

       // String tocken = Prefs.getString(PrefsConstants.AUTH_TOKEN_KEY);
        List<RplFormPhotoEntity> attendancePhotoEntityList = dataBase.getRplFormPhotoDao().getSavedRplFormPic();

        for (RplFormPhotoEntity entity : attendancePhotoEntityList) {
            //Create a file object using file path
            File file = new File(entity.attendancePhotoURI);

            if (file.exists()) {
                // Create a request body with file and video media type
              //  entity.trainingPhotoURI = response.body().data;
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("video/mp4"), file);
                // Create MultipartBody.Part using file request-body,file name and part name
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                RplFormImageRequest uploadRequest = new RplFormImageRequest();
                uploadRequest.pictureid = entity.attendancePhotoId;
                uploadRequest.picturetypeid = String.valueOf(entity.pictureTypeId);
                uploadRequest.rotaid = String.valueOf(entity.rotaId);
                uploadRequest.file=entity.attendancePhotoURI;

                if (String.valueOf(entity.pictureTypeId).equals("6")) {
                    syncApi.uploadImageRpl(uploadRequest).enqueue(new Callback<BaseApiResponse>() {
                        @Override
                        public void onResponse(Call<BaseApiResponse> call, Response<BaseApiResponse> response) {
                            try {
                                if (response.body().statusCode == 200 || response.isSuccessful()) {

                                    dataBase.getRplFormPhotoDao().deleteRplPhoto(entity.id)
                                            .subscribeOn(Schedulers.io())
                                            .subscribe();
                                    Log.v(" rpl Attendance Photo", " Deleted : " + entity.id);

                                    // MTrainerApplication.getInstance().getUpdateDBInstance().updateTrainingFileAttachment(photoId, "1");

                                    //  MTrainerApplication.getInstance().getDeletionDBInstance().deleteFileSyncRecord(photoId, base);
                                }
                            } catch (Exception e) {
                            }
                        }
                        @Override
                        public void onFailure(Call<BaseApiResponse> call, Throwable t) {

                        }
                    });

                } else {
                }
            }


        }
    }
*/


        private synchronized void uploadAssessmentVideo() {
        //String tocken = Prefs.getString(PrefsConstants.AUTH_TOKEN_KEY);
        List<AssementEntity> assementEntityList = dataBase.getAssessmentDao().getAssessmentVideoForSync();

        for (AssementEntity entity : assementEntityList) {
            //Create a file object using file path
            //logger.logDb(assementEntityList,"Assessement Table");
            File file = new File(entity.getVideoPath());
            // Create a request body with file and image media type
            if (file.exists()) {
                //logger.log("File Exists -> "+file.getAbsolutePath());
               // Log.v("MIME : ", " uploadTrainingImage");
                String str = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.toLowerCase());
                if(type == null)
                    type = "*/*";
                //Log.v("MIME TYPE : ", " " + type);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse(type), file);
                // Create MultipartBody.Part using file request-body,file name and part name
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                /*RequestBody companyId =
                        RequestBody.create(
                                okhttp3.MultipartBody.FORM, Prefs.getString(PrefsConstants.COMPANY_ID));*/
                //Create request body with text description and text media type

                try {
                 Response<ImageUploadResponse> response = syncApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))).execute();
                //  Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                    if (response.body() != null && response.body().statusCode == 200) {
                        //logger.log("Video Uploaded Sucessfully");
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
                            //logger.log("Video File Entry Deleted Sucessfully --> ");
                               //List<AssementEntity> assementEntityList = dataBase.getAssessmentDao().getAssessmentVideoForSync();
                                 //logger.logDb(dataBase.getAssessmentDao().getAssessmentVideoForSync(),"Assessement Table after deleting");

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

        dashBoardApi.getTrainerPerformance(request)
                //.delay(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onTrainingPerformanceSuccess, th -> {});
    }

}
