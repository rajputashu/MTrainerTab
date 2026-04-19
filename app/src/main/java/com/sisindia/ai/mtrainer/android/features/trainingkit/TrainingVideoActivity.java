package com.sisindia.ai.mtrainer.android.features.trainingkit;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.utils.Utils;

import java.io.File;

public class TrainingVideoActivity extends AppCompatActivity implements OnPreparedListener {

    VideoView videoView;
    String video_name, videoId;
    Uri video;
    Utils mUtils;
    int position = 0;
    Runnable timerRunnable;
    Handler timerHandler;

//    private final String lastMessage = "";
//    private ServerService mBoundService;
//    protected int selectedIndex;
//    WebView webView;
    //    protected VideoControls defaultControls;
    // ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_video);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mUtils = new Utils(TrainingVideoActivity.this);

//        MtrainerDataBase dataBase = MtrainerDataBase.getDatabase(getApplicationContext());
//        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        String videoDuration = getIntent().getStringExtra("duration");

//        final Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            //Do something after 100ms
//            startServer(mHandler, documentRoot, Integer.valueOf(port),videoDuration);
//        }, 500);

//        assert wm != null;
//        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//        tempurl = "http://" + ip + ":" + "8082" + "/";

        videoView = findViewById(R.id.videostraining);
        timerHandler = new Handler();

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (position <= Integer.parseInt(videoDuration)) {
                    position = position + 1;
                    Log.e("position", String.valueOf(position));
                    timerHandler.postDelayed(this, 1000);
                } else {
                    timerHandler.removeCallbacks(timerRunnable);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
        //resetPlayer();
//        progressBar = findViewById(R.id.progressbar);
//
//        progressBar.setVisibility(View.VISIBLE);
//        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        assert wm != null;
//        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//        String tempurl = "http://" + ip + ":" + "8082" + "/";
        videoView.setOnPreparedListener(this);
        if (getIntent() != null) {
            video_name = getIntent().getStringExtra("videoname").replaceAll("\\\\", "/");
            videoId = getIntent().getStringExtra("videoId");
        }
//        MediaController vidControl = new MediaController(this);

        if (video_name != null) {

            String documentRoot = mUtils.getDocRoot();
            File file = new File(documentRoot + video_name);
            Log.e("path332", file.getPath());
            if (file.exists()) {
                video = Uri.parse(mUtils.getIpAddressAndUrl() + video_name);
                videoView.setMedia(video);
//                videoView.setVideoURI(video);
//                Log.e("pos", String.valueOf(videoView.getCurrentPosition()));
            } else {
                new Utils(TrainingVideoActivity.this).showAlert("File not found");
            }
            // video = Uri.parse(tempurl + "LearningMantra-MTRAINER/PHYSICALTRAINING/6Nishkash.mp4");
            // video = Uri.parse("/storage/emulated/0/MTrainerVideo/" + video_name.trim() + ".mp4");
        } else {
            new Utils(TrainingVideoActivity.this).showAlert("File not found");
            // video =Uri.parse(tempurl+video_name);
            // video = Uri.parse(tempurl + "LearningMantra-MTRAINER/PHYSICALTRAINING/6Nishkash.mp4");
            //video = Uri.parse("/storage/emulated/0/MTrainerVideo/Test.mp4");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a");
//        String strDate = sdf.format(c.getTime());
        // dataBase.getMtrainerVideoDao().insertVideoClick(video_name, "1", String.valueOf(convertToMilli(position)),strDate, strDate, Prefs.getString(PrefsConstants.ROTA_ID),videoId);
//        Log.e("playing", timeConversion(position));
        timerHandler.removeCallbacksAndMessages(null);
    }
//

    //resetting player content
    /*private void resetPlayer() {
        if (videoView != null) {
            videoView.reset();
            videoView.stopPlayback();
        }
    }*/

    @Override
    public void onPrepared() {
        videoView.start();
    }


}
