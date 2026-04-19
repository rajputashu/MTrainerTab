package com.sisindia.ai.mtrainer.android.features.myunits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import com.droidcommons.base.BaseFragment;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentMyUnitsBinding;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.MyUnitsResponse;
import com.sisindia.ai.mtrainer.android.models.RegionData;
import com.sisindia.ai.mtrainer.android.models.SiteData;

import java.util.List;

public class MyUnitsFragment extends MTrainerBaseFragment {

    private FragmentMyUnitsBinding binding;
    private MyUnitsViewModel viewModel;

    public static BaseFragment newInstance() {
        return new MyUnitsFragment();
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (MyUnitsViewModel) getAndroidViewModel(MyUnitsViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentMyUnitsBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        viewModel.clearRegion();
        viewModel.fetchRegionList();
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {

        viewModel.getMyUnitsList().observe(this, new Observer<List<MyUnitsResponse.MyUnitsDetailList>>() {
            @Override
            public void onChanged(List<MyUnitsResponse.MyUnitsDetailList> myUnitsResponses) {
                viewModel.refreshMyUnitsView(myUnitsResponses);

                if(myUnitsResponses.size()!=0){
                    binding.tvNoRota.setText(""+View.GONE);
                    binding.tvNoRota.setText("");
                }
                else {
                    binding.tvNoRota.setText(""+View.VISIBLE);
                    binding.tvNoRota.setText("Data not found");
                }
            }
        });

        viewModel.getRegionList().observe(this, new Observer<List<RegionData>>() {
            @Override
            public void onChanged(List<RegionData> regionDataList) {
                viewModel.setRegionList(regionDataList);
            }
        });
        viewModel.getBranchList().observe(this, new Observer<List<BranchData>>() {
            @Override
            public void onChanged(List<BranchData> branchDataList) {
                viewModel.setBranchList(branchDataList);
            }
        });

        viewModel.getSiteList().observe(this, new Observer<List<SiteData>>() {
            @Override
            public void onChanged(List<SiteData> siteDataList) {
                viewModel.setSiteList(siteDataList);
            }
        });

    }

    @Override
    protected void onCreated() {
        //  viewModel.fetchBranchList();

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_units;
    }
}
