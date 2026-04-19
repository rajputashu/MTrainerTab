package com.sisindia.ai.mtrainer.android.features.trainingkit;

import static com.sisindia.ai.mtrainer.android.utils.Utils.generateNoteOnSD;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.Constant;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.Presenatation;
import com.sisindia.ai.mtrainer.android.db.entities.SlideModuleEntity;
import com.sisindia.ai.mtrainer.android.features.server.ServerService;
import com.sisindia.ai.mtrainer.android.features.syncadapter.NetworkClient;
import com.sisindia.ai.mtrainer.android.models.DataResponse;
import com.sisindia.ai.mtrainer.android.models.MLCVideoDetailsModel;
import com.sisindia.ai.mtrainer.android.models.MyModule;
import com.sisindia.ai.mtrainer.android.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingKitActivity extends AppCompatActivity {

    public static Intent newIntent(Activity activity) {
        return new Intent(activity, TrainingKitActivity.class);
    }

    RecyclerView trainingKitRV;
    SearchView searchTrainingKitView;
    //    ImageView back;
    ImageView alphabet_filter;
    private MtrainerDataBase dataBase;
    SwipeRefreshLayout refreshLayout;
    TextView videos;
    TextView mlcvideos;
    TextView presentation;
    TextView sop;
    ProgressDialog progressDialog;
    TextView synced_text;
    LinearLayoutCompat reset_layout;
    ImageView imgback;
    //    ImageView screenshot;
    //    MyAdapterPresentation myAdapterPresentation;
    private String documentRoot;
    private ServerService mBoundService;
    Boolean isascending = true;
    List<MyModule.MyModuleslist> myModuleList;
    List<com.sisindia.ai.mtrainer.android.db.entities.SubModules.MySubModuleslist> videoDetailsModelList1;
    List<VideoDetailsModel.VideoDetailsModellist> videoDetailsModelList3;
    List<MLCVideoDetailsModel.MlcVideoDetailsModellist> mlcvideoDetailsModelList;
    List<SlideModuleEntity.SlideModuleEntityList> videoDetailsModelList2;
    List<VideoDetailsModel.VideoDetailsModellist> sopModelList1;
    List<com.sisindia.ai.mtrainer.android.db.entities.SubModules.MySubModuleslist> videoDetailsModelList;
    Utils utils;
    //    private static final int REQUEST_WRITE_STORAGE = 112;
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            generateNoteOnSD(getApplicationContext(), b.getString("msg"));
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_kit);
        videos = findViewById(R.id.videos);
        mlcvideos = findViewById(R.id.mlcvideos);
        presentation = findViewById(R.id.presentation);
        trainingKitRV = findViewById(R.id.trainingKitRV);
        refreshLayout = findViewById(R.id.refreshVideos);
        alphabet_filter = findViewById(R.id.alphabet_filter);
        imgback = findViewById(R.id.toolBarBackButton);
        sop = findViewById(R.id.sop);
        synced_text = findViewById(R.id.synced_text);
        reset_layout = findViewById(R.id.reset_layout);
        searchTrainingKitView = findViewById(R.id.searchTrainingKitView);

        if (Utils.isTablet(TrainingKitActivity.this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        progressDialog = new ProgressDialog(this);
        utils = new Utils(getApplicationContext());
        //  mTrainerDB = new MTrainerDB(getApplicationContext());
        dataBase = MtrainerDataBase.getDatabase(getApplicationContext());
        //  doBindService();

        documentRoot = utils.getDocRoot();
//        documentRoot = utils.getDocRootV2();
        imgback.setOnClickListener(view -> finish());

        List<VideoDetailsModel.VideoDetailsModellist> model = dataBase.getMtrainerVideoDao().getTrainingVideo();

        //   final List<VideoDetailsModel> model = MTrainerApplication1.getInstance().getSelectDBInstance().getTrainingVideo();
        if (model != null) {
            videos.setText("VIDEOS " + "(" + model.size() + ")");
        }
        // ArrayList<VideoDetailsModel> model1 = MTrainerApplication1.getInstance().getSelectDBInstance().getTrainingPresentation();
        List<Presenatation> model1 = dataBase.getPresenatationDao().getTrainingPresentation();

        if (model1 != null) {
            presentation.setText("PRESENTATION " + "(" + model1.size() + ")");
        }
        videos.setTextColor(getResources().getColor(R.color.red));
        presentation.setTextColor(getResources().getColor(R.color.DarkBlue));
        mlcvideos.setTextColor(getResources().getColor(R.color.DarkBlue));
//        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        assert wm != null;
//        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//        tempurl = "http://" + ip + ":" + "8082" + "/";
        NetworkClient.RestServiceTempData restServiceTempData = NetworkClient.getNetworkClientInstance().getTempNoccCall();
        restServiceTempData.getModules().enqueue(new Modules());

        NetworkClient.RestServiceTempData restServiceTempData3 = NetworkClient.getNetworkClientInstance().getTempNoccCall();
        restServiceTempData3.getMLPVideos().enqueue(new MLPVideos());

        videos.setText("VIDEOS " + "(" + dataBase.getMtrainerVideoDao().getVideoModuleCount() + ")");
        presentation.setText("PRESENTATION " + "(" + dataBase.getSubModuleDao().getSubModuleCount() + ")");
        mlcvideos.setText("MLC Videos " + "(" + dataBase.getMtrainerMLCVideoDao().getVideoModuleCount() + ")");

        mlcvideos.setOnClickListener(v -> {
            videos.setTextColor(getResources().getColor(R.color.DarkBlue));
            presentation.setTextColor(getResources().getColor(R.color.DarkBlue));
            sop.setTextColor(getResources().getColor(R.color.DarkBlue));
            mlcvideos.setTextColor(getResources().getColor(R.color.red));

            if (new Utils(getApplicationContext()).isConnectingToInternet()) {
                // befor not null
                if (dataBase.getMtrainerMLCVideoDao().getVideoModuleCount() > 0) {
                    mlcvideoDetailsModelList = dataBase.getMtrainerMLCVideoDao().getTrainingVideo();
                    final MlcVideoAdapter myAdapter = new MlcVideoAdapter(TrainingKitActivity.this, mlcvideoDetailsModelList);
                    trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                    trainingKitRV.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                    alphabet_filter.setOnClickListener(v1 -> {
                        if (isascending) {
                            Collections.sort(mlcvideoDetailsModelList, (lhs, rhs) -> lhs.getEnglishName().compareTo(rhs.getEnglishName()));
                            final MlcVideoAdapter myAdapter1 = new MlcVideoAdapter(TrainingKitActivity.this, mlcvideoDetailsModelList);
                            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                            trainingKitRV.setAdapter(myAdapter1);
                            myAdapter1.notifyDataSetChanged();
                            alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_z_a);
                            isascending = false;
                        } else {
                            Collections.reverse(mlcvideoDetailsModelList);
                            final MlcVideoAdapter myAdapter1 = new MlcVideoAdapter(TrainingKitActivity.this, mlcvideoDetailsModelList);
                            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                            trainingKitRV.setAdapter(myAdapter1);
                            myAdapter1.notifyDataSetChanged();
                            alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_a_z);
                            isascending = true;
                        }
                    });

                    searchTrainingKitView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            myAdapter.getFilter().filter(query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            myAdapter.getFilter().filter(newText);
                            return false;
                        }
                    });
                }
            }
        });

        sop.setOnClickListener(v -> {
            sopModelList1 = new ArrayList<>();
            videos.setTextColor(getResources().getColor(R.color.DarkBlue));
            presentation.setTextColor(getResources().getColor(R.color.DarkBlue));
            mlcvideos.setTextColor(getResources().getColor(R.color.DarkBlue));
            sop.setTextColor(getResources().getColor(R.color.red));
            VideoDetailsModel.VideoDetailsModellist videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "HOTEL SECURITY");
            sopModelList1.add(videoDetailsModel);
            videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "RETAIL SECURITY (MALL & SHOWROOM)");
            sopModelList1.add(videoDetailsModel);
            videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "HOSPITAL SECURITY");
            sopModelList1.add(videoDetailsModel);
            videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "MINES SECURITY");
            sopModelList1.add(videoDetailsModel);
            videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "BANK SECURITY");
            sopModelList1.add(videoDetailsModel);
            videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "MONUMENTS & MUSEUMS SECURITY");
            sopModelList1.add(videoDetailsModel);
            videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "CORPORATE OFFICE SECURITY");
            sopModelList1.add(videoDetailsModel);
            videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "RESIDENTIAL COMPLEX SECURITY");
            sopModelList1.add(videoDetailsModel);
            videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "EDUCATIONAL INSTITUTION SECURITY");
            sopModelList1.add(videoDetailsModel);
            videoDetailsModel = new VideoDetailsModel.VideoDetailsModellist("1", "IT & BPO SECURITY");
            sopModelList1.add(videoDetailsModel);
            // sopModelList1 = mTrainerDB.getSubModules();
            //   Toast.makeText(getApplicationContext(),String.valueOf(myModuleList.size()),Toast.LENGTH_LONG).show();
            final SectorAdapter myAdapter = new SectorAdapter(TrainingKitActivity.this, sopModelList1);
            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
            trainingKitRV.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        });

        videos.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "video clicked", Toast.LENGTH_SHORT).show();

            videos.setTextColor(getResources().getColor(R.color.red));
            presentation.setTextColor(getResources().getColor(R.color.DarkBlue));
            sop.setTextColor(getResources().getColor(R.color.DarkBlue));
            mlcvideos.setTextColor(getResources().getColor(R.color.DarkBlue));

            if (new Utils(getApplicationContext()).isConnectingToInternet()) {
                // befor not null
                if (dataBase.getMyModuleDao().getModuleCount() > 0) {
                    myModuleList = dataBase.getMyModuleDao().getModules();
                    //   Toast.makeText(getApplicationContext(),String.valueOf(myModuleList.size()),Toast.LENGTH_LONG).show();
                    final VideoAdapter myAdapter = new VideoAdapter(TrainingKitActivity.this, myModuleList);
                    trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                    trainingKitRV.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                    alphabet_filter.setOnClickListener(v -> {
                        if (isascending) {
                            Collections.sort(myModuleList, (lhs, rhs) -> lhs.getEnglishName().compareTo(rhs.getEnglishName()));
                            VideoAdapter myAdapter1 = new VideoAdapter(TrainingKitActivity.this, myModuleList);
                            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                            trainingKitRV.setAdapter(myAdapter1);
                            myAdapter1.notifyDataSetChanged();
                            alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_z_a);
                            isascending = false;
                        } else {
                            Collections.reverse(myModuleList);
                            VideoAdapter myAdapter1 = new VideoAdapter(TrainingKitActivity.this, myModuleList);
                            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                            trainingKitRV.setAdapter(myAdapter1);
                            myAdapter1.notifyDataSetChanged();
                            alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_a_z);
                            isascending = true;
                        }
                    });

                    searchTrainingKitView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            myAdapter.getFilter().filter(query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            myAdapter.getFilter().filter(newText);
                            return false;
                        }
                    });

                }

            } else {
                Toast.makeText(TrainingKitActivity.this, "Please Connect To Internet", Toast.LENGTH_SHORT).show();
            }

        });

        presentation.setOnClickListener(view -> {
            videos.setTextColor(getResources().getColor(R.color.DarkBlue));
            presentation.setTextColor(getResources().getColor(R.color.red));
            sop.setTextColor(getResources().getColor(R.color.DarkBlue));
            mlcvideos.setTextColor(getResources().getColor(R.color.DarkBlue));
            if (new Utils(getApplicationContext()).isConnectingToInternet()) {
//
                if (dataBase.getSubModuleDao().getSubModuleCount() > 0) {
                    myModuleList = new ArrayList<>();
                    videoDetailsModelList1 = dataBase.getSubModuleDao().getSubModules();
//                        Toast.makeText(getApplicationContext(),String.valueOf(videoDetailsModelList1.size()),Toast.LENGTH_LONG).show();
//                        for(int i=0;i<videoDetailsModelList1.size();i++)
//                        {
//                            MyModule module = new MyModule();
//                            module.setEnglishName(videoDetailsModelList1.get(i).getEnglishName());
//                            module.setHindiName(videoDetailsModelList1.get(i).getHindiName());
//                            myModuleList.add(module);
//                        }
                    final PresentationAdapter myAdapter = new PresentationAdapter(TrainingKitActivity.this, videoDetailsModelList1);
                    trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                    trainingKitRV.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();

                    alphabet_filter.setOnClickListener(v -> {
                        if (isascending) {
                            Collections.sort(videoDetailsModelList1, (lhs, rhs) -> lhs.getEnglishName().compareTo(rhs.getEnglishName()));
                            PresentationAdapter myAdapter13 = new PresentationAdapter(TrainingKitActivity.this, videoDetailsModelList1);
                            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                            trainingKitRV.setAdapter(myAdapter13);
                            myAdapter13.notifyDataSetChanged();
                            alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_z_a);
                            isascending = false;
                        } else {
                            Collections.reverse(myModuleList);
                            PresentationAdapter myAdapter13 = new PresentationAdapter(TrainingKitActivity.this, videoDetailsModelList1);
                            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                            trainingKitRV.setAdapter(myAdapter13);
                            myAdapter13.notifyDataSetChanged();
                            alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_a_z);
                            isascending = true;
                        }

                    });
                    searchTrainingKitView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            myAdapter.getFilter().filter(newText);
                            return true;
                        }
                    });
                }

            } else {
                Toast.makeText(TrainingKitActivity.this, "Please Connect To Internet", Toast.LENGTH_SHORT).show();
            }


        });
        if (dataBase.getMyModuleDao().getModuleCount() > 0) {
            myModuleList = dataBase.getMyModuleDao().getModules();
            final VideoAdapter myAdapter = new VideoAdapter(TrainingKitActivity.this, myModuleList);
            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
            trainingKitRV.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
            alphabet_filter.setOnClickListener(v -> {
                if (isascending) {
                    Collections.sort(myModuleList, (lhs, rhs) -> lhs.getEnglishName().compareTo(rhs.getEnglishName()));
                    VideoAdapter myAdapter12 = new VideoAdapter(TrainingKitActivity.this, myModuleList);
                    trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                    trainingKitRV.setAdapter(myAdapter12);
                    myAdapter12.notifyDataSetChanged();
                    alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_z_a);
                    isascending = false;
                } else {
                    Collections.reverse(myModuleList);
                    VideoAdapter myAdapter12 = new VideoAdapter(TrainingKitActivity.this, myModuleList);
                    trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                    trainingKitRV.setAdapter(myAdapter12);
                    myAdapter12.notifyDataSetChanged();
                    alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_a_z);
                    isascending = true;
                }

            });
            searchTrainingKitView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    myAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }
        refreshLayout.setOnRefreshListener(() -> {
            NetworkClient.RestServiceTempData restServiceTempData1 = NetworkClient.getNetworkClientInstance().getTempNoccCall();
            restServiceTempData1.getModules().enqueue(new Modules());
            Toast.makeText(getBaseContext(), "Refreshed", Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(mConnection);
            stopServer();
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            startServer();
        } catch (Exception ignored) {

        }
    }

    private void startServer() {

        try {
            doUnbindService();
            stopServer();
            doBindService();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                startServer(mHandler, documentRoot, Integer.parseInt(Constant.PORT));
            }, 500);

        } catch (Exception ignored) {

        }
    }

    private void doBindService() {
        bindService(new Intent(this, ServerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void doUnbindService() {
        if (mBoundService != null) {
            unbindService(mConnection);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((ServerService.LocalBinder) service).getService();
            try {
                String lastMessage = "";
                mBoundService.updateNotification(lastMessage);
            } catch (Exception e) {
                Log.e("Exception", Objects.requireNonNull(e.getMessage()));
            }

        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    public class Modules implements Callback<DataResponse> {
        @Override
        public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
            try {
                assert response.body() != null;
                if (response.body().getStatusCode() == 200) {
                    myModuleList = new ArrayList<>();
                    final JSONArray jsonArray = new JSONArray(response.body().getData());
                    final int count = jsonArray.length();
                    if (dataBase.getMyModuleDao().getModuleCount() < count) {
                        final int new_count = count - dataBase.getMyModuleDao().getModuleCount();
                        reset_layout.setEnabled(true);

                        synced_text.setText("Last Synced " + utils.getString("DATE") + "\n" + String.valueOf(new_count) + " new content available for update");
                        progressDialog.setMessage("Loading.....");
                        progressDialog.show();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            dataBase.getMyModuleDao().deleteModule(String.valueOf(Prefs.getInt(PrefsConstants.ROTA_ID)));
                            dataBase.getSubModuleDao().deleteSubModule(String.valueOf(Prefs.getInt(PrefsConstants.ROTA_ID)));
                            dataBase.getMtrainerVideoDao().deleteVideoModule(String.valueOf(Prefs.getInt(PrefsConstants.ROTA_ID)));
                            JSONObject jsonObject = null;

                            try {
                                jsonObject = jsonArray.getJSONObject(i);

                                MyModule.MyModuleslist myModule = new MyModule.MyModuleslist();
                                myModule.setModuleNo(jsonObject.getString("ModuleNo"));
                                myModule.setHindiName(jsonObject.getString("HindiName"));
                                myModule.setEnglishName(jsonObject.getString("EnglishName"));
                                myModuleList.add(myModule);

                                //   dataBase.getMyModuleDao().insertModule(myModuleList);

                                  /*  dataBase.getMyModuleDao().insertModule(myModuleList)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe();
*/

                                utils.saveString("DATE", getDate());
                                synced_text.setEnabled(true);
                                synced_text.setText("Last Synced " + utils.getString("DATE") + "\n" + String.valueOf(0) + " new content available for update");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.show();
                            }
                        }
                        //dataBase.getMasterAttendanceDao().insertMasterEmployeeList(myModuleList);

                        dataBase.getMyModuleDao().insertModule(myModuleList);
                            /*dataBase.getMyModuleDao().insertModule(myModuleList)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
*/


                        // NetworkClient.RestServiceTempData restServiceTempData = NetworkClient.getNetworkClientInstance().getTempNoccCall();
                        //restServiceTempData.getModules().enqueue(new com.sis.mtrainer.trainingreport.TrainingKitActivity.Modules());
                        NetworkClient.RestServiceTempData restServiceTempData1 = NetworkClient.getNetworkClientInstance().getTempNoccCall();
                        restServiceTempData1.getSubModules().enqueue(new SubModules());

                        NetworkClient.RestServiceTempData restServiceTempData3 = NetworkClient.getNetworkClientInstance().getTempNoccCall();
                        restServiceTempData3.getSlides().enqueue(new Slides());
                        NetworkClient.RestServiceTempData restServiceTempData2 = NetworkClient.getNetworkClientInstance().getTempNoccCall();
                        restServiceTempData2.getVideos().enqueue(new Videos());

                        //   });
                    } else {
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = null;
//                            try {
//                                jsonObject = jsonArray.getJSONObject(i);
//                                MyModule module = new MyModule();
//                                module.setModuleNo(jsonObject.getString("ModuleNo"));
//                                module.setHindiName(jsonObject.getString("HindiName"));
//                                module.setEnglishName(jsonObject.getString("EnglishName"));
//                                myModuleList.add(module);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            mTrainerDB.insertModule(myModuleList);
//                        }
//
                        reset_layout.setEnabled(false);
                        synced_text.setText("Last Synced " + utils.getString("DATE") + "\n" + String.valueOf(0) + " new content available for update");
                    }

                }
            } catch (Exception exception) {

            }
        }

        @Override
        public void onFailure(Call<DataResponse> call, Throwable t) {

        }
    }

    public class MLPVideos implements Callback<DataResponse> {

        @Override
        public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
            try {
                if ((response.body() != null ? response.body().getStatusCode() : 0) == 200) {
                    mlcvideoDetailsModelList = new ArrayList<>();
                    final JSONArray jsonArray = new JSONArray(response.body().getData());
                    final int count = jsonArray.length();
                    if (dataBase.getMtrainerMLCVideoDao().getVideoModuleCount() < count) {
                        final int new_count = count - dataBase.getMtrainerMLCVideoDao().getVideoModuleCount();
                        reset_layout.setEnabled(true);

                        synced_text.setText("Last Synced " + utils.getString("DATE") + "\n" + String.valueOf(new_count) + " new content available for update");

                        try {
                            dataBase.getMtrainerMLCVideoDao().deleteVideoModule();
                        } catch (Exception ignored) {

                        }
                        progressDialog.setMessage("Loading.....");
                        progressDialog.show();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = null;

                            try {
                                jsonObject = jsonArray.getJSONObject(i);

                                MLCVideoDetailsModel.MlcVideoDetailsModellist myModule = new MLCVideoDetailsModel.MlcVideoDetailsModellist();
                                myModule.setModuleNo("" + jsonObject.getInt("ModuleNo"));
                                myModule.setHindiName(jsonObject.getString("HindiName"));
                                myModule.setEnglishName(jsonObject.getString("EnglishName"));
                                myModule.setDuration("" + jsonObject.getInt("Duration"));
                                myModule.setVideoId("" + jsonObject.getInt("id"));
                                myModule.setPath(jsonObject.getString("Path"));
                                myModule.setSequence("" + jsonObject.getInt("VideoNo"));
                                mlcvideoDetailsModelList.add(myModule);

                                mlcvideos.setText("MLC Videos " + "(" + String.valueOf(mlcvideoDetailsModelList.size()) + ")");
                                //   dataBase.getMyModuleDao().insertModule(myModuleList);

                                  /*  dataBase.getMyModuleDao().insertModule(myModuleList)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe();
*/

                                utils.saveString("DATE", getDate());
                                synced_text.setEnabled(true);
                                synced_text.setText("Last Synced " + utils.getString("DATE") + "\n" + String.valueOf(0) + " new content available for update");
                            } catch (Exception e) {

                            }

                        }

                        dataBase.getMtrainerMLCVideoDao().insertVideoModule(mlcvideoDetailsModelList);

                    } else {
                        reset_layout.setEnabled(false);
                        synced_text.setText("Last Synced " + utils.getString("DATE") + "\n" + String.valueOf(0) + " new content available for update");
                    }
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onFailure(Call<DataResponse> call, Throwable t) {

        }
    }


    public String getDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public class SubModules implements Callback<DataResponse> {
        @Override
        public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
            try {
                if (response.body().getStatusCode() == 200) {
                    videoDetailsModelList = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response.body().getData());
                    final int count = jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        com.sisindia.ai.mtrainer.android.db.entities.SubModules.MySubModuleslist module = new com.sisindia.ai.mtrainer.android.db.entities.SubModules.MySubModuleslist();
                        module.setModuleNo(jsonObject.getString("ModuleNo"));
                        module.setHindiName(jsonObject.getString("HindiName"));
                        module.setEnglishName(jsonObject.getString("EnglishName"));
                        module.setPPTPath(jsonObject.getString("PPTPath"));
                        module.setSubModuleNo(jsonObject.getString("SubModuleNo"));
                        videoDetailsModelList.add(module);
                    }
                    if (dataBase.getSubModuleDao().getSubModuleCount() > 0) {
                        //nothing
                    } else
                        dataBase.getSubModuleDao().insertSubModule(videoDetailsModelList);
                }
            } catch (Exception exception) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<DataResponse> call, Throwable t) {
            progressDialog.dismiss();
        }
    }

    public class Videos implements Callback<DataResponse> {
        VideoAdapter myAdapter;

        @Override
        public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
            try {
                if (response.body().getStatusCode() == 200) {
                    videoDetailsModelList3 = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response.body().getData());
                    final int count = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        VideoDetailsModel.VideoDetailsModellist module = new VideoDetailsModel.VideoDetailsModellist();
                        module.setVideoId(jsonObject.getString("id"));
                        module.setModuleNo(jsonObject.getString("ModuleNo"));
                        module.setHindiName(jsonObject.getString("HindiName"));
                        module.setEnglishName(jsonObject.getString("EnglishName"));
                        module.setPath(jsonObject.getString("Path"));
                        module.setDuration(jsonObject.getString("Duration"));
                        module.setSubModuleNo(jsonObject.getString("SubModuleNo"));
                        module.setSequence(jsonObject.getString("VideoNo"));
                        videoDetailsModelList3.add(module);
/*

                        dataBase.getMtrainerVideoDao().insertVideoModule(videoDetailsModelList1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
*/

                    }
                    if (dataBase.getMtrainerVideoDao().getVideoModuleCount() > 0) {

                    } else {
                        //  mTrainerDB.insertVideoModule(videoDetailsModelList1);
                        dataBase.getMtrainerVideoDao().insertVideoModule(videoDetailsModelList3);
                      /* dataBase.getMtrainerVideoDao().insertVideoModule(videoDetailsModelList1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
*/

                    }
                    myModuleList = dataBase.getMyModuleDao().getModules();
                    //   Toast.makeText(getApplicationContext(),String.valueOf(myModuleList.size()),Toast.LENGTH_LONG).show();
                    myAdapter = new VideoAdapter(TrainingKitActivity.this, myModuleList);
                    trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                    trainingKitRV.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                    videos.setText("VIDEOS " + "(" + String.valueOf(dataBase.getMtrainerVideoDao().getVideoModuleCount()) + ")");
                    presentation.setText("PRESENTATION " + "(" + String.valueOf(dataBase.getSubModuleDao().getSubModuleCount()) + ")");

                    alphabet_filter.setOnClickListener(v -> {
                        if (isascending) {
                            Collections.sort(myModuleList, (lhs, rhs) -> lhs.getEnglishName().compareTo(rhs.getEnglishName()));
                            VideoAdapter myAdapter = new VideoAdapter(TrainingKitActivity.this, myModuleList);
                            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                            trainingKitRV.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged();
                            alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_z_a);
                            isascending = false;
                        } else {
                            Collections.reverse(myModuleList);
                            VideoAdapter myAdapter = new VideoAdapter(TrainingKitActivity.this, myModuleList);
                            trainingKitRV.setLayoutManager(new LinearLayoutManager(TrainingKitActivity.this));
                            trainingKitRV.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged();
                            alphabet_filter.setImageResource(R.drawable.ic_alphabetical_sorting_a_z);
                            isascending = true;
                        }

                    });
                    searchTrainingKitView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            myAdapter.getFilter().filter(query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            myAdapter.getFilter().filter(newText);
                            return false;
                        }
                    });
                    progressDialog.dismiss();
                }
            } catch (Exception exception) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<DataResponse> call, Throwable t) {
            progressDialog.dismiss();
        }
    }

    public class Slides implements Callback<DataResponse> {
//        VideoAdapter myAdapter;

        @Override
        public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
            try {
                if (response.body().getStatusCode() == 200) {
                    videoDetailsModelList2 = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response.body().getData());
                    final int count = jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        SlideModuleEntity.SlideModuleEntityList module = new SlideModuleEntity.SlideModuleEntityList();
                        module.setModuleNo(jsonObject.getString("ModuleNo"));
                        module.setPath(jsonObject.getString("ImagePath"));
                        module.setSubModuleNo(jsonObject.getString("SubModuleNo"));
                        videoDetailsModelList2.add(module);
                    }
                    if (dataBase.getSlideModuleDao().getSlideModuleCount() > 0) {
                        dataBase.getSlideModuleDao().deleteSlideModule();
                        dataBase.getSlideModuleDao().insertSlideModule(videoDetailsModelList2);
                    } else {
                        dataBase.getSlideModuleDao().insertSlideModule(videoDetailsModelList2);
                    }
                    //   myModuleList = mTrainerDB.getModules();
                    //   Toast.makeText(getApplicationContext(),String.valueOf(myModuleList.size()),Toast.LENGTH_LONG).show();
                }
            } catch (Exception exception) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<DataResponse> call, Throwable t) {
            progressDialog.dismiss();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startServer(Handler handler, String documentRoot, int port) {
        if (mBoundService == null) {
            Toast.makeText(TrainingKitActivity.this, "Service not connected", Toast.LENGTH_SHORT).show();
        } else {
            mBoundService.startServer(handler, documentRoot, port);
        }
    }

    private void stopServer() {
        if (mBoundService != null) {
            try {
                mBoundService.removeNotification();
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
            mBoundService.stopServer();
        }
    }
}