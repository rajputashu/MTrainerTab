package com.sisindia.ai.mtrainer.android.features.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemEmployeeReportBinding;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemTrainingCoursesBinding;
import com.sisindia.ai.mtrainer.android.models.EmployeeReportsResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesDataResponse;

import java.util.ArrayList;
import java.util.List;

public class ReportsAdapter extends BaseRecyclerAdapter<EmployeeReportsResponse>  {

    private ReortsViewListeners listener;

    List<EmployeeReportsResponse> topicsDataModels1;
    List<EmployeeReportsResponse> topicsDataModels;

    public void setTopicsDataModels(List<EmployeeReportsResponse> topicsDataModels) {
        this.topicsDataModels1 = topicsDataModels;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        RecyclerAdapterItemEmployeeReportBinding binding= DataBindingUtil.inflate(
                inflater,
                R.layout.recycler_adapter_item_employee_report,
                parent,
                false
        );
        return new ReportsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReportsViewHolder viewHolder = (ReportsViewHolder) holder;
        EmployeeReportsResponse item = getItem(position);
        viewHolder.onBind(item);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }



    public void setListener(ReortsViewListeners listener) {
        this.listener = listener;
    }
}


