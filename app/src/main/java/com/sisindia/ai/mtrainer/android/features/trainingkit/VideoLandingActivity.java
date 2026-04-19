package com.sisindia.ai.mtrainer.android.features.trainingkit;

import static com.sisindia.ai.mtrainer.android.utils.Utils.generateNoteOnSD;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.Constant;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.SubModules;
import com.sisindia.ai.mtrainer.android.features.server.ServerService;
import com.sisindia.ai.mtrainer.android.models.WholeData;
import com.sisindia.ai.mtrainer.android.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//list of conent will be handled in this class
public class VideoLandingActivity extends AppCompatActivity {

    public static int tempCount = 0;

    public static final String SERVER_SOCKET_BROADCAST_ACTION = "yourpackagename.server.socket";
    /*
        @BindView(R.id.toolBarBackButton)
        ImageView back;*/
    RelativeLayout lecture1_heading, section2_lecture2, section2_lecture3;
    String videoname;
    // MTrainerDB mTrainerDB;
    ImageView imgback;
    private MtrainerDataBase dataBase;
    RecyclerView recyclerView;
    List<VideoDetailsModel.VideoDetailsModellist> videoDetailsModelList;
    List<SubModules.MySubModuleslist> subModelList;
    List<WholeData> wholeDataList = new ArrayList<>();
    String id;
    TextView module_name;

    int count = 0;
    //@BindView(R.id.screenshot)
    ImageView screenshot;

    private String documentRoot;

    private String lastMessage = "";

    private ServerService mBoundService;

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            generateNoteOnSD(getApplicationContext(), b.getString("msg"));
            //log(b.getString("msg"));
        }
    };
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_landing);
        // ButterKnife.bind(this);

        if (Utils.isTablet(VideoLandingActivity.this) == true) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        dataBase = MtrainerDataBase.getDatabase(getApplicationContext());

        // screenshot=findViewById(R.id.screenshot);
        module_name = findViewById(R.id.module_name);
        videoname = getIntent().getStringExtra("videoname");
        module_name.setText(videoname);
        imgback = findViewById(R.id.toolBarBackButton);

        recyclerView = findViewById(R.id.recycler_view);
        id = getIntent().getStringExtra("id");
        subModelList = dataBase.getSubModuleDao().getSubModules1(id);
        // String wifiIp = getWifiIPAddress(VideoLandingActivity.this);
        // String mobileIp = getMobileIPAddress();
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        utils = new Utils(getApplicationContext());
       /* screenshot.setOnClickListener(v -> {

            utils.takeScreenshot(VideoLandingActivity.this);
        });*/

        // doBindService();

        documentRoot = utils.getDocRoot();
        Log.d("documentRoot", "onCreate: " + documentRoot);

      /*  final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Do something after 100ms
            startServer(mHandler, documentRoot, Integer.valueOf(Constants.PORT));
        }, 500);*/
