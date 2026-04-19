package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;

import com.sisindia.ai.mtrainer.android.databinding.AdapterCoveredTopicsBinding;

/**
 * Used for Showing topics name
 */
public class CoveredTopicsRecyclerAdapter  extends BaseRecyclerAdapter<String> {
    

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

         AdapterCoveredTopicsBinding binding= DataBindingUtil.inflate(inflater, R.layout.adapter_covered_topics, parent, false);

        return new CoveredTopicsRecyclerAdapter.CoveredTopicsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final String item = getItem(position);

        final CoveredTopicsViewHolder viewHolder = (CoveredTopicsViewHolder) holder;

        viewHolder.onBind(item);

    }

    public class CoveredTopicsViewHolder extends BaseViewHolder<String>{
        private AdapterCoveredTopicsBinding binding;

        public CoveredTopicsViewHolder(@NonNull AdapterCoveredTopicsBinding itemBinding) {
            super(itemBinding);
            this.binding=itemBinding;
        }



        @Override
        public void onBind(String item) {
            binding.setItem(item);

        }
    }
}

