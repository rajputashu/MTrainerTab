package com.sisindia.ai.mtrainer.android.features.rota;

import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.TRAINING_KIT;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.droidcommons.base.BaseFragment;
import com.droidcommons.preference.Prefs;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentDashBoardRotaBinding;
import com.sisindia.ai.mtrainer.android.features.checkin.CheckInFragment;
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardActivity;
import com.sisindia.ai.mtrainer.android.features.rplform.RplFormFragment;
import com.sisindia.ai.mtrainer.android.features.trainingcalendar.TrainingCalendarFragment;
import com.sisindia.ai.mtrainer.android.features.trainingkit.TrainingKitActivity;
import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;

import java.util.ArrayList;
import java.util.List;

public class DashBoardRotaFragment extends MTrainerBaseFragment {
    private FragmentDashBoardRotaBinding binding;
    private static DashBoardRotaViewModel viewModel;

    public static BaseFragment newInstance() {
        return new DashBoardRotaFragment();
    }

    @Override
    protected void extractBundle() {
    }

    @Override
    protected void initViewModel() {
        viewModel = (DashBoardRotaViewModel) getAndroidViewModel(DashBoardRotaViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentDashBoardRotaBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {
        viewModel.getRotaDetails().observe(this, trainingCalendars -> {
            // Log.d("dashboardfragmenchanged", "" + trainingCalendars.size());
            // List<TrainingCalendarResponse.TrainingCalendar> completedList = new ArrayList<>();
            String percentage = 0 + "% Done";
            List<TrainingCalendarResponse.TrainingCalendar> completedList = new ArrayList<>();
            for (TrainingCalendarResponse.TrainingCalendar e : trainingCalendars) {
                if (e.traningStatusId == 11)
                    completedList.add(e);
            }
            //List<TrainingCalendarResponse.TrainingCalendar> completedList = trainingCalendars.stream().filter(it -> it.traningStatusId == 11).collect(Collectors.toList());
            if (!trainingCalendars.isEmpty())
                percentage = ((int) ((((float) completedList.size()) / ((float) trainingCalendars.size())) * 100)) + "% Done";
            binding.completedRotaCount.setText(percentage);
            // below two lines i commited
            // if(completedList.size() > 0)
            //updatePerformance(completedList);
            viewModel.refreshRecylerView(trainingCalendars);
            //logger.log(trainingCalendars.getClass().getSimpleName());
            //logger.logDb(trainingCalendars, "Training Calender");

            if (!trainingCalendars.isEmpty()) {
                binding.norota.setText("" + View.GONE);
                binding.norota.setText("");
            } else {
                binding.norota.setText("" + View.VISIBLE);
                binding.norota.setText("No Rota");
            }

        });

        viewModel.getPerformanceDetails()
                .observe(this,
                        performanceResponseList -> viewModel.refreshPerfRecylerView(performanceResponseList));

        binding.vpDashBoard.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                try {
                    viewModel.regionalrank.set("" + viewModel.performancelist.get(position).panIndiaRank + "/" + viewModel.performancelist.get(position).totalTrainer);
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

       /* viewModel.canUpdatePerformance().observe(this, new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> integers) {
                viewModel.getDashboardPerformance();
            }
        });
        */

        viewModel.getTimelineDetails1().observe(this,
                timeLineEntities -> viewModel.refreshPerfRecylerView1(timeLineEntities));
    }

    @Override
    protected void onCreated() {
        ((DashBoardActivity) getContext()).showRotaButton();

        intDashBordRatingBar();
        initTabLayout();
        viewModel.getDashboardPerformance();
        //viewModel.getTrainingCalendar();

        // TODO: support, need to check once again.
        if (binding.trainingContent != null)
            binding.trainingContent.setOnClickListener(view -> openTrainingKitScreen());
        if (binding.trainingCalender != null)
            binding.trainingCalender.setOnClickListener(view -> openTrainingCalenderScreen());
        if (binding.rplForm != null)
            binding.rplForm.setOnClickListener(view -> openRplFormScreen());
        if (binding.checkIn != null)
            binding.checkIn.setOnClickListener(view -> openChenInScreen());
    }

    public void openTrainingKitScreen() {
        startActivityForResult(TrainingKitActivity.newIntent(getActivity()), TRAINING_KIT);
    }

    private void openRplFormScreen() {
        // startActivityForResult(RplFormFragment.newInstance(), FRAGMENT_REPLACE, false);
      /*  Fragment fragment = fragmentManager.findFragmentByTag(RplFormFragment.newInstance().getClass().getSimpleName());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flDashBoard, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/
        FragmentTransaction t = this.getFragmentManager().beginTransaction();
        Fragment mFrag = new RplFormFragment();
        t.replace(R.id.flDashBoard, mFrag);
        t.addToBackStack(null);
        t.commit();
    }

    private void openChenInScreen() {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        Fragment fragment = new CheckInFragment();
        fragmentTransaction.replace(R.id.flDashBoard, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openTrainingCalenderScreen() {
        FragmentTransaction fragmentTransaction;
        if (this.getFragmentManager() != null) {
            fragmentTransaction = this.getFragmentManager().beginTransaction();
            Fragment fragment = new TrainingCalendarFragment();
            fragmentTransaction.replace(R.id.flDashBoard, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void intDashBordRatingBar() {
        int companyId = Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID));

        if (companyId == 1 || companyId == 4 || companyId == 8) {
            binding.llBelowTopLayoutDB.setVisibility(View.GONE);
        } else {
            binding.llBelowTopLayoutDB.setVisibility(View.VISIBLE);
        }
    }

    public static void refreshDashboardApi() {
        viewModel.getDashboardPerformance();
        viewModel.getTrainingCalendar();
    }

    @Override
    public void onResume() {
        super.onResume();
        //viewModel.getDashboardPerformance();
        viewModel.getTrainingCalendar();
        ((DashBoardActivity) requireActivity()).showRefreshButton(true);
    }

    private void initTabLayout() {
        new TabLayoutMediator(binding.tlDashBoard, binding.vpDashBoard, (tab, position) -> {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_tab_header_indicator, null);
            tab.setCustomView(view);
        }).attach();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_dash_board_rota;
    }

    /*private void updatePerformance(List<TrainingCalendarResponse.TrainingCalendar> completedList) {
        int totalFeedbackTaken = 0;
        float totalRating = 0;
        int totalGuardTrained = 0;
        int totalUnitCovered = 0;
        double avgRating = 0;
        long totalMin = 0;
        for (TrainingCalendarResponse.TrainingCalendar entry : completedList) {
            if (!entry.givenRating.equals("NA") && Float.valueOf(entry.givenRating) > 0) {
                totalFeedbackTaken = totalFeedbackTaken + 1;
                totalRating = totalRating + Float.valueOf(entry.givenRating);
            }
            if (!(entry.totalTrained.equals("NA")))
                totalGuardTrained = totalGuardTrained + Integer.parseInt(entry.totalTrained);
            if (!entry.savedStartTime.equals("NA") && !entry.savedEndTime.equals("NA"))
                totalMin = totalMin + getTimeDiff(entry.savedStartTime, entry.savedEndTime);
        }


        //totalUnitCovered = (int) completedList.stream().map(it -> it.unitCode).distinct().count();
        Set<String> totalDistinctUnit = new HashSet<>();
        for (TrainingCalendarResponse.TrainingCalendar e : completedList)
            totalDistinctUnit.add(e.unitCode);
        totalUnitCovered = totalDistinctUnit.size();

        double totalTime = ((float) totalMin / 60.0) + (((float) (totalMin % 60)) / 60.0);
        if (totalFeedbackTaken > 0)
            avgRating = (double) totalRating / (double) totalFeedbackTaken;
        //viewModel.updatePerformance(new DecimalFormat("##.#").format(totalTime), new DecimalFormat("##.#").format(avgRating), String.valueOf(totalFeedbackTaken), String.valueOf(completedList.size()), String.valueOf(totalGuardTrained), String.valueOf(totalUnitCovered));
        if (Prefs.getString(PrefsConstants.ROLE).equals("Training Champ") || Prefs.getString(PrefsConstants.ROLE).equals("Unit Commander")) {
            if (totalMin > 30)
                viewModel.updatePerformance(new DecimalFormat("#0.0").format(totalTime), new DecimalFormat("##.#").format(avgRating), String.valueOf(totalFeedbackTaken), String.valueOf(completedList.size()), String.valueOf(totalGuardTrained), String.valueOf(totalUnitCovered));
            else
                viewModel.updatePerformance(String.valueOf(totalMin), new DecimalFormat("##.#").format(avgRating), String.valueOf(totalFeedbackTaken), String.valueOf(completedList.size()), String.valueOf(totalGuardTrained), String.valueOf(totalUnitCovered));
        } else {
            viewModel.updatePerformance(new DecimalFormat("#0.0").format(totalTime), new DecimalFormat("##.#").format(avgRating), String.valueOf(totalFeedbackTaken), String.valueOf(completedList.size()), String.valueOf(totalGuardTrained), String.valueOf(totalUnitCovered));
        }
    }*/

    /*private long getTimeDiff(String startTime, String endTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        Date startDate = new Date();
        Date endDate = new Date();
        long min = 0;
        try {
            startDate = timeFormat.parse(startTime);
            endDate = timeFormat.parse(endTime);
            long diffMs = endDate.getTime() - startDate.getTime();
            long diffSec = diffMs / 1000;
            min = diffSec / 60;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return min;
    }*/

}

