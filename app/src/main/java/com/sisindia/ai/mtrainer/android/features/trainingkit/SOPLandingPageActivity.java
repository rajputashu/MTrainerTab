package com.sisindia.ai.mtrainer.android.features.trainingkit;

import static com.sisindia.ai.mtrainer.android.utils.Utils.generateNoteOnSD;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.Constant;
import com.sisindia.ai.mtrainer.android.databinding.ActivitySoplandingPageBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.SlideModuleEntity;
import com.sisindia.ai.mtrainer.android.features.server.ServerService;
import com.sisindia.ai.mtrainer.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SOPLandingPageActivity extends MTrainerBaseActivity {
    private ActivitySoplandingPageBinding binding;
    private TrainingKitViewModel viewModel;
    List<SlideModuleEntity.SlideModuleEntityList> sopModelList1 = new ArrayList<>();
    private String documentRoot;
    Utils utils;
    private MtrainerDataBase dataBase;
    private ServerService mBoundService;

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            generateNoteOnSD(getApplicationContext(), b.getString("msg"));
        }
    };

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewState() {

    }

    @Override
    protected void onCreated() {
        utils = new Utils(getApplicationContext());
        documentRoot = utils.getDocRoot();
        dataBase = MtrainerDataBase.getDatabase(getApplicationContext());

        sopModelList1 = new ArrayList<>();
        SlideModuleEntity.SlideModuleEntityList videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList(1, "PARKING AREA DUTY");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList(1, "MAIN GATE DUTY");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList(1, "STAFF GATE DUTY");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList(1, "SWIMMING POOL DUTY");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList(1, "LOBBY DUTY");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList(1, "SUPPLY GATE DUTY");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList(1, "FLOOR DUTY");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList(1, "PATROLING DUTY");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList(1, "CONTROL ROOM DUTY");
        sopModelList1.add(videoDetailsModel);
        final MyAdapter myAdapter = new MyAdapter(sopModelList1);
        binding.trainingKitRV.setLayoutManager(new LinearLayoutManager(SOPLandingPageActivity.this));
        binding.trainingKitRV.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        insertDB();
    }

    ///static data
    public void insertDB() {
        sopModelList1 = new ArrayList<>();

        SlideModuleEntity.SlideModuleEntityList videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide1.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide2.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide3.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide4.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide5.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide6.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide7.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide8.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide9.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide10.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide11.jpg");
        sopModelList1.add(videoDetailsModel);
        videoDetailsModel = new SlideModuleEntity.SlideModuleEntityList("0", "0", "ParkingDuty/Slide12.jpg");
        sopModelList1.add(videoDetailsModel);
        if (dataBase.getSlideModuleDao().getSlideModuleCount("0", "0") > 0) {
            dataBase.getSlideModuleDao().deleteSlideModule("0", "0");
            // dataBase.getSlideModuleDao().insertSlideModule(sopModelList1);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        List<SlideModuleEntity.SlideModuleEntityList> models;

        public MyAdapter(List<SlideModuleEntity.SlideModuleEntityList> models) {
            this.models = models;
        }

        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams")
            View v = getLayoutInflater().inflate(R.layout.training_kit_single_row, null, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            holder.textview.setText(models.get(position).getName());
            holder.imageicon.setImageResource(R.drawable.ic_slideshow_grey_700_36dp);
            holder.relative_trainingkit.setOnClickListener(view -> new Utils(SOPLandingPageActivity.this)
                    .showAlert("Content not available"));
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            TextView textview;
            ImageView imageicon;
            RelativeLayout relative_trainingkit;

            public ViewHolder(View itemView) {
                super(itemView);
                textview = itemView.findViewById(R.id.fireandsafety);
                imageicon = itemView.findViewById(R.id.imageicon);
                relative_trainingkit = itemView.findViewById(R.id.relative_trainingkit);
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
        viewModel = (TrainingKitViewModel) getAndroidViewModel(TrainingKitViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_soplanding_page;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        stopServer();
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
            Log.e("Exception 1", "stopped");
        } else {
            try {
                mBoundService.removeNotification();
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
        bindService(new Intent(SOPLandingPageActivity.this, ServerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    void startser() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Do something after 100ms
            startServer(mHandler, documentRoot, Integer.parseInt(Constant.PORT));
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
