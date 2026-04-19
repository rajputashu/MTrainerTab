package com.sisindia.ai.mtrainer.android.features.attendancemodule;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.commons.FolderNames;
import com.sisindia.ai.mtrainer.android.constants.AttendanceConstants;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingPicandsigBinding;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;
import com.sisindia.ai.mtrainer.android.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrainingPicAndSignActivity extends MTrainerBaseActivity {

    private TrainingPicAndSignatureViewModel viewModel;
    private ActivityTrainingPicandsigBinding binding;
    final int CAMERA_CAPTURE = 3;
    //    public static final String GridViewDemo_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GridViewDemo/";
//    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private String image_path;
    private String attendancePhotoFile;
    //    private MtrainerDataBase dataBase;
    private String employeeName;
    private int employeeID;
    private String empCode;
    private float score;
    private int postId;
    private String postName;

    public static Intent newIntent(Activity activity) {
        return new Intent(activity, TrainingPicAndSignActivity.class);
    }

    @Override
    protected void extractBundle() {
        employeeID = getIntent().getIntExtra("ID", -1);
        employeeName = getIntent().getStringExtra("NAME");
        empCode = getIntent().getStringExtra("CODE");
        score = getIntent().getFloatExtra("SCORE", -1);
        postId = getIntent().getIntExtra("POST_ID", -1);
        postName = getIntent().getStringExtra("POST_NAME");
    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            if (message.what == NavigationConstants.SAVE_SIGNATURE_AND_IMAGE)
                saveData((String) message.obj);
        });
    }

    @Override
    protected void onCreated() {
//        dataBase = MtrainerDataBase.getDatabase(getApplicationContext());
        binding.userName.setText(employeeName);
        binding.clickPhoto.setOnClickListener(v -> takephoto());
        binding.retake.setOnClickListener(v -> takephoto());
    }

    public void takephoto() {
        try {
            binding.takePhotoText.setVisibility(View.GONE);
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUriV2());
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException anfe) {
            showToast("Device doesn't support capturing images!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {
                mark();
                binding.retake.setVisibility(View.VISIBLE);
                binding.imagePreview.setVisibility(View.VISIBLE);
                binding.clickPhoto.setVisibility(View.INVISIBLE);

                attendancePhotoFile = image_path;
            }
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inDither = false;
            bfOptions.inPurgeable = true;
            bfOptions.inInputShareable = true;
            bfOptions.inTempStorage = new byte[32 * 1024];

            FileInputStream fs = null;
            Bitmap bm;

            try {
                fs = new FileInputStream(String.valueOf(attendancePhotoFile));
                if (fs != null) {
                    bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
                    binding.photoPreView.setImageBitmap(bm);
                }
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingPicAndSignatureViewModel) getAndroidViewModel(TrainingPicAndSignatureViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_training_picandsig;
    }

    private void saveData(String signaturePath) {
        try {
            if (attendancePhotoFile == null || attendancePhotoFile.isEmpty()) {
                showToast("Signature and Image both are needed");
            } else {
                AttendancePhotoEntity attendanceSignatureEntity = new AttendancePhotoEntity();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
                String signatureId = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + empCode + "_" + AttendanceConstants.ATTENDANCE_SIGNATURE + "_" + sdf.format(new Date());
                attendanceSignatureEntity.id = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + empCode + "_" + AttendanceConstants.ATTENDANCE_SIGNATURE;
                attendanceSignatureEntity.setAttendancePhotoURI(signaturePath);
                attendanceSignatureEntity.setAttendancePhotoId(signatureId);
                attendanceSignatureEntity.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
                attendanceSignatureEntity.postId = postId;
                attendanceSignatureEntity.pictureTypeId = 3;
                attendanceSignatureEntity.setStatus(AttendanceConstants.CANT_SYNCED);

                /*dataBase.getAttendancePhotoDao()
                        .insertAttendancePhoto(attendanceSignatureEntity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();*/

                AttendancePhotoEntity attendancePhotoEntity = new AttendancePhotoEntity();
                String photoId = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + empCode + "_" + AttendanceConstants.ATTENDANCE_PICTURE + "_" + sdf.format(new Date());
                attendancePhotoEntity.id = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + empCode + "_" + AttendanceConstants.ATTENDANCE_PICTURE;
                attendancePhotoEntity.setAttendancePhotoURI(attendancePhotoFile);
                attendancePhotoEntity.setAttendancePhotoId(photoId);
                attendancePhotoEntity.postId = postId;
                attendancePhotoEntity.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
                attendancePhotoEntity.pictureTypeId = 1;
                attendancePhotoEntity.setStatus(AttendanceConstants.CANT_SYNCED);
                showToast("Successfully added");

                /*dataBase.getAttendancePhotoDao()
                        .insertAttendancePhoto(attendancePhotoEntity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();*/

                AttendanceEntity attendanceEntity = new AttendanceEntity();
                attendanceEntity.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
                attendanceEntity.employeeId = employeeID;
                attendanceEntity.employeeName = employeeName;
                attendanceEntity.postName = postName;
                attendanceEntity.postId = postId;
                attendanceEntity.photoId = photoId;
                attendanceEntity.signatureId = signatureId;
                attendanceEntity.empCode = empCode;
                attendanceEntity.score = score;

                List<AttendancePhotoEntity> photoList = new ArrayList<>();
                photoList.add(attendanceSignatureEntity);
                photoList.add(attendancePhotoEntity);
                viewModel.saveTrainingPicAndSignToDB(photoList, attendanceEntity);

                Intent data = new Intent();
                data.putExtra("EMP_CODE", empCode);
                setResult(RESULT_OK, data);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private Uri getFileUri() {
        String imgcurTime = dateFormat.format(new Date());
        File imageDirectory = new File(GridViewDemo_ImagePath);
        imageDirectory.mkdirs();
        image_path = GridViewDemo_ImagePath + Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + empCode + "_" + AttendanceConstants.ATTENDANCE_PICTURE + "_" + imgcurTime + ".jpg";
        File file = new File(image_path);
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
    }*/

    private Uri getFileUriV2() {

        String rootPath = FileUtils.getRootPathV2(this, FolderNames.GridViewDemo);
        if (FileUtils.createOrExistsDir(rootPath)) {
            image_path = FileUtils.createFilePathV2(rootPath, Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + empCode + "_" + AttendanceConstants.ATTENDANCE_PICTURE);
            File file = FileUtils.getFileByPath(image_path);
            try {
                if (file != null && (file.exists() || file.createNewFile())) {
                    return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                }
            } catch (Throwable t) {
                Log.e("ERROR", "Error compressing file." + t);
            }
        }
        return null;
    }

    private void mark() {
//        Bitmap bm1 = null;
        Bitmap bmp1 = null;
        Bitmap src = decodeSampledBitmapFromFile(image_path, 720, 1080);

        try {
            Bitmap.Config config = src.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }

            bmp1 = Bitmap.createBitmap(src.getWidth(), src.getHeight(), config);

            Canvas canvas = new Canvas(bmp1);
            canvas.drawBitmap(src, 0, 0, null);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String watermark = Prefs.getString(PrefsConstants.EMPPLOYEE_NAME) + " " + sdf.format(new Date()) + " " + "Lat : " + Prefs.getDouble(PrefsConstants.LAT) + " " + "Long : " + Prefs.getDouble(PrefsConstants.LONGI);
            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setColor(Color.RED);
            paintText.setTextSize(16);
            paintText.setStyle(Paint.Style.FILL);
            Rect rect = new Rect();
            paintText.getTextBounds(watermark, 0, watermark.length(), rect);
            canvas.drawText(watermark, 10, src.getHeight() - 30, paintText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(image_path);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(image_path);
            bmp1.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }

        int expectedWidth = width / inSampleSize;
        if (expectedWidth > reqWidth) {
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}
