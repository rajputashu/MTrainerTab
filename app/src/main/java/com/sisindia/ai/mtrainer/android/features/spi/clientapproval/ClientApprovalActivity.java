package com.sisindia.ai.mtrainer.android.features.spi.clientapproval;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_PRINTING_SPI_SCREEN;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.droidcommons.preference.Prefs;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentClientApprovalBinding;
import com.sisindia.ai.mtrainer.android.features.spi.SpiMainActivity;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.features.spi.printing.PrintingActivity;

public class ClientApprovalActivity extends MTrainerBaseActivity {
    private ClientApprovalViewModel viewModel;
    private FragmentClientApprovalBinding binding;
    private int position;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, ClientApprovalActivity.class);
        return intent;
    }

    @Override
    protected void extractBundle() {
        position = getIntent().getIntExtra(NavigationConstants.SPI_SPINNER_POSITION, -1);

    }

    @Override
    protected void initViewModel() {
        viewModel = (ClientApprovalViewModel) getAndroidViewModel(ClientApprovalViewModel.class);

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
                case OPEN_PRINTING_SPI_SCREEN:
                    openPrintinglScreen();
                    break;
            }
        });

    }

    @Override
    protected void onCreated() {
        //  viewModel.ClientApprovalDetails();
        binding.refreshClientApproval.setOnClickListener(v -> {
            viewModel.ClientApprovalDetails();
            showToast("Refreshed Successfully");

        });
        statusposition();

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_client_approval;
    }

    void openPrintinglScreen() {
        Intent intent = new Intent(this, PrintingActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, position);
        startActivity(intent);

        // startActivityForResult(PrintingActivity.newIntent(this), IntentRequestCodes.BASIC_SPI_DETAIL);

    }

    public void statusposition() {
        SpiStatusRequest request = new SpiStatusRequest(Prefs.getInt(PrefsConstants.SPI_ID), 5);
        viewModel.getDraftStatus(request);
    }


    @Override
    public void onBackPressed() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Alert")
                .setMessage("do you want to go back main spi screen!! ")
                .setPositiveButton("Yes", (dialog1, which) -> {
                    statusposition();
                    showToast("going back to main screen");
                    openSPIMainScreen();

                })
                .setNegativeButton("No", (dialog12, i) -> dialog12.dismiss()).setCancelable(false)
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
