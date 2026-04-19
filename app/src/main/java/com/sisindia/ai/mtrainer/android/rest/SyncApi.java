package com.sisindia.ai.mtrainer.android.rest;

import com.sisindia.ai.mtrainer.android.features.spi.model.DraftImageUploadResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaDataResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaImageData;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaImageResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaRequest;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.ImageDataUploadResponse;
import com.sisindia.ai.mtrainer.android.models.ImageUploadRequest;
import com.sisindia.ai.mtrainer.android.models.ImageUploadResponse;
import com.sisindia.ai.mtrainer.android.models.RplFormImageRequest;
import com.sisindia.ai.mtrainer.android.models.RplFormUploadRequest;
import com.sisindia.ai.mtrainer.android.models.SyncApiResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingFinalSubmitRequest;
import com.sisindia.ai.mtrainer.android.models.sync.DraftSpiData;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface SyncApi {

   // String SYNC_DATA_TO_SERVER = "api/SyncData/AddTrainingResult";
    // below syncing url used for sis only
    String SYNC_DATA_TO_SERVER = "api/SyncData/AddSISTrainingResult";
    String POST_IMAGE = "api/UploadImages/UploadFile";
    String POST_IMAGE1 = "api/UploadImages/UploadFile";

    // below api for  testing use
   // String POST_IMAGE = "api/TrainingOperation/PostRotaImage";
    String UPLOAD_IMAGE = "api/TrainingOperation/UpdateRotaImages";

    String UPLOAD_DRAFT_SPI_IMAGE="api/SPITracker/SPIDraft";
    String UPLOAD_MOUNTED_SPI_IMAGE="api/SPITracker/SPIMounted";

    String RPLFORM_UPLOAD = "api/EmployeeData/addRPLForm";
    String RPLFORM_IMAGE_UPLOAD = "api/EmployeeData/AddRPLFormPicture";
    String UPLOAD_UMBRELLA_DATA = "api/UmbrellaTracker/SubmitData";
    String UPLOAD_SPI_UMBRELLA_DATA = "api/UmbrellaTracker/SPIPosterData";
    String UPLOAD_UMBRELLA_IMAGE_DATA = "api/UmbrellaTracker/UpdateUmbrellaImage";

    /*Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(BODY))
            .connectTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.MTRAINER_HOST)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();*/

    @POST(SYNC_DATA_TO_SERVER)
    Call<SyncApiResponse> SubmitTrainingReport(@Body TrainingFinalSubmitRequest request);

    @POST(UPLOAD_UMBRELLA_DATA)
    Call<UmbrellaDataResponse> uploadUmbrelladata(@Body UmbrellaRequest request);

 @POST(UPLOAD_SPI_UMBRELLA_DATA)
 Call<UmbrellaDataResponse> uploadspiUmbrelladata(@Body UmbrellaRequest request);

   @Multipart
   @POST(POST_IMAGE)
   Call<ImageUploadResponse> uploadImages(@Part MultipartBody.Part file, @Query("CompanyId") int companyId);

 @Multipart
 @POST(POST_IMAGE1)
 Single<ImageUploadResponse> uploadImages1(@Part MultipartBody.Part file, @Query("CompanyId") int companyId);

    @POST(UPLOAD_IMAGE)
    Call<ImageDataUploadResponse> uploadImageData(@Body ImageUploadRequest request);

    @POST(UPLOAD_UMBRELLA_IMAGE_DATA)
    Call<UmbrellaImageResponse> uploadUmbrellaImageData(@Body UmbrellaImageData request);

    @POST(RPLFORM_UPLOAD)
    Call<BaseApiResponse> rplform(@Body RplFormUploadRequest request);


    @POST(RPLFORM_IMAGE_UPLOAD)
    Call<BaseApiResponse> uploadImageRpl(@Body RplFormImageRequest rplFormImageRequest);


    @POST(UPLOAD_DRAFT_SPI_IMAGE)
    Single<DraftImageUploadResponse> uploadDraftImageData(@Body DraftSpiData request);


    @POST(UPLOAD_MOUNTED_SPI_IMAGE)
    Single<DraftImageUploadResponse> uploadMountedImageData(@Body DraftSpiData request);
}