//        StoragePath storagePath;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            storagePath = new StoragePath(getExternalFilesDirs(null));
//        }else {
//            storagePath = new StoragePath();
//        }
//
//        String[] storages;
//        storages = storagePath.getDeviceStorages();
//        for(int i=0;i<storages.length;i++)
//        {
//            Log.e("paths"+String.valueOf(i),storages[i]);
//        }
        for (int i = 0; i < subModelList.size(); i++) {
            //wholeDataList=new ArrayList<>();
            WholeData wholeData = new WholeData();
            wholeData.setId(String.valueOf(Integer.parseInt(subModelList.get(i).getSubModuleNo())));
            wholeData.setName(subModelList.get(i).getEnglishName());
            wholeData.setPath("null");
            wholeData.setIssubmodule(true);
            wholeDataList.add(wholeData);
            videoDetailsModelList = dataBase.getMtrainerVideoDao().getVideos(subModelList.get(i).getSubModuleNo(), id);
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Download");
            Log.e("fbp", getExternalFilesDir(null).getPath());

            for (int j = 0; j < videoDetailsModelList.size(); j++) {
                WholeData wholeData1 = new WholeData();
                wholeData1.setId(String.valueOf(Integer.parseInt(videoDetailsModelList.get(j).getSequence())));
                wholeData1.setName(videoDetailsModelList.get(j).getEnglishName());
                wholeData1.setPath(videoDetailsModelList.get(j).getPath());
                wholeData1.setDuration(videoDetailsModelList.get(j).getDuration());
                wholeData1.setIssubmodule(false);
                wholeData1.setVideoId(videoDetailsModelList.get(j).getVideoId());
                wholeDataList.add(wholeData1);
            }
        }

        // Toast.makeText(getApplicationContext(), String.valueOf(videoDetailsModelList.size()), Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), String.valueOf(wholeDataList.size()), Toast.LENGTH_LONG).show();
        //   videoDetailsModelList = mTrainerDB.getVideos(id);
        VideoLandingAdapter videoLandingAdapter = new VideoLandingAdapter(VideoLandingActivity.this, wholeDataList, id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(videoLandingAdapter);
//        lecture1_heading = findViewById(R.id.lecture1_heading);
//        section2_lecture2= findViewById(R.id.section2_lecture2);
//        section2_lecture3 = findViewById(R.id.section2_lecture3);
//        section2_lecture2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(VideoLandingActivity.this, TrainingVideoActivity.class);
//                i.putExtra("videoname","Bhumika.mp4");
//                startActivity(i);
//            }
//        });
//        section2_lecture3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(VideoLandingActivity.this, TrainingVideoActivity.class);
//                i.putExtra("videoname","HumDodeteKyuHai.mp4");
//                startActivity(i);
//            }
//        });
//        lecture1_heading.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(VideoLandingActivity.this, TrainingVideoActivity.class);
//                i.putExtra("videoname","Nishkash.mp4");
//                startActivity(i);
//            }
//        });
        //   Toast.makeText(VideoLandingActivity.this, "running", Toast.LENGTH_LONG).show();
        //  startserver();


        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        doUnbindService();
//        stopServer();
        tempCount = 0;
        /*unbindService(mConnection);
        stopServer();*/
    }

    private void doUnbindService() {
        if (mBoundService != null) {
            unbindService(mConnection);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startServer(Handler handler, String documentRoot, int port) {
        if (mBoundService == null) {
            //   Toast.makeText(VideoLandingActivity.this, "Service not connected", Toast.LENGTH_SHORT).show();
            doBindService();
            startser();

        } else {
            mBoundService.startServer(handler, documentRoot, port);
        }
    }

    private void stopServer() {
        if (mBoundService == null) {
            //     Toast.makeText(VideoLandingActivity.this, "Service not connected", Toast.LENGTH_SHORT).show();
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

    private ServiceConnection mConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((ServerService.LocalBinder) service).getService();
            //   Toast.makeText(TrainingKitActivity.this, "Service connected", Toast.LENGTH_SHORT).show();
            try {
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
        bindService(new Intent(VideoLandingActivity.this, ServerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    void startser() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Do something after 100ms
            startServer(mHandler, documentRoot, Integer.valueOf(Constant.PORT));
        }, 500);
    }


    /*@OnClick(R.id.toolBarBackButton)
    void backbutton() {

        onBackPressed();
    }*/


    @Override
    protected void onPause() {
        super.onPause();
//        if(null!=UpdateUiReceiver){
//            VideoLandingActivity.this.unregisterReceiver(UpdateUiReceiver);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        IntentFilter filter = new IntentFilter(SERVER_SOCKET_BROADCAST_ACTION);
//        VideoLandingActivity.this.registerReceiver(UpdateUiReceiver, filter);
        //startserver();
    }

    BroadcastReceiver UpdateUiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("socket trainner ", "UpdateUiReceive =BroadcastReceiver");

            // onResume();
            startserver();

        }
    };


    private void startserver() {

        try {
            doUnbindService();
            stopServer();

            doBindService();

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                //Do something after 100ms
                startServer(mHandler, documentRoot, Integer.valueOf(Constant.PORT));
            }, 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
