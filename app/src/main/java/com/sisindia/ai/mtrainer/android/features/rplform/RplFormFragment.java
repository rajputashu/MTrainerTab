package com.sisindia.ai.mtrainer.android.features.rplform;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.core.content.FileProvider;

import com.droidcommons.base.BaseFragment;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.commons.FolderNames;
import com.sisindia.ai.mtrainer.android.constants.AttendanceConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentRplFormBinding;
import com.sisindia.ai.mtrainer.android.db.entities.RplFormEntity;
import com.sisindia.ai.mtrainer.android.db.entities.RplFormPhotoEntity;
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardActivity;
import com.sisindia.ai.mtrainer.android.features.syncadapter.SyncAdapterInitialization;
import com.sisindia.ai.mtrainer.android.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RplFormFragment extends MTrainerBaseFragment {

    private FragmentRplFormBinding binding;
    private RplFormViewModel viewModel;
    ArrayList<String> client_category;
    ArrayAdapter<String> arrayAdapter;
    //    public static final String GridViewDemo_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RPLPhoto/";
//    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//    String Rpl_path, Rpl_path2;
    String imagefile, imagefile2;
    //    private MtrainerDataBase dataBase;
    final int CAMERA_CAPTURE_FRONT = 1;
    final int CAMERA_CAPTURE_BACK = 2;

    public static BaseFragment newInstance() {
        return new RplFormFragment();
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (RplFormViewModel) getAndroidViewModel(RplFormViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentRplFormBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {

    }

    @Override
    protected void onCreated() {
        ((DashBoardActivity) getContext()).hideRotaButton();
//        dataBase = MtrainerDataBase.getDatabase(getContext());
        client_category = new ArrayList<>();
        client_category.add("ST");
        client_category.add("SC");
        client_category.add("OBC");
        client_category.add("GENERAL");
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, client_category);
        binding.spinnerClientContact.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        binding.regnoEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.registrationNameET.setText("");
                // viewModel.callAPIToGetEmployeeDetails();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 7) {
                    //hideKeyboard();
                    viewModel.callAPIToGetEmployeeDetails();
                }
            }
        });
        binding.frontViewPhotoButton.setOnClickListener(view -> frontviewphoto());

        binding.backViewPhotoButton.setOnClickListener(view -> backviewphoto());

        binding.submitRplForm.setOnClickListener(view -> saverpldata());
    }

    public void frontviewphoto() {
        try {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUriV2(AttendanceConstants.RPL_PICTURE));
            startActivityForResult(captureIntent, CAMERA_CAPTURE_FRONT);
        } catch (ActivityNotFoundException anfe) {
            showToast("device doesn't support capturing images!");
        }
    }

    public void backviewphoto() {
        try {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUriV2(AttendanceConstants.RPL_PICTURE1));
            startActivityForResult(captureIntent, CAMERA_CAPTURE_BACK);
        } catch (ActivityNotFoundException anfe) {
            showToast("device doesn't support capturing images!");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CAMERA_CAPTURE_FRONT) {
//            if (requestCode == CAMERA_CAPTURE_FRONT) {
              /*  Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                String imgcurTime = dateFormat.format(new Date());
                File imageDirectory = new File(GridViewDemo_ImagePath);
                imageDirectory.mkdirs();
                Rpl_path = GridViewDemo_ImagePath + Prefs.getInt(PrefsConstants.ROTA_ID) + "_" +   "_"+ AttendanceConstants.RPL_PICTURE + "_"+ imgcurTime + ".png";

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(Rpl_path);
                    thePic.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.close();

                } catch (FileNotFoundException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
//                imagefile = Rpl_path;
//            }

            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inDither = false;
            bfOptions.inPurgeable = true;
            bfOptions.inInputShareable = true;
            bfOptions.inTempStorage = new byte[32 * 1024];
            FileInputStream fs = null;
            Bitmap bm;

            try {
                fs = new FileInputStream(String.valueOf(imagefile));
                bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
                binding.aadharFrontPhotoPreview.setImageBitmap(bm);
            } catch (IOException ignored) {

            } finally {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        } else
//            if (imagefile == null || imagefile.isEmpty())
        {
            binding.aadharFrontPhotoPreview.setVisibility(View.VISIBLE);
        }

        if (resultCode == RESULT_OK && requestCode == CAMERA_CAPTURE_BACK) {
//            if (requestCode == CAMERA_CAPTURE_BACK) {
               /* Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                String imgcurTime = dateFormat.format(new Date());
                File imageDirectory = new File(GridViewDemo_ImagePath);
                imageDirectory.mkdirs();
                Rpl_part1 = GridViewDemo_ImagePath + Prefs.getInt(PrefsConstants.ROTA_ID) + "_" +   "_"+ AttendanceConstants.RPL_PICTURE1 + "_"+ imgcurTime + ".png";


                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(Rpl_part1);
                    thePic.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.close();

                } catch (FileNotFoundException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
//                imagefile2 = Rpl_path2;
//            }

            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inDither = false;
            bfOptions.inPurgeable = true;
            bfOptions.inInputShareable = true;
            bfOptions.inTempStorage = new byte[32 * 1024];
            FileInputStream fs = null;
            Bitmap bm;
            try {
                fs = new FileInputStream(String.valueOf(imagefile2));
                if (fs != null || fs != null) {
                    bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
                    binding.aadharBackPhotoPreview.setImageBitmap(bm);
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

        } else {
//            if (imagefile2 == null || imagefile2.isEmpty()) {
            binding.aadharBackPhotoPreview.setVisibility(View.VISIBLE);
        }
    }

    public void saverpldata() {

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        String cdt = sdf1.format(new Date());
        if (imagefile == null || imagefile.isEmpty() || imagefile2 == null || imagefile2.isEmpty() || binding.regnoEdt.getText().toString().isEmpty() || binding.unitNameEdit.getText().toString().isEmpty() || binding.branchNameEdit.getText().toString().isEmpty()
                || binding.mobNumberedt.getText().toString().isEmpty() || binding.eductnQualifaction.getText().toString().isEmpty() || binding.eductnQualifaction.getText().toString().equals("") && imagefile2.equals("")
                || binding.registrationNameET.getText().toString().isEmpty()) {
            showToast("Please fill all details");
        } else {
            RplFormPhotoEntity attendancePhotoEntity = new RplFormPhotoEntity();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
            String photoId = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + "employeeID " + "_" + AttendanceConstants.RPL_PICTURE + "_" + sdf.format(new Date());
            attendancePhotoEntity.id = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + "employeeID" + "_" + AttendanceConstants.RPL_PICTURE;
            attendancePhotoEntity.setRplFormPhotoURI(imagefile);
            attendancePhotoEntity.setRplFormPhotoId(photoId);
            attendancePhotoEntity.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
            attendancePhotoEntity.pictureTypeId = 6;
            attendancePhotoEntity.setStatus(AttendanceConstants.NOT_SYNCED);

            //2nd photo
            RplFormPhotoEntity attendancePhotoEntity1 = new RplFormPhotoEntity();
            String photoId1 = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + "employeeID " + "_" + AttendanceConstants.RPL_PICTURE1 + "_" + sdf.format(new Date());
            attendancePhotoEntity1.id = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + "employeeID " + "_" + AttendanceConstants.RPL_PICTURE1;
            attendancePhotoEntity1.setRplFormPhotoURI(imagefile2);
            attendancePhotoEntity1.setRplFormPhotoId(photoId1);
            attendancePhotoEntity1.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
            attendancePhotoEntity1.pictureTypeId = 6;
            attendancePhotoEntity1.setStatus(AttendanceConstants.NOT_SYNCED);

            RplFormEntity rplForm = new RplFormEntity();
            rplForm.setRotaid(String.valueOf(Prefs.getInt(PrefsConstants.ROTA_ID)));
            rplForm.setRegNo(binding.regnoEdt.getText().toString());
            rplForm.setUnitName(binding.unitNameEdit.getText().toString());
            rplForm.setBranchName(binding.branchNameEdit.getText().toString());
            rplForm.setMobilebNo(binding.mobNumberedt.getText().toString());
            rplForm.setEducationalQualification(binding.eductnQualifaction.getText().toString());
            rplForm.setPhotoid1(photoId);
            rplForm.setPhotoid2(photoId1);
            rplForm.setImagepath1(imagefile);
            rplForm.setImagepath2(imagefile2);
            rplForm.setSyncdatetime(cdt);
            rplForm.setSiteId(Prefs.getString(PrefsConstants.SITE_ID));
            rplForm.setBranchId(Prefs.getString(PrefsConstants.BRANCH_ID));
            rplForm.setCategory(binding.spinnerClientContact.getSelectedItem().toString());
            rplForm.setStatus("0");
            rplForm.setRegistrationNameET(binding.registrationNameET.getText().toString());

            List<RplFormPhotoEntity> photoList = new ArrayList<>();
            photoList.add(attendancePhotoEntity);
            photoList.add(attendancePhotoEntity1);
            viewModel.saveRPLDataToDB(photoList, rplForm);

            /*dataBase.getRplFormPhotoDao().insertRplFormPhoto(attendancePhotoEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

            dataBase.getRplFormPhotoDao().insertRplFormPhoto(attendancePhotoEntity1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

            dataBase.getRplFormDao().insertRplFormValues(rplForm)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();*/

            Bundle b = new Bundle();
            SyncAdapterInitialization syncAdapterInitialization = new SyncAdapterInitialization(getContext());
            syncAdapterInitialization.startForceSyncing(b);
            showToast("Saved RplForm Details ");

            binding.regnoEdt.setText("");
            binding.unitNameEdit.setText("");
            binding.branchNameEdit.setText("");
            binding.mobNumberedt.setText("");
            binding.eductnQualifaction.setText("");
            binding.aadharFrontPhotoPreview.setImageBitmap(null);
            binding.aadharBackPhotoPreview.setImageBitmap(null);
            binding.aadharFrontPhotoPreview.setVisibility(View.INVISIBLE);
            getFragmentManager().popBackStack();
        }
    }

    private Uri getFileUriV2(int rplPictureTypeId) {

        String rootPath = FileUtils.getRootPathV2(requireActivity(), FolderNames.RPLPhoto);
        if (FileUtils.createOrExistsDir(rootPath)) {
            String _path = FileUtils.createFilePathV2(rootPath, Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + rplPictureTypeId);
            if (rplPictureTypeId == AttendanceConstants.RPL_PICTURE)
                imagefile = _path;
            else if (rplPictureTypeId == AttendanceConstants.RPL_PICTURE1)
                imagefile2 = _path;
            File file = FileUtils.getFileByPath(_path);
            try {
                if (file != null && (file.exists() || file.createNewFile())) {
                    return FileProvider.getUriForFile(requireActivity(),
                            BuildConfig.APPLICATION_ID + ".provider", file);
                }
            } catch (Throwable t) {
                Log.e("ERROR", "Error compressing file." + t);
            }
        }
        return null;
    }

    /*private Uri getFileUri() {
        String imgcurTime = dateFormat.format(new Date());
        File imageDirectory = new File(GridViewDemo_ImagePath);
        imageDirectory.mkdirs();
        Rpl_path = GridViewDemo_ImagePath + Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + "_" + AttendanceConstants.RPL_PICTURE + "_" + imgcurTime + ".jpg";
        File file = new File(Rpl_path);
        return FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
    }

    private Uri getFileUriBack() {
        String imgcurTime = dateFormat.format(new Date());
        File imageDirectory = new File(GridViewDemo_ImagePath);
        imageDirectory.mkdirs();
        Rpl_path2 = GridViewDemo_ImagePath + Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + "_" + AttendanceConstants.RPL_PICTURE1 + "_" + imgcurTime + ".jpg";
        File file = new File(Rpl_path2);
        return FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
    }*/

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_rpl_form;
    }
}
