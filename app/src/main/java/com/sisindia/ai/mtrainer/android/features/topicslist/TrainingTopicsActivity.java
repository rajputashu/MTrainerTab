package com.sisindia.ai.mtrainer.android.features.topicslist;

import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.TRAINING_TOPICS_ACTIVIY;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingCoursesBinding;
import com.sisindia.ai.mtrainer.android.features.trainingprogram.TrainingProgramActivity;
import com.sisindia.ai.mtrainer.android.features.videoscormplayer.VideoScorm;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;
import com.sisindia.ai.mtrainer.android.models.online.TrainingTopicDataResponseMO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TrainingTopicsActivity extends MTrainerBaseActivity {
    ActivityTrainingCoursesBinding binding;
    TrainingTopicsDataModel trainingTopicsDataModel;
    //    TrainingTopicDataResponseMO trainingTopicsDataModel1;
    private boolean isDownloading = false;

//    File videoFile;
    //private ProgressDialog progressDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_training_courses;
    }

    private TrainingTopicsViewmodel viewModel;

    @Override
    protected void extractBundle() {
//        TrainingTopicsBodyMO trainingCoursesModel = Objects.requireNonNull(getIntent().getExtras()).getParcelable(SELECTED_TRAINING_TOPICS_BODY_MO);
//        viewModel.topicBodyMO.set(trainingCoursesModel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == TRAINING_TOPICS_ACTIVIY) {
            if (resultCode == Activity.RESULT_OK) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    //Do something after 100ms
                    viewModel.getTopicList();
                }, 2000);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initViewState() {

        liveData.observe(this, message -> {
            /*TrainingTopicsDataModel trainingTopicsDataModel = (TrainingTopicsDataModel) message.obj;
            //if(trainingTopicsDataModel.isOffline==1){
            trainingTopicsDataModel1 = trainingTopicsDataModel;*/
            TrainingTopicDataResponseMO apiItem = (TrainingTopicDataResponseMO) message.obj;
            trainingTopicsDataModel = TrainingTopicMapper.mapToDbModel(apiItem);

          /*  if (trainingTopicsDataModel.getCourseContentType().equals("Video")) {
                Log.d("MP4URL",trainingTopicsDataModel1.getFileURL());
                String videoLocalPath = getDownloadedMp4FolderPath(TrainingTopicsActivity.this) + "/" + getFileNameFromUrl(trainingTopicsDataModel1.getFileURL());
               File videoFile = new File(videoLocalPath);
                Log.d("VIDEOFILEPATHE", videoFile.getPath());
                if (!videoFile.exists()) {
                    Log.d("MP$VIDEOURl", trainingTopicsDataModel1.getFileURL());
                    new DownloadMP4VideoTask(TrainingTopicsActivity.this).execute(trainingTopicsDataModel1.getFileURL()+"?"+ Prefs.getString(PrefsConstants.SAS_TOKEN));
                }
                else{

                    Log.d("MP4VIDEDOWNLOADED", "ALREADY DOWNLOADED");
                  //  Log.d("MP$VIDEOURl",new File(zipDownloadLocalPath).getAbsolutePath());
                    Intent intent = new Intent(TrainingTopicsActivity.this, VideoScorm.class);
                    Bundle bundle =  new Bundle();
                    bundle.putParcelable("trainingTopic",(Parcelable) trainingTopicsDataModel);
                    bundle.putString("VIdeoMP4url", new File(videoLocalPath).getAbsolutePath());
                    intent.putExtras(bundle);
                    activityResultLauncher.launch(intent);

                    // startActivityForResult(intent,REQUEST_CODE);
                }
            }*/
            if (trainingTopicsDataModel.getCourseContentType().equals("Video")) {
//                    Log.d("MP$VIDEOURl", trainingTopicsDataModel1.getFileURL());
                Intent intent = new Intent(TrainingTopicsActivity.this, VideoScorm.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("trainingTopic", trainingTopicsDataModel);
                intent.putExtras(bundle);
                startActivityForResult(intent, TRAINING_TOPICS_ACTIVIY);
                // activityResultLauncher.launch(intent);

            } else {
//                String[] parts = trainingTopicsDataModel1.getFileURL().split("/");
                String[] parts = trainingTopicsDataModel.getFileURL().split("/");
                String zipDownloadLocalPath = getDownloadedFolderPath(TrainingTopicsActivity.this) + "/" + parts[parts.length - 1].replace(".zip", "");

                if (!new File(zipDownloadLocalPath).exists()) {
                    new DownloadAndUnzipTask(TrainingTopicsActivity.this).execute(trainingTopicsDataModel.getFileURL() + "?" + Prefs.getString(PrefsConstants.SAS_TOKEN));
                } else {
//                    Log.d("MP$VIDEOURl", new File(zipDownloadLocalPath).getAbsolutePath());
                    Intent intent = new Intent(TrainingTopicsActivity.this, VideoScorm.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("trainingTopic", trainingTopicsDataModel);
                    bundle.putString("scromdownloadedurl", new File(zipDownloadLocalPath).getAbsolutePath());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, TRAINING_TOPICS_ACTIVIY);
                    // startActivityForResult(intent,REQUEST_CODE);
                }
            }

            /*}else {
                Intent intent = new Intent(TrainingTopicsActivity.this, VideoScorm.class);
                Bundle bundle =  new Bundle();
                bundle.putParcelable("trainingTopic",(Parcelable) trainingTopicsDataModel);
                intent.putExtras(bundle);
                startActivity(intent);
            }*/

        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.filterTopics(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.filterTopics(newText);
                return true;
            }
        });
    }

    @Override
    protected void onCreated() {
        viewModel.getTopicList();
        binding.backtodashbtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, TrainingProgramActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void initViewBinding() {
        binding = (ActivityTrainingCoursesBinding) bindActivityView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingTopicsViewmodel) getAndroidViewModel(TrainingTopicsViewmodel.class);
    }

    public class DownloadAndUnzipTask extends AsyncTask<String, Integer, String> {
        private final Dialog progressDialog;
        ProgressBar progressBar;
        TextView textViewProgress;
        TextView percentageTextView;
        private Context context;

        public DownloadAndUnzipTask(Context context) {
            this.context = context;
            progressDialog = new Dialog(context);
            progressDialog.setContentView(R.layout.custom_progress_dialo);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            percentageTextView = progressDialog.findViewById(R.id.percentagetext);

            textViewProgress = progressDialog.findViewById(R.id.textViewProgress);
            textViewProgress.setText("Downloading...");

            progressBar = progressDialog.findViewById(R.id.progressBar);
            progressBar.setMax(100);

            ImageView cancelButton = progressDialog.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(view -> {
                isDownloading = false;
                progressDialog.dismiss();
                cancel(true);
            });

           /*
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIcon(R.drawable.close);*/

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

         /*  progressDialog.setMessage("Downloading...");


//            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialo, null);
//            progressDialog.setView(dialogView);


            Button cancelButton = progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (cancelButton != null) {
                cancelButton.setTextColor(getResources().getColor(android.R.color.black)); // Change to the color you prefer
            }

            progressDialog.setButton(progressDialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Cancel download task
                    isDownloading = false;
                    progressDialog.dismiss();
                    cancel(true);
                }
            });*/

            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                isDownloading = true;
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    int fileLength = connection.getContentLength();
                    InputStream input = new BufferedInputStream(url.openStream());
                    String zipDownloadLocalPath = null;// getDownloadedFolderPath(context) + File.separator + trainingTopicsDataModel1.courseTopicTitle;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        String[] parts = trainingTopicsDataModel.getFileURL().split("/");
                        zipDownloadLocalPath = getDownloadedFolderPath(TrainingTopicsActivity.this) + "/" + parts[parts.length - 1];
                    }

                    FileOutputStream output = new FileOutputStream(zipDownloadLocalPath);
                    byte[] data = new byte[1024];
                    int total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress((int) ((total * 100) / fileLength));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    return zipDownloadLocalPath;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            if (values[0] >= progressBar.getProgress()) {
                percentageTextView.setText(values[0] + "%");
            } else {
                percentageTextView.setText("");
                textViewProgress.setText("Please wait..");
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != "") {
                // File downloaded successfully
                // Now you can proceed to unzip the file
                Log.d("resultscorm", result);
                //new UnzipTask().execute(result);
                // String destDirectory = Environment.getExternalStorageDirectory().getPath() + "/"+trainingTopicsDataModel1.getFileURL().split("/")[trainingTopicsDataModel1.getFileURL().split("/").length-1].split("\\.")[0];
                try {
                    File destDir = new File(result.replace(".zip", ""));
                    Log.d("resultscorm", destDir.getPath());
                    percentageTextView.setText("");
                    textViewProgress.setText("Unzipping Please wait..");
                    unzip(new File(result), destDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Handle download failure
            }
            progressDialog.dismiss();
            isDownloading = false;

        }

/*
    @Override
    public void onBackPressed() {
        if(progressDialog.isShowing()){
            isDownloading = false;
            progressDialog.dismiss();
            progressDialog.cancel();

        }
        else{
            super.onBackPressed();
        }
    }*/
    }

    public void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[1024];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
            Intent intent = new Intent(TrainingTopicsActivity.this, VideoScorm.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("trainingTopic", trainingTopicsDataModel);
            bundle.putString("scromdownloadedurl", targetDirectory.getAbsolutePath());
            intent.putExtras(bundle);
            //activityResultLauncher.launch(intent);
            startActivityForResult(intent, TRAINING_TOPICS_ACTIVIY);
        }
    }


    public static String getDownloadedFolderPath(Context context) {
        return context.getExternalFilesDir(File.separator + ".mTrainer" + File.separator + "Downloaded").toString();
    }


    public static String getSelectedLanguageCode(Context context) {
        String selectedLangCode = "";
        SharedPreferences preferences = context.getSharedPreferences("Language",
                Context.MODE_PRIVATE);
        selectedLangCode = preferences.getString("LanguageCode", null);
        if (selectedLangCode == null)
            selectedLangCode = "en";

        return selectedLangCode;
    }
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(TrainingTopicsActivity.this,TrainingCoursesActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        super.onBackPressed();
//    }
}


