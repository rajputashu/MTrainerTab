package com.sisindia.ai.mtrainer.android.features.takeassessment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.droidcommons.preference.Prefs;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.commons.FolderNames;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityAssessmentBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AssessmentQuestionActivity extends MTrainerBaseActivity implements Player.Listener {

    //    private static final String TAG = "AssessmentQuestionActivity";
    boolean recording = false;
    private ActivityAssessmentBinding binding;
    private AssesmentQuestionViewModel viewModel;
    private Camera mCamera;
    private String currentFileName;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private boolean cameraFront = false;
    private boolean canFinish = false;
    private String rootPath = "";
    private ExoPlayer exoPlayer;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (viewModel.isLoading.get() == View.GONE) {
                // get the number of cameras
                if (!recording) {
                    int camerasNumber = Camera.getNumberOfCameras();
                    if (camerasNumber > 1) {
                        // release the old camera instance
                        // switch camera, from the front and the back and vice versa

                        releaseCamera();
                        chooseCamera();
                    } else {
                        showToast("Sorry, your phone has only one camera!");
                    }
                }
            } else {
                showToast("Please wait data is loading...");
            }
        }
    };

    View.OnClickListener captureVidListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (viewModel.isLoading.get() == View.GONE) {
                if (recording) {
                    binding.recodeBtn.setBackground(getDrawable(R.drawable.ic_play_video));
                    // stop recording and release camera
                    try {
                        viewModel.stopTimer();
                        mediaRecorder.stop(); // stop the recording
                        releaseMediaRecorder(); // release the MediaRecorder object
                        showToast("Video captured!");
                        binding.doneBtn.setClickable(true);
                        binding.doneBtn.setAlpha(1f);
                        recording = false;
                        binding.recodeBtn.setAlpha(0.3f);
                        binding.recodeBtn.setClickable(false);
                        releaseCamera();
                        showVideoThumb();

                    } catch (RuntimeException stopException) {
                        showToast("No Video captured!");
                        viewModel.stopTimer();
                        viewModel.timeTracker.set(0);
                        releaseMediaRecorder(); // release the MediaRecorder object
                        recording = false;
                    }

                } else {
                    binding.recodeBtn.setBackground(getDrawable(R.drawable.ic_stop_video));
                    if (!prepareMediaRecorder()) {
                        showToast("Fail in prepareMediaRecorder()!\n - Ended -");
                        finish();
                    }
                    // work on UiThread for better performance
                    runOnUiThread(() -> {
                        // If there are stories, add them to the table
                        try {
                            mediaRecorder.start();
                            viewModel.startTimer();
                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    });
                    recording = true;
                }
            } else {
                showToast("Please wait data is loading...");
            }
        }
    };

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewState() {
        viewModel.getAssessmentQuestions();
        liveData.observe(this, message -> {
            switch (message.what) {
                case NavigationConstants.UPDATE_ASSESSMENT_VIDEO_VIEW:
                    lastEntry();
                    break;
                case NavigationConstants.STOP_ASSESSMENT_VIDEO:
                    videoLimitReached();
                    break;
            }

        });
    }

    @Override
    protected void onCreated() {
        setupToolBarForBackArrow(binding.tbStartTraining);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initialize();
        MtrainerDataBase dataBase = MtrainerDataBase.getDatabase(this);
        binding.doneBtn.setClickable(false);
        binding.doneBtn.setAlpha(0.3f);
    }

    @Override
    protected void initViewModel() {
        viewModel = (AssesmentQuestionViewModel) getAndroidViewModel(AssesmentQuestionViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_assessment;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasCamera(this)) {
            showToast("Sorry, your phone does not have a camera!");
            setResult(RESULT_CANCELED);
            finish();
        }

        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                showToast("No front facing camera found.");
                binding.cameraSwitch.setVisibility(View.GONE);
            }
            mCamera = Camera.open(findBackFacingCamera());
            mPreview.refreshCamera(mCamera);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (recording) {
            binding.recodeBtn.setBackground(getDrawable(R.drawable.ic_play_video));
            // stop recording and release camera
            try {
                viewModel.stopTimer();
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                showToast("Video captured!");
                binding.doneBtn.setClickable(true);
                binding.doneBtn.setAlpha(1f);
                recording = false;
                binding.recodeBtn.setAlpha(0.3f);
                binding.recodeBtn.setClickable(false);
                showVideoThumb();
            } catch (RuntimeException stopException) {
                showToast("No Video captured!");
                viewModel.stopTimer();
                viewModel.timeTracker.set(0);
                releaseMediaRecorder(); // release the MediaRecorder object
                recording = false;
            }
        }

        if (exoPlayer.isPlaying()) {
            exoPlayer.setPlayWhenReady(false);
            //showVideoThumb();
        }
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void initialize() {

        createVideoFolder();
        PlayerView playerView = binding.videoPlayerView;
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);
        exoPlayer.addListener(this);
        mPreview = new CameraPreview(this, mCamera);
        binding.cameraLinId.addView(mPreview);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mPreview.setLayoutParams(layoutParams);
        binding.recodeBtn.setOnClickListener(captureVidListener);
        binding.cameraSwitch.setOnClickListener(switchCameraListener);

        binding.doneBtn.setOnClickListener(v -> {
            viewModel.saveDataToDb(currentFileName);
            if (canFinish) {
                setResult(RESULT_OK);
                finish();
            } else {
                viewModel.getNext();
                reInitCameraPreview();
                viewModel.timeTracker.set(0);
                binding.doneBtn.setClickable(false);
                binding.doneBtn.setAlpha(0.3f);
            }
        });

        binding.playBtn.setOnClickListener(v -> prepareForVideoPlay());
        binding.deleteBtn.setOnClickListener(v -> {
            deleteFile();
            viewModel.timeTracker.set(0);
        });

        binding.deleteBtn.setClickable(false);
        binding.deleteBtn.setAlpha(0.3f);
        binding.playBtn.setClickable(false);
        binding.playBtn.setAlpha(0.3f);
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        CamcorderProfile vgaCamera = CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);
        //mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA));
        //mediaRecorder.setVideoEncodingBitRate();
