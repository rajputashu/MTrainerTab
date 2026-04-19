package com.sisindia.ai.mtrainer.android.features.myconveyance;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemMyConveyanceBinding;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemMyUnitsTabBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.myunits.UnitsiteViewListeners;
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyData;
import com.sisindia.ai.mtrainer.android.models.UnitsData;

import java.util.ArrayList;
import java.util.List;

public class MyConveyanceMonthlyRecyclerAdapter extends BaseRecyclerAdapter<ConveyanceMonthlyData> {

    private List<ConveyanceMonthlyData> unitsDataObservableList;
    private  MyconvenceListeners listeners;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterItemMyConveyanceBinding binding= DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_item_my_conveyance,parent,false);
        return new MyUnitsItemViewHolder(binding);
    }

    public MyConveyanceMonthlyRecyclerAdapter(List<ConveyanceMonthlyData> unitsDataObservableList){
        this.unitsDataObservableList = unitsDataObservableList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyUnitsItemViewHolder viewHolder= (MyUnitsItemViewHolder) holder;
        final ConveyanceMonthlyData item = getItem(position);
        viewHolder.onBind(item, position);
    }

    public void setitems(List<ConveyanceMonthlyData> unitsDataObservableList1){
        unitsDataObservableList = unitsDataObservableList1;
    }

    public void setListener(MyconvenceListeners listener) {
        this.listeners = listener;
    }

    private class MyUnitsItemViewHolder extends BaseViewHolder<ConveyanceMonthlyData> {
        private  final RecyclerAdapterItemMyConveyanceBinding binding;

        public MyUnitsItemViewHolder(RecyclerAdapterItemMyConveyanceBinding itemBinding) {
            super(itemBinding);
            this.binding=itemBinding;
        }

        @Override
        public void onBind(ConveyanceMonthlyData item, int position) {
            binding.setAdapterItem(item);
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
        public void onBind(ConveyanceMonthlyData item) {


        }
    }
}
