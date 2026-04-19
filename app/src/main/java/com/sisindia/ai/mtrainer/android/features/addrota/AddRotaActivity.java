package com.sisindia.ai.mtrainer.android.features.addrota;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityAddRotaBinding;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.RegionData;
import com.sisindia.ai.mtrainer.android.models.SiteData;
import com.sisindia.ai.mtrainer.android.models.UnitListResponse;

import java.util.List;

public class AddRotaActivity extends MTrainerBaseActivity {

   private AddRotaViewmodel viewmodel;
   private ActivityAddRotaBinding binding;

    public static Intent newIntent(Activity activity) {
        Intent intent= new Intent(activity, AddRotaActivity.class);
        return intent;
    }
    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewState() {
        viewmodel.initListener();
        liveData.observe(this, message -> {
            switch (message.what) {
                case NavigationConstants.ON_TASK_CREATED_SUCCESS:
                    onTaskCreatedSuccess();
                    break;
            }
        });

        viewmodel.getMasterList().observe(this, new Observer<List<ChooseTopicsResponse.TopicsResponse>>() {
            @Override
            public void onChanged(List<ChooseTopicsResponse.TopicsResponse> topicsResponses) {
                viewmodel.setData(topicsResponses);
            }
        });

        viewmodel.getRegionList().observe(this, new Observer<List<RegionData>>() {
            @Override
            public void onChanged(List<RegionData> regionDataList) {
                viewmodel.setRegionList(regionDataList);
            }
        });

        viewmodel.getBranchList().observe(this, new Observer<List<BranchData>>() {
            @Override
            public void onChanged(List<BranchData> branchDataList) {
                viewmodel.setBranchList(branchDataList);
            }
        });

        viewmodel.getSiteList().observe(this, new Observer<List<SiteData>>() {
            @Override
            public void onChanged(List<SiteData> siteDataList) {
                viewmodel.setSiteList(siteDataList);
            }
        });

        viewmodel.getUnitListFromDb().observe(this, new Observer<List<UnitListResponse.Unit>>() {
            @Override
            public void onChanged(List<UnitListResponse.Unit> unitList) {
                if(unitList.size() > 0)
                    viewmodel.setUnitList(unitList);
            }
        });
    }

    private void onTaskCreatedSuccess() {
        finish();
    }

    @Override
    protected void onCreated() {
        setupToolBarForBackArrow(binding.tbTakeAttendance);
        viewmodel.getTopics();

        if(Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8")) {
            binding.addRotaTrainingTypeContainer.setVisibility(View.GONE);
        } else {
            binding.addRotaTrainingTypeContainer.setVisibility(View.VISIBLE);
        }

/*
        binding.etTraineeNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    binding.etTraineeNumber.setHint("");
                }
                else
                    binding.etTraineeNumber.setHint("5");
            }
        });

        binding.etTraineeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewmodel.setNumberOfTrainee(s.toString());
            }
        });*/
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewmodel);
        viewmodel.clearRegion();
        viewmodel.fetchRegionList();
        if(Prefs.getString(PrefsConstants.ROLE).equals("Training Champ") || Prefs.getString(PrefsConstants.ROLE).equals("Unit Commander"))
            viewmodel.fetchUnitList();
        viewmodel.getTypeList();
        binding.executePendingBindings();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViewModel() {
        viewmodel= (AddRotaViewmodel) getAndroidViewModel(AddRotaViewmodel.class);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_rota;
    }
}




