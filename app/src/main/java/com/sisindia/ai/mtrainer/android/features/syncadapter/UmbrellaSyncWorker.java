package com.sisindia.ai.mtrainer.android.features.syncadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.MimeTypeMap;

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
import com.sisindia.ai.mtrainer.android.db.entities.AssementEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedback;
import com.sisindia.ai.mtrainer.android.db.entities.TrainingPhotoAttachmentEntity;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaMaster;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaPost;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.DetailsItem;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaDataResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaImageData;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaImageResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaRequest;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class UmbrellaSyncWorker extends Worker {
    private MtrainerDataBase dataBase;
    private SyncApi syncApi;
    private DashBoardApi dashBoardApi;
    private Context context;
    private int companyId;
    private int trainerId;
    private final Logger logger = new Logger(this.getClass().getSimpleName());


    private static final String TAG = "UmbrellaSyncWorker";

    public UmbrellaSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dataBase = MtrainerDataBase.getDatabase(context);
        companyId = Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID, "-1"));
        trainerId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID, -1);
        initRetrofit();
        this.context = context;
        //logger.log("UmbrellaSyncWorker: Created");
    }

    @NonNull
    @Override
    public Result doWork() {
        /*Bundle b = new Bundle();
        SyncAdapterInitialization syncAdapterInitialization = new SyncAdapterInitialization(context);
        syncAdapterInitialization.startForceSyncing(b);*/
        //Log.d(TAG, "doWork: Start");
        if(companyId != -1) {
            //logger.log("doWork: Start");
            List<UmbrellaMaster> umbrellaMasters = dataBase.getUmbrellaMasterDao().getUmbrellaMasterData();
            try {
                uploadUmbrellaData(umbrellaMasters);
            } catch (Exception e) {
                logger.log("uploadUmbrellaData Calling", e);
            }
            try {
                uploadUmbrellaImages();
            } catch (Exception e) {
                logger.log("uploadUmbrellaImages Calling", e);
            }
        }


       //logger.log( "doWork: about to return");
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
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);


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
       //logger.log( "initRetrofit: ");
    }

    private void uploadUmbrellaData(List<UmbrellaMaster> umbrellaMasters) {
            for (UmbrellaMaster master : umbrellaMasters) {
                try {
                    UmbrellaRequest request = new UmbrellaRequest();
                    request.setCompanyId(companyId);
                    request.setTotalUmbrella(master.umbrellaCount);
                    request.setSiteId(master.siteId);
                    request.setTrainerId(trainerId);
                    List<DetailsItem> detailsItems = new ArrayList<>();
                    List<UmbrellaPost> posts = dataBase.getUmbrellaPostDao().getUmbrellaImage(master.umbrellaId);
                    for (UmbrellaPost e : posts) {
                        DetailsItem item = new DetailsItem();
                        item.setImageId(e.ImageId);
                        item.setIsAdhocPost(e.isAdhoc);
                        item.setPostId(e.postId);
                        item.setPostName(e.postName);
                        detailsItems.add(item);
                    }
                    request.setDetails(detailsItems);
                    Response<UmbrellaDataResponse> response = syncApi.uploadUmbrelladata(request).execute();
                    if (response.body() != null && response.body().statusCode == 200) {
                        int keyId = response.body().keyId;
                        dataBase.getUmbrellaPostDao().updateUmbrellaImageState(master.umbrellaId, keyId);
                        dataBase.getUmbrellaMasterDao().clearCurrentUmbrellaMaster(master.umbrellaId);
                    }
                } catch (Exception e) {
                    logger.log("Exception in uploadUmbrellaData", e);
                }
            }
    }

    private void uploadUmbrellaImages() {
        List<UmbrellaPost> umbrellaImages = dataBase.getUmbrellaPostDao().getUmbrellaImageForSync();
       //logger.log( "uploadUmbrellaImages: " + umbrellaImages.size());
        for(UmbrellaPost image : umbrellaImages) {
            try {
                if (image.isMark == 0) {
                    boolean isMarked = mark(image.waterMark, image.imagePath);
                    if (!isMarked) {
                        //logger.log("Marking Error -> " + image.imagePath);
                        continue;
                    }
                    dataBase.getUmbrellaPostDao().updateImageWatermarkState(image.id);
                }
                if (image.isCompress == 0) {
                   try {
                       //logger.log("uploadUmbrellaImages: Inside Compress " + image.imagePath);
                       File originalFile = new File(image.imagePath);
                       File imageDir = new File(image.imagePath.substring(0, image.imagePath.lastIndexOf("/")));
                      // logger.log("uploadTrainingImage: Inside Compress   If " + imageDir + " exist -> " + imageDir.exists() + " If file " + originalFile + " Exist --> " + originalFile.exists());
                       if (imageDir.exists() && originalFile.exists()) {
                          // logger.log("uploadTrainingImage: Inside Compress Calling Compresss");
                           Compressor compressor = new Compressor(context);
                           compressor.setDestinationDirectoryPath(imageDir.getAbsolutePath());
                           try {
                               File compressedFile = compressor.compressToFile(originalFile, "min_" + originalFile.getName());
                              // logger.log("uploadTrainingImage: Compressed File : " + compressedFile.getAbsolutePath());
                               dataBase.getUmbrellaPostDao().updateImageCompressState(image.id, compressedFile.getAbsolutePath());
                               image.imagePath = compressedFile.getAbsolutePath();
                               image.isCompress = 1;
                               if (originalFile.exists())
                                   originalFile.delete();
                           } catch (IOException e) {
                               logger.log("Compression Error " + "[" + originalFile + "] -> ", e);
                               continue;
                           }
                       } else
                           logger.log("Umbrella Image [Compress] -> File Not Exist -> " + originalFile);
                   }catch (Exception e){

                   }
                }
                /*if (image.isCompress == 1) {*/
                    //Create a file object using file path
                    if (image.hasUrl == 0) {
                        File file = new File(image.imagePath);
                        // Create a request body with file and image media type
                        String str = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
                        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.toLowerCase());
                        if (type == null)
                            type = "*/*";
                        RequestBody fileReqBody = RequestBody.create(MediaType.parse(type), file);
                        // Create MultipartBody.Part using file request-body,file name and part name
                        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                        try {
                            Response<ImageUploadResponse> response = syncApi.uploadImages(part, companyId).execute();
                            // Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                            if (response.body() != null && response.body().statusCode == 200) {
                                // TODO: Need to uncomment
//                            if (file.exists())
//                                file.delete();
                                image.imagePath = response.body().data;
                                dataBase.getUmbrellaPostDao().updateUmbrellaImageUrl(image.id, image.imagePath);
                                UmbrellaImageData umbrellaImageData = new UmbrellaImageData();
                                umbrellaImageData.setImageId(image.ImageId);
                                umbrellaImageData.setImageurl(image.imagePath);
                                umbrellaImageData.setIsAdhocPost(image.isAdhoc);
                                umbrellaImageData.setPostId(image.postId);
                                umbrellaImageData.setPostName(image.postName);
                                umbrellaImageData.setKeyId(image.keyId);
                                Response<UmbrellaImageResponse> dataUploadResponse = syncApi.uploadUmbrellaImageData(umbrellaImageData).execute();
                                if (dataUploadResponse.body() != null && dataUploadResponse.body().statusCode == 200) {
                                    dataBase.getUmbrellaPostDao().removeSingleUmbrellaImage(image.id);
                                }
                            }
                        } catch (IOException e) {
                            logger.log("UmbrellaImage hasurl 0", e);
                        }
                    } else {
                        try {
                            UmbrellaImageData umbrellaImageData = new UmbrellaImageData();
                            umbrellaImageData.setImageId(image.ImageId);
                            umbrellaImageData.setImageurl(image.imagePath);
                            umbrellaImageData.setIsAdhocPost(image.isAdhoc);
                            umbrellaImageData.setPostId(image.postId);
                            umbrellaImageData.setPostName(image.postName);
                            umbrellaImageData.setKeyId(image.keyId);
                            Response<UmbrellaImageResponse> dataUploadResponse = syncApi.uploadUmbrellaImageData(umbrellaImageData).execute();
                            if (dataUploadResponse.body() != null && dataUploadResponse.body().statusCode == 200) {
                                dataBase.getUmbrellaPostDao().removeSingleUmbrellaImage(image.id);
                            }
                        } catch (Exception e) {
                            logger.log("UmbrellaImage hasurl 1", e);
                        }
                    }
               /* }*/
            } catch (Exception e) {
                logger.log("Exception in uploadUmbrellaImages", e);
            }
        }
    }

    /*private void mark(String watermark, String path) {

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
        }
    }*/

    private synchronized boolean mark(String watermark, String path) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        if (bitmap == null) {
            //logger.log("Bitmap null [" + path + "]");
            return false;
        }
        if (watermark == null) {
           // logger.log("Watermark null [" + path + "] -> " + watermark);
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
           // logger.log("Watermarked Bitmap null -> ", e);
            return false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            watermarkedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        } catch (IOException e) {
            copyAndDeleteFile(duplicateFileName, path);
           // logger.log("mark", e);
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                  //  logger.log("Mark Closeing Stream Error", e);
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
                //logger.log("copyAndDeleteFile [" + file.getAbsolutePath() + "] InputStream Issue", e);
                return false;
            } else if (level == 2) {
               // logger.log("copyAndDeleteFile [" + dest + "] OutputStream Issue", e);
                return false;
            } else if (level == 3) {
               // logger.log("copyAndDeleteFile [" + dest + "] OutputStream Inserting Issue", e);
            } else {
               // logger.log("copyAndDeleteFile [" + file.getAbsolutePath() + "] Unknown Issue", e);
                return false;
            }
        } finally {
            try {
                if (in != null)
                    in.close();
                if (dupFileStream != null)
                    dupFileStream.close();
            } catch (IOException e) {
              //  logger.log("copyAndDeleteFile Closeing Stream Error", e);
            }
        }
        if (file.exists()) {
            file.delete();
        }
        return true;
    }
}