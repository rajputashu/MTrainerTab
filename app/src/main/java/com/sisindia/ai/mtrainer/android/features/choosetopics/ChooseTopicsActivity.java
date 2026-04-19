package com.sisindia.ai.mtrainer.android.features.choosetopics;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityChooseTopicsBinding;
import com.sisindia.ai.mtrainer.android.databinding.AddAdhocTopicDialogBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.SavedTopic;
import com.sisindia.ai.mtrainer.android.db.entities.AdhocSavedTopics;
import com.sisindia.ai.mtrainer.android.models.AdhocTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Predicate;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChooseTopicsActivity extends MTrainerBaseActivity {


    private ChooseTopicsViewModel viewModel;
    public ActivityChooseTopicsBinding binding;
    private Dialog dialog;
    MtrainerDataBase dataBase;
   String ATsize;


    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, ChooseTopicsActivity.class);
        return intent;
    }

    @Override
    protected void extractBundle() {

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_choose_topic, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionVideo:
                //showToast("Video Clicked");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_take_assessment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreated() {
        setupToolBarForBackArrow(binding.tbStartTraining);
        dataBase = MtrainerDataBase.getDatabase(getApplicationContext());
        //viewModel.getChooseTopics();
        binding.topicFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnAdhocTopic.setOnClickListener( v->{
            openAdhocTopicScreen();

        });

    }

    @Override
    protected void initViewState() {

        viewModel.getMasterList().observe(this, new Observer<List<ChooseTopicsResponse.TopicsResponse>>() {
            @Override
            public void onChanged(List<ChooseTopicsResponse.TopicsResponse> topicsResponses) {
                if (topicsResponses.size() != 0) {
                    String rotaTopicId = Prefs.getString(PrefsConstants.TOPIC_ID);
                    ArrayList<ChooseTopicsResponse.TopicsResponse> rotaTopicList = new ArrayList<>();
                    if (!rotaTopicId.trim().isEmpty() && rotaTopicId.length() != 1) {
                        StringTokenizer tokenizer = new StringTokenizer(rotaTopicId, ",");
                        while (tokenizer.hasMoreElements()) {
                            int topicId = Integer.parseInt(tokenizer.nextToken().trim());
                            ChooseTopicsResponse.TopicsResponse topicsResponse = null;

                            for(ChooseTopicsResponse.TopicsResponse e : topicsResponses) {
                                if(topicId == e.topicId)
                                    topicsResponse = e;
                            }
                            if (topicsResponse != null)
                                rotaTopicList.add(topicsResponse);
                        }
                    } else if (!rotaTopicId.trim().isEmpty()) {
                        int topicId = Integer.parseInt(rotaTopicId);
                        ChooseTopicsResponse.TopicsResponse topicsResponse = null;
                        for(ChooseTopicsResponse.TopicsResponse e : topicsResponses) {
                            if(topicId == e.topicId)
                                topicsResponse = e;
                        }

                        if (topicsResponse != null)
                            rotaTopicList.add(topicsResponse);
                    }
                    topicsResponses.removeAll(rotaTopicList);
                    viewModel.setRotaData(rotaTopicList);
                    viewModel.setData(topicsResponses);
                }
            }
        });


        viewModel.getAdhocSavedList().observe(this, new Observer<List<AdhocSavedTopics>>() {
            @Override
            public void onChanged(List<AdhocSavedTopics> adhocSavedTopics) {
                viewModel.adhocCount.set(adhocSavedTopics.size());

            }
        });




        viewModel.getSavedList().observe(this, new Observer<List<SavedTopic>>() {
            @Override
            public void onChanged(List<SavedTopic> savedTopics) {
                viewModel.topicCount.set(savedTopics.size());

            }
        });


        viewModel.getAdhocTopics().observe(this, new Observer<List<AdhocTopicsResponse.AdhocTopics>>() {
            @Override
            public void onChanged(List<AdhocTopicsResponse.AdhocTopics> adhocTopics) {
                viewModel.setAdhocTopic(adhocTopics);
            }
        });


    }
    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (ChooseTopicsViewModel) getAndroidViewModel(ChooseTopicsViewModel.class);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_choose_topics;
    }
    @Override
    protected void onResume() {
        super.onResume();
        viewModel.recoverTopicsState();
        viewModel.recoverAdhocTopicsState();
    }
    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("RECOVER", 1);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Prefs.putIntOnMainThread(PrefsConstants.SELECTED_TOPIC_COUNT, viewModel.selectedTopicSet.size());
        Prefs.putIntOnMainThread(PrefsConstants.SELECTED_ADHOC_TOPIC_COUNT, viewModel.selectedAdhocTopicSet.size());
    }

    private void openAdhocTopicScreen(){
        dialog = new Dialog(ChooseTopicsActivity.this);
        AddAdhocTopicDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(ChooseTopicsActivity.this),R.layout.add_adhoc_topic_dialog,null,false);
        dialogBinding.setVm(viewModel);


        dialogBinding.closeBtn.setOnClickListener(v -> {
            if(dialog.isShowing())
                dialog.dismiss();
        });

        dialogBinding.btnAddAdhocTopic.setOnClickListener(v ->{

            if(dialogBinding.etAdhocTopic==null || dialogBinding.etAdhocTopic.getText().toString().isEmpty()){
                showToast("Please enter adhoc topic");
            }
            else {
                String adhocTopicName = dialogBinding.etAdhocTopic.getText().toString();
                AdhocTopicsResponse.AdhocTopics adhocTopics= new   AdhocTopicsResponse.AdhocTopics();
                adhocTopics.topicName= adhocTopicName;
                adhocTopics.rotaId=Prefs.getInt(PrefsConstants.ROTA_ID);

                dataBase.getAdhocTopicsDao().insertAdhocTopics(Collections.singletonList(adhocTopics))
                       // .doOnComplete(() ->viewModel.saveManualTopic(adhocTopics.Id, adhocTopics.topicName))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                showToast("Successfully added adhoc topic");
                dialogBinding.etAdhocTopic.setText("");
                dialog.dismiss();

            }

        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }





}