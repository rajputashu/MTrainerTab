package com.sisindia.ai.mtrainer.android.features.rota;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterTimeLineBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;

public class TimeLineRecyclerAdapter extends BaseRecyclerAdapter<TimeLineEntity> {
    MtrainerDataBase dataBase;

    public TimeLineRecyclerAdapter(MtrainerDataBase dataBase) {
        this.dataBase=dataBase;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterTimeLineBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_time_line, parent, false);
        return new TimeLineRecyclerAdapter.TimeLineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final TimeLineViewHolder viewHolder = (TimeLineViewHolder) holder;
        final TimeLineEntity item = getItem(position);

        if (item.dutyon.equals("Training Cancelled")) {
                    viewHolder.binding.timeLineImg.setImageResource(R.drawable.ic_cancelled);
                   viewHolder.binding.duty.setText(item.dutyon);
                   viewHolder.binding.time.setText(item.time);
                    viewHolder.binding.km.setText(item.kmmeter);
                }
                else {
                    viewHolder.binding.timeLineImg.setImageResource(R.drawable.ic_completed);
                    viewHolder.binding.duty.setText(item.dutyon);
                    viewHolder.binding.time.setText(item.time);
                    viewHolder.binding.km.setText(item.kmmeter);
                }


        viewHolder.onBind(item);


    }

    public class TimeLineViewHolder extends BaseViewHolder<TimeLineEntity> {
        private final RecyclerAdapterTimeLineBinding binding;

       // public TimeLineRecyclerAdapter1 timeLineRecyclerAdapter = new TimeLineRecyclerAdapter1();


        public TimeLineViewHolder(@NonNull RecyclerAdapterTimeLineBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;

        }

        @Override
        public void onBind(TimeLineEntity item) {
            binding.setAdapterItem(item);


        }
    }
}
