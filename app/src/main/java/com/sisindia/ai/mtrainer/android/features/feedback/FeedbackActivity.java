package com.sisindia.ai.mtrainer.android.features.feedback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityFeedbackBinding;
import com.sisindia.ai.mtrainer.android.models.FeedbackReasonQuestionItem;

import java.util.ArrayList;
import java.util.HashSet;

public class FeedbackActivity extends MTrainerBaseActivity {

    private FeedbackActivityViewModel viewModel;
    private ActivityFeedbackBinding binding;
    // First Entry is Client Name, Second Enter is Email, Third entry is Mobile Number and
    // Fourth is ClientId
    public static ArrayList<String> selectedFeedbackDetails = new ArrayList<>();
    //    public static int rotaid;
    public static float selectedRating = 0;
    public static HashSet<Integer> selectedFeedback = new HashSet<>();
    public static HashSet<FeedbackReasonQuestionItem> selectedReasonList = new HashSet<>();
    public static String feedbackRemarks = "";
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private String totalTopic;
    private String totalEmployee;
    private String totalPost;
    private boolean isDtss = false;


    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, FeedbackActivity.class);
        return intent;
    }


    @Override
    protected void extractBundle() {
        totalTopic = getIntent().getStringExtra("TOTAL_TOPIC");
        totalEmployee = getIntent().getStringExtra("TOTAL_EMPLOYEE");
        totalPost = getIntent().getStringExtra("TOTAL_POST");
        isDtss = getIntent().getBooleanExtra("IS_DTSS", false);
    }

    @Override
    protected void initViewState() {

        liveData.observe(this, message -> {
            switch (message.what) {
                case NavigationConstants.CLIENT_NOT_PRESENT:
                    selectedReasonList.clear();
                    feedbackRemarks = "";
                    selectedFeedbackDetails.clear();
                    selectedRating = 0;
                    if (getSupportFragmentManager().findFragmentByTag(FeedbackBottomSheet.class.getSimpleName()) == null)
                        ClientNABottomSheet.newInstance().show(getSupportFragmentManager(), ClientNABottomSheet.class.getSimpleName());
                    break;

                case NavigationConstants.CLIENT_REPRESENTATIVE:
                    selectedFeedbackDetails.clear();
                    selectedReasonList.clear();
                    feedbackRemarks = "";
                    selectedRating = 0;
                    if (getSupportFragmentManager().findFragmentByTag(FeedbackBottomSheet.class.getSimpleName()) == null)
                        FeedbackBottomSheet.newInstance().show(getSupportFragmentManager(), FeedbackBottomSheet.class.getSimpleName());
                    break;

                case NavigationConstants.CLOSE_FEEDBACK_ACTIVITY:
                    Intent data = new Intent();
                    Prefs.putFloatOnMainThread(PrefsConstants.RATING, selectedRating);
                    String rating = String.valueOf(selectedRating);
                    data.putExtra("RATING", rating);
                    setResult(RESULT_OK, data);
                    finish();
                    break;

                case NavigationConstants.SAVE_FEEDBACK_DATA:
                    viewModel.initLocalSaving();
                    break;

                case NavigationConstants.CLIENT_RATING:
//                    Fragment fragment = fragmentManager.findFragmentByTag(FeedbackRatingFragment.newInstance().getClass().getSimpleName());
                    if (fragmentManager.findFragmentByTag(FeedbackRatingFragment.newInstance().getClass().getSimpleName()) == null) {
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        Fragment fragment = FeedbackRatingFragment.newInstance();
                        transaction.replace(R.id.feedback_fragment_container, fragment, fragment.getClass().getSimpleName());
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        binding.savedTrainningDetailContainer.setVisibility(View.VISIBLE);
                        binding.includeTimeSpent.timeSpentContainer.setVisibility(View.GONE);
                    }
                    break;

                case NavigationConstants.FINAL_FEEDBACK:
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment finalFragment = fragmentManager.findFragmentByTag(FeedbackFinalFragment.newInstance().getClass().getSimpleName());
                    if (finalFragment == null) {
                        selectedReasonList.clear();
                        feedbackRemarks = "";
                        FragmentTransaction finalTransaction = fragmentManager.beginTransaction();
                        Fragment fragment = FeedbackFinalFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("IS_DTSS", isDtss);
                        fragment.setArguments(bundle);
                        finalTransaction.replace(R.id.feedback_fragment_container, fragment, fragment.getClass().getSimpleName());
                        finalTransaction.addToBackStack(null);
                        finalTransaction.commitAllowingStateLoss();
                    } else {
                        if (!isDtss && selectedReasonList.isEmpty()) {
                            Toast.makeText(FeedbackActivity.this, "Please select at least one reason", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                if (isDtss) {
                                    if (feedbackRemarks.isEmpty())
                                        Toast.makeText(FeedbackActivity.this, "Please write remarks", Toast.LENGTH_SHORT).show();
                                    else
                                        viewModel.initLocalSaving();
                                } else {
                                    try {
//                                        viewModel.feedbackOtp();
                                        setFeedbackOtpData();
                                        if (getSupportFragmentManager().findFragmentByTag(FeedbackOtpBottomSheet.class.getSimpleName()) == null) {
                                            FeedbackOtpBottomSheet f = FeedbackOtpBottomSheet.newInstance();
                                            f.setCancelable(false);
                                            f.show(getSupportFragmentManager(), FeedbackOtpBottomSheet.class.getSimpleName());
                                        }
                                    } catch (Exception e) {
                                        logger.log("exception in feedbackactivity", e);
                                    }
                                }
                            } catch (Exception e) {
                                logger.log("exception in feedbackactivity livedata", e);
                            }
                        }
                    }
            }
        });

        // for write this code in this activity saved topic counts from table
        viewModel.getSavedList().observe(this,
                savedTopics -> viewModel.selectedTopic.set(savedTopics.size()));
    }

    /*private void bindReviewInformationTime(int seconds) {
        binding.includeTimeSpent.tvTimeSpent.setText(TimeUtils.convertIntSecondsToHHMMSS(seconds));
    }*/

    @Override
    protected void onCreated() {
        viewModel.getRatingMaster();
        viewModel.getContactMaster();
//        rotaid = Prefs.getInt(PrefsConstants.ROTA_ID);
        setupToolBarForBackArrow(binding.tbTakeFeedback);
        binding.savedTrainningDetailContainer.setVisibility(View.GONE);
        binding.feedbackActivityFab.setOnClickListener(v -> {
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(FeedbackRatingFragment.newInstance().getClass().getSimpleName());
                if (fragment != null) {
                    if (selectedRating == 0) {
                        Toast.makeText(FeedbackActivity.this, "Give Ratting", Toast.LENGTH_SHORT).show();
                    } else if ((selectedRating >= 3 && !isDtss) || (selectedRating > 3 && isDtss)) {
                        if (isDtss) {
                            viewModel.initLocalSaving();
                        } else if (getSupportFragmentManager().findFragmentByTag(FeedbackOtpBottomSheet.class.getSimpleName()) == null) {
                            try {
                                setFeedbackOtpData();
//                                viewModel.feedbackOtp();
//                                FeedbackOtpBottomSheet.newInstance().show(getSupportFragmentManager(), FeedbackOtpBottomSheet.class.getSimpleName());
                                FeedbackOtpBottomSheet f = FeedbackOtpBottomSheet.newInstance();
                                f.setCancelable(false);
                                f.show(getSupportFragmentManager(), FeedbackOtpBottomSheet.class.getSimpleName());
                            } catch (Exception e) {
                                logger.log("Exception in feedback activity fab button", e);
                            }
                        }
                    } else {
                        viewModel.startFinalFeedback();
                    }
                } else if (!selectedFeedback.isEmpty()) {
                    viewModel.nextFabPressed(selectedFeedback.iterator().next());
                } else
                    Toast.makeText(FeedbackActivity.this, "No Option Selected", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        });
        binding.includeTimeSpent.tvTimeSpent.setText(Prefs.getString(PrefsConstants.STARTED_TIME));
        selectedFeedback.clear();
        //loadFragment(R.id.feedback_fragment_container, MainFeedbackFragment.newInstance(), FRAGMENT_REPLACE, true);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.feedback_fragment_container, MainFeedbackFragment.newInstance(), MainFeedbackFragment.newInstance().getClass().getSimpleName());
        //transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private void setFeedbackOtpData() {
        Prefs.putString(PrefsConstants.CLIENT_EMAIL, selectedFeedbackDetails.get(1));
        Prefs.putString(PrefsConstants.CLIENT_PHONE_NUMBER, selectedFeedbackDetails.get(2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_take_assessment, menu);
//        return super.onCreateOptionsMenu(menu);
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViewBinding() {

        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (FeedbackActivityViewModel) getAndroidViewModel(FeedbackActivityViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_feedback;
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            binding.savedTrainningDetailContainer.setVisibility(View.GONE);
            selectedRating = 0;
            binding.includeTimeSpent.timeSpentContainer.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    void onSuccessFinishActivity() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.updateSummaryUI(totalTopic, totalEmployee, totalPost);
    }
}