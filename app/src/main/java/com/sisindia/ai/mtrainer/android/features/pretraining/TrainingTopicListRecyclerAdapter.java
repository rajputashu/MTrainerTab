package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemTrainingTopicListBinding;

public class TrainingTopicListRecyclerAdapter extends BaseRecyclerAdapter<Object> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterItemTrainingTopicListBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_training_topic_list, parent, false);

        return new TrainingTopicListViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TrainingTopicListViewHolder viewHolder = (TrainingTopicListViewHolder) holder;
        final Object item = getItem(position);
        viewHolder.onBind(item);
    }


    class TrainingTopicListViewHolder extends BaseViewHolder<Object> {

        private final AdapterItemTrainingTopicListBinding itemBinding;


        public TrainingTopicListViewHolder(@NonNull AdapterItemTrainingTopicListBinding itemBinding) {
            super(itemBinding);
            this.itemBinding = itemBinding;
        }

        @Override
        public void onBind(Object item) {

        }
    }
}
