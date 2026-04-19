package com.sisindia.ai.mtrainer.android.features.spi.mounted;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.DraftSpiPhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.MountedSpiPhotoEntity;
import com.sisindia.ai.mtrainer.android.features.spi.model.MountedStatusResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiImage;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.DesignSpiRequest;
import com.sisindia.ai.mtrainer.android.models.DesignSpiResponse;
import com.sisindia.ai.mtrainer.android.models.ImageUploadResponse;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;
import com.sisindia.ai.mtrainer.android.models.SpiPrintingRequest;
import com.sisindia.ai.mtrainer.android.models.spi.MountedResponse;
import com.sisindia.ai.mtrainer.android.models.sync.DraftSpiData;
import com.sisindia.ai.mtrainer.android.models.sync.DraftSpiPicturesItems;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;
import com.sisindia.ai.mtrainer.android.rest.SyncApi;
import com.sisindia.ai.mtrainer.android.utils.SpiUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
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

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_AGAIN_SPI_MAIN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_APPROVAL_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TAKE_IMAGE;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TAKE_MOUNTED_IMAGE;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class MountedViewModel extends MTrainerViewModel {
    public MountedRecyclerAdapter mountedRecyclerAdapter;

    public ObservableField<String> status= new ObservableField<>();

    public ObservableField<String> customer= new ObservableField<>();
    public ObservableField<String> customerid= new ObservableField<>();
    public ObservableField<String> type= new ObservableField<>();
    public ObservableField<String> mountedstatus= new ObservableField<>("pending");
    @Inject
    DashBoardApi dashBoardApi;
    MtrainerDataBase dataBase;
    public int imagecontains;
    @Inject
    Picasso picasso;
    private static final String TAG = "DraftSpiSync";
    @Inject
    SyncApi syncApi;
    @Inject
    AuthApi authApi;
    public   String mountedStatusDone="in progress";

    @Inject
    public MountedViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        mountedRecyclerAdapter = new MountedRecyclerAdapter(dataBase, picasso,application);
        dataBase= MtrainerDataBase.getDatabase(getApplication());
        customer.set(Prefs.getString(PrefsConstants.SPI_CUSTOMER));
        customerid.set(Prefs.getString(PrefsConstants.SPI_UNIT_CODE));
        type.set(Prefs.getString(PrefsConstants.SPI_TYPE));
        status.set(Prefs.getString(PrefsConstants.SPI_STATUS));
    }

    private void initRetrofit() {
        OkHttpClient.Builder builder1 = new OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        MtrainerLogIntercepter mtrainerLogIntercepter = new MtrainerLogIntercepter();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        builder1.addInterceptor(headerInterceptor);
        builder1.addInterceptor(mtrainerLogIntercepter);

        builder1.addInterceptor(httpLoggingInterceptor);
        Retrofit retrofit1 = new Retrofit.Builder().baseUrl(BuildConfig.MTRAINER_HOST).client(builder1.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        authApi = retrofit1.create(AuthApi.class);
    }


    private void onPreAuthResponse1(PreAuthResponse response) {
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }


    public MountedViewListener viewListener=new MountedViewListener() {
        @Override
        public void onMountedItemClick(SpiBasicInfoResponse.SpiBasicInfoDetailsData item, int position) {
            validateMountedItemClick(item, position);
            Prefs.putInt(PrefsConstants.SPI_POST_ID, item.postid);
            Prefs.putString(PrefsConstants.SPI_POST_NAME, item.postName);
            Prefs.putString(PrefsConstants.SPI_UNIQU_Id, item.uniqueId);
            Prefs.putInt(PrefsConstants.SPI_ID, item.keyid);

        }
    };

    // this is taking for image
    public void validateMountedItemClick(SpiBasicInfoResponse.SpiBasicInfoDetailsData item, int viewPosition) {
        addDisposable(dataBase.getMountedSpiPhotoDao().getOccupiedPositions(item.keyid, item.postid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    message.what = TAKE_MOUNTED_IMAGE;
                    message.arg2 = getNextPosition(data);
                    message.arg1 = viewPosition;
                    liveData.postValue(message);
                }, throwable -> {
                }));
    }

    public void submitmounted() {
        setIsLoading(true);
        if(mountedRecyclerAdapter.isEmpty()){
            showToast("Please take image!");
        }
        else {
            showToast("Please wait! image syncing started");
            addDisposable(dataBase.getMountedSpiPhotoDao().getMountedPhotoForSync(Prefs.getInt(PrefsConstants.SPI_ID, -1))
                    .subscribeOn(Schedulers.io())
                    .map(data -> {
                        uploadAllImages(data);
                        return dataBase.getMountedSpiPhotoDao().getPendingImageCount(Prefs.getInt(PrefsConstants.SPI_ID, -1));
                    }).flatMap(count -> {
                        if (count > 0) {
                            showToast("Error Uploading Images, Please try again");
                            throw new IllegalArgumentException("SPI Image uploading error...");
                        } else
                            return dataBase.getMountedSpiPhotoDao().getSyncedImageData(Prefs.getInt(PrefsConstants.SPI_ID, -1));
                    }).flatMap(data -> {
                        if (data.size() == 0)
                            throw new IllegalStateException("No Image");
                        DraftSpiData request = new DraftSpiData();
                        request.trainerId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
                        request.spiId = Prefs.getInt(PrefsConstants.SPI_ID, -1);
                        ArrayList<DraftSpiPicturesItems> picturesItems = new ArrayList<>();
                        Map<Integer, List<MountedSpiPhotoEntity>> photoEntityMap = SpiUtils.getGroupedMountDataForSyncing(data);
                        for (Map.Entry<Integer, List<MountedSpiPhotoEntity>> item : photoEntityMap.entrySet()) {
                            DraftSpiPicturesItems spiPicturesItems = new DraftSpiPicturesItems();
                            spiPicturesItems.postId = item.getKey();
                            ArrayList<String> child = new ArrayList<>();
                            for (MountedSpiPhotoEntity photoEntity : item.getValue())
                                child.add(photoEntity.imageUrl);
                            spiPicturesItems.imageUrl = child;
                            picturesItems.add(spiPicturesItems);
                        }
                        request.draftSpiPicturesItems = picturesItems;
                        return syncApi.uploadMountedImageData(request);
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        setIsLoading(false);
                        if (response != null && response.data) {
                            //TODO: Write what to do after uploading the images
                            showToast("Image Uploaded!!!");

                            dataBase.getMountedSpiPhotoDao().deleteMountedPhoto(Prefs.getInt(PrefsConstants.SPI_ID));

//                            dataBase.getMountedSpiPhotoDao().flushMountedPhotoTable()
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe();
                            mountedRecyclerAdapter.notifyDataSetChanged();
                            flushMountedPhotos();

                            MountedStatus();
                           // message.what = OPEN_AGAIN_SPI_MAIN;
                           // liveData.postValue(message);


                            //TODO:For Testing

                        } else {
                            showToast("Unexpected Error");
                        }
                    }, error -> {
                        setIsLoading(false);
                    }));

        }

    }
public  void flushMountedPhotos(){
    dataBase.getMountedDao().flushMountedTable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe();

}


    // TODO: Image Compression Have to Implement
    void uploadAllImages(List<MountedSpiPhotoEntity> draftSpiPhotoEntityList) {

        for (MountedSpiPhotoEntity entity : draftSpiPhotoEntityList) {
            setIsLoading(true);

            if(entity.isCompress == 0) {
                Log.d(TAG, "uploadDraftSpiImage: Inside Compress");
                File originalFile = new File(entity.imageUrl);
                File imageDir = new File(entity.imageUrl.substring(0, entity.imageUrl.lastIndexOf("/")));
                Log.d(TAG, "uploadDraftSpiImage: Inside Compress   If " + imageDir + " exist -> " + imageDir.exists() + " If file "+ originalFile +" Exist --> " + originalFile.exists());
                if(imageDir.exists() && originalFile.exists()) {
                    Log.d(TAG, "uploadDraftSpiImage: Inside Compress Calling Compresss");
                    Compressor compressor = new Compressor(getApplication());
                    compressor.setDestinationDirectoryPath(imageDir.getAbsolutePath());
                    try {
                        File compressedFile = compressor.compressToFile(originalFile, "min_" + originalFile.getName());
                        Log.d(TAG, "uploadDraftImage: Compressed File : " + compressedFile.getAbsolutePath());
                        dataBase.getMountedSpiPhotoDao().imageCompressed(entity.imageUrl, compressedFile.getAbsolutePath());
                        entity.imageUrl = compressedFile.getAbsolutePath();
                        if(originalFile.exists())
                            originalFile.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            //Create a file object using file path
            File file = new File(entity.imageUrl);
            // Create a request body with file and image media type
            if (file.exists()) {
                Log.v("MIME : ", " uploadTrainingImage" + file.getAbsolutePath());
                String str = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.toLowerCase());
                if (type == null)
                    type = "*/*";
                Log.v("MIME TYPE : ", " " + type);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse(type), file);
                // Create MultipartBody.Part using file request-body,file name and part name
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                try {
                    Response<ImageUploadResponse> response = syncApi.uploadImages(part, Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))).execute();
                    if (response.body() != null && response.body().statusCode == 200) {
                        if (file.exists())
                            file.delete();
                        entity.imageUrl = response.body().data;
                        entity.isSynced = 1;
                        dataBase.getMountedSpiPhotoDao().updateMountedSpiPhoto(entity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    void initRecylerView() {
        addDisposable(
                dataBase.getSpiBasicInfoDao().getSpiBasicInfo(Prefs.getInt(PrefsConstants.SPI_ID))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                            mountedRecyclerAdapter.clearAndSetItems(data);
                            mountedRecyclerAdapter.notifyDataSetChanged();
                        }, th -> {
                            showToast("Something went worng");
                        })
        );
    }

    void updateImage(List<SpiImage> imageList) {
        addDisposable(
                Observable.just(1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(data -> SpiUtils.getGroupedData(imageList))
                        .subscribe(data -> {
                            imagecontains=data.size();
                            mountedRecyclerAdapter.updateSpiImageList(data, Prefs.getInt(PrefsConstants.SPI_VIEW_POSITION, -1));
                            mountedRecyclerAdapter.notifyDataSetChanged();
                        }, th -> {
                        })
        );
    }



    // this is saving image into draftspi photo table
    void saveMountedImage(String imagePath, int branchid, int customerid, int spipostid) {

        MountedSpiPhotoEntity mountedSpiPhotoEntity = new MountedSpiPhotoEntity();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
        mountedSpiPhotoEntity.imageUrl = imagePath;
        mountedSpiPhotoEntity.uniqueId = Prefs.getString(PrefsConstants.SPI_UNIQU_Id);
        mountedSpiPhotoEntity.branchid = branchid;
        mountedSpiPhotoEntity.customerid = customerid;
        mountedSpiPhotoEntity.postId = spipostid;
        mountedSpiPhotoEntity.spiId = Prefs.getInt(PrefsConstants.SPI_ID);
        mountedSpiPhotoEntity.position = Prefs.getInt(PrefsConstants.SPI_POSITION, -1);
        mountedSpiPhotoEntity.viewposition = Prefs.getInt(PrefsConstants.SPI_VIEW_POSITION);
        mountedSpiPhotoEntity.unitId = Prefs.getInt(PrefsConstants.SPI_UNIT_ID);

        dataBase.getMountedSpiPhotoDao().insertMountedPhoto(mountedSpiPhotoEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    LiveData<List<SpiImage>> getImageList() {
        return dataBase.getMountedSpiPhotoDao().getMountedPhotoList(Prefs.getInt(PrefsConstants.SPI_ID));
    }

    int getNextPosition(List<Integer> imagePosition) {
        int counter = 1;
        // Collections.sort(imagePosition);
        if (imagePosition.isEmpty())
            return 1;
        else {
            for (Integer i : imagePosition) {
                if (counter != i)
                    break;
                counter = counter + 1;
            }
            return counter;
        }
    }
 public  void fushMountedPhoto() {
     dataBase.getMountedSpiPhotoDao().flushMountedPhotoTable()
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe();
 }


    public void SpiMountedDetails() {

//Prefs.getInt(PrefsConstants.SPI_ID)
        addDisposable(dashBoardApi.getMounted(new DesignSpiRequest(Prefs.getInt(PrefsConstants.SPI_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpiDesignSuccess, this::onApiError));

    }

    private void onSpiDesignSuccess(MountedResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

         /*   dataBase.getMountedDao().insertMountedData(response.mountedData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();*/

            if(response.mountedData != null)
                Observable.fromIterable(response.mountedData)
                        .map(data -> {
                            data.uniqueId = data.keyid + "_" + data.postId;
                            return data;
                        }).toList()
                        .map(data -> {
                            dataBase.getMountedDao().insertMountedData(response.mountedData);
                            return true;
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            if(response.mountedData.isEmpty()) {
                showToast("Not found any rota ");
            }
            else {}
        }
    }




    public  void MountedStatus() {
//Prefs.getInt(PrefsConstants.SPI_ID)
        addDisposable(dashBoardApi.getMountedStatus(new SpiPrintingRequest( Prefs.getInt(PrefsConstants.SPI_ID)
        )).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMountedtatussuccess, this::onApiError));
    }

    private void onMountedtatussuccess(MountedStatusResponse response) {
        setIsLoading(false);

        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getMountedStatusDao().insertMountedData(response.mountedStatuses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            if(!response.mountedStatuses.isEmpty()){
                mountedstatus.set(response.mountedStatuses.get(0).status);
                mountedStatusDone=response.mountedStatuses.get(0).status;
            }
            else {
                showToast("Not found any rota ");
            }
        }
    }


    void getDraftStatus(SpiStatusRequest request) {
        addDisposable(dashBoardApi.getSpiStatus(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpistatusSuccess, this::onApiError));
    }

    private void onSpistatusSuccess(BaseApiResponse response) {
        if(response.statusCode==SUCCESS_RESPONSE){

        }
    }
}
