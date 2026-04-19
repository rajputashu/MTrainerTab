/*
package com.sisindia.ai.mtrainer.android.features.spi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import com.droidcommons.base.BaseFragment;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentSitePostInstructionBinding;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.SpiBranchData;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;

import java.util.List;

public class SitePostInstructionFragment extends MTrainerBaseFragment {


    private FragmentSitePostInstructionBinding binding;
    private SpiViewModel viewModel;


    public static BaseFragment newInstance() {
        SitePostInstructionFragment fragment = new SitePostInstructionFragment();
        return fragment;
    }

    @Override
    protected void extractBundle() {
    }

    @Override
    protected void initViewModel() {
        viewModel= (SpiViewModel) getAndroidViewModel(SpiViewModel.class);

    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentSitePostInstructionBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        viewModel.fetchBranchList();
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {

        viewModel.getBranchList().observe(this, new Observer<List<SpiBranchData>>() {
            @Override
            public void onChanged(List<SpiBranchData> branchData) {
                viewModel.setBranchList(branchData);
            }
        });

        viewModel.getSpiTableDetails().observe(this, new Observer<List<SpiTableDetailsResponse.SpiTableDetailsData>>() {
            @Override
            public void onChanged(List<SpiTableDetailsResponse.SpiTableDetailsData> spiEntities) {
                viewModel.spiDetails(spiEntities);

                if(spiEntities.size()!=0){
                    binding.tvNoSpi.setText(""+View.GONE);
                    binding.tvNoSpi.setText("");
                }
                else {
                    binding.tvNoSpi.setText(""+View.VISIBLE);
                    binding.tvNoSpi.setText("Data not found");
                }
            }
        });

    }

    @Override
    protected void onCreated() {
      //  viewModel.initViewModel();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_site_post_instruction;
    }
}
*/
