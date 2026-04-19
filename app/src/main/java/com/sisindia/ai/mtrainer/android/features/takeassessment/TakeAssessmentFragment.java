package com.sisindia.ai.mtrainer.android.features.takeassessment;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_COURSE_TOPIC_CHIP_SELECTED;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_REMOVING_ALL_CHIPS;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.UPDATE_ASSESSMENT_VIEW;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.droidcommons.preference.Prefs;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentTakeAssessmentBinding;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.models.assesments.CourseListResponse;
import com.sisindia.ai.mtrainer.android.models.assesments.TopicListResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TakeAssessmentFragment extends MTrainerBaseFragment {

    private FragmentTakeAssessmentBinding binding;
    private TakeAssessmentViewModel viewModel;

    public static TakeAssessmentFragment newInstance() {
        return new TakeAssessmentFragment();
    }

    @Override
    protected void extractBundle() {
    }

    @Override
    protected void initViewModel() {
        viewModel = (TakeAssessmentViewModel) getAndroidViewModel(TakeAssessmentViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {
        viewModel.initviewmodel();
        viewModel.getPostList().observe(this, postItems -> {
            if (!(Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8")))
                inflatePostChip(postItems);
            else {
                inflatePostChip(new ArrayList<>());
            }
        });
        liveData.observe(this, message -> {
            if (message.what == UPDATE_ASSESSMENT_VIEW)
                updateFab();
            /*else if (message.what == ON_COURSE_CHIP_SELECTED)
                addCourseChip((CourseListResponse.CourseListData) message.obj);*/
            else if (message.what == ON_COURSE_TOPIC_CHIP_SELECTED)
                addTopicChips((TopicListResponse.TopicListData) message.obj);
            else if (message.what == ON_REMOVING_ALL_CHIPS) {
                if (viewModel.topicIdList != null) {
                    viewModel.topicIdList.clear();
                }
                binding.chipGroup.removeAllViews();
            }
        });
    }

    @Override
    protected void onCreated() {
        viewModel.fetchCourseTypeList();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_take_assessment;
    }

    private void inflatePostChip(List<PostItem> postList) {

        View view = binding.getRoot();
        ChipGroup chipGroup = view.findViewById(R.id.post_container);
        chipGroup.removeAllViews();
        SparseArray<PostItem> map = new SparseArray<>();

        for (PostItem s : postList) {
            Chip chip =
                    (Chip) getLayoutInflater().inflate(R.layout.single_post_item_chip, chipGroup, false);
            chip.setText(s.postName);
            chipGroup.addView(chip);
            map.append(chip.getId(), s);
            if (s.postId == 0) {
                chip.setChecked(true);
                AssessmentViewModel.selectedPost = s;
            }
        }

        chipGroup.setOnCheckedChangeListener((group, checkedId) ->
                AssessmentViewModel.selectedPost = map.get(checkedId));
    }

    /*
        private void updateFab() {
            if(PreTrainingReviewActivity.selectedAssessmentEmpId.isEmpty()) {
                binding.flButtonTraining.setAlpha(0.3f);
            } else {
                binding.flButtonTraining.setAlpha(1f);
            }
        }*/
    private void updateFab() {
        if (PreTrainingReviewActivity.selectedAssessmentEmpId.isEmpty()) {
            binding.flButtonTraining.setAlpha(0.3f);
        } else {
            binding.flButtonTraining.setAlpha(1f);
        }
    }

    private void addCourseChip(CourseListResponse.CourseListData courseDataMO) {
        String courseName = courseDataMO.courseName;
        for (int i = 0; i < Objects.requireNonNull(binding.chipGroup).getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(courseName)) {
                Toast.makeText(getActivity(), "Course Already Selected", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Chip chip = new Chip(requireActivity());
        chip.setText(courseName);
        chip.setTag(String.valueOf(courseDataMO.courseId));
        chip.setCloseIconVisible(true);
        chip.setClickable(true);
        chip.setCheckable(false);

        chip.setOnCloseIconClickListener(v -> {
            binding.chipGroup.removeView(chip);
//            int courseId = (int) chip.getTag();
            String courseId = (String) chip.getTag();
            viewModel.coursesIdList.remove(courseId);
        });

        binding.chipGroup.addView(chip);
        viewModel.coursesIdList.add(String.valueOf(courseDataMO.courseId));
    }

    private void addTopicChips(TopicListResponse.TopicListData topicData) {
        String topicName = topicData.topicName;
        for (int i = 0; i < Objects.requireNonNull(binding.chipGroup).getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(topicName)) {
                Toast.makeText(getActivity(), "Topic Already Selected", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Chip chip = new Chip(requireActivity());
        chip.setText(topicName);
        chip.setTag(String.valueOf(topicData.topicId));
        chip.setCloseIconVisible(true);
        chip.setClickable(true);
        chip.setCheckable(false);

        chip.setOnCloseIconClickListener(v -> {
            binding.chipGroup.removeView(chip);
            String topicId = (String) chip.getTag();
            viewModel.topicIdList.remove(topicId);
        });

        binding.chipGroup.addView(chip);
        viewModel.topicIdList.add(String.valueOf(topicData.topicId));
    }
}
