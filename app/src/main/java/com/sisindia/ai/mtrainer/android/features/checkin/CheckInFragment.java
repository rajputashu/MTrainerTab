package com.sisindia.ai.mtrainer.android.features.checkin;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.droidcommons.base.BaseFragment;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentCheckInBinding;
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardActivity;
import com.sisindia.ai.mtrainer.android.models.CancelTrainingResponse;
import com.sisindia.ai.mtrainer.android.models.CheckInRequest;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CheckInFragment extends MTrainerBaseFragment {

    private FragmentCheckInBinding binding;
    private CheckInViewModel viewModel;

    String val = "";

    @Inject
    DashBoardApi dashBoardApi;

    public static BaseFragment newInstance() {
        return new CheckInFragment();
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (CheckInViewModel) getAndroidViewModel(CheckInViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentCheckInBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {

    }

    @Override
    protected void onCreated() {
        ((DashBoardActivity) getContext()).hideRotaButton();

        binding.rgIssue.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_1) {
                    val = "Branch";
                    binding.otherIsue.setVisibility(View.GONE);

                } else if (checkedId == R.id.rb_2) {
                    val = "Region";
                    binding.otherIsue.setVisibility(View.GONE);


                } else if (checkedId == R.id.rb_3) {
                    val = "Corporate";
                    binding.otherIsue.setVisibility(View.GONE);


                } else if (checkedId == R.id.rb_4) {
                    val = "Client Meeting";
                    binding.otherIsue.setVisibility(View.GONE);


                } else if (checkedId == R.id.rb_5) {
                    val = "Special Duty";
                    binding.otherIsue.setVisibility(View.GONE);


                } else if (checkedId == R.id.rb_6) {
                    val = "Others ";
                    binding.otherIsue.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.buttonCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.rgIssue.getCheckedRadioButtonId() != -1) {

                    double lat = Prefs.getDouble(PrefsConstants.LAT);
                    double longi = Prefs.getDouble(PrefsConstants.LONGI);
                    String rotaid = String.valueOf(Prefs.getInt(PrefsConstants.ROTA_ID));
                    CheckInRequest request = new CheckInRequest(rotaid, lat, longi, val, binding.otherIsue.getText().toString());

                    dashBoardApi.getCheckIn(request)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::onCheckinTrainingSuccess);
                } else {
                    showToast("Please Select any one !");
                }
            }

            private void onCheckinTrainingSuccess(CancelTrainingResponse response) {

                Toast.makeText(getContext(), response.statusMessage, Toast.LENGTH_SHORT).show();

                if (response.statusCode == SUCCESS_RESPONSE) {
                    // getActivity().finish();
                    getActivity().getFragmentManager().popBackStack();
                    showToast("You have successfully Checked in");
                }
            }
        });


        binding.trainerToolbar.toolBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


    }


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_check_in;
    }
}
