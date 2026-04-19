package com.sisindia.ai.mtrainer.android.features.trainingkit;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.Constant;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.SlideModuleEntity;
import com.sisindia.ai.mtrainer.android.features.server.ServerService;
import com.sisindia.ai.mtrainer.android.utils.OnSwipeTouchListener;
import com.sisindia.ai.mtrainer.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

//this class using to play ppt content
public class TrainingSlidesActivity extends AppCompatActivity {
    ImageView back;
    ImageView img1, img2;
    WebView img;
    TextView slide_number, course_name;
    List<SlideModuleEntity.SlideModuleEntityList> videoDetailsModelList;
    int i = 0;
    String name, id, submoduleno;
    String duration;
    private String documentRoot;
    Utils mUtils;

    private ServerService mBoundService;

    Handler mhandler;

    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_slides);
        MtrainerDataBase dataBase = MtrainerDataBase.getDatabase(getApplicationContext());
        back = findViewById(R.id.toolBarBackButton);

        if (Utils.isTablet(TrainingSlidesActivity.this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mUtils = new Utils(TrainingSlidesActivity.this);

        try {
            mhandler = new Handler();
            img = findViewById(R.id.leave_img);
            img1 = findViewById(R.id.leave_left);
            img2 = findViewById(R.id.leave_right);
            slide_number = findViewById(R.id.slide_number);
            course_name = findViewById(R.id.course_name);

            documentRoot = mUtils.getDocRoot();

            videoDetailsModelList = new ArrayList<>();
            if (getIntent() != null) {
                id = getIntent().getStringExtra("id");
                course_name.setText(getIntent().getStringExtra("videoname"));
                submoduleno = getIntent().getStringExtra("submodule");
                duration = getIntent().getStringExtra("duration");
            }
            videoDetailsModelList = dataBase.getSlideModuleDao().getSlides(submoduleno, id);
            slide_number.setText("1 OF " + videoDetailsModelList.size() + " SLIDES");
            img.getSettings().setJavaScriptEnabled(true);
            img.setVerticalScrollBarEnabled(true);
            // if size is one showing alert as no ppt present
            if (videoDetailsModelList.size() == 1) {
                img1.setVisibility(View.INVISIBLE);
                img2.setVisibility(View.INVISIBLE);
                Log.e("path", mUtils.getIpAddressAndUrl() + videoDetailsModelList.get(0).getPath());
                mhandler.postDelayed(() -> {
                    //Do something after 100ms
                    loadImage(mUtils.getIpAddressAndUrl() + videoDetailsModelList.get(0).getPath());
                }, 500);
                new Utils(TrainingSlidesActivity.this).showAlert("Content not found");
                //checking whether image exist or not
            } else if (videoDetailsModelList.isEmpty() || !videoDetailsModelList.get(0).getPath().contains(".")) {
                //progressDialog.dismiss();
                Toast.makeText(TrainingSlidesActivity.this, "No Image", Toast.LENGTH_SHORT).show();
                new Utils(getApplicationContext()).showAlert("Content not found");
            } else {
                img1.setVisibility(View.VISIBLE);//forward button
                img2.setVisibility(View.VISIBLE);//backward button
                i = 0;
                mhandler.postDelayed(() -> {
                    //Do something after 100ms
                    loadImage(mUtils.getIpAddressAndUrl() + videoDetailsModelList.get(0).getPath());
                }, 500);

                if (i == 0) {
                    img1.setVisibility(View.INVISIBLE); //disabling forward
                }
                //handling swipe behavior to load next content
                img.setOnTouchListener(new OnSwipeTouchListener(TrainingSlidesActivity.this) {
                    public void onSwipeTop() {

                        // Toast.makeText(TrainingSlidesActivity.this, "top", Toast.LENGTH_SHORT).show();
                    }

                    public void onSwipeRight() {
                        //  Toast.makeText(TrainingSlidesActivity.this, "right", Toast.LENGTH_SHORT).show();
                        showNextImage();
                    }

                    public void onSwipeLeft() {
                        showPreviousImage();
                        //Toast.makeText(TrainingSlidesActivity.this, "left", Toast.LENGTH_SHORT).show();
                    }

                    public void onSwipeBottom() {
                        //  Toast.makeText(TrainingSlidesActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                    }
                });
                //handling forward actions
                img1.setOnClickListener(v -> {
                    if (i < videoDetailsModelList.size()) {
                        img2.setVisibility(View.VISIBLE);
                        i = i - 1;
                        int j = i + 1;
                        slide_number.setText(j + " OF " + videoDetailsModelList.size() + " SLIDES");
                        loadImage(mUtils.getIpAddressAndUrl() + videoDetailsModelList.get(i).getPath());

                    } else {
                        Toast.makeText(TrainingSlidesActivity.this, "No Image", Toast.LENGTH_SHORT).show();
                    }

                    if (i == 0) {
                        img1.setVisibility(View.INVISIBLE);
                        img2.setVisibility(View.VISIBLE);
                    }

                });

                img2.setOnClickListener(v -> {
                    if (i < videoDetailsModelList.size()) {
                        i = i + 1;
                        int j = i + 1;
                        slide_number.setText(j + " OF " + videoDetailsModelList.size() + " SLIDES");
                        img1.setVisibility(View.VISIBLE);
                        Log.e("url", "i" + i);
                        loadImage(mUtils.getIpAddressAndUrl() + videoDetailsModelList.get(i).getPath());

                    } else {
                        Toast.makeText(TrainingSlidesActivity.this, "No Image", Toast.LENGTH_SHORT).show();
                    }

                    if (i == videoDetailsModelList.size() - 1) {
                        img2.setVisibility(View.INVISIBLE);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        stopServer();
    }

    public void showPreviousImage() {
        if (i < videoDetailsModelList.size()) {
            i = i + 1;
            int j = i + 1;
            slide_number.setText(j + " OF " + videoDetailsModelList.size() + " SLIDES");
            img1.setVisibility(View.VISIBLE);
            Log.e("url", "i" + i);
            loadImage(mUtils.getIpAddressAndUrl() + videoDetailsModelList.get(i).getPath());

        } else {
            Toast.makeText(TrainingSlidesActivity.this, "No Image", Toast.LENGTH_SHORT).show();
        }
        if (i == videoDetailsModelList.size() - 1) {

            img2.setVisibility(View.INVISIBLE);
        }
    }

    public void showNextImage() {
        if (i < videoDetailsModelList.size()) {

            img2.setVisibility(View.VISIBLE);
            i = i - 1;
            int j = i + 1;
            slide_number.setText(j + " OF " + videoDetailsModelList.size() + " SLIDES");
            loadImage(mUtils.getIpAddressAndUrl() + videoDetailsModelList.get(i).getPath());
        } else {
            Toast.makeText(TrainingSlidesActivity.this, "No Image", Toast.LENGTH_SHORT).show();
        }
        if (i == 0) {
            img1.setVisibility(View.INVISIBLE);
            img2.setVisibility(View.VISIBLE);
        }
    }

    public void loadImage(String url) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        //   Display display = getWindowManager().getDefaultDisplay();
//        int width = display.getWidth();
//        int height = display.getHeight();
        String data = "<html><head></head>";
        data = data + "<body><img width='100%' height='96%' src=\"" + url + "\" /></body></html>";
        img.loadData(data, "text/html", null);
    }

    private void doUnbindService() {
        if (mBoundService != null) {
            unbindService(mConnection);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startServer(Handler handler, String documentRoot, int port) {
        if (mBoundService == null) {

        } else {
            mBoundService.startServer(handler, documentRoot, port);
        }
    }

    private void stopServer() {
        if (mBoundService == null) {
            Log.e("Exception 1", "stopped");
        } else {
            try {
                mBoundService.removeNotification();
                Log.e("Exception 2", "stopped");
            } catch (Exception e) {
                Log.e("Exception 3", e.getMessage());
            }
            mBoundService.stopServer();
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((ServerService.LocalBinder) service).getService();
            //   Toast.makeText(TrainingKitActivity.this, "Service connected", Toast.LENGTH_SHORT).show();
            try {
                String lastMessage = "";
                mBoundService.updateNotification(lastMessage);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
            //   Toast.makeText(TrainingKitActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    private void doBindService() {
        bindService(new Intent(TrainingSlidesActivity.this, ServerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    /*void startser() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Do something after 100ms
            startServer(handler, documentRoot, Integer.valueOf(Constant.PORT));
        }, 500);
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        try {
            doUnbindService();
            stopServer();
            doBindService();

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                startServer(mhandler, documentRoot, Integer.parseInt(Constant.PORT));
            }, 500);

        } catch (Exception ignored) {
        }
    }
}
