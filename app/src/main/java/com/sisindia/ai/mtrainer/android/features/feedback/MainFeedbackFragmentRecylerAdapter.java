package com.sisindia.ai.mtrainer.android.features.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.FeedbackRecylerItemBinding;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.ContactListResponse;

import java.util.HashSet;
import java.util.List;

import static com.sisindia.ai.mtrainer.android.features.feedback.FeedbackActivity.selectedFeedback;
import static com.sisindia.ai.mtrainer.android.features.feedback.FeedbackActivity.selectedFeedbackDetails;

public class MainFeedbackFragmentRecylerAdapter extends BaseRecyclerAdapter<ContactListResponse.ClientData> {

    private ClearActivityView clearActivityView;

    public MainFeedbackFragmentRecylerAdapter(ClearActivityView clearActivityView) {
        this.clearActivityView = clearActivityView;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        FeedbackRecylerItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.feedback_recyler_item, parent, false);

        return new MainFeedbackFragmentRecylerAdapter.FeedBackActivityViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MainFeedbackFragmentRecylerAdapter.FeedBackActivityViewHolder viewHolder= (MainFeedbackFragmentRecylerAdapter.FeedBackActivityViewHolder) holder;
        final ContactListResponse.ClientData item = getItem(position);
        viewHolder.onBind(item);
        if(selectedFeedback.contains(item.clientId))
            viewHolder.binding.feedbackRadioBtn.setChecked(true);
        else
            viewHolder.binding.feedbackRadioBtn.setChecked(false);
        viewHolder.binding.feedbackItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearActivityView.clearActivityView();
                if(viewHolder.binding.feedbackRadioBtn.isChecked()) {
                    selectedFeedback.clear();
                    selectedFeedbackDetails.clear();
                    notifyDataSetChanged();
                } else {
                    selectedFeedback.clear();
                    selectedFeedback.add(item.clientId);
                    if(item.clientId >= 1) {
                        selectedFeedbackDetails.clear();
                        selectedFeedbackDetails.add(item.clientName);
                        selectedFeedbackDetails.add(item.email);
                        selectedFeedbackDetails.add(item.firstMobileNumber);
                        selectedFeedbackDetails.add(String.valueOf(item.clientId));
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }
    class FeedBackActivityViewHolder extends BaseViewHolder<ContactListResponse.ClientData> {
        public FeedbackRecylerItemBinding binding;

        public FeedBackActivityViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding= (FeedbackRecylerItemBinding) itemBinding;
        }
        @Override
        public void onBind(ContactListResponse.ClientData item) {
            binding.setTextItem(item);
        }
    }
    public interface ClearActivityView {
        void clearActivityView();
    }
}
