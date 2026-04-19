package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemTrainingTopicItemBinding;

import java.util.ArrayList;

public class TrainingTopicItemViewPagerAdapter extends BaseRecyclerAdapter<Object> {


    private PreTrainingViewListeners listeners;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterItemTrainingTopicItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_training_topic_item, parent, false);

        return new TrainingTopicItemViewPagerViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TrainingTopicItemViewPagerViewHolder viewHolder = (TrainingTopicItemViewPagerViewHolder) holder;
        final Object item = getItem(position);
        viewHolder.onBind(item);
    }

    public void setListeners(PreTrainingViewListeners listeners) {
        this.listeners = listeners;
    }

    class TrainingTopicItemViewPagerViewHolder extends BaseViewHolder<Object> {

        private final AdapterItemTrainingTopicItemBinding itemBinding;

        public TrainingTopicListRecyclerAdapter listRecyclerAdapter = new TrainingTopicListRecyclerAdapter();

        public TrainingTopicItemViewPagerViewHolder(@NonNull AdapterItemTrainingTopicItemBinding itemBinding) {
            super(itemBinding);
            this.itemBinding = itemBinding;
        }

        @Override
        public void onBind(Object item) {
            Glide.with(itemBinding.getRoot()).load(R.drawable.static_image).into(itemBinding.ivThumbnail);
            itemBinding.setListRecyclerAdapter(listRecyclerAdapter);

            ArrayList<Object> list = new ArrayList<>();
            list.add("Maintenance of fire safety equipments");
            list.add("Fire evacuation in high rise");
            list.add("Fire evacuation in high rise");
            list.add("Fire evacuation in high rise");
            list.add("Fire evacuation in high rise");
            list.add("Fire evacuation in high rise");

            listRecyclerAdapter.clearAndSetItems(list);

            itemBinding.getRoot().setOnClickListener(v -> {
                if (listeners != null) {
                    listeners.openTrainingDetail();
                }
            });
        }
    }
}
