package com.sisindia.ai.mtrainer.android.commons;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemTraningTopicBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;

public class TrainingTopicsRecyclerAdapter extends BaseRecyclerAdapter<ChooseTopicsResponse.TopicsResponse> {

    private MtrainerDataBase dataBase;

    public TrainingTopicsRecyclerAdapter(MtrainerDataBase dataBase) {
        this.dataBase=dataBase;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterItemTraningTopicBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_traning_topic, parent, false);

        return new TrainingTopicsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final TrainingTopicsViewHolder viewHolder = (TrainingTopicsViewHolder) holder;
        final ChooseTopicsResponse.TopicsResponse item = getItem(position);
        viewHolder.onBind(item);
    }


    class TrainingTopicsViewHolder extends BaseViewHolder<ChooseTopicsResponse.TopicsResponse> {
        private AdapterItemTraningTopicBinding binding;

        public TrainingTopicsViewHolder(@NonNull AdapterItemTraningTopicBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }

        @Override
        public void onBind(ChooseTopicsResponse.TopicsResponse item) {
            binding.setTextItem(item);
        }
    }
}
