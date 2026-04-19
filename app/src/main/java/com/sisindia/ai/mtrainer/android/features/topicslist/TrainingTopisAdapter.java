package com.sisindia.ai.mtrainer.android.features.topicslist;

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
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemTrainingTopicsBinding;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;

import java.util.ArrayList;
import java.util.List;

public class TrainingTopisAdapter extends BaseRecyclerAdapter<TrainingTopicsDataModel> implements Filterable {

    private TrainingTopicsViewListeners listener;
    List<TrainingTopicsDataModel> topicsDataModels1;
    List<TrainingTopicsDataModel> topicsDataModels;


    public void setTopicsDataModels(List<TrainingTopicsDataModel> topicsDataModels) {
        this.topicsDataModels1 = topicsDataModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        RecyclerAdapterItemTrainingTopicsBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.recycler_adapter_item_training_topics,
                parent,
                false
        );
        return new TrainingTopicsViewHolder(binding);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase().trim();

                if (topicsDataModels1 == null) {
                    topicsDataModels1 = new ArrayList<>();
                }
                if (charString.isEmpty()) {
                    /*List<TrainingTopicsDataModel> filteredList = new ArrayList<>();

                    topicsDataModels = filteredList;
                    //topicsDataModels = new ArrayList<>(topicsDataModels1);
                    setItems(topicsDataModels);*/

                    topicsDataModels = new ArrayList<>(topicsDataModels1);
                    setItems(topicsDataModels);
                } else {
                    List<TrainingTopicsDataModel> filteredList = new ArrayList<>();
                    for (TrainingTopicsDataModel data : topicsDataModels1) {
                        if (data.getCourseTopicTitle().toLowerCase().startsWith(charString.toLowerCase())) {
                            filteredList.add(data);
                        }
                    }
                    topicsDataModels = filteredList;
                    setItems(topicsDataModels);
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = topicsDataModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                topicsDataModels = (ArrayList<TrainingTopicsDataModel>) filterResults.values;
                setItems(topicsDataModels);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TrainingTopicsViewHolder viewHolder = (TrainingTopicsViewHolder) holder;
        TrainingTopicsDataModel item = getItem(position);
        Log.d("TOOPICITME", String.valueOf(item.isActive()));
        viewHolder.onBind(item);
        viewHolder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.onCourseClick(item);
            }
        });
    }

    public void setListener(TrainingTopicsViewListeners listener) {
        this.listener = listener;
    }
}


