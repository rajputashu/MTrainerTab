package com.sisindia.ai.mtrainer.android.features.myattendance;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemTestBinding;

public class MyAttendanceRecyclerAdapter extends BaseRecyclerAdapter<Object> {


    private MyAttendanceViewListeners listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterItemTestBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_test, parent, false);

        return new MyAttendanceViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyAttendanceViewHolder viewHolder = (MyAttendanceViewHolder) holder;
        final Object item = getItem(position);
        viewHolder.onBind(item);
    }


    public void setListener(MyAttendanceViewListeners listener) {
        this.listener = listener;
    }

    class MyAttendanceViewHolder extends BaseViewHolder<Object> {

        private final AdapterItemTestBinding binding;

        public MyAttendanceViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding = (AdapterItemTestBinding) itemBinding;
        }

        @Override
        public void onBind(Object item) {
            binding.setItem(item);
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    // TODO: 2020-03-06 implement item click here
                }
            });
        }
    }
}
