package com.sisindia.ai.mtrainer.android.features.trainingcalendar;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemTrainingCalendarBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.addrota.AddTaskViewListeners;
import com.sisindia.ai.mtrainer.android.models.CalendarResponse;

public class TrainingCalendarRecyclerAdapter extends BaseRecyclerAdapter<CalendarResponse.CalendarRotaResponse> {

    private AddTaskViewListeners listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterItemTrainingCalendarBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.recycler_adapter_item_training_calendar, parent, false);
        return new TrainingCalendarItemViewHolder(binding);
    }

    public TrainingCalendarRecyclerAdapter(MtrainerDataBase dataBase) {
    }

    public void initListener(AddTaskViewListeners listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TrainingCalendarItemViewHolder viewHolder = (TrainingCalendarItemViewHolder) holder;
        final CalendarResponse.CalendarRotaResponse item = getItem(position);
        int i = position + 1;
        if (viewHolder.binding.position != null)
            viewHolder.binding.position.setText("" + i);
        viewHolder.onBind(item);
    }

    private class TrainingCalendarItemViewHolder extends BaseViewHolder<CalendarResponse.CalendarRotaResponse> {
        private final RecyclerAdapterItemTrainingCalendarBinding binding;

        public TrainingCalendarItemViewHolder(@NonNull RecyclerAdapterItemTrainingCalendarBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }

        @Override
        public void onBind(CalendarResponse.CalendarRotaResponse item) {
            binding.setAdapterItem(item);
            if (binding.sendMailAction != null) {
                binding.sendMailAction.setOnClickListener(view -> {
                    listener.onSendEmailClicked(getLayoutPosition());
                });
            }
        }
    }
}
