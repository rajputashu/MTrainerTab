package com.sisindia.ai.mtrainer.android.features.syncadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaMaster;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaPost;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.DetailsItem;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaDataResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaImageData;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaImageResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaRequest;
import com.sisindia.ai.mtrainer.android.models.ApiError;
import com.sisindia.ai.mtrainer.android.models.ImageUploadResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.SyncApi;
import com.sisindia.ai.mtrainer.android.utils.WaterMarkUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

import static android.view.View.GONE;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class UmbrellaSPISyncWorker extends Worker {

    private MtrainerDataBase dataBase;
    private SyncApi syncApi;
    private DashBoardApi dashBoardApi;
    private Context context;
    private int companyId;
    private int trainerId;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Logger logger;
    private static final String TAG = "UmbrellaSPISyncWorker";

    public UmbrellaSPISyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Toast.makeText(context, "Sync Started", Toast.LENGTH_SHORT).show();
        dataBase = MtrainerDataBase.getDatabase(context);
        logger = new Logger(this.getClass().getSimpleName());
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
            Log.d("ssasafsdf","doWork: Start");
            //logger.log("doWork: Start");

           /* List<UmbrellaMaster> umbrellaMasters = dataBase.getUmbrellaMasterDao().getUmbrellaMasterData();
            try {
                uploadUmbrellaData(umbrellaMasters);
            } catch (Exception e) {
                logger.log("uploadUmbrellaData Calling", e);
            }*/

            try {
                uploadUmbrellaImages();
            } catch (Exception e) {
                Log.d("ssasafsdf",e.getMessage());
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
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(0, 5,TimeUnit.MINUTES))
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
                request.setTrainerId(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID, -1));
                List<DetailsItem> detailsItems = new ArrayList<>();
                List<UmbrellaPost> posts = dataBase.getUmbrellaPostDao().getUmbrellaImage(master.umbrellaId);
                for (UmbrellaPost e : posts) {
                    DetailsItem item = new DetailsItem();
                    item.setImageId(e.imagePath);
                    item.setIsAdhocPost(e.isAdhoc);
                    item.setPostId(e.postId);
                    item.setPostName(e.postName);
                    detailsItems.add(item);
                }
                request.setDetails(detailsItems);
                addDisposable(dashBoardApi.uploadspiUmbrelladata(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onumbrellaSuccess, this::onApiError));

                /*Response<UmbrellaDataResponse> response = dashBoardApi.uploadspiUmbrelladata(request).execute();
                if (response.body() != null && response.body().statusCode == 200) {
                    Log.d("sadfsffdsf","Data uploaded sucessfully");
                   // dataBase.getUmbrellaMasterDao().clearCurrentUmbrellaMaster(master.umbrellaId);
                    Log.d("sadfsffdsf","Data uploaded sucessfully");
                }*/
            } catch (Exception e) {
                Log.d("sadfsffdsf",e.getCause().toString());
                logger.log("Exception in uploadUmbrellaData", e);
            }
        }
        List<UmbrellaPost> umbrellaImages = dataBase.getUmbrellaPostDao().getUmbrellaImageForSync();
        for(UmbrellaPost image : umbrellaImages) {
            dataBase.getUmbrellaPostDao().removeSingleUmbrellaImage(image.id);
        }

        for (UmbrellaMaster master : umbrellaMasters){
            dataBase.getUmbrellaMasterDao().clearCurrentUmbrellaMaster(master.umbrellaId);
        }
    }

    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    private void onumbrellaSuccess(UmbrellaDataResponse response) {
        //setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

        }
    }

    protected void onApiError(Throwable throwable) {
        Timber.e(throwable);
        if (throwable instanceof HttpException) {
            Response errorResponse = ((HttpException) throwable).response();
            if (errorResponse != null) {
                ResponseBody errorResponseBody = errorResponse.errorBody();
                if (errorResponseBody != null) {
                    ApiError response = new Gson().fromJson(errorResponseBody.charStream(), ApiError.class);
                    if (response != null) {
                        showToast(TextUtils.isEmpty(response.statusMessage) ? response.description : response.statusMessage);
                    }
                  /*  if(response.statusCode==500){
                        showToast("server error");
                    }
*/
                    else {
                        showToast(R.string.something_went_wrong);
                    }
                } else {
                    showToast(R.string.something_went_wrong);
                }
            }

            else {
                showToast(R.string.something_went_wrong);
            }
        } else {
            showToast(R.string.something_went_wrong);
        }
    }

    protected void showToast(@StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    private void uploadUmbrellaImages() {
        List<UmbrellaPost> umbrellaImages = dataBase.getUmbrellaPostDao().getUmbrellaspiImageForSync();
        Log.d("ssasafsdf",""+umbrellaImages.size());
        Log.d("syncdsfcdsf",""+umbrellaImages.size());
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
                        Log.d("ssasafsdf","Inside Compress");
                        //logger.log("uploadUmbrellaImages: Inside Compress " + image.imagePath);
                        File originalFile = new File(image.imagePath);
                        File imageDir = new File(image.imagePath.substring(0, image.imagePath.lastIndexOf("/")));
                        //logger.log("uploadTrainingImage: Inside Compress   If " + imageDir + " exist -> " + imageDir.exists() + " If file " + originalFile + " Exist --> " + originalFile.exists());
                        if (imageDir.exists() && originalFile.exists()) {
                            Log.d("ssasafsdf","Inside Compress Calling Compress");
                            //logger.log("uploadTrainingImage: Inside Compress Calling Compresss");
                            Compressor compressor = new Compressor(context);
                            compressor.setDestinationDirectoryPath(imageDir.getAbsolutePath());
                            try {
                                File compressedFile = compressor.compressToFile(originalFile, "min_" + originalFile.getName());
                                //logger.log("uploadTrainingImage: Compressed File : " + compressedFile.getAbsolutePath());
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
                        logger.log("uploadUmbrellaImages: Inside Compress exception"+e.getMessage());
                    }
                }
                if (image.isCompress == 1) {
                    Log.d("ssasafsdf","Inside Compress Calling 1");
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
                            Log.d("ssasafsdf","starteduploadfileapi");
                            Response<ImageUploadResponse> response = dashBoardApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID, "-1"))).execute();
                            addDisposable(syncApi.uploadImages1(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID, "-1")))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(((imageUploadResponse)->{
                                        if (imageUploadResponse!=null&& imageUploadResponse.statusCode == 200){
                                            image.imagePath = imageUploadResponse.data;
                                            dataBase.getUmbrellaPostDao().updateUmbrellaImageUrl(image.id, image.imagePath);
                                            dataBase.getUmbrellaPostDao().updateUmbrellaSpiImageState(image.umbrellaId);
                                        }
                                    }), this::onApiError));

                            // Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                           /* if (response.body() != null && response.body().statusCode == 200) {*/
                                // TODO: Need to uncomment
