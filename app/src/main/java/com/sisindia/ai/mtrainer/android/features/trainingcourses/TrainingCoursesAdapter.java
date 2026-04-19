package com.sisindia.ai.mtrainer.android.features.trainingcourses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemTrainingCoursesBinding;
import com.sisindia.ai.mtrainer.android.features.rota.DashBoardRotaViewListeners;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesDataResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;

import java.util.ArrayList;
import java.util.List;

public class TrainingCoursesAdapter extends BaseRecyclerAdapter<TrainingCoursesDataResponse> implements Filterable {

    private TrainingCourseViewListeners listener;

    List<TrainingCoursesDataResponse> topicsDataModels1;
    List<TrainingCoursesDataResponse> topicsDataModels;

    public void setTopicsDataModels(List<TrainingCoursesDataResponse> topicsDataModels) {
        this.topicsDataModels1 = topicsDataModels;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        RecyclerAdapterItemTrainingCoursesBinding binding= DataBindingUtil.inflate(
                inflater,
                R.layout.recycler_adapter_item_training_courses,
                parent,
                false
        );
        return new TrainigCourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TrainigCourseViewHolder viewHolder = (TrainigCourseViewHolder) holder;
        TrainingCoursesDataResponse item = getItem(position);
        viewHolder.onBind(item);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCourseClick(item);
            }
        });
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

                    List<TrainingCoursesDataResponse> filteredList = new ArrayList<>();
                    for (TrainingCoursesDataResponse data : topicsDataModels1) {
                        if (data.isActive() == 1) {
                            filteredList.add(data);
                        }
                    }
                    topicsDataModels = filteredList;
                    //topicsDataModels = new ArrayList<>(topicsDataModels1);
                    setItems(topicsDataModels);
                } else {
                    List<TrainingCoursesDataResponse> filteredList = new ArrayList<>();
                    for (TrainingCoursesDataResponse data : topicsDataModels1) {
                        if (data.getCourseTitle().toLowerCase().startsWith(charString.toLowerCase())) {
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
                topicsDataModels = (ArrayList<TrainingCoursesDataResponse>) filterResults.values;
                setItems(topicsDataModels);
                notifyDataSetChanged();
            }
        };
    }


    public void setListener(TrainingCourseViewListeners listener) {
        this.listener = listener;
    }
}


