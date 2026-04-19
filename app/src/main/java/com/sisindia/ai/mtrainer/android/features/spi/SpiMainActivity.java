package com.sisindia.ai.mtrainer.android.features.spi;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.INIT_SPI_STATE;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_OPEN_BASIC_INFO_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_CLIENT_APPROVAL_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DESIGN_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_APPROVAL_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_MOUNTED_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_PRINTING_SPI_SCREEN;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivitySpiMainBinding;
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardActivity;
import com.sisindia.ai.mtrainer.android.features.spi.basicinformation.BasicInfoActvity;
import com.sisindia.ai.mtrainer.android.features.spi.clientapproval.ClientApprovalActivity;
import com.sisindia.ai.mtrainer.android.features.spi.design.DesignActivity;
import com.sisindia.ai.mtrainer.android.features.spi.draftapproval.DraftApprovalActivity;
import com.sisindia.ai.mtrainer.android.features.spi.draftspi.DraftSpiActivity;
import com.sisindia.ai.mtrainer.android.features.spi.mounted.MountedActivity;
import com.sisindia.ai.mtrainer.android.features.spi.printing.PrintingActivity;
import com.sisindia.ai.mtrainer.android.models.SpiBranchData;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;

import java.util.List;

public class SpiMainActivity extends MTrainerBaseActivity {
    private SpiViewModel viewModel;
    private ActivitySpiMainBinding binding;
    private int position = -1;
    Handler handler = new Handler(Looper.getMainLooper());

    public static Intent newIntent(Activity activity) {
        return new Intent(activity, SpiMainActivity.class);
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();

        return super.onSupportNavigateUp();

    }*/


    @Override
    public void onBackPressed() {
        openDashboard();
    }

    void openDashboard() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        startActivity(intent);

    }

    @Override
    protected void extractBundle() {
        position = getIntent().getIntExtra(NavigationConstants.SPI_SPINNER_POSITION, -1);
    }

    @Override
    protected void initViewState() {

        liveData.observe(this, message -> {
            switch (message.what) {
                case ON_OPEN_BASIC_INFO_SCREEN:
                    openBasicInfoScreen(message.arg1, message.arg2);
                    break;
                // open for draft spi screen if user stopped in this screen
                case OPEN_DRAFT_SPI_SCREEN:
                    openDraftSpi(message.arg1);
                    break;
                case OPEN_DRAFT_APPROVAL_SCREEN:
                    openDraftApproval(message.arg1);
                    break;
                case OPEN_DESIGN_SPI_SCREEN:
                    openDesignSpi(message.arg1);
                    break;
                case OPEN_CLIENT_APPROVAL_SCREEN:
                    openClientApproval(message.arg1);
                    break;
                case OPEN_PRINTING_SPI_SCREEN:
                    openPrintingSpi(message.arg1);
                    break;
                case OPEN_MOUNTED_SPI_SCREEN:
                    openMounted(message.arg1);
                    break;
                case INIT_SPI_STATE:
                    Log.v("first spi", "position" + position);
                    handler.postDelayed(() -> {
                        if (position != -1) {
                            binding.spBranchName.setSelection(position + 1);
                        }
                    }, 1000);
                    break;
            }
        });

        viewModel.getBranchList().observe(this, new Observer<>() {
            @Override
            public void onChanged(List<SpiBranchData> branchData) {
                viewModel.setBranchList(branchData);
            }
        });

        viewModel.getSpiTableDetails().observe(this, new Observer<List<SpiTableDetailsResponse.SpiTableDetailsData>>() {
            @Override
            public void onChanged(List<SpiTableDetailsResponse.SpiTableDetailsData> spiEntities) {
                viewModel.spiDetails(spiEntities);

                if (spiEntities.size() != 0) {
                    binding.tvNoSpi.setText("" + View.GONE);
                    binding.tvNoSpi.setText("");
                } else {
                    binding.tvNoSpi.setText("" + View.VISIBLE);
                    binding.tvNoSpi.setText("Data not found");
                }
            }
        });
    }

    private void openBasicInfoScreen(int spinnerPosition, int flag) {
        // startActivityForResult(BasicInfoActvity.newIntent(this), IntentRequestCodes.IRQ_SPI_DETAIL);
        Intent intent = new Intent(this, BasicInfoActvity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, spinnerPosition);
        intent.putExtra(NavigationConstants.IS_SPI_COMPLETED, flag == 1);
        startActivity(intent);
    }

    /*written for draft spi open if user left from this screen prviously*/
    private void openDraftSpi(int spinnerPosition) {
        Intent intent = new Intent(this, DraftSpiActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, spinnerPosition);
        startActivity(intent);
    }

    private void openDraftApproval(int spinnerPosition) {
        Intent intent = new Intent(this, DraftApprovalActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, spinnerPosition);
        startActivity(intent);
    }

    private void openDesignSpi(int spinnerPosition) {
        Intent intent = new Intent(this, DesignActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, spinnerPosition);
        startActivity(intent);
    }

    private void openClientApproval(int spinnerPosition) {
        Intent intent = new Intent(this, ClientApprovalActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, spinnerPosition);
        startActivity(intent);
    }

    private void openPrintingSpi(int spinnerPosition) {
        Intent intent = new Intent(this, PrintingActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, spinnerPosition);
        startActivity(intent);
    }

    private void openMounted(int spinnerPosition) {
        Intent intent = new Intent(this, MountedActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, spinnerPosition);
        startActivity(intent);
    }

    @Override
    protected void onCreated() {
        // setupToolBarForBackArrow(binding.tbStartTraining);
       /* if(Prefs.getInt(PrefsConstants.SPI_MAIN_BRANCH_ID)!=0){
            viewModel.SpiTableDetails();}
        else {

        }*/
    }


    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        viewModel.fetchBranchList();
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (SpiViewModel) getAndroidViewModel(SpiViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_spi_main;
    }


}
