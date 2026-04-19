package com.sisindia.ai.mtrainer.android.features.myconveyance;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemMyConveyanceDailyBinding;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemMyConveyanceTimelineBinding;
import com.sisindia.ai.mtrainer.android.models.ConveyanceDailyData;
import com.sisindia.ai.mtrainer.android.models.ConveyanceTimeLineData;
import com.sisindia.ai.mtrainer.android.models.ConveyanceTimelineResponse;

import java.util.List;

public class MyConveyanceTimelineRecyclerAdapter extends BaseRecyclerAdapter<ConveyanceTimeLineData> {

    private List<ConveyanceTimeLineData> unitsDataObservableList;
    private  MyconvencetimelineListeners listeners;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterItemMyConveyanceTimelineBinding binding= DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_item_my_conveyance_timeline,parent,false);
        return new MyUnitsItemViewHolder(binding);
    }

    public MyConveyanceTimelineRecyclerAdapter(List<ConveyanceTimeLineData> unitsDataObservableList){
        this.unitsDataObservableList = unitsDataObservableList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyUnitsItemViewHolder viewHolder= (MyUnitsItemViewHolder) holder;
        final ConveyanceTimeLineData item = getItem(position);
        viewHolder.onBind(item, position);
    }


    public void setitems(List<ConveyanceTimeLineData> unitsDataObservableList1){
        unitsDataObservableList = unitsDataObservableList1;
    }

    public void setListener(MyconvencetimelineListeners listener) {
        this.listeners = listener;
    }


    private class MyUnitsItemViewHolder extends BaseViewHolder<ConveyanceTimeLineData> {

        private  final RecyclerAdapterItemMyConveyanceTimelineBinding binding;

        public MyUnitsItemViewHolder(RecyclerAdapterItemMyConveyanceTimelineBinding itemBinding) {
            super(itemBinding);
            this.binding=itemBinding;
        }

        @Override
        public void onBind(ConveyanceTimeLineData item, int position) {
            binding.setAdapterItem(item);

            if (item.getTaskEndTime()==null) {
                binding.tvpresentdayaname.setText("");
            }else {
                binding.tvpresentdayaname.setText(item.getTaskEndTime());
            }

            if (item.getSiteName()==null){
                binding.tvsitename.setText("");
            }else {
                binding.tvsitename.setText(item.getSiteName());
            }
        }

        @Override
        public void onBind(ConveyanceTimeLineData item) {


        }
    }
}
