package com.sisindia.ai.mtrainer.android.features.timeline;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterTimeLine1Binding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;

public class TimeLineRecyclerAdapter1 extends BaseRecyclerAdapter<TimeLineEntity> {
    MtrainerDataBase dataBase;

    public TimeLineRecyclerAdapter1(MtrainerDataBase dataBase) {
        this.dataBase=dataBase;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterTimeLine1Binding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_time_line1, parent, false);
        return new TimeLineRecyclerAdapter1.TimeLineViewHolder(binding);

}

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final TimeLineRecyclerAdapter1.TimeLineViewHolder viewHolder = (TimeLineRecyclerAdapter1.TimeLineViewHolder) holder;
        final TimeLineEntity item = getItem(position);
        //viewHolder.onBind(item);
            if (item.dutyon.trim().equals("Training Cancelled")) {
                viewHolder.binding.timeLineImg.setImageResource(R.drawable.ic_cancelled);
                viewHolder.binding.duty.setText(item.dutyon);
                viewHolder.binding.time.setText(item.time);
                viewHolder.binding.km.setText(item.kmmeter);
            } else {
                viewHolder.binding.timeLineImg.setImageResource(R.drawable.ic_completed);
                viewHolder.binding.duty.setText(item.dutyon);
                viewHolder.binding.time.setText(item.time);
                viewHolder.binding.km.setText(item.kmmeter);
            }
    }

    public class TimeLineViewHolder extends BaseViewHolder<TimeLineEntity> {
        private final RecyclerAdapterTimeLine1Binding binding;

        public TimeLineViewHolder(@NonNull RecyclerAdapterTimeLine1Binding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }

        @Override
        public void onBind(TimeLineEntity item) {
            //binding.setAdapterItem(item);
        }
    }
}
