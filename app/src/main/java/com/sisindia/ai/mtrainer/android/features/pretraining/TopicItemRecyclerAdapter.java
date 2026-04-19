package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemTrainingTopicBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.previous.DetailsItem;
import com.squareup.picasso.Picasso;

public class TopicItemRecyclerAdapter extends BaseRecyclerAdapter<DetailsItem> {
    private static final String TAG = "TopicItemRecyclerAdapte";
    private PreTrainingViewListeners viewListeners;

    private Picasso picasso;
    public TopicItemRecyclerAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterItemTrainingTopicBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_training_topic, parent, false);
        return new TopicItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TopicItemViewHolder viewHolder = (TopicItemViewHolder) holder;
        final DetailsItem item = getItem(position);
        viewHolder.onBind(item);

    }

    public void setViewListeners(PreTrainingViewListeners viewListeners) {
        this.viewListeners = viewListeners;
    }

    class TopicItemViewHolder extends BaseViewHolder<DetailsItem> {
        AdapterItemTrainingTopicBinding itemBinding;

        public TopicItemViewHolder(@NonNull AdapterItemTrainingTopicBinding itemBinding) {
            super(itemBinding);
            this.itemBinding = itemBinding;
        }

        @Override
        public void onBind(DetailsItem item) {

            picasso.load(item.getImage())
                    .fit()
                    .into(itemBinding.ivTopicThumbnail);
            itemBinding.setItem(item);
            Log.d(TAG, "onBind: " + item.getPost() + "  "  + item.getAttendence());

            itemBinding.getRoot().setOnClickListener(v -> {
                if (viewListeners != null) {
                    //viewListeners.onTopicItemClick();
                }
            });
        }
    }
}
