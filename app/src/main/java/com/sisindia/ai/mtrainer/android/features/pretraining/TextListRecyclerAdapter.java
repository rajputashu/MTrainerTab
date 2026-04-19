package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemTextListBinding;

public class TextListRecyclerAdapter extends BaseRecyclerAdapter<String> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterItemTextListBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_text_list, parent, false);

        return new TextListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TextListViewHolder viewHolder = (TextListViewHolder) holder;
        viewHolder.onBind(getItem(position));

    }


    class TextListViewHolder extends BaseViewHolder<String> {
        AdapterItemTextListBinding itemBinding;

        public TextListViewHolder(@NonNull AdapterItemTextListBinding itemBinding) {
            super(itemBinding);
            this.itemBinding = itemBinding;
        }

        @Override
        public void onBind(String item) {
            itemBinding.setTextItem(item);
        }
    }
}
