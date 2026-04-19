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
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemMyConveyanceBinding;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemMyConveyanceDailyBinding;
import com.sisindia.ai.mtrainer.android.models.ConveyanceDailyData;
import com.sisindia.ai.mtrainer.android.models.ConveyanceDailyResponse;
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyData;

import java.util.List;

public class MyConveyanceDialyRecyclerAdapter extends BaseRecyclerAdapter<ConveyanceDailyData> {

    private List<ConveyanceDailyData> unitsDataObservableList;
    private  MyconvencedailyListeners listeners;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterItemMyConveyanceDailyBinding binding= DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_item_my_conveyance_daily,parent,false);
        return new MyUnitsItemViewHolder(binding);
    }

    public MyConveyanceDialyRecyclerAdapter(List<ConveyanceDailyData> unitsDataObservableList){
        this.unitsDataObservableList = unitsDataObservableList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyUnitsItemViewHolder viewHolder= (MyUnitsItemViewHolder) holder;
        final ConveyanceDailyData item = getItem(position);
        viewHolder.onBind(item, position);
    }


    public void setitems(List<ConveyanceDailyData> unitsDataObservableList1){
        unitsDataObservableList = unitsDataObservableList1;
    }

    public void setListener(MyconvencedailyListeners listener) {
        this.listeners = listener;
    }


    private class MyUnitsItemViewHolder extends BaseViewHolder<ConveyanceDailyData> {
        private  final RecyclerAdapterItemMyConveyanceDailyBinding binding;

        public MyUnitsItemViewHolder(RecyclerAdapterItemMyConveyanceDailyBinding itemBinding) {
            super(itemBinding);
            this.binding=itemBinding;
        }

        @Override
        public void onBind(ConveyanceDailyData item, int position) {
            binding.setAdapterItem(item);
            binding.tvpresentdayaname.setText(item.getDayName());
            binding.onclick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("adapterposition",""+position);
                    if (unitsDataObservableList!=null) {
                     listeners.onconveyanceitemclick(item,position);
                    }

                }
            });
        }

        @Override
        public void onBind(ConveyanceDailyData item) {


        }
    }
}
