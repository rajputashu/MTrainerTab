package com.sisindia.ai.mtrainer.android.features.umbrellareport;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.addrota.AddTaskViewListeners;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaMaster;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaPost;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.PostRequest;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.UmbrellaDetailRequest;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.UmbrellaImageRvItem;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.DetailsItem;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaDataResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaRequest;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.BranchRegionRequest;
import com.sisindia.ai.mtrainer.android.models.BranchResponse;
import com.sisindia.ai.mtrainer.android.models.ImageUploadResponse;
import com.sisindia.ai.mtrainer.android.models.SiteData;
import com.sisindia.ai.mtrainer.android.models.SiteResponse;
import com.sisindia.ai.mtrainer.android.models.UnitListResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.utils.WaterMarkUtil;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class UmbrellaReportViewModel extends MTrainerViewModel {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public ImageCaptureListener imageCaptureListener = new ImageCaptureListener() {
        @Override
        public void onTakeImage(int postId, String postName, int absoluteAdapterPosition) {
            message.what = NavigationConstants.TAKE_UMBRELLA_PICTURE;
            message.arg1 = postId;
            message.obj = postName;
            message.arg2 = absoluteAdapterPosition;
            liveData.postValue(message);
        }
    };
    public UmbrellaReportAdapter umbrellaReportAdapter = new UmbrellaReportAdapter();
    public ObservableInt isLoadingint = new ObservableInt(GONE);
    public final String role = Prefs.getString(PrefsConstants.ROLE);
    public final String companyId = Prefs.getString(PrefsConstants.COMPANY_ID);
    private final MtrainerDataBase dataBase;
    public ObservableList<UnitListResponse.Unit> unitList = new ObservableArrayList<>();
    public ObservableList<BranchData> branchList = new ObservableArrayList<>();
    public ObservableList<SiteData> siteList = new ObservableArrayList<>();
    public int branchId, siteId;
    private MutableLiveData<Integer> siteRequestLiveData = new MutableLiveData<>();
    private LiveData<List<SiteData>> siteListLiveData;
    private MutableLiveData<String> umbrellaIdLiveData = new MutableLiveData<>();
    public LiveData<List<UmbrellaImageRvItem>> umbrellaImageLiveData;
    private int umbrellaCount;
    private String postName;
    private String branchName = "";
    private String siteName;
    Application application1;
    public ObservableBoolean addPostLoading = new ObservableBoolean(false);
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public ObservableField<String> mntCount = new ObservableField<>("");
    public ObservableField<String> staticBranchName = new ObservableField<>("");
    public ObservableField<String> staticSiteName = new ObservableField<>("");
    @Inject
    DashBoardApi dashBoardApi;



    @Inject
    public UmbrellaReportViewModel(@NonNull Application application) {
        super(application);
        application1 = application;
        dataBase = MtrainerDataBase.getDatabase(application);
        siteListLiveData = Transformations.switchMap(siteRequestLiveData, branchId -> dataBase.getSite1Dao().getSiteList(branchId));
        umbrellaImageLiveData = Transformations.switchMap(umbrellaIdLiveData, umbrellaId -> dataBase.getUmbrellaPostDao().getUmbrellaImageList(umbrellaId));
    }

    void fetchPostList(int siteId, boolean isManual, String umbrellaId) {
        /**
         * isManual means User can not change the site and branch because he might have left it in
         * the middle
         */
        if (isManual) {
            setIsLoading(true);
            PostRequest request = new PostRequest();
            request.siteId = siteId;
            umbrellaIdLiveData.setValue(umbrellaId);
            addDisposable(dataBase.getUmbrellaPostDao().getSiteCount(umbrellaId, siteId)
                    .subscribeOn(Schedulers.io())
                    .flatMap(count -> {
                        if (count > 0)
                            throw new HasDataException();
                        else
                            return dashBoardApi.getUmbrellaPostList(request);
                    })
                    .map(data -> {
                        if (data.statusCode == 200) {
                            Log.d("TAG", "fetchPostList: " + data.data.size());
                            dataBase.getUmbrellaPostDao().insertUmbrellaPostList(data.data);
                            return true;
                        } else
                            return false;
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(isSuccess -> {
                        if (!isSuccess)
                            showToast("Errored Occoured");
                        setIsLoading(false);
                    }, th -> {
                        if(!(th instanceof HasDataException))
                            showToast("Errored Occoured");
                        logger.log("getUmbrellaPostList API Error", th);
                        setIsLoading(false);
                    }));

        } else {
            setIsLoading(true);
            setUmbrellaState(branchId, siteId);
            PostRequest request = new PostRequest();
            request.siteId = siteId;
            addDisposable(dashBoardApi.getUmbrellaPostList(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(data -> {
                        if (data.statusCode == 200) {
                            Log.d("TAG", "fetchPostList: " + data.data.size());
                            dataBase.getUmbrellaPostDao().insertUmbrellaPostList(data.data);
                            return true;
                        } else {
                            return false;
                        }
                    })
                    .subscribe(isSuccess -> {
                        if (!isSuccess)
                            showToast("Errored Occoured");
                        setIsLoading(false);
                    }, th -> {
                        showToast("Errored Occoured");
                        logger.log("getUmbrellaPostList API Error", th);
                        setIsLoading(false);
                    }));
        }
    }

    public AddTaskViewListeners viewListeners = new AddTaskViewListeners() {

        @Override
        public void onDateChanged(int viewId, LocalDate date) {
        }

        @Override
        public void onStartTimeSelected(LocalTime time) {
        }

        @Override
        public void onEndTimeSelected(LocalTime time) {
        }

        @Override
        public void onItemSpinnerSelected(int viewId, int position) {
            switch (viewId) {
                case R.id.sp_branch_name:
                    branchId = branchList.get(position).branchId;
                    branchName = branchList.get(position).branchName;
                    siteRequestLiveData.setValue(branchId);
                    siteId = 0;
                    fetchSiteList();
                    break;
                case R.id.sp_unit_name3:
                    if (siteList.size() != 0) {
                        siteId = siteList.get(position).siteId;
                        siteName = siteList.get(position).siteName;
                        fetchPostList(siteId, false, "");
                    }
                    break;
            }
        }
    };

    private void fetchSiteList() {
        setIsLoading(true);
        addDisposable(
                dataBase.getSite1Dao().haveSiteData(branchId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(count -> {
                            if (count <= 0) {
                                addDisposable(dashBoardApi.getSite(new BranchRegionRequest("GetSite",
                                        Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)), Prefs.getInt(PrefsConstants.REGION_ID), branchId, Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(this::onSiteListSuccess, this::onApiError));
                            } else
                                setIsLoading(false);
                        }, throwable -> {
                            showToast("Something went wrong");
                            setIsLoading(false);
                        })
        );
    }

    private void onumbrellaSuccess(UmbrellaDataResponse response) {
        //setIsLoading(false);
        isLoadingint.set(GONE);
        if (response.statusCode == SUCCESS_RESPONSE) {
            message.what = NavigationConstants.CLOSE_UMBRELLA_REPORT;
            liveData.postValue(message);
        }
    }


    private void onSiteListSuccess(SiteResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getSite1Dao().insertSiteList(response.siteDataList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    void setSiteList(List<SiteData> siteList) {
        this.siteList.clear();
        this.siteList.addAll(siteList);
    }

    LiveData<List<BranchData>> getBranchList() {
        return dataBase.getBranchDao().getBranchList(Prefs.getInt(PrefsConstants.REGION_ID));
    }

    void setBranchList(List<BranchData> regionList) {
        this.branchList.clear();
        this.branchList.addAll(regionList);
    }

    void fetchBranchList() {
        if (Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_STATE, UmbrellaConstants.NEW) == UmbrellaConstants.STATRED) {
            // Retrive Data
            branchId = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_BRANCH_ID, 0);
            String branchName = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_BRANCH_NAME, "");
            String siteName = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_SITE_NAME, "");
            siteId = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_SITE_ID, 0);
            String umbrellaId = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID, "");
            staticBranchName.set(branchName);
            staticSiteName.set(siteName);
            // Get Post
            fetchPostList(siteId, true, umbrellaId);
        } else {
            setIsLoading(true);
            addDisposable(
                    dataBase.getBranchDao().haveBranchData(Prefs.getInt(PrefsConstants.REGION_ID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(count -> {
                                if (count <= 0) {
                                    addDisposable(dashBoardApi.getBranch(new BranchRegionRequest("GetBranch",
                                            Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)), Prefs.getInt(PrefsConstants.REGION_ID), 0, Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(this::onBranchListSuccess, this::onApiError));
                                } else
                                    setIsLoading(false);
                            }, throwable -> {
                                showToast("Something went wrong");
                                setIsLoading(false);
                            })
            );
        }
    }

    private void onBranchListSuccess(BranchResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getBranchDao().insertBranchList(response.regionDataList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    LiveData<List<SiteData>> getSiteList() {
        return siteListLiveData;
    }

    LiveData<List<UmbrellaImageRvItem>> getUmbrellaImages() {
        return umbrellaImageLiveData;
    }

    void setUmbrellaState(int branchId, int siteId) {
        if (Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_STATE, UmbrellaConstants.NEW) == UmbrellaConstants.NEW) {
            String umbrellaId = dateFormat.format(new Date()) + "_" + Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
            Prefs.putStringOnMainThread(PrefsConstants.CURRENT_UMBRELLA_ID, umbrellaId);
            Prefs.putIntOnMainThread(PrefsConstants.CURRENT_UMBRELLA_STATE, UmbrellaConstants.STATRED);
            Prefs.putIntOnMainThread(PrefsConstants.CURRENT_UMBRELLA_BRANCH_ID, branchId);
            Prefs.putStringOnMainThread(PrefsConstants.CURRENT_UMBRELLA_BRANCH_NAME, branchName);
            Prefs.putIntOnMainThread(PrefsConstants.CURRENT_UMBRELLA_SITE_ID, siteId);
            Prefs.putStringOnMainThread(PrefsConstants.CURRENT_UMBRELLA_SITE_NAME, siteName);
            umbrellaIdLiveData.setValue(umbrellaId);
        } else if (Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_STATE, UmbrellaConstants.NEW) == UmbrellaConstants.STATRED) {
            addDisposable(dataBase.getUmbrellaPostDao().clearCurrentUmbrellaData(Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID, ""))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Prefs.putIntOnMainThread(PrefsConstants.CURRENT_UMBRELLA_BRANCH_ID, branchId);
                        Prefs.putIntOnMainThread(PrefsConstants.CURRENT_UMBRELLA_SITE_ID, siteId);
                        Prefs.putStringOnMainThread(PrefsConstants.CURRENT_UMBRELLA_BRANCH_NAME, branchName);
                        Prefs.putStringOnMainThread(PrefsConstants.CURRENT_UMBRELLA_SITE_NAME, siteName);
                        umbrellaIdLiveData.setValue(Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID, ""));
                    }, th -> {
                        showToast("Something went wrong");
                        setIsLoading(false);
                    }));
        }
    }

    void clearUmbrellaState() {
        Prefs.putIntOnMainThread(PrefsConstants.CURRENT_UMBRELLA_STATE, UmbrellaConstants.NEW);
    }

    void setUmbrellaReportAdapter(List<UmbrellaImageRvItem> itemList) {
        umbrellaReportAdapter.clearAndSetItems(itemList);
    }

    void saveImage(int postId, String umbrellaId, String imagePath, String imageId) {
        String watermark = sdf.format(new Date()) + " " + Prefs.getString(PrefsConstants.EMPPLOYEE_NAME) + " " + Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_SITE_NAME, "") + " " + "Lat : " + Prefs.getDouble(PrefsConstants.LAT) + " " + "Long : " + Prefs.getDouble(PrefsConstants.LONGI);
        setIsLoading(true);
        addDisposable(
                dataBase.getUmbrellaPostDao().insertUmbrellaImage(postId, umbrellaId, imagePath, imageId, watermark)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                                    showToast("Image Saved Sucessfully");
                                    setIsLoading(false);
                                },
                                th -> {
                                    showToast("Something went wrong");
                                    setIsLoading(false);
                                })
        );
    }

    void saveImage(String adhocPostId, String umbrellaId, String imagePath, String imageId, String postName) {
        setIsLoading(true);
        UmbrellaPost umbrellaPost = new UmbrellaPost();
        String watermark = sdf.format(new Date()) + " " + Prefs.getString(PrefsConstants.EMPPLOYEE_NAME) + " " + Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_SITE_NAME, "") + " " + "Lat : " + Prefs.getDouble(PrefsConstants.LAT) + " " + "Long : " + Prefs.getDouble(PrefsConstants.LONGI);
        umbrellaPost.adHocPostId = adhocPostId;
        umbrellaPost.umbrellaId = umbrellaId;
        umbrellaPost.imagePath = imagePath;
        umbrellaPost.ImageId = imageId;
        umbrellaPost.postName = postName;
        umbrellaPost.isAdhoc = 1;
        umbrellaPost.waterMark = watermark;
        addDisposable(
                dataBase.getUmbrellaPostDao().insertUmbrellaPost(umbrellaPost)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                                    showToast("Image Saved Sucessfully");
                                    setIsLoading(false);
                                },
                                th -> {
                                    showToast("Something went wrong");
                                    setIsLoading(false);
                                })
        );
    }

    public void setUmbrellaCount(int umbrellaCount) {
        this.umbrellaCount = umbrellaCount;
    }

    public void savespiUmbrella(View view) {
        /*if (umbrellaCount == 0) {
            showToast("Please Enter Correct Count");
        } else*/ if (siteId == 0 || branchId == 0) {
            showToast("Please Enter Data");
        } else {
            //setIsLoading(true);
            isLoadingint.set(VISIBLE);
            addDisposable(
                    dataBase.getUmbrellaPostDao().getImageCount(Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID))
                            .subscribeOn(Schedulers.io())
                            .map(count -> {
                                Log.d("TAG", "saveUmbrella: " + count);
                                return count > 0;
                            }).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(hasImage -> {
                                        if (hasImage)
                                            spisave();
                                        else
                                            showToast("Please Take at least one Picture");
                                    }, th -> {
                                        showToast("Unexpected Error Occoured");
                                    }
                            )
            );
        }
    }


    public void saveUmbrella(View view) {
        if (umbrellaCount == 0) {
            showToast("Please Enter Correct Count");
        } else if (siteId == 0 || branchId == 0) {
            showToast("Please Enter Data");
        } else {
            addDisposable(
                    dataBase.getUmbrellaPostDao().getImageCount(Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID))
                            .subscribeOn(Schedulers.io())
                            .map(count -> {
                                Log.d("TAG", "saveUmbrella: " + count);
                                return count > 0;
                            }).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(hasImage -> {
                                        if (hasImage)
                                            save();
                                        else
                                            showToast("Please Take at least one Picture");
                                    }, th -> {
                                        showToast("Unexpected Error Occoured");
                                    }
                            )
            );
        }
    }

    public void savePost(View view) {
        if (postName == null || postName.isEmpty()) {
            showToast("Please Enter Post Name");
        } else {
            String adHocPostId = dateFormat.format(new Date()) + "_" + Prefs.getInt(PrefsConstants.BASE_TRAINER_ID) + "_" + siteId + "_" + branchId;
            Prefs.putStringOnMainThread(PrefsConstants.CURRENT_UMBRELLA_POST_NAME, postName);
            Prefs.putIntOnMainThread(PrefsConstants.CURRENT_UMBRELLA_POST_ID, -1);
            Prefs.putStringOnMainThread(PrefsConstants.CURRENT_UMBRELLA_ADHOC_POST_ID, adHocPostId);
            message.what = NavigationConstants.TAKE_UMBRELLA_PICTURE;
            message.arg1 = -1;
            message.obj = postName;
            liveData.postValue(message);
        }
    }

    void spisave() {

        UmbrellaMaster master = new UmbrellaMaster();
        master.branchId = branchId;
        master.siteId = siteId;
        master.umbrellaCount = umbrellaCount;
        master.umbrellaId = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID);
        addDisposable(dataBase.getUmbrellaMasterDao().insertUmbrellaMasterData(master)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    //showToast("Successfully saved data");
                   /* message.what = NavigationConstants.CLOSE_UMBRELLA_REPORT;
                    liveData.postValue(message);*/
                    //setIsLoading(false);

                    int company = Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID, "-1"));
                    if(company != -1) {
                        Log.d("ssasafsdf","doWork: Start");
                        //logger.log("doWork: Start");

                        try {
                            uploadUmbrellaImages();
                        } catch (Exception e) {
                            setIsLoading(false);
                            Log.d("ssasafsdf",e.getMessage());
                            logger.log("uploadUmbrellaImages Calling", e);
                        }

                    }

                    //uploadUmbrellaImages();
                }));
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
                            Compressor compressor = new Compressor(application1.getApplicationContext());
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

                    }
                }
                if (image.isCompress == 1) {
                    Log.d("ssasafsdf","Inside Compress Calling 1");
                    //Create a file object using file path
                    if (image.hasUrl == 0) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                        StrictMode.setThreadPolicy(policy);
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
                            // Response<ImageUploadResponse> response = syncApi.uploadImages(part).execute();
                            if (response.body() != null && response.body().statusCode == 200) {
                                // TODO: Need to uncomment
//                            if (file.exists())
//                                file.delete();
                                image.imagePath = response.body().data;
                                dataBase.getUmbrellaPostDao().updateUmbrellaImageUrl(image.id, image.imagePath);
                                dataBase.getUmbrellaPostDao().updateUmbrellaSpiImageState(image.umbrellaId);
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
                            }
                        } catch (IOException e) {
                            setIsLoading(false);
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
                setIsLoading(false);
                Log.d("ssasafsdf",""+e.getMessage());
                logger.log("Exception in uploadUmbrellaImages", e);
            }
        }


        List<UmbrellaMaster> umbrellaMasters = dataBase.getUmbrellaMasterDao().getUmbrellaMasterData();
        try {
            uploadUmbrellaData(umbrellaMasters);
        } catch (Exception e) {
            setIsLoading(false);
            logger.log("uploadUmbrellaData Calling", e);
        }

    }

    private void uploadUmbrellaData(List<UmbrellaMaster> umbrellaMasters) {
        for (UmbrellaMaster master : umbrellaMasters) {
            try {
                UmbrellaRequest request = new UmbrellaRequest();
                request.setCompanyId(Integer.parseInt(companyId));
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
            logger.log("Watermarked Bitmap null -> ", e);
            return false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            watermarkedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        } catch (IOException e) {
            copyAndDeleteFile(duplicateFileName, path);
            logger.log("mark", e);
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.log("Mark Closeing Stream Error", e);
                }
            }
        }
        return true;
    }

    private boolean copyAndDeleteFile(String src, String dest) {
        File file = new File(src);
        Log.d("Uploaddata", "copyAndDeleteFile: new File -> " + dest);
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



    void save() {
        UmbrellaMaster master = new UmbrellaMaster();
        master.branchId = branchId;
        master.siteId = siteId;
        master.umbrellaCount = umbrellaCount;
        master.umbrellaId = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID);
        addDisposable(dataBase.getUmbrellaMasterDao().insertUmbrellaMasterData(master)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    showToast("Successfully saved data");
                    message.what = NavigationConstants.CLOSE_UMBRELLA_REPORT;
                    liveData.postValue(message);
                }));
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public void openAddPostDialog(View view) {
        message.what = NavigationConstants.OPEN_UMBRELLA_POST_DIALOG;
        liveData.postValue(message);
    }

    void checkHasUnsavedData() {
        addDisposable(
                dataBase.getUmbrellaPostDao().getImageCount(Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID))
                        .subscribeOn(Schedulers.io())
                        .map(count -> {
                            Log.d("TAG", "saveUmbrella: " + count);
                            return count > 0;
                        }).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(hasImage -> {
                                    if (hasImage) {
                                        message.what = NavigationConstants.OPEN_UMBRELLA_ALERT_DIALOG;
                                        liveData.postValue(message);
                                    } else
                                        clearUmbrellaDataAndClose();
                                }, th -> {
                                    showToast("Unexpected Error Occoured");
                                }
                        )
        );
    }

    void clearUmbrellaDataAndClose() {
        addDisposable(dataBase.getUmbrellaPostDao().clearCurrentUmbrellaData(Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    message.what = NavigationConstants.CLOSE_UMBRELLA_REPORT;
                    liveData.postValue(message);
                }, th -> {
                    showToast("Something went wrong");
                    setIsLoading(false);
                }));
    }

    void getUmbrellaDetails() {
        UmbrellaDetailRequest request = new UmbrellaDetailRequest();
        request.trainerId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID, 0);
        addDisposable(
                dashBoardApi.getUmbrellaDetails(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                                    if (data.getStatusCode() == 200)
                                        mntCount.set(data.getData().get(0).getData());
                                },
                                th -> {
                                    showToast("Something went wrong");
                                }
                        )
        );
    }

    static class HasDataException extends Exception {
    }
}
