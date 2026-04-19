package com.sisindia.ai.mtrainer.android.features.topicslist;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.VIDEO_PLAY;
import static com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicMapper.mapToDbModel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingCoursesBinding;
import com.sisindia.ai.mtrainer.android.features.videoscormplayer.VideoScorm;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;
import com.sisindia.ai.mtrainer.android.models.online.TrainingTopicDataResponseMO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

public class TrainingTopicsActivityV2 extends MTrainerBaseActivity {

    private ActivityTrainingCoursesBinding binding;
    private TrainingTopicsViewmodel viewModel;

    // -------------------------------------------------------------------------
    // This field is intentionally NOT reset in onResume() because V2 always
    // calls finish() before launching VideoScorm. That means this Activity is
    // fully destroyed and recreated fresh every time — so the field always
    // starts as null on each new instance. No stale-guard issue exists here.
    //
    // The null-guard in the LiveData observer only protects against a rapid
    // double-tap on the same card within the same Activity instance lifetime.
    // -------------------------------------------------------------------------
    private TrainingTopicsDataModel trainingTopicsDataModel;

    private DownloadAndUnzipTask currentTask;

    // -------------------------------------------------------------------------
    // Base-class boilerplate
    // -------------------------------------------------------------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_training_courses;
    }

    @Override
    protected void extractBundle() { /* no bundle required */ }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingTopicsViewmodel) getAndroidViewModel(TrainingTopicsViewmodel.class);
    }

    @Override
    protected void initViewBinding() {
        binding = (ActivityTrainingCoursesBinding) bindActivityView(this, getLayoutResource());
        binding.setVm(viewModel);
    }

    @Override
    protected void onCreated() {
        viewModel.getTopicList();
        binding.backtodashbtn.setOnClickListener(v -> finish());
    }

    // -------------------------------------------------------------------------
    // LiveData + search wiring
    // -------------------------------------------------------------------------

    @Override
    protected void initViewState() {

        liveData.observe(this, message -> {

            // Ignore unrelated LiveData messages
            if (message.what != VIDEO_PLAY) return;

            // Double-tap guard — only valid within a single Activity instance.
            // Safe here because finish() destroys this instance before VideoScorm
            // launches, so on return this Activity is always a fresh instance
            // with trainingTopicsDataModel == null.
            if (trainingTopicsDataModel != null) return;

            TrainingTopicDataResponseMO apiItem = (TrainingTopicDataResponseMO) message.obj;
            trainingTopicsDataModel = mapToDbModel(apiItem);
            handleContent();
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

    // -------------------------------------------------------------------------
    // NOTE: No onActivityResult / onResume reset needed.
    //
    // Why? Because finish() is called before every VideoScorm launch (both
    // openVideo and openScorm). This fully destroys V2. When the user presses
    // back inside VideoScorm, the system recreates V2 fresh (new instance,
    // trainingTopicsDataModel = null, getTopicList() called in onCreated).
    //
    // This is the intentional architecture to avoid CordovaActivity's back-stack
    // bug that caused V2 to reload twice on back press.
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Routing
    // -------------------------------------------------------------------------

    private void handleContent() {
        if ("Video".equalsIgnoreCase(trainingTopicsDataModel.getCourseContentType())) {
            openVideo();
        } else {
            handleScorm();
        }
    }

    private void openVideo() {
        Intent intent = new Intent(this, VideoScorm.class);
        intent.putExtra("trainingTopic", trainingTopicsDataModel);
        startActivity(intent);
        finish(); // Remove V2 from back stack to prevent CordovaActivity back-stack bug
    }

    // -------------------------------------------------------------------------
    // SCORM cache logic
    // -------------------------------------------------------------------------

    private void handleScorm() {

        String fileUrl = trainingTopicsDataModel.getFileURL();

        if (fileUrl == null || fileUrl.isEmpty()) {
            Timber.e("handleScorm: fileURL is null or empty");
            Toast.makeText(this, "Content URL is missing", Toast.LENGTH_SHORT).show();
            // Reset guard so user can retry without restarting the app
            trainingTopicsDataModel = null;
            return;
        }

        String[] parts = fileUrl.split("/");
        String folderName = parts[parts.length - 1].replace(".zip", "");
        String folderPath = getDownloadedFolderPath(this) + "/" + folderName;
        File folder = new File(folderPath);

        // Safety-check: delete corrupted (empty) folder so it re-downloads cleanly
        if (folder.exists()) {
            String[] contents = folder.list();
            if (contents == null || contents.length == 0) {
                Timber.w("handleScorm: folder exists but is empty/corrupted — deleting");
                deleteRecursive(folder);
            }
        }

        if (!folder.exists()) {
            Timber.d("handleScorm: folder not found — starting download");
            currentTask = new DownloadAndUnzipTask(this);
            currentTask.execute(fileUrl + "?" + Prefs.getString(PrefsConstants.SAS_TOKEN));
        } else {
            Timber.d("handleScorm: folder found — opening directly");
            openScorm(folderPath);
        }
    }

    private void openScorm(String path) {
        Intent intent = new Intent(this, VideoScorm.class);
        intent.putExtra("trainingTopic", trainingTopicsDataModel);
        intent.putExtra("scromdownloadedurl", path);
        startActivity(intent);
        finish(); // Remove V2 from back stack to prevent CordovaActivity back-stack bug
    }

    /** Recursively deletes a file or directory. */
    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) deleteRecursive(child);
            }
        }
        file.delete();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    public static String getDownloadedFolderPath(Context context) {
        return context.getExternalFilesDir(".mTrainer/Downloaded").toString();
    }

    // -------------------------------------------------------------------------
    // AsyncTask — Download + Unzip
    // -------------------------------------------------------------------------

    private class DownloadAndUnzipTask extends AsyncTask<String, Integer, String> {

        private final Dialog dialog;
        private final ProgressBar progressBar;
        private final TextView percentage;
        private final TextView percentageTextView;

        DownloadAndUnzipTask(Context context) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_progress_dialo);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            progressBar = dialog.findViewById(R.id.progressBar);
            percentage = dialog.findViewById(R.id.percentagetext);
            percentageTextView = dialog.findViewById(R.id.percentagetext);

            ImageView cancel = dialog.findViewById(R.id.cancelButton);
            cancel.setOnClickListener(v -> {
                cancel(true);
                if (dialog.isShowing()) dialog.dismiss();
                // Reset guard so user can tap a different card after cancelling
                trainingTopicsDataModel = null;
            });
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                connection.setInstanceFollowRedirects(true);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Timber.e("Download failed — HTTP %d", responseCode);
                    return null;
                }

                int fileLength = connection.getContentLength();
                boolean hasValidLength = fileLength > 0;

                InputStream input = connection.getInputStream();

                String[] parts = trainingTopicsDataModel.getFileURL().split("/");
                String fileName = parts[parts.length - 1];
                String downloadDir = getDownloadedFolderPath(TrainingTopicsActivityV2.this);

                // Ensure the download directory exists before writing
                new File(downloadDir).mkdirs();

                String filePath = downloadDir + "/" + fileName;
                FileOutputStream output = new FileOutputStream(filePath);

                byte[] buffer = new byte[8192];
                int total = 0, count;

                while ((count = input.read(buffer)) != -1) {
                    if (isCancelled()) {
                        output.close();
                        input.close();
                        new File(filePath).delete(); // clean up partial download
                        return null;
                    }
                    total += count;
                    if (hasValidLength) {
                        int progress = Math.max(0, Math.min((int) ((total * 100L) / fileLength), 100));
                        publishProgress(progress);
                    } else {
                        publishProgress(-1); // indeterminate — no Content-Length header
                    }
                    output.write(buffer, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                return filePath;

            } catch (Exception e) {
                Timber.e(e, "DownloadAndUnzipTask doInBackground error");
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            if (progress < 0) {
                percentageTextView.setText("Downloading...");
            } else {
                progressBar.setProgress(progress);
                percentage.setText(progress + "%");
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) dialog.dismiss();

            if (isCancelled()) return;

            if (result == null || result.isEmpty()) {
                // Download failed — show feedback and reset guard so user can retry
                Timber.e("Download failed — result is null");
                Toast.makeText(TrainingTopicsActivityV2.this,
                        "Download failed. Please try again.", Toast.LENGTH_SHORT).show();
                trainingTopicsDataModel = null; // allow retry tap
                return;
            }

            // Unzip then open SCORM.
            // openScorm() calls finish() so V2 is removed from back stack here too.
            try {
                File zipFile = new File(result);
                File targetDir = new File(result.replace(".zip", ""));
                unzip(zipFile, targetDir);
                openScorm(targetDir.getAbsolutePath());

            } catch (Exception e) {
                // Unzip failed — show feedback and reset guard so user can retry
                Timber.e(e, "Unzip failed");
                Toast.makeText(TrainingTopicsActivityV2.this,
                        "Failed to extract content. Please try again.", Toast.LENGTH_SHORT).show();
                trainingTopicsDataModel = null; // allow retry tap
            }
        }
    }

    // -------------------------------------------------------------------------
    // Unzip utility — pure I/O only, no Intent launching inside.
    // Intent is fired by onPostExecute() after this returns cleanly.
    // This fixes the original's anti-pattern of launching Activities from
    // inside a utility method's finally block.
    // -------------------------------------------------------------------------
    private void unzip(File zipFile, File targetDir) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(targetDir, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    new File(file.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[8192];
                    int len;
                    try {
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    } finally {
                        fos.close();
                    }
                }
                zis.closeEntry();
            }
        } finally {
            zis.close();
        }
        // No Intent here — openScorm() is called by onPostExecute() after this returns
    }

    // -------------------------------------------------------------------------
    // Lifecycle — cancel download task cleanly on exit
    // -------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        try {
            if (currentTask != null && !currentTask.isCancelled()) {
                currentTask.cancel(true);
                Timber.d("onBackPressed: download task cancelled");
            }
        } catch (Exception e) {
            Timber.e(e, "Error cancelling task in onBackPressed");
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        try {
            if (currentTask != null && !currentTask.isCancelled()) {
                currentTask.cancel(true);
                Timber.d("onDestroy: download task cancelled");
            }
        } catch (Exception e) {
            Timber.e(e, "Error cancelling task in onDestroy");
        }
        super.onDestroy();
    }
}