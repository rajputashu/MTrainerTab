package com.sisindia.ai.mtrainer.android.features.spi.design;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_CLIENT_APPROVAL_SCREEN;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.droidcommons.preference.Prefs;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentDesignBinding;
import com.sisindia.ai.mtrainer.android.features.spi.SpiMainActivity;
import com.sisindia.ai.mtrainer.android.features.spi.clientapproval.ClientApprovalActivity;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.models.DesignSpiResponse;

import java.util.List;

public class DesignActivity extends MTrainerBaseActivity {
    private DesignViewModel viewModel;
    private FragmentDesignBinding binding;
    private int position;


    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, DesignActivity.class);
        return intent;
    }

    @Override
    protected void extractBundle() {
        position = getIntent().getIntExtra(NavigationConstants.SPI_SPINNER_POSITION, -1);

    }

    @Override
    protected void initViewModel() {
        viewModel = (DesignViewModel) getAndroidViewModel(DesignViewModel.class);

    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();

    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            switch (message.what) {
                case OPEN_CLIENT_APPROVAL_SCREEN:
                    openClientApprovalScreen();
                    break;
            }
        });
        viewModel.getDesignSpiDetails().observe(this, new Observer<List<DesignSpiResponse.DesignSpiData>>() {
            @Override
            public void onChanged(List<DesignSpiResponse.DesignSpiData> spiEntities) {
                viewModel.designSpiDetails(spiEntities);

            }
        });


    }

    @Override
    protected void onCreated() {
        viewModel.SpiDesignDetails();
        // stopped flushing db
        //  viewModel.flushDraftApproval();
        //viewModel.DesignStatus();

        binding.refreshDesign.setOnClickListener(v -> {
            viewModel.SpiDesignDetails();
            showToast("Refreshed Successfully");

        });
        statusposition();

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_design;
    }

    void openClientApprovalScreen() {

        Intent intent = new Intent(this, ClientApprovalActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, position);
        startActivity(intent);
        // startActivityForResult(ClientApprovalActivity.newIntent(this), IntentRequestCodes.BASIC_SPI_DETAIL);

    }

    public void statusposition() {
        SpiStatusRequest request = new SpiStatusRequest(Prefs.getInt(PrefsConstants.SPI_ID), 4);
        viewModel.getDraftStatus(request);
    }


    @Override
    public void onBackPressed() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Alert")
                .setMessage("do you want to go back main spi screen!! ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statusposition();
                        showToast("going back to main screen");
                        openSPIMainScreen();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).setCancelable(false)
                .create();
        dialog.show();

    }

    void openSPIMainScreen() {
        Intent intent = new Intent(this, SpiMainActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, position);
        startActivity(intent);

        // startActivityForResult(SpiMainActivity.newIntent(this), IntentRequestCodes.BASIC_SPI_DETAIL);
    }

}
