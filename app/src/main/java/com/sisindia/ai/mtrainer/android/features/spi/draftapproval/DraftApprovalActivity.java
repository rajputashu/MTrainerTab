package com.sisindia.ai.mtrainer.android.features.spi.draftapproval;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DESIGN_SPI_SCREEN;

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
import com.sisindia.ai.mtrainer.android.databinding.FragmentDraftApprovalBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.spi.SpiMainActivity;
import com.sisindia.ai.mtrainer.android.features.spi.design.DesignActivity;
import com.sisindia.ai.mtrainer.android.features.spi.draftspi.DraftSpiActivity;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.models.spi.DraftApprovalResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DraftApprovalActivity extends MTrainerBaseActivity {

    private MtrainerDataBase dataBase;
    private DraftApprovalViewModel viewModel;
    private FragmentDraftApprovalBinding binding;
    public List<DraftApprovalResponse.DraftApprovalTableDetailsData> approve = new ArrayList<>();
    // private int reuplodposition = -2;

    private int position;


    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, DraftApprovalActivity.class);
        return intent;
    }

    @Override
    protected void extractBundle() {
        position = getIntent().getIntExtra(NavigationConstants.SPI_SPINNER_POSITION, -1);
        //  reuplodposition=getIntent().getIntExtra(NavigationConstants.SPI_REUPLOAD_POSITION,-2);

    }

    @Override
    protected void initViewModel() {
        viewModel = (DraftApprovalViewModel) getAndroidViewModel(DraftApprovalViewModel.class);

    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            switch (message.what) {
                case OPEN_DESIGN_SPI_SCREEN:
                    openSpiDesignScreen();
                    break;
            }
        });

        viewModel.getDraftApprovalDetails().observe(this, new Observer<List<DraftApprovalResponse.DraftApprovalTableDetailsData>>() {
            @Override
            public void onChanged(List<DraftApprovalResponse.DraftApprovalTableDetailsData> spiEntities) {
                viewModel.DraftApprovalDetails(spiEntities);


            }
        });

    }

    @Override
    protected void onCreated() {
        dataBase = MtrainerDataBase.getDatabase(this);
        viewModel.getDraftAprrovalData();
        viewModel.flashDraftSpiPhotos();

        binding.btnReupload.setOnClickListener(v -> {
            dataBase.getDraftApprovalDao().getNotApprovedDraft(Prefs.getInt(PrefsConstants.SPI_ID))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(count -> {
                        if (count > 0) {
                            viewModel.getReuploadDraftSpiData();
                            viewModel.storeToBasicInfoTable();
                            openDraftSpiScreen();
                        } else {
                            showToast("Not found any rejected draft spi");
                        }

                    }, throwable -> {
                        showToast("Something went wrong");

                    });
        });

        binding.refreshDraftApproval.setOnClickListener(v -> {
            viewModel.getDraftAprrovalData();
            showToast("refreshed successfully");

        });

        binding.btnApproval.setOnClickListener(v -> {

            viewModel.draftAppovecondition();







          /*  else if(viewModel.draftapproved.equals("Uploaded (Pending)")){
                showToast("draft approval status pending!!");
            }

            else {
                showToast("draft not approved!! you can't go next step");
            }*/
        });

        statusposition();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        viewModel.getDraftAprrovalData();
    }


    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_draft_approval;
    }

    void openSpiDesignScreen() {
        Intent intent = new Intent(this, DesignActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, position);
        startActivity(intent);
        //  startActivityForResult(DesignActivity.newIntent(this), IntentRequestCodes.BASIC_SPI_DETAIL);

    }

    public void statusposition() {
        SpiStatusRequest request = new SpiStatusRequest(Prefs.getInt(PrefsConstants.SPI_ID), 3);
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
                        openSPIMainScreen();
                        showToast("going back to main screen");


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

    // open draft spi screen if images rejected
    void openDraftSpiScreen() {
        Intent intent = new Intent(this, DraftSpiActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, position);
        startActivity(intent);


    }


}
