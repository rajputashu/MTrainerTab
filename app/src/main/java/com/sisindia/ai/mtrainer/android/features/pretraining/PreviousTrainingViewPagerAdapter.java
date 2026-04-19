package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.commons.TrainingTopicsRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemPreviousTrainingBinding;
import com.sisindia.ai.mtrainer.android.models.previous.DataItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PreviousTrainingViewPagerAdapter extends BaseRecyclerAdapter<DataItem> {


    private PreTrainingViewListeners listeners;
    private Picasso picasso;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterItemPreviousTrainingBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_previous_training, parent, false);

        return new PreviousTrainingViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final PreviousTrainingViewHolder viewHolder = (PreviousTrainingViewHolder) holder;
        final DataItem item = getItem(position);
        viewHolder.onBind(item);
    }

    public void setListeners(PreTrainingViewListeners listeners) {
        this.listeners = listeners;
    }

    public void setPicasso(Picasso picasso) {
        this.picasso = picasso;
    }

    class PreviousTrainingViewHolder extends BaseViewHolder< DataItem> {

        private final AdapterItemPreviousTrainingBinding binding;
        //here i commented for viewpagew topics name adding

        public CoveredTopicsRecyclerAdapter topicsCoveredRecyclerAdapter = new CoveredTopicsRecyclerAdapter();
        public TopicItemRecyclerAdapter topicItemRecyclerAdapter = new TopicItemRecyclerAdapter(picasso);

        public PreviousTrainingViewHolder(@NonNull AdapterItemPreviousTrainingBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }
        @Override
        public void onBind(DataItem item) {
            binding.setItem(item);
            binding.setTopicsCoveredRecyclerAdapter(topicsCoveredRecyclerAdapter);
            binding.setTopicItemRecyclerAdapter(topicItemRecyclerAdapter);
            binding.setViewListeners(listeners);

            /*ArrayList<String> strings = new ArrayList<>();
            strings.add("Maintenance of fire safety equipments");
            strings.add("Fire evacuation in high rise");*/
            topicsCoveredRecyclerAdapter.clearAndSetItems(item.getTopic());

            /*ArrayList<Object> list = new ArrayList<>();
            list.add("Maintenance of fire safety equipments");
            list.add("Fire evacuation in high rise");
            list.add("Fire evacuation in high rise");
            list.add("Fire evacuation in high rise");
            list.add("Fire evacuation in high rise");
            list.add("Fire evacuation in high rise");*/
            topicItemRecyclerAdapter.clearAndSetItems(item.getDetails());
            binding.getRoot().setOnClickListener(v -> {

            });
        }
    }
}
