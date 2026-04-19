package com.sisindia.ai.mtrainer.android.features.trainingimages;

import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_CODE_RESOLUTION_REQUIRED;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_LOCATION_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.preference.Prefs;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.commons.FolderNames;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityImageCaptureBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class TrainingImageActivity extends MTrainerBaseActivity implements TrainingImageAdapter.ShowImage, TrainingImageAdapter.RemoveImage {

    private ActivityImageCaptureBinding binding;
    private TrainingImageViewModel viewModel;
    //    private Uri currentImageUri;
    private CaptureImageType imageType = CaptureImageType.UNKOWN;
    final int CAMERA_CAPTURE = 1;
    //    public static final String GridViewDemo_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ChoosePhoto/";
    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    String _path;
    RecyclerView recyclerView;
    private PostItem selectedPost;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    //    private float scaleFactor = 1.0f;
    private String previewImageUrl;
    private boolean isClicked = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private int imageListSize;
    @Inject
    Picasso picasso;
    private TrainingImageAdapter adapter;

    public static Intent newIntentForPhotoEvaluation(Activity activity) {
        return new Intent(activity, TrainingImageActivity.class);
    }

    @Override
    protected void extractBundle() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(CaptureImageType.class.getSimpleName())) {
                imageType = (CaptureImageType) getIntent().getExtras().getSerializable(CaptureImageType.class.getSimpleName());
            }
        }
    }

    @Override
    protected void initViewState() {
        liveData.observe(this, msg -> {
            if (msg.what == NavigationConstants.ON_IMAGE_CAPTURED) {
                //  openImageResultScreen((Uri) msg.obj);
            }
        });

        viewModel.getSavedImageList().observe(this, data -> {
            adapter.setImageDataList(data);
            imageListSize = data.size();
        });

    }

    @Override
    protected void onCreated() {
        setupToolBarForBackArrow(binding.tbPreTraining);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    mLastLocation = locationResult.getLastLocation();
                    Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                    Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                    takeImage();
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                } else {
                    viewModel.setIsLoading(false);
                    isClicked = false;
                    showToast("Error fetching location");
                }
            }
        };

        recyclerView = findViewById(R.id.gridview_picture);
        MtrainerDataBase dataBase = MtrainerDataBase.getDatabase(getApplicationContext());
        adapter = new TrainingImageAdapter(this, this, picasso);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));

