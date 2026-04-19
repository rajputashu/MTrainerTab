package com.sisindia.ai.mtrainer.android.features.takeassessment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemTakeAssessmentBinding;
import com.sisindia.ai.mtrainer.android.databinding.AssesmentItemHeaderBinding;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

public class TakeAssessmentRecyclerAdapter extends BaseRecyclerAdapter<AttendanceEntity> {
    private AssessmentItemListener listener;
    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;
    private Set<String> selectedEmployeeSet = new HashSet<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SECTION_VIEW) {
            AssesmentItemHeaderBinding binding = DataBindingUtil.inflate(inflater, R.layout.assesment_item_header, parent, false);
            return new TakeAssessmentRecyclerAdapter.SelectionViewHolder(binding);
        } else {

            AdapterItemTakeAssessmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_take_assessment, parent, false);
            return new TakeAssessmentViewHolder(binding);
        }
    }

    public void setListener(AssessmentItemListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (SECTION_VIEW == getItemViewType(position)) {
            final AttendanceEntity item = getItem(position);
            SelectionViewHolder selectionViewHolder = (SelectionViewHolder) holder;
            selectionViewHolder.onBind(item);
            return;
        } else {
            final TakeAssessmentViewHolder viewHolder = (TakeAssessmentViewHolder) holder;
            final AttendanceEntity item = getItem(position);
            viewHolder.onBind(item, position);
            if (selectedEmployeeSet.contains(item.empCode))
                viewHolder.binding.assementRadioBtn.setChecked(true);
            else
                viewHolder.binding.assementRadioBtn.setChecked(false);
        }
    }


    public void setSelectedEmployeeSet(Set<String> selectedEmployeeSet) {
        this.selectedEmployeeSet = selectedEmployeeSet;
    }

    static class TakeAssessmentViewHolder extends BaseViewHolder<AttendanceEntity> {
        AdapterItemTakeAssessmentBinding binding;

        public TakeAssessmentViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding = (AdapterItemTakeAssessmentBinding) itemBinding;

        }

        @Override
        public void onBind(AttendanceEntity item) {
        }

        @Override
        public void onBind(AttendanceEntity item, int position) {
            binding.setAssessmentItem(item);
//            binding.setScoreMetric(Float.valueOf(Prefs.getString(PrefsConstants.SCORE_METRIC)));
            /*binding.getRoot().setOnClickListener(v -> {
                listener.onAssessmentItemClick(item, position);
            });*/
        }
    }

    public class SelectionViewHolder extends BaseViewHolder<AttendanceEntity> {
        AssesmentItemHeaderBinding binding;

        public SelectionViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding = (AssesmentItemHeaderBinding) itemBinding;
        }

        @Override
        public void onBind(AttendanceEntity item) {
            binding.setAdapterItem(item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isselected) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }
}