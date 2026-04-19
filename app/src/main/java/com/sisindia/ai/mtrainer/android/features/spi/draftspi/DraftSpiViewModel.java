package com.sisindia.ai.mtrainer.android.features.spi.draftspi;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_APPROVAL_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TAKE_IMAGE;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
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
import com.sisindia.ai.mtrainer.android.constants.StartActivityItemConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.DraftSpiPhotoEntity;
import com.sisindia.ai.mtrainer.android.features.spi.basicinformation.model.CompletedDataItem;
import com.sisindia.ai.mtrainer.android.features.spi.model.ReuploadDraftSpiResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiImage;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostReFetchRequest;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.ImageUploadResponse;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;
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

public class DraftSpiViewModel extends MTrainerViewModel {
    public DraftSpiRecyclerAdapter draftSpiRecyclerAdapter;
    public ObservableField<String> status = new ObservableField<>();
    public ObservableInt pictureType = new ObservableInt(StartActivityItemConstants.DRAFT_SPI_PICTURE);

    public ObservableField<String> customer = new ObservableField<>();
    public ObservableField<String> customerid = new ObservableField<>();
    public ObservableField<String> type = new ObservableField<>();
    public ObservableField<String> imagecount = new ObservableField<>("0");
    private static final String TAG = "DraftSpiSync";
    MtrainerDataBase dataBase;
    public DraftSpiRecyclerAdapter.RemoveDraftImage removeDraftImage;
    List<ReuploadDraftSpiResponse.ReuploadDraftSpiData> reuploadsize = new ArrayList<>();
    @Inject
    Picasso picasso;
    @Inject
    DashBoardApi dashBoardApi;
    @Inject
    SyncApi syncApi;
    @Inject
    AuthApi authApi;
    private final Application application;

