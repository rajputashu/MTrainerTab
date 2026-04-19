package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemTrainingDetailImageBinding;

public class TrainingDetailImageViewPagerAdapter extends BaseRecyclerAdapter<Object> {


    private PreTrainingViewListeners listeners;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterItemTrainingDetailImageBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_training_detail_image, parent, false);

        return new TrainingDetailImageViewPagerViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TrainingDetailImageViewPagerViewHolder viewHolder = (TrainingDetailImageViewPagerViewHolder) holder;
        final Object item = getItem(position);
        viewHolder.onBind(item);
    }

    public void setListeners(PreTrainingViewListeners listeners) {
        this.listeners = listeners;
    }

    class TrainingDetailImageViewPagerViewHolder extends BaseViewHolder<Object> {

        private final AdapterItemTrainingDetailImageBinding itemBinding;


        public TrainingDetailImageViewPagerViewHolder(@NonNull AdapterItemTrainingDetailImageBinding itemBinding) {
            super(itemBinding);
            this.itemBinding = itemBinding;
        }

        @Override
        public void onBind(Object item) {

        }
    }
}
