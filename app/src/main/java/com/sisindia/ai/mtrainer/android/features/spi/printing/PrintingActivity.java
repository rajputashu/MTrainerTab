package com.sisindia.ai.mtrainer.android.features.spi.printing;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_MOUNTED_SPI_SCREEN;

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
import com.sisindia.ai.mtrainer.android.databinding.FragmentPrintingBinding;
import com.sisindia.ai.mtrainer.android.features.spi.SpiMainActivity;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.features.spi.mounted.MountedActivity;

public class PrintingActivity extends MTrainerBaseActivity {
    FragmentPrintingBinding binding;
    private PrintingViewModel viewModel;
    private int position;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, PrintingActivity.class);
        return intent;
    }

    @Override
    protected void extractBundle() {
        position = getIntent().getIntExtra(NavigationConstants.SPI_SPINNER_POSITION, -1);

    }

    @Override
    protected void initViewModel() {
        viewModel = (PrintingViewModel) getAndroidViewModel(PrintingViewModel.class);
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

                case OPEN_MOUNTED_SPI_SCREEN:
                    openMountedScreen();
                    break;
            }
        });

    }

    @Override
    protected void onCreated() {
        //  viewModel.PrintingDetails();

        binding.refreshPrinting.setOnClickListener(v -> {
            viewModel.PrintingDetails();
            showToast("Refreshed Successfully");

        });
        statusposition();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_printing;
    }

    void openMountedScreen() {
        Intent intent = new Intent(this, MountedActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, position);
        startActivity(intent);
        // startActivityForResult(MountedActivity.newIntent(this), IntentRequestCodes.BASIC_SPI_DETAIL);
    }

    public void statusposition() {
        SpiStatusRequest request = new SpiStatusRequest(Prefs.getInt(PrefsConstants.SPI_ID), 6);
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
