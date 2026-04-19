package com.sisindia.ai.mtrainer.android.features.assessment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AssesmentItemHeaderBinding;
import com.sisindia.ai.mtrainer.android.databinding.RowAssessmentReportBinding;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;

public class AssessmentReportAdapter extends BaseRecyclerAdapter<AttendanceEntity> {

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SECTION_VIEW) {
            AssesmentItemHeaderBinding binding = DataBindingUtil.inflate(inflater, R.layout.assesment_item_header, parent, false);
            return new HeaderLetterVH(binding);
        } else {
            RowAssessmentReportBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_assessment_report, parent, false);
            return new AssessmentReportVH(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (SECTION_VIEW == getItemViewType(position)) {
            final AttendanceEntity item = getItem(position);
            HeaderLetterVH headerVH = (HeaderLetterVH) holder;
            headerVH.onBind(item);
        } else {
            final AssessmentReportVH viewHolder = (AssessmentReportVH) holder;
            final AttendanceEntity item = getItem(position);
            viewHolder.onBind(item, position);
        }
    }

    static class AssessmentReportVH extends BaseViewHolder<AttendanceEntity> {
        RowAssessmentReportBinding binding;

        public AssessmentReportVH(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding = (RowAssessmentReportBinding) itemBinding;
        }

        @Override
        public void onBind(AttendanceEntity item) {
        }

        @Override
        public void onBind(AttendanceEntity item, int position) {
            binding.setAssessmentItem(item);
        }
    }

    public static class HeaderLetterVH extends BaseViewHolder<AttendanceEntity> {
        AssesmentItemHeaderBinding binding;

        public HeaderLetterVH(@NonNull ViewDataBinding itemBinding) {
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