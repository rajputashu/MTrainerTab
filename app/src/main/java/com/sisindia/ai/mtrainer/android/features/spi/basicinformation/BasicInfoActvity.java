package com.sisindia.ai.mtrainer.android.features.spi.basicinformation;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.droidcommons.preference.Prefs;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentBasicInfoBinding;
import com.sisindia.ai.mtrainer.android.databinding.SpiPostDialogBinding;
import com.sisindia.ai.mtrainer.android.features.spi.SpiMainActivity;
import com.sisindia.ai.mtrainer.android.features.spi.draftspi.DraftSpiActivity;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostRequest;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoRequest;

import java.util.ArrayList;
import java.util.List;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.features.spi.SpiPageConstanstKt.BASIC_INFO_PAGE;

public class BasicInfoActvity extends MTrainerBaseActivity {
    private BasicInformationViewModel viewModel;
    private FragmentBasicInfoBinding binding;
    String allpost = "", allpostavailable = "", missingpost = "", verifiedpost = "";
    int branchid, typeid, unitid, spiCustomerid;
    private int position;
    private boolean isSpiCompleted = false;
    String[] content;
    private Dialog dialog;
    List<SpiPostResponse.SpiPostdata> postData = new ArrayList<>();
    StringBuilder builder;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, BasicInfoActvity.class);
        return intent;
    }

    @Override
    protected void extractBundle() {
        position = getIntent().getIntExtra(NavigationConstants.SPI_SPINNER_POSITION, -1);
        isSpiCompleted = getIntent().getBooleanExtra(NavigationConstants.IS_SPI_COMPLETED, false);
    }

    @Override
    protected void initViewModel() {
        viewModel = (BasicInformationViewModel) getAndroidViewModel(BasicInformationViewModel.class);
    }

    @Override
    protected void initViewState() {

        liveData.observe(this, message -> {
            switch (message.what) {
                case OPEN_DRAFT_SPI_SCREEN:
                    openDraftSpiScreen();
                    break;
            }
        });

        /*if (isSpiCompleted)
            viewModel.getPendingSpiPostList().observe(this, new Observer<List<SpiPostResponse.SpiPostdata>>() {
                @Override
                public void onChanged(List<SpiPostResponse.SpiPostdata> postnameData) {
                    viewModel.setSpiPost(postnameData);
                    postData.addAll(postnameData);
                }
            });
        else*/
            viewModel.getSpiPostist().observe(this, new Observer<List<SpiPostResponse.SpiPostdata>>() {
                @Override
                public void onChanged(List<SpiPostResponse.SpiPostdata> postnameData) {
                    viewModel.setSpiPost(postnameData);
                    postData.addAll(postnameData);
                }
            });
    }

    private void openDraftSpiScreen() {
        Intent intent = new Intent(this, DraftSpiActivity.class);
        intent.putExtra(NavigationConstants.SPI_SPINNER_POSITION, position);
        startActivity(intent);
        //  startActivityForResult(DraftSpiActivity.newIntent(this), IntentRequestCodes.BASIC_SPI_DETAIL);
    }

    @Override
    protected void onCreated() {
        branchid = Prefs.getInt(PrefsConstants.SPI_BRANCHID);
        typeid = Prefs.getInt(PrefsConstants.SPI_TYPEID);
        unitid = Prefs.getInt(PrefsConstants.SPI_UNIT_ID);
        spiCustomerid = Prefs.getInt(PrefsConstants.CUSTOMER_ID);

        // Getting posts
        SpiPostRequest spiPostRequest = new SpiPostRequest(unitid);
        viewModel.getBasicInfopost(spiPostRequest);
        if(isSpiCompleted)
            Log.v("Checking","SPI Completed");
            // TODO: Fetch Completed Posts or Chain them
        // Fetch Completed Posts

        binding.rgAllPost.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.yes_radio_btn) {
                    allpost = "1";
                } else if (checkedId == R.id.no_radio_btn) {
                    allpost = "2";
                }
            }
        });

        binding.rgAllPostAvailable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.yes_available_radio_btn) {
                    allpostavailable = "1";

                } else if (checkedId == R.id.no_available_radio_btn) {
                    allpostavailable = "2";


                }
            }
        });

        binding.rgMissingPost.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.yes_missing_radio_btn) {
                    missingpost = "1";

                } else if (checkedId == R.id.no_missing_radio_btn) {
                    missingpost = "2";

                } else if (checkedId == R.id.notapplicable_radio_btn) {
                    missingpost = "3";

                }
            }
        });

        binding.rgVeriedPostAvailable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.yes_veried_radio_btn) {
                    verifiedpost = "1";

                } else if (checkedId == R.id.no_verified_radio_btn) {
                    verifiedpost = "2";

                }
            }
        });

        binding.basicinfoButton.setOnClickListener(v -> {
            if(viewModel.isCompleted.get()) {
                viewModel.getBasicInfoForCompleted();
            }
            else if (!binding.etPostcount.getText().toString().equals("") && !allpost.equals("") && !allpostavailable.equals("")
                    && !missingpost.equals("") && !verifiedpost.equals("")) {
                SpiBasicInfoRequest request = new SpiBasicInfoRequest(spiCustomerid, Prefs.getInt(PrefsConstants.BASE_TRAINER_ID), branchid,
                        Prefs.getString(PrefsConstants.COMPANY_ID), typeid, unitid, allpost, binding.etPostcount.getText().toString(), allpostavailable
                        , missingpost, verifiedpost);
                viewModel.getBasicInfo(request);
            } else {
                showToast("Please Select All Basic Info");
            }
        });
        statusposition();

        binding.spiPostName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*for(int i = 0; i < postData.size(); i++) {
                    if (postData.size()>0) {
                        content=(postData.get(i).postName)+"\n"; }
                }*/
                //if(isSpiCompleted)
                    openPostDialog();
                /*else {
                    content = new String[postData.size()];
                    for (int i = 0; i < postData.size(); i++) {
                        content[i] = postData.get(i).postName + "\n";
                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getRootView().getContext());
                    View dialogview = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.spi_dialog, null);
                    alertDialog.setView(dialogview);
                    alertDialog.setTitle("Post Names");
                    ListView lv = (ListView) dialogview.findViewById(R.id.listView1);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BasicInfoActvity.this, android.R.layout.simple_list_item_1, content);
                    lv.setAdapter(adapter);
                    alertDialog.show();
                }*/
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
    protected int getLayoutResource() {
        return R.layout.fragment_basic_info;
    }
/*

  public void statusposition(){
      SpiStatusRequest request= new SpiStatusRequest(spiCustomerid,Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),branchid,
              Prefs.getString(PrefsConstants.COMPANY_ID),typeid,unitid,"basic Info",11);
      viewModel.getBasicInfoStatus(request);
  }
*/


    public void statusposition() {
        SpiStatusRequest request = new SpiStatusRequest(Prefs.getInt(PrefsConstants.SPI_ID), BASIC_INFO_PAGE);
        viewModel.getBasicInfoStatus(request);
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

    void openPostDialog() {
        dialog = new Dialog(this);
        SpiPostDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.spi_post_dialog, null, false);
        dialogBinding.setVm(viewModel);
        dialogBinding.closeBtn.setOnClickListener(v -> {
            if(dialog != null && dialog.isShowing())
                dialog.dismiss();
        });
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}