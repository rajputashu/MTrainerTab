package com.sisindia.ai.mtrainer.android.features.rota;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemDashBoardPerformanceBinding;
import com.sisindia.ai.mtrainer.android.models.TrainerPerformanceResponse;

public class DashBoardPerformanceRecyclerAdapter extends BaseRecyclerAdapter<TrainerPerformanceResponse.PerformanceResponse> {

    private DashBoardRotaViewListeners listeners;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterItemDashBoardPerformanceBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_item_dash_board_performance, parent, false);
        return new DashBoardPerformanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DashBoardPerformanceViewHolder viewHolder = (DashBoardPerformanceViewHolder) holder;
        final TrainerPerformanceResponse.PerformanceResponse item = getItem(position);
        viewHolder.onBind(item);
        if(item.typeId == 1) {
            viewHolder.binding.performanceTitle.setText("Today's Performance");
        } else if(item.typeId == 2) {
            viewHolder.binding.performanceTitle.setText("Weekly Performance");
        } else if(item.typeId == 3) {
            viewHolder.binding.performanceTitle.setText("Monthly Performance");
        } else {
            viewHolder.binding.performanceTitle.setText("Previous Month Performance");
        }
    }

    public void setListeners(DashBoardRotaViewListeners listeners) {
        this.listeners = listeners;
    }


    class DashBoardPerformanceViewHolder extends BaseViewHolder<TrainerPerformanceResponse.PerformanceResponse> {

        public final RecyclerAdapterItemDashBoardPerformanceBinding binding;

        public DashBoardPerformanceViewHolder(@NonNull RecyclerAdapterItemDashBoardPerformanceBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }

        @Override
        public void onBind(TrainerPerformanceResponse.PerformanceResponse item) {

            binding.setAdapterItem(item);
            binding.setCompanyId(Prefs.getString(PrefsConstants.COMPANY_ID));
            binding.setRole(Prefs.getString(PrefsConstants.ROLE));
            binding.getRoot().setOnClickListener(v -> {
                if (listeners != null) {
                    listeners.onPerformanceItemClick();
                }
            });
        }
    }
}
