package com.sisindia.ai.mtrainer.android.features.umbrellareport;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.droidcommons.preference.Prefs;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivitySpiUmbrellaReportBaseBinding;
import com.sisindia.ai.mtrainer.android.databinding.AdhocPostDialogBinding;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.SiteData;
import com.sisindia.ai.mtrainer.android.utils.LocationUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

public class UmbrellaSPIReportBaseActivity extends MTrainerBaseActivity {
    private ActivitySpiUmbrellaReportBaseBinding binding;
    private UmbrellaReportViewModel viewModel;
    public static final String BASE_IMAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Umbrella/";
    private final int UMBRELLA_IMAGE_CAPTURE = 1001;
    private boolean isClicked = false;
    private Dialog dialog;

    @Override
    protected void extractBundle() {}

    @Override
    protected void initViewState() {

        liveData.observe(this, message -> {
            switch (message.what) {
                case NavigationConstants.TAKE_UMBRELLA_PICTURE:
                    isClicked = true;
                    int postId = message.arg1;
                    String postName =(String) message.obj;
                    int position = message.arg2;
                    if(postId == -1) {
                        if(dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }
                    takeImage(postName, postId,position);
                    break;
                case NavigationConstants.CLOSE_UMBRELLA_REPORT:
                    viewModel.clearUmbrellaState();
                   /* Constraints constraints = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();

                    WorkRequest immediateSync = new OneTimeWorkRequest.Builder(UmbrellaSPISyncWorker.class)
                            .setConstraints(constraints)
                            .build();

                    WorkManager.getInstance(this).enqueue(immediateSync);*/
                    finish();
                    break;
                case NavigationConstants.OPEN_UMBRELLA_POST_DIALOG:
                    openAddPostDialog();
                    break;
                case NavigationConstants.OPEN_UMBRELLA_ALERT_DIALOG:
                        openAlertDialog();
                    break;
            }
        });


        viewModel.getBranchList().observe(this, new Observer<List<BranchData>>() {
            @Override
            public void onChanged(List<BranchData> branchDataList) {
                viewModel.setBranchList(branchDataList);
            }
        });

        viewModel.getSiteList().observe(this, new Observer<List<SiteData>>() {
            @Override
            public void onChanged(List<SiteData> siteDataList) {
                viewModel.setSiteList(siteDataList);
            }
        });

        viewModel.getUmbrellaImages().observe(this, (umbrellaImages) -> {
            viewModel.setUmbrellaReportAdapter(umbrellaImages);
        });
    }

    @Override
    protected void onCreated() {
        viewModel.getUmbrellaDetails();
        viewModel.fetchBranchList();

/*
        binding.etTraineeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    viewModel.setUmbrellaCount(Integer.parseInt(s.toString().trim()));
                } catch (Exception e) {
                    showToast("Please Enter Number");
                }
            }
        });
*/
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
    }

    @Override
    protected void initViewModel() {
        viewModel = (UmbrellaReportViewModel) getAndroidViewModel(UmbrellaReportViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_spi_umbrella_report_base;
    }

    private void takeImage(String postName, int postId, int position) {
        try {
            //use standard intent to capture an image
            LocationUtils.getLocationData(this);
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File imagesDir = new File(getExternalFilesDir(null), "Umbrella");
            String path =  viewModel.dateFormat.format(new Date()) + "_"+ Prefs.getInt(PrefsConstants.BASE_TRAINER_ID) + "_"+ postId+".jpg";
            //we will handle the returned data in onActivityResult
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(imagesDir,path));
            viewModel.setIsLoading(false);
            Log.d("saveimage:",path);
            Prefs.putStringOnMainThread(PrefsConstants.CURRENT_UMBRELLA_PICTURE_PATH,imagesDir+"/"+path);
            Prefs.putStringOnMainThread(PrefsConstants.CURRENT_UMBRELLA_POST_NAME, postName);
            Prefs.putIntOnMainThread(PrefsConstants.CURRENT_UMBRELLA_POST_ID, postId);
            Prefs.putIntOnMainThread(PrefsConstants.CURRENT_UMBRELLA_ADAPTER_POSITION, position);
            someActivityResultLauncher.launch(captureIntent);
           // startActivityForResult(captureIntent, UMBRELLA_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            //display an error message
            Log.d("saveimage:",e.toString());
            String errorMessage = "Device doesn't support capturing images!";
            viewModel.setIsLoading(false);
            isClicked = false;
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            Log.d("saveimage:",e.toString());
            logger.log("Umbrella Camera Activity Error", e);
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("imagepath:",result.getResultCode()+"");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        //String data = result.getData().getData().getPath();
                           // Log.d("imagepath:",data);
                            String imagePath = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_PICTURE_PATH);
                            String postName = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_POST_NAME);
                            int  postId = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_POST_ID);
                            int  position = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_ADAPTER_POSITION);
                            Log.d("saveimage:",imagePath);
                            String adhocPostId = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ADHOC_POST_ID, "");
                            String umbrellaId = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID, "");
                            int branchId = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_BRANCH_ID,0);
                            int siteId = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_SITE_ID,0);
                            String imageId = viewModel.dateFormat.format(new Date()) + "_"+ Prefs.getInt(PrefsConstants.BASE_TRAINER_ID) + "_"+ siteId + "_" + branchId + "_" + postId;
                            if(imagePath.isEmpty() || postName.isEmpty()) {
                                Log.d("saveimage:",imagePath);
                                showToast("Unable Capture Image");
                                return;
                            } else {
                                viewModel.umbrellaReportAdapter.getItem(position).imagePath = imagePath;
                                viewModel.umbrellaReportAdapter.notifyDataSetChanged();
                                if(postId != -1) {
                                    viewModel.saveImage(postId, umbrellaId, imagePath, imageId);
                                }else {
                                    viewModel.saveImage(adhocPostId, umbrellaId, imagePath, imageId, postName);
                                }
                            }
                            isClicked = false;

                    }
                }
            });


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UMBRELLA_IMAGE_CAPTURE) {
            //user is returning from capturing an image using the camera
            if (resultCode == RESULT_OK) {
                String imagePath = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_PICTURE_PATH);
                String postName = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_POST_NAME);
                int  postId = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_POST_ID);
                int  position = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_ADAPTER_POSITION);
                Log.d("saveimage:",imagePath);
                String adhocPostId = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ADHOC_POST_ID, "");
                String umbrellaId = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID, "");
                int branchId = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_BRANCH_ID,0);
                int siteId = Prefs.getInt(PrefsConstants.CURRENT_UMBRELLA_SITE_ID,0);
                String imageId = viewModel.dateFormat.format(new Date()) + "_"+ Prefs.getInt(PrefsConstants.BASE_TRAINER_ID) + "_"+ siteId + "_" + branchId + "_" + postId;
                if(imagePath.isEmpty() || postName.isEmpty()) {
                    Log.d("saveimage:",imagePath);
                    showToast("Unable Capture Image");
                    return;
                } else {
                    if(postId != -1) {
                        viewModel.saveImage(postId, umbrellaId, imagePath, imageId);
                    }else {
                        viewModel.saveImage(adhocPostId, umbrellaId, imagePath, imageId, postName);
                    }
                }
                isClicked = false;
            }
        } else
            isClicked = false;
    }

    private Uri getFileUri(File imagesDir, String path) {
        //File imageDirectory = new File(BASE_IMAGE_PATH);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();// Create the directory if it doesn't exist
        }
        File file = new File(imagesDir,path);
        //if(file.exists())
        return FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
    }

    void openAddPostDialog() {
        dialog = new Dialog(this);
        AdhocPostDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.adhoc_post_dialog, null, false);
        dialogBinding.setVm(viewModel);
        dialogBinding.etPostName.setText("");
        dialogBinding.etPostName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setPostName(s.toString());
            }
        });

        dialogBinding.closeBtn.setOnClickListener(v -> {
            if(dialog != null && dialog.isShowing())
                dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        viewModel.setPostName("");
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onBackPressed() {
        viewModel.checkHasUnsavedData();
    }

    void openAlertDialog() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Alert")
                .setMessage("Current data will be deleted")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.clearUmbrellaDataAndClose();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false)
                .create();
        dialog.show();
    }
}