package com.sisindia.ai.mtrainer.android.features.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterFeedbackFinalItemBinding;
import com.sisindia.ai.mtrainer.android.databinding.ClientNaItemBinding;
import com.sisindia.ai.mtrainer.android.models.FeedbackReasonQuestionItem;

import java.util.HashSet;

public class FeedbackFinalRecylerAdapter extends BaseRecyclerAdapter<FeedbackReasonQuestionItem> {
    private HashSet<String> selectedOption = new HashSet<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterFeedbackFinalItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_feedback_final_item, parent, false);

        return new FeedbackFinalRecylerAdapter.FeedbackFinalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final FeedbackFinalRecylerAdapter.FeedbackFinalViewHolder viewHolder= (FeedbackFinalRecylerAdapter.FeedbackFinalViewHolder) holder;
        final FeedbackReasonQuestionItem item = getItem(position);
        viewHolder.onBind(item);
        if(FeedbackActivity.selectedReasonList.contains(item))
            viewHolder.binding.finalCheckbox.setChecked(true);
        else
            viewHolder.binding.finalCheckbox.setChecked(false);
        /*viewHolder.binding.finalCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(viewHolder.binding.finalCheckbox.isChecked()){
                    selectedOption.clear();
                    notifyDataSetChanged();
                } else {
                    selectedOption.clear();
                    selectedOption.add(item);
                    notifyDataSetChanged();
                }
            }
        });*/
        viewHolder.binding.feedbackItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewHolder.binding.finalCheckbox.isChecked()){
                    FeedbackActivity.selectedReasonList.remove(item);
                    notifyDataSetChanged();
                } else {
                    FeedbackActivity.selectedReasonList.add(item);
                    notifyDataSetChanged();
                }
            }
        });
    }
    class FeedbackFinalViewHolder extends BaseViewHolder<FeedbackReasonQuestionItem> {
        public AdapterFeedbackFinalItemBinding binding;

        public FeedbackFinalViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding= (AdapterFeedbackFinalItemBinding) itemBinding;
        }

        @Override
        public void onBind(FeedbackReasonQuestionItem item) {
            binding.setTextItem(item);
        }
    }

}
