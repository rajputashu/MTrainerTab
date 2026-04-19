package com.sisindia.ai.mtrainer.android.features.spi.mounted;

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
import androidx.databinding.ObservableField;

import com.droidcommons.preference.Prefs;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentMountedBinding;
import com.sisindia.ai.mtrainer.android.db.entities.MountedSpiPhotoEntity;
import com.sisindia.ai.mtrainer.android.features.spi.SpiMainActivity;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiImage;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_AGAIN_SPI_MAIN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TAKE_MOUNTED_IMAGE;

public class MountedActivity extends MTrainerBaseActivity {
    private MountedViewModel viewModel;
    private FragmentMountedBinding binding;

    final int CAMERA_CAPTURE = 5;
    public static final String GridViewDemo_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MountedPhoto/";
    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    String _path;
    private boolean isClicked = false;
    private int mountedimageSize;
    public ObservableField<String> done= new ObservableField<>("Done");
    private int position;

    public static Intent newIntent(Activity activity) {
        Intent intent= new Intent(activity, MountedActivity.class);
        return intent;
    }
    @Override
    protected void extractBundle() {
        position=getIntent().getIntExtra(NavigationConstants.SPI_SPINNER_POSITION,-1);

    }

    @Override
    protected void initViewModel() {
        viewModel= (MountedViewModel) getAndroidViewModel(MountedViewModel.class);

    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();

    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            switch (message.what) {
                case TAKE_MOUNTED_IMAGE:
                    takeImage(message.arg1, message.arg2);
                    break;
                case OPEN_AGAIN_SPI_MAIN:
                   // openSPIMainScreen();
            }
        });
        viewModel.getImageList().observe(this, (imageList -> {
            viewModel.updateImage(imageList);
            mountedimageSize=imageList.size();

        }));

    }

    private void takeImage(int viewPosition, int imagePosition) {
        try {
            //use standard intent to capture an image
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri());
            Prefs.putStringOnMainThread(PrefsConstants.SPI_MOUNTED_PATH, _path);
            Prefs.putIntOnMainThread(PrefsConstants.SPI_POSITION, imagePosition);
            Prefs.putIntOnMainThread(PrefsConstants.SPI_VIEW_POSITION, viewPosition);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Device doesn't support capturing images!";
            viewModel.setIsLoading(false);
            isClicked = false;
            Toast toast = Toast.makeText(MountedActivity.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private Uri getFileUri() {
        String imgcurTime = dateFormat.format(new Date());
        File imageDirectory = new File(GridViewDemo_ImagePath);
        imageDirectory.mkdirs();
        _path = GridViewDemo_ImagePath + "_" + Prefs.getInt(PrefsConstants.BASE_TRAINER_ID) + "_" + Prefs.getInt(PrefsConstants.SPI_POST_ID)+ "_" + imgcurTime + ".jpg";

        File file = new File (_path);
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID +".provider", file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                //   mark();
                if(_path == null) {
                    _path = Prefs.getString(PrefsConstants.SPI_MOUNTED_PATH);
                }

                if(Prefs.getString(PrefsConstants.SPI_POST_NAME)==null ||_path == null) {
                    showToast("Unable Capture Image");
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String watermark = Prefs.getString(PrefsConstants.EMPPLOYEE_NAME) + " " + sdf.format(new Date()) + " " + "Lat : " + Prefs.getDouble(PrefsConstants.LAT) + " " + "Long : " + Prefs.getDouble(PrefsConstants.LONGI);
                viewModel.saveMountedImage(_path,Prefs.getInt(PrefsConstants.SPI_BRANCHID),
                        Prefs.getInt(PrefsConstants.CUSTOMER_ID),Prefs.getInt(PrefsConstants.SPI_POST_ID));
                //  setPreview(_path);
                //draftSpiRecyclerAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == CAMERA_CAPTURE)
            isClicked = false;
    }



    @Override
    protected void onCreated() {
       // viewModel.SpiMountedDetails();
        statusposition();
        viewModel.initRecylerView();
        viewModel.fushMountedPhoto();
       // viewModel.MountedStatus();

        binding.refreshMounted.setOnClickListener(v->{
            viewModel.MountedStatus();
            showToast("refreshed successfully!!");
        });

        binding.btnMounted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mountedimageSize !=0){
                    viewModel.submitmounted();
                    // TODO: Need to put this after uploading
                    statuspositionmain();
                    openSPIMainScreen();
                }
                else {
                    showToast("please take picture ");
                }


            }
        });

       /* binding.btnMounted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewModel.mountedStatusDone.equals("Done")){
                   binding.btnUpload.setVisibility(View.INVISIBLE);

                   showToast("successfully submitted!!");
                    openSPIMainScreen();
                }
                else {
                    showToast("Mounted status not completed");
                }


            }
        });
*/

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_mounted;
    }

    void  openSPIMainScreen(){
        Intent intent=new Intent(this, SpiMainActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION,position);
        startActivity(intent);

       // startActivityForResult(SpiMainActivity.newIntent(this), IntentRequestCodes.BASIC_SPI_DETAIL);
    }



    public void statusposition(){
        SpiStatusRequest request= new SpiStatusRequest(Prefs.getInt(PrefsConstants.SPI_ID),7);
        viewModel.getDraftStatus(request);
    }


    public void statuspositionmain(){
        SpiStatusRequest request= new SpiStatusRequest(Prefs.getInt(PrefsConstants.SPI_ID),9);
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
                        viewModel.flushMountedPhotos();
                        openSPIMainScreenStatus();

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

    void  openSPIMainScreenStatus(){
        Intent intent=new Intent(this, SpiMainActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION,position);
        startActivity(intent);

        // startActivityForResult(SpiMainActivity.newIntent(this), IntentRequestCodes.BASIC_SPI_DETAIL);
    }

}
