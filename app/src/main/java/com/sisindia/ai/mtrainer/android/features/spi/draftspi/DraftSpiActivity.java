package com.sisindia.ai.mtrainer.android.features.spi.draftspi;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_APPROVAL_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TAKE_IMAGE;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.droidcommons.preference.Prefs;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActvityDraftSpiBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.spi.SpiMainActivity;
import com.sisindia.ai.mtrainer.android.features.spi.draftapproval.DraftApprovalActivity;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DraftSpiActivity extends MTrainerBaseActivity implements DraftSpiRecyclerAdapter.RemoveDraftImage {
    private DraftSpiViewModel viewModel;
    private ActvityDraftSpiBinding binding;
    private boolean isClicked = false;
    //DraftSpiRecyclerAdapter draftSpiRecyclerAdapter;
    private int position;
    private int reuplodposition = -2;

    private int draftimageSize;

    MtrainerDataBase dataBase;
    final int CAMERA_CAPTURE = 4;
    public static final String GridViewDemo_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DraftPhoto/";
    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    String _path;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, DraftSpiActivity.class);
        return intent;
    }

    @Override
    protected void extractBundle() {
        position = getIntent().getIntExtra(NavigationConstants.SPI_SPINNER_POSITION, -1);
    }

    @Override
    protected void initViewModel() {
        viewModel = (DraftSpiViewModel) getAndroidViewModel(DraftSpiViewModel.class);
    }


    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            switch (message.what) {
                case TAKE_IMAGE:
                    takeImage(message.arg1, message.arg2);
                    break;
                case OPEN_DRAFT_APPROVAL_SCREEN:
                    openDraftApprovalScreen();
            }
        });

        viewModel.getImageList().observe(this, (imageList -> {
            viewModel.updateImage(imageList);
            draftimageSize = imageList.size();
        }));
    }

    @Override
    protected void onCreated() {
        viewModel.initRecylerView();
        binding.refreshDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.initRecylerView();
                showToast("refreshed successfully");
            }
        });

        binding.btnDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (draftimageSize != 0) {
                    viewModel.submitdraft();
                } else {
                    showToast("please take pictures!!");
                }


            }
        });
        statusposition();
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }


    private void takeImage(int viewPosition, int imagePosition) {
        try {
            //use standard intent to capture an image
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri());
            Prefs.putStringOnMainThread(PrefsConstants.SPI_PICTURE_PATH, _path);
            Prefs.putIntOnMainThread(PrefsConstants.SPI_POSITION, imagePosition);
            Prefs.putIntOnMainThread(PrefsConstants.SPI_VIEW_POSITION, viewPosition);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Device doesn't support capturing images!";
            viewModel.setIsLoading(false);
            isClicked = false;
            Toast toast = Toast.makeText(DraftSpiActivity.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private Uri getFileUri() {
        String imgcurTime = dateFormat.format(new Date());
        File imageDirectory = new File(GridViewDemo_ImagePath);
        imageDirectory.mkdirs();
        _path = GridViewDemo_ImagePath + "_" + Prefs.getInt(PrefsConstants.BASE_TRAINER_ID) + "_" + Prefs.getInt(PrefsConstants.SPI_POST_ID) + "_" + imgcurTime + ".jpg";

        File file = new File(_path);
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                //   mark();
                if (_path == null) {
                    _path = Prefs.getString(PrefsConstants.SPI_PICTURE_PATH);
                }

                if (Prefs.getString(PrefsConstants.SPI_POST_NAME) == null || _path == null) {
                    showToast("Unable Capture Image");
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String watermark = Prefs.getString(PrefsConstants.EMPPLOYEE_NAME) + " " + sdf.format(new Date()) + " " + "Lat : " + Prefs.getDouble(PrefsConstants.LAT) + " " + "Long : " + Prefs.getDouble(PrefsConstants.LONGI);
                viewModel.saveDraftImage(_path, Prefs.getInt(PrefsConstants.SPI_BRANCHID),
                        Prefs.getInt(PrefsConstants.CUSTOMER_ID), Prefs.getInt(PrefsConstants.SPI_POST_ID));
                //  setPreview(_path);
                //draftSpiRecyclerAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == CAMERA_CAPTURE)
            isClicked = false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.actvity_draft_spi;
    }

    @Override
    public void DarftimageRemoved(int position, String uniqueid, String imageurl) {
        viewModel.removeDraftImage(position, uniqueid, imageurl);

    }

    public void openDraftApprovalScreen() {
        Intent intent = new Intent(this, DraftApprovalActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, position);
        startActivity(intent);
        // startActivityForResult(DraftApprovalActivity.newIntent(this), IntentRequestCodes.BASIC_SPI_DETAIL);
    }

    public void statusposition() {
        SpiStatusRequest request = new SpiStatusRequest(Prefs.getInt(PrefsConstants.SPI_ID), 2);
        viewModel.getDraftStatus(request);
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Alert")
                .setMessage("do you want to go back main spi screen!! ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statusposition();
                        showToast("going back to main screen");
                        viewModel.flushSpiPhotos();
                        openSPIMainScreen();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).setCancelable(false)
                .create();
        dialog.show();

    }

    void openSPIMainScreen() {
        Intent intent = new Intent(this, SpiMainActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, position);
        startActivity(intent);
    }
}