/*
     recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
*/
        binding.fabCapture.setOnClickListener(v -> {
            if (viewModel.isLoading.get() == View.GONE && !isClicked) {
                if (selectedPost != null) {
                    if (imageListSize < 7) {
                        isClicked = true;
                        takeImage();

                        //viewModel.setIsLoading(true);
                        //onLocationPermissionRequest();
                        //Prefs.putDouble(PrefsConstants.LAT,0.0);
                        //Prefs.putDouble(PrefsConstants.LONGI,0.0);
                        // Log.v("Location", "Lat : " + location.getLatitude() + "  Long : " + location.getLongitude() + "  Time : " + location.getTime() + " Time Diff : " + (new Date().getTime() - location.getTime()));
                        // Log.v("take training","pic");
                        // takeImage();

                    } else {
                        showToast("Maximum limit reached");
                    }
                } else {
                    showToast("Please select group");
                }
            }
        });

        dataBase.getAttendanceDao()
                .getAttendancePostList(Prefs.getInt(PrefsConstants.ROTA_ID))
                .observe(this, this::inflatePostChip);

        // Saving Data
        binding.flButtonPhoto.setOnClickListener(view -> {
            if (imageListSize != 0) {
                finish();
            } else {
                showToast("Take at least one photo");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingImageViewModel) getAndroidViewModel(TrainingImageViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_image_capture;
    }

    @Override
    public void onImageSelected(String imageUrl) {
        binding.takePhotoText.setVisibility(View.GONE);
        setPreview(imageUrl);
    }

    @Override
    public void imageRemoved(String imageUrl) {
        viewModel.removeImage(imageUrl);
        if (previewImageUrl != null && imageUrl.contains(previewImageUrl)) {
            binding.imagePhoto1.setVisibility(View.GONE);
            binding.takePhotoText.setVisibility(View.VISIBLE);
        }

    }

    /*   @Override
       public void onBackPressed() {
           Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flLiveImage);
           if (currentFragment instanceof CaptureImageFragment) {
               super.onBackPressed();
           } else {
               openImageCaptureScreen();
           }
       }
   */
    enum CaptureImageType {
        PHOTO_EVALUATION,
        UNKOWN
    }


    private void inflatePostChip(List<PostItem> postList) {

        View view = binding.getRoot();
        ChipGroup chipGroup = view.findViewById(R.id.post_container);
        chipGroup.removeAllViews();
        SparseArray<PostItem> map = new SparseArray<>();

        for (PostItem s : postList) {
            Chip chip =
                    (Chip) getLayoutInflater().inflate(R.layout.single_post_item_chip, chipGroup, false);
            chip.setText(s.postName);
            chipGroup.addView(chip);
            map.append(chip.getId(), s);
            if (s.postId == 0) {
                chip.setChecked(true);
                selectedPost = s;
            }
        }

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> selectedPost = map.get(checkedId));
    }

    private void setPreview(String imagePath) {
        previewImageUrl = imagePath;
        binding.imagePhoto1.setVisibility(View.VISIBLE);
        binding.imagePhoto1.zoomTo(1f, false);
//        picasso.load(new File(imagePath))
        picasso.load(new File(FileUtils.getAbsolutePathFromString(imagePath)))
                .fit()
                .centerInside()
                .into(binding.imagePhoto1);
    }

    private void onLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && (new Date().getTime() - location.getTime()) <= 120000) {
                Prefs.putDouble(PrefsConstants.LAT, location.getLatitude());
                Prefs.putDouble(PrefsConstants.LONGI, location.getLongitude());
//                Log.v("Location", "Lat : " + location.getLatitude() + "  Long : " + location.getLongitude() + "  Time : " + location.getTime() + " Time Diff : " + (new Date().getTime() - location.getTime()));
                takeImage();
                mFusedLocationClient.requestLocationUpdates(getLocationRequest(), new LocationCallback(), null);
            } else
                mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
        });
    }

    // Setting the type of Request
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setMaxWaitTime(1000);
        locationRequest.setFastestInterval(200);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;

        // locationRequest.setInterval(1000);
        // locationRequest.setMaxWaitTime(4000);
        // locationRequest.setFastestInterval(500);
    }

    private void checkLocationSetting() {
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(getLocationRequest());
        LocationSettingsRequest locationSettingsRequest = builder.build();
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Timber.i("All location settings are satisfied.");
                    onLocationRequest();
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Timber.i("Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(TrainingImageActivity.this, REQUEST_CODE_RESOLUTION_REQUIRED);
                            } catch (IntentSender.SendIntentException sie) {
                                Timber.i("PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            viewModel.setIsLoading(false);
                            isClicked = false;
                            Timber.e(errorMessage);
                            Toast.makeText(TrainingImageActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSetting();
            } else {
                viewModel.setIsLoading(false);
                isClicked = false;
                Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takeImage() {
        try {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
//            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri());
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUriV2());
            viewModel.setIsLoading(false);
            Prefs.putStringOnMainThread(PrefsConstants.CURRENT_PICTURE_PATH, _path);
            Prefs.putStringOnMainThread(PrefsConstants.CURRENT_POST_NAME, selectedPost.postName);
            Prefs.putIntOnMainThread(PrefsConstants.CURRENT_POST_ID, selectedPost.postId);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Device doesn't support capturing images!";
            viewModel.setIsLoading(false);
            isClicked = false;
            Toast.makeText(TrainingImageActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /*private Uri getFileUri() {
        String imgcurTime = dateFormat.format(new Date());
        File imageDirectory = new File(GridViewDemo_ImagePath);
        imageDirectory.mkdirs();
        _path = GridViewDemo_ImagePath + "_" + Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + selectedPost.postId + "_" + imgcurTime + ".jpg";

        int compressionRatio = 2; //1 == originalImage, 2 = 50% compression, 4=25% compress
        File file = new File(_path);
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream(file));
            Log.d("Compress size ", "is" + file);
        } catch (Throwable t) {
            Log.e("ERROR", "Error compressing file." + t.toString());
            t.printStackTrace();
        }
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
    }*/

    private Uri getFileUriV2() {

        String rootPath = FileUtils.getRootPathV2(this, FolderNames.ChoosePhoto);
        if (FileUtils.createOrExistsDir(rootPath)) {
            _path = FileUtils.createFilePathV2(rootPath, Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + selectedPost.postId);
            File file = FileUtils.getFileByPath(_path);
            try {

                if (file != null && (file.exists() || file.createNewFile())) {
                    return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                } else {
                    Log.e("ERROR", "File creation failed.");
                }

//                int compressionRatio = 2;

/*//                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(file));
                Bitmap bitmap = BitmapFactory.decodeFile(Objects.requireNonNull(file).getAbsolutePath());
//                bitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream(file));
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, fos);
                fos.close(); // Ensure the output stream is closed
                Log.d("Compress size ", "is" + file);
//                return Uri.fromFile(file);
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);*/
            } catch (Throwable t) {
                Log.e("ERROR", "Error compressing file." + t);
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION_REQUIRED) {
            if (resultCode == Activity.RESULT_OK) {
                onLocationRequest();
            } else {
                viewModel.setIsLoading(false);
                isClicked = false;
                showToast("GPS is needed");
            }

        } else if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {

                if (_path == null) {
                    _path = Prefs.getString(PrefsConstants.CURRENT_PICTURE_PATH);
                }

                if (selectedPost == null) {
                    selectedPost = new PostItem();
                    selectedPost.postId = Prefs.getInt(PrefsConstants.CURRENT_POST_ID);
                    selectedPost.postName = Prefs.getString(PrefsConstants.CURRENT_POST_NAME);
                }
                if (selectedPost.postName == null || _path == null) {
                    showToast("Unable Capture Image");
                    return;
                }

                try {
                    File file = new File(_path);
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    if (bitmap != null) {
                        // Compress the bitmap
                        FileOutputStream fos = new FileOutputStream(file);
                        int compressionRatio = 50; // Compress by 50%
                        bitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, fos);
                        fos.close();
                        Log.d("Compress size ", "Compressed image saved to " + file);
                    } else {
                        Log.e("BitmapFactory", "Failed to decode image from file.");
                    }
                } catch (Exception e) {
                    Log.e("ERROR", "Error compressing file: " + e);
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String watermark = Prefs.getString(PrefsConstants.EMPPLOYEE_NAME) + " " + sdf.format(new Date()) + " " +Prefs.getDouble(PrefsConstants.LAT)+","+Prefs.getDouble(PrefsConstants.LONGI);
                viewModel.saveImage(_path, selectedPost, viewModel.pictureType.get(), watermark);
                setPreview(_path);
                adapter.notifyDataSetChanged();
                isClicked = false;
                binding.takePhotoText.setVisibility(View.GONE);
            }
        } else if (requestCode == CAMERA_CAPTURE)
            isClicked = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Prefs.putIntOnMainThread(PrefsConstants.TRAINING_IMAGE_COUNT, imageListSize);
    }
}

