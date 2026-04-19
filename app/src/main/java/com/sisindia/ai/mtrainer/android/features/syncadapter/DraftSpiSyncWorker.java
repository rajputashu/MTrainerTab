//package com.sisindia.ai.mtrainer.android.features.syncadapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.webkit.MimeTypeMap;
//
//import androidx.annotation.NonNull;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import com.droidcommons.preference.Prefs;
//import com.google.gson.FieldNamingPolicy;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.sisindia.ai.mtrainer.android.BuildConfig;
//import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
//import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;
//import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
//import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
//import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
//import com.sisindia.ai.mtrainer.android.db.entities.DraftSpiPhotoEntity;
//import com.sisindia.ai.mtrainer.android.models.ImageDataUploadResponse;
//import com.sisindia.ai.mtrainer.android.models.ImageUploadResponse;
//import com.sisindia.ai.mtrainer.android.models.TrainingFinalSubmitResponse;
//import com.sisindia.ai.mtrainer.android.models.sync.DraftSpiData;
//import com.sisindia.ai.mtrainer.android.models.sync.DraftSpiPicturesItems;
//import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
//import com.sisindia.ai.mtrainer.android.rest.SyncApi;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;
//import timber.log.Timber;
//
//public class DraftSpiSyncWorker extends Worker {
//    private MtrainerDataBase dataBase;
//    private SyncApi syncApi;
//    private DashBoardApi dashBoardApi;
//    private Context context;
//    private final Logger logger = new Logger(this.getClass().getSimpleName());
//
//
//    private static final String TAG = "SyncWorker";
//
//    public DraftSpiSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//        dataBase = MtrainerDataBase.getDatabase(context);
//        initRetrofit();
//        this.context = context;
//        //Log.d(TAG, "SyncWorker: Created");
//        logger.log("SyncWorker: Created");
//
//
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        /*Bundle b = new Bundle();
//        SyncAdapterInitialization syncAdapterInitialization = new SyncAdapterInitialization(context);
//        syncAdapterInitialization.startForceSyncing(b);*/
//        logger.log("doWork: Start");
//
//        uploadDraftSpiImages();
//        Log.d(TAG, "doWork: about to return");
//        return Result.success();
//    }
//
//    private void initRetrofit() {
//        Log.d(TAG, "initRetrofit: ");
//        final int CONNECT_TIMEOUT_MILLIS = 1500 * 1000; // 15s
//        final int READ_TIMEOUT_MILLIS = 2000 * 1000; // 20s
//
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Timber.d(message));
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                .create();
//
//        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS);
//
//
//        RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
//        MtrainerLogIntercepter mtrainerLogIntercepter = new MtrainerLogIntercepter();
//        builder.addInterceptor(headerInterceptor);
//        builder.addInterceptor(mtrainerLogIntercepter);
//        builder.addInterceptor(loggingInterceptor);
//
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.MTRAINER_HOST).client(builder.build())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//
//        syncApi = retrofit.create(SyncApi.class);
//        dashBoardApi = retrofit.create(DashBoardApi.class);
//        Log.d(TAG, "initRetrofit: ");
//    }
//
//
//
//
//
//    // below method is draft spi image syncing
//    private synchronized void uploadDraftSpiImages() {
//
//        List<DraftSpiPhotoEntity> draftSpiPhotoEntityList = dataBase.getDraftSpiPhotoDao().getDraftSpiPhotoForSync();
//
//        for (DraftSpiPhotoEntity entity : draftSpiPhotoEntityList) {
//            //Create a file object using file path
//            File file = new File(entity.imageUrl);
//            // Create a request body with file and image media type
//            if (file.exists()) {
//                Log.v("MIME : ", " uploadTrainingImage" + file.getAbsolutePath());
//                String str = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
//                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.toLowerCase());
//                if(type == null)
//                    type = "*/*";
//                Log.v("MIME TYPE : ", " " + type);
//                RequestBody fileReqBody = RequestBody.create(MediaType.parse(type), file);
//                // Create MultipartBody.Part using file request-body,file name and part name
//                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
//
//                try {
//                    Response<ImageUploadResponse> response = syncApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))).execute();
//                    if (response.body() != null && response.body().statusCode == 200) {
//                        if (file.exists())
//                            file.delete();
//                        entity.imageUrl = response.body().data;
//                        dataBase.getDraftSpiPhotoDao().updateDraftSpiPhoto(entity);
//                        DraftSpiData draftSpiData= new DraftSpiData();
//                        draftSpiData.spiId = entity.spiId;
//                        draftSpiData.trainerId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
//
//                        // int postid = dataBase.getDraftSpiPhotoDao().getDraftSpiList(Prefs.getInt(PrefsConstants.SPI_POST_ID));
//                        //  List<String> spiId = dataBase.getDraftSpiPhotoDao().getDraftSpiid(response.body().data);
//
//                        List<DraftSpiPicturesItems> syncDraftSpiPictureList = new ArrayList<>();
//                        syncDraftSpiPictureList = getDraftPicture(Collections.singletonList(response.body().data),Prefs.getInt(PrefsConstants.SPI_POST_ID));
//
//                        //  syncDraftSpiPictureList = getDraftPicture(Collections.singletonList(response.body().data),draftspiId);
//
//                        // draftSpiData.draftSpiPicturesItems.get(0).imageUrl=(Collections.singletonList(response.body().data));
//
//                        draftSpiData.setDraftSpiPicturesItems(syncDraftSpiPictureList);
//
//                        Response<ImageDataUploadResponse> dataUploadResponse = syncApi.uploadDraftImageData(draftSpiData).execute();
//                        if (dataUploadResponse.body() != null && dataUploadResponse.body().statusCode == 200) {
//                            dataBase.getDraftSpiPhotoDao().deleteDraftPhoto(entity.spiId);
//                            Log.v("Training Photo", " Deleted : " + entity.spiId);
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    // below code for Draft Spi images getting from table
//
//    private List<DraftSpiPicturesItems> getDraftPicture(List<String> spiid, int postid) {
//        List<DraftSpiPicturesItems> draftSpiPicturesItemList = new ArrayList<>();
//        for (String spiId : spiid){
//            List<String> trainingPhotoList = dataBase.getDraftSpiPhotoDao().getDraftSpiPhotoList(spiId);
//            int spiPostId = dataBase.getDraftSpiPhotoDao().getDraftSpiPostid(postid);
//
//            DraftSpiPicturesItems picturesItem = new DraftSpiPicturesItems();
//            picturesItem.setImageUrl(trainingPhotoList);
//            picturesItem.setPostId(spiPostId);
//            draftSpiPicturesItemList.add(picturesItem);
//        }
//        return draftSpiPicturesItemList;
//    }
//
//
//}