    @Inject
    public DraftSpiViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);

        draftSpiRecyclerAdapter = new DraftSpiRecyclerAdapter(dataBase, picasso, removeDraftImage, application);
        customer.set(Prefs.getString(PrefsConstants.SPI_CUSTOMER));
        customerid.set(Prefs.getString(PrefsConstants.SPI_UNIT_CODE));
        type.set(Prefs.getString(PrefsConstants.SPI_TYPE));
        status.set(Prefs.getString(PrefsConstants.SPI_STATUS));
        type.set(Prefs.getString(PrefsConstants.SPI_TYPE));
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


    //draftspi adapter listener
    public DraftSpiViewlistener viewListeners = (item, position) -> {
        validateSpiItemClick(item, position);
        Prefs.putInt(PrefsConstants.SPI_POST_ID, item.postid);
        Prefs.putString(PrefsConstants.SPI_POST_NAME, item.postName);
        Prefs.putString(PrefsConstants.SPI_UNIQU_Id, item.uniqueId);
        Prefs.putInt(PrefsConstants.SPI_ID, item.keyid);
    };

    // this is taking for image
    public void validateSpiItemClick(SpiBasicInfoResponse.SpiBasicInfoDetailsData item, int viewPosition) {
        if (item.statusId == 0 || item.statusId == 10) {
            addDisposable(dataBase.getDraftSpiPhotoDao().getOccupiedPositions(item.keyid, item.postid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(data -> {
                        message.what = TAKE_IMAGE;
                        message.arg2 = getNextPosition(data);
                        message.arg1 = viewPosition;
                        liveData.postValue(message);
                    }, throwable -> {
                    }));

        } else {
            showToast("Already draft spi approved!! No need to take image again");
        }
    }

    public void submitdraft() {
        setIsLoading(true);
        showToast("Please wait! image syncing started");
        addDisposable(dataBase.getDraftSpiPhotoDao().getDraftSpiPhotoForSync(Prefs.getInt(PrefsConstants.SPI_ID, -1))
                .subscribeOn(Schedulers.io())
                .map(data -> {
                    uploadAllImages(data);
                    return dataBase.getDraftSpiPhotoDao().getPendingImageCount(Prefs.getInt(PrefsConstants.SPI_ID, -1));
                }).flatMap(count -> {
                    if (count > 0) {
                        showToast("Error Uploading Images, Please try again");
                        throw new IllegalArgumentException("SPI Image uploading error...");
                    } else
                        return dataBase.getDraftSpiPhotoDao().getSyncedImageData(Prefs.getInt(PrefsConstants.SPI_ID, -1));
                }).flatMap(data -> {
                    if (data.size() == 0)
                        throw new IllegalStateException("No Image");
                    DraftSpiData request = new DraftSpiData();
                    request.trainerId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
                    request.spiId = Prefs.getInt(PrefsConstants.SPI_ID, -1);
                    ArrayList<DraftSpiPicturesItems> picturesItems = new ArrayList<>();
                    Map<Integer, List<DraftSpiPhotoEntity>> photoEntityMap = SpiUtils.getGroupedDataForSyncing(data);
                    for (Map.Entry<Integer, List<DraftSpiPhotoEntity>> item : photoEntityMap.entrySet()) {
                        DraftSpiPicturesItems spiPicturesItems = new DraftSpiPicturesItems();
                        spiPicturesItems.postId = item.getKey();
                        ArrayList<String> child = new ArrayList<>();
                        for (DraftSpiPhotoEntity photoEntity : item.getValue())
                            child.add(photoEntity.imageUrl);
                        spiPicturesItems.imageUrl = child;
                        picturesItems.add(spiPicturesItems);
                    }
                    request.draftSpiPicturesItems = picturesItems;
                    return syncApi.uploadDraftImageData(request);
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    setIsLoading(false);
                    if (response != null && response.data) {
                        //TODO: Write what to do after uploading the images
                        showToast("Image Uploaded!!!");
                        //flushDraftSpiPhotos();
                        flushDraftSpiPhotos();
                        //TODO:For Testing

                    } else {
                        showToast("Unexpected Error");
                    }
                }, error -> {
                    setIsLoading(false);
                }));
    }

    public void flushDraftSpiPhotos() {
        addDisposable(dataBase.getDraftSpiPhotoDao().deleteDraftPhoto(Prefs.getInt(PrefsConstants.SPI_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    message.what = OPEN_DRAFT_APPROVAL_SCREEN;
                    liveData.postValue(message);
                }, th -> {
                    logger.log("flushDraftSpiPhotos Error", th);
                    showToast("Unexpected Error");
                }));
        draftSpiRecyclerAdapter.notifyDataSetChanged();

    }

    public void flushSpiPhotos() {
        addDisposable(dataBase.getDraftSpiPhotoDao().deleteDraftPhoto(Prefs.getInt(PrefsConstants.SPI_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {

                }, th -> {
                    logger.log("flushDraftSpiPhotos Error", th);
                    showToast("Unexpected Error");
                }));
    }

    // TODO: Image Compression Have to Implement
    void uploadAllImages(List<DraftSpiPhotoEntity> draftSpiPhotoEntityList) {

        for (DraftSpiPhotoEntity entity : draftSpiPhotoEntityList) {
            setIsLoading(true);

            if (entity.isCompress == 0) {
                File originalFile = new File(entity.imageUrl);
                File imageDir = new File(entity.imageUrl.substring(0, entity.imageUrl.lastIndexOf("/")));
//                Log.d(TAG, "uploadDraftSpiImage: Inside Compress   If " + imageDir + " exist -> " + imageDir.exists() + " If file " + originalFile + " Exist --> " + originalFile.exists());
                if (imageDir.exists() && originalFile.exists()) {

                    Compressor compressor = new Compressor(application);
                    compressor.setDestinationDirectoryPath(imageDir.getAbsolutePath());
                    try {
                        File compressedFile = compressor.compressToFile(originalFile, "min_" + originalFile.getName());
                        Log.d(TAG, "uploadDraftImage: Compressed File : " + compressedFile.getAbsolutePath());
                        dataBase.getDraftSpiPhotoDao().imageCompressed(entity.imageUrl, compressedFile.getAbsolutePath());
                        entity.imageUrl = compressedFile.getAbsolutePath();
                        if (originalFile.exists())
                            originalFile.delete();
                    } catch (IOException ignored) {
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
                        dataBase.getDraftSpiPhotoDao().updateDraftSpiPhoto(entity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void initRecylerView() {
        setIsLoading(false);
        addDisposable(
                dataBase.getSpiBasicInfoDao().getSpiBasicInfo(Prefs.getInt(PrefsConstants.SPI_ID))
                        .subscribeOn(Schedulers.io())
                        .flatMap(data -> {
                            if (data.isEmpty())
                                return dashBoardApi.reFetchPostInfo(new SpiPostReFetchRequest(Prefs.getInt(PrefsConstants.SPI_ID)));
                            else
                                return dataBase.getSpiBasicInfoDao().getPendingSpiBasicInfo(Prefs.getInt(PrefsConstants.SPI_ID));
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                            if (data instanceof SpiBasicInfoResponse)
                                onSpiBasicInfoSuccess((SpiBasicInfoResponse) data);
                            else {
                                Log.v("initRecylerView", "" + data.toString());
                                draftSpiRecyclerAdapter.clearAndSetItems((List<SpiBasicInfoResponse.SpiBasicInfoDetailsData>) data);
                                draftSpiRecyclerAdapter.notifyDataSetChanged();
                            }
                        }, th -> {
                            showToast("Something went worng");
                        })
        );
    }

    private void onSpiBasicInfoSuccess(SpiBasicInfoResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {
            if (response.spiBasicInfoDetailsData != null && !response.spiBasicInfoDetailsData.isEmpty()) {
                addDisposable(Observable.fromIterable(response.spiBasicInfoDetailsData)
                        .map(data -> {
                            data.uniqueId = data.keyid + "_" + data.postid;
                            return data;
                        }).toList()
                        .subscribeOn(Schedulers.io())
                        .flatMap(data -> {
                            dataBase.getSpiBasicInfoDao().insertSpiBasicinfo(response.spiBasicInfoDetailsData);
                            //return dataBase.getSpiBasicInfoDao().getSpiBasicInfo(Prefs.getInt(PrefsConstants.SPI_ID));
                            return dashBoardApi.completedPostInfo(new SpiPostReFetchRequest(Prefs.getInt(PrefsConstants.SPI_ID)));
                        })
                        .flatMap(data -> {
                            List<CompletedDataItem> items = data.getData();
                            for (CompletedDataItem item : items) {
                                dataBase.getSpiBasicInfoDao().markPostCompleted(item.getPostId());
                            }
                            return dataBase.getSpiBasicInfoDao().getPendingSpiBasicInfo(Prefs.getInt(PrefsConstants.SPI_ID));
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((data) -> {
                                    Log.d(TAG, "onSpiBasicInfoSuccess: " + data.toArray());
                                    draftSpiRecyclerAdapter.clearAndSetItems((List<SpiBasicInfoResponse.SpiBasicInfoDetailsData>) data);
                                    draftSpiRecyclerAdapter.notifyDataSetChanged();
                                    setIsLoading(false);
                                }
                                , Throwable::printStackTrace));
            } else if (response.spiBasicInfoDetailsData != null) {
                showToast("Sorry! No post available for this site.");
            } else {
                showToast("Unexpected error occurred");
            }
        }
    }

    void updateImage(List<SpiImage> imageList) {
        addDisposable(
                Observable.just(1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(data -> SpiUtils.getGroupedData(imageList))
                        .subscribe(data -> {
                            draftSpiRecyclerAdapter.updateSpiImageList(data, Prefs.getInt(PrefsConstants.SPI_VIEW_POSITION, -1));
                            draftSpiRecyclerAdapter.notifyDataSetChanged();
                        }, th -> {
                        })
        );
    }

    /*LiveData<List<SpiBasicInfoResponse.SpiBasicInfoDetailsData>> getSpiBasicInfo() {
        return dataBase.getSpiBasicInfoDao().getSpiBasicInfo();
    }
*/
// this is saving image into draftspi photo table
    void saveDraftImage(String imagePath, int branchid, int customerid, int spipostid) {

        DraftSpiPhotoEntity draftSpiPhotoEntity = new DraftSpiPhotoEntity();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
        draftSpiPhotoEntity.imageUrl = imagePath;
        draftSpiPhotoEntity.uniqueId = Prefs.getString(PrefsConstants.SPI_UNIQU_Id);
        draftSpiPhotoEntity.branchid = branchid;
        draftSpiPhotoEntity.customerid = customerid;
        draftSpiPhotoEntity.postId = spipostid;
        draftSpiPhotoEntity.spiId = Prefs.getInt(PrefsConstants.SPI_ID);
        draftSpiPhotoEntity.position = Prefs.getInt(PrefsConstants.SPI_POSITION, -1);
        draftSpiPhotoEntity.viewposition = Prefs.getInt(PrefsConstants.SPI_VIEW_POSITION);
        draftSpiPhotoEntity.unitId = Prefs.getInt(PrefsConstants.SPI_UNIT_ID);

        String photoId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID) + "_" + Prefs.getInt(PrefsConstants.SPI_POST_ID) + "_" + sdf.format(new Date());
        // draftSpiPhotoEntity.setTrainingPhotoId(photoId);
        //draftSpiPhotoEntity.pictureTypeId = String.valueOf(pictureTypeId);
        // draftSpiPhotoEntity.setStatus(CANT_SYNCED);
        //draftSpiPhotoEntity.waterMark = waterMark;

        dataBase.getDraftSpiPhotoDao().insertDraftPhoto(draftSpiPhotoEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /*LiveData<List<DraftSpiPhotoEntity>> getSavedDraftImageList() {
        return dataBase.getDraftSpiPhotoDao().getDraftSpiPhotoList(Prefs.getInt(PrefsConstants.SPI_ID));
    }*/

    LiveData<List<SpiImage>> getImageList() {
        return dataBase.getDraftSpiPhotoDao().getSpiPhotoList(Prefs.getInt(PrefsConstants.SPI_ID));
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

    void removeDraftImage(int position, String uniqueid, String imageurl) {
      /*  addDisposable(dataBase.getDraftSpiPhotoDao().deleteDraftImage(position, uniqueid,imageurl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> setIsLoading(false), throwable -> showToast("Error removing draft image")));*/
    }


    void getDraftStatus(SpiStatusRequest request) {
        addDisposable(dashBoardApi.getSpiStatus(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpistatusSuccess, this::onApiError));
    }

    private void onSpistatusSuccess(BaseApiResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {

        }
    }
}