//        Log.d(TAG, "prepareMediaRecorder: video" + camcorderProfile.videoBitRate);
//        Log.d(TAG, "prepareMediaRecorder: audio" + camcorderProfile.videoBitRate);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        currentFileName = assmentDir.getPath() + "/" + Prefs.getInt(PrefsConstants.ROTA_ID) + dateFormat.format(new Date()) + "_" + viewModel.currentAssessmentEntity.getEmpCode() + ".mp4";

        String fileName = Prefs.getInt(PrefsConstants.ROTA_ID) + dateFormat.format(new Date()) + "_" + viewModel.currentAssessmentEntity.getEmpCode();
        currentFileName = FileUtils.createVideoFilePathV2(rootPath, fileName);

        mediaRecorder.setOutputFile(currentFileName);
        mediaRecorder.setMaxDuration(60 * 1000); // Set max duration 60 sec.
        //mediaRecorder.setVideoEncodingBitRate(200000);
        mediaRecorder.setVideoFrameRate(vgaCamera.videoFrameRate);
        mediaRecorder.setVideoSize(vgaCamera.videoFrameWidth, vgaCamera.videoFrameHeight);
        //mediaRecorder.setVideoSize(480, 360);
        mediaRecorder.setVideoEncodingBitRate(vgaCamera.videoBitRate);
        mediaRecorder.setAudioEncodingBitRate(vgaCamera.audioBitRate * 3);
        mediaRecorder.setAudioChannels(camcorderProfile.audioChannels);
        mediaRecorder.setAudioSamplingRate(camcorderProfile.audioSampleRate);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void createVideoFolder() {
        rootPath = FileUtils.getRootPathV2(this, FolderNames.AssessmentVideo);
        FileUtils.createOrExistsDir(rootPath);
    }

    private void deleteFile() {
        File currentFile = new File(currentFileName);
        if (currentFile.exists()) {
            if (currentFile.delete()) {
                showToast("File Deleted");
                reInitCameraPreview();
            } else
                showToast("Error: Unable to delete file");
        }
    }

    private void lastEntry() {
        binding.doneBtn.setText("DONE");
        binding.doneBtn.setClickable(false);
        binding.doneBtn.setAlpha(0.3f);
        canFinish = true;
    }

    private void showVideoThumb() {
        binding.cameraLinId.setVisibility(View.GONE);
        binding.videoPlayerView.setVisibility(View.GONE);
        binding.videoThumb.setVisibility(View.VISIBLE);
        binding.assessmentActionHolder.setVisibility(View.VISIBLE);
        binding.cameraLinId.removeAllViews();
        binding.deleteBtn.setClickable(true);
        binding.deleteBtn.setAlpha(1f);
        binding.playBtn.setClickable(true);
        binding.playBtn.setAlpha(1f);
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        Glide.with(this)
                .asBitmap()
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .load(currentFileName)
                .into(binding.videoThumb);
    }

    private void reInitCameraPreview() {
        binding.cameraLinId.setVisibility(View.VISIBLE);
        binding.videoPlayerView.setVisibility(View.GONE);
        binding.videoThumb.setVisibility(View.GONE);
        binding.assessmentActionHolder.setVisibility(View.VISIBLE);
        mPreview = new CameraPreview(this, mCamera);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mPreview.setLayoutParams(layoutParams);
        binding.cameraLinId.removeAllViews();
        binding.cameraLinId.addView(mPreview);
        mCamera = Camera.open(findBackFacingCamera());
        mPreview.refreshCamera(mCamera);
        binding.doneBtn.setClickable(false);
        binding.doneBtn.setAlpha(0.3f);
        binding.deleteBtn.setClickable(false);
        binding.deleteBtn.setAlpha(0.3f);
        binding.playBtn.setClickable(false);
        binding.playBtn.setAlpha(0.3f);
        binding.recodeBtn.setAlpha(1f);
        binding.recodeBtn.setClickable(true);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "Mtrainer");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));
    }

    private void prepareForVideoPlay() {
        binding.assessmentActionHolder.setVisibility(View.GONE);
        binding.cameraLinId.setVisibility(View.GONE);
        binding.videoPlayerView.setVisibility(View.VISIBLE);
        binding.videoThumb.setVisibility(View.GONE);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare(buildMediaSource(Uri.fromFile(new File(currentFileName))));
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady && playbackState == Player.STATE_READY) {

        } else if (playWhenReady) {
            // might be idle (plays after prepare()),
            // buffering (plays when data available)
            // or ended (plays when seek away from end)
        } else {
            showVideoThumb();
        }
        if (playbackState == Player.STATE_ENDED) {
            showVideoThumb();
        }
    }

    private void videoLimitReached() {
        binding.recodeBtn.setBackground(getDrawable(R.drawable.ic_play_video));
        // stop recording and release camera
        try {
            mediaRecorder.stop(); // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            showToast("Video captured!");
            binding.doneBtn.setClickable(true);
            binding.doneBtn.setAlpha(1f);
            recording = false;
            binding.recodeBtn.setAlpha(0.3f);
            binding.recodeBtn.setClickable(false);
            releaseCamera();
            showVideoThumb();

        } catch (RuntimeException stopException) {
            showToast("No Video captured!");
            releaseMediaRecorder(); // release the MediaRecorder object
            recording = false;
        }
    }

}