//                            if (file.exists())
//                                file.delete();
                                /*image.imagePath = response.body().data;
                                dataBase.getUmbrellaPostDao().updateUmbrellaImageUrl(image.id, image.imagePath);
                                dataBase.getUmbrellaPostDao().updateUmbrellaSpiImageState(image.umbrellaId);*/
                                //dataBase.getUmbrellaPostDao().removeSingleUmbrellaImage(image.id);
                                /*UmbrellaImageData umbrellaImageData = new UmbrellaImageData();
                                umbrellaImageData.setImageId(image.imagePath);
                                umbrellaImageData.setImageurl(image.imagePath);
                                umbrellaImageData.setIsAdhocPost(image.isAdhoc);
                                umbrellaImageData.setPostId(image.postId);
                                umbrellaImageData.setPostName(image.postName);
                                umbrellaImageData.setKeyId(image.keyId);
                                Response<UmbrellaImageResponse> dataUploadResponse = syncApi.uploadUmbrellaImageData(umbrellaImageData).execute();
                                if (dataUploadResponse.body() != null && dataUploadResponse.body().statusCode == 200) {
                                    dataBase.getUmbrellaPostDao().removeSingleUmbrellaImage(image.id);
                                }*/
                            /*}*/
                        } catch (IOException e) {
                            Log.d("ssasafsdf",""+e.getMessage());
                            logger.log("UmbrellaImage hasurl 0", e);
                        }
                    } /*else {
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
                    }*/
                }
            } catch (Exception e) {
                Log.d("ssasafsdf",""+e.getMessage());
                logger.log("Exception in uploadUmbrellaImages", e);
            }
        }


        List<UmbrellaMaster> umbrellaMasters = dataBase.getUmbrellaMasterDao().getUmbrellaMasterData();
        try {
            uploadUmbrellaData(umbrellaMasters);
        } catch (Exception e) {
            logger.log("uploadUmbrellaData Calling", e);
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