package com.sisindia.ai.mtrainer.android.features.attendancemodule;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.AttendanceConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemHeaderTitleBinding;
import com.sisindia.ai.mtrainer.android.databinding.AdapterTariningAttendanceBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.schedulers.Schedulers;

public class TrainingAttendanceRecyclerAdapter extends BaseRecyclerAdapter<TrainingAttendanceResponse.AttendanceResponse> {

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    private OnAttendance onAttendance;
    private MtrainerDataBase dataBase;
    private PostItem postItem;
    private Set<String> selectedEmployeeSet = new HashSet<>();



    public TrainingAttendanceRecyclerAdapter(MtrainerDataBase dataBase, OnAttendance onAttendance) {
        this.dataBase = dataBase;
        this.onAttendance = onAttendance;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SECTION_VIEW) {
            AdapterItemHeaderTitleBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_header_title, parent, false);
            return new SelectionViewHolder(binding);
        } else {
            AdapterTariningAttendanceBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_tarining_attendance, parent, false);
            return new TrainingAttendanceViewHolder(binding);
        }
    }


    @Override
    public void clearAndSetItems(List<TrainingAttendanceResponse.AttendanceResponse> items) {
        AttendanceDiffCallback diffCallback = new AttendanceDiffCallback(getItems(), items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        setItems(items);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (SECTION_VIEW == getItemViewType(position)) {
            final TrainingAttendanceResponse.AttendanceResponse item = getItem(position);
            SelectionViewHolder selectionViewHolder = (SelectionViewHolder) holder;
            selectionViewHolder.onBind(item);
            return;
        } else {
            final TrainingAttendanceViewHolder viewHolder = (TrainingAttendanceViewHolder) holder;
            final TrainingAttendanceResponse.AttendanceResponse item = getItem(position);
            viewHolder.onBind(item);

            if (selectedEmployeeSet.contains(item.employeeCode))
                viewHolder.binding.empRadioBtn.setChecked(true);
            else
                viewHolder.binding.empRadioBtn.setChecked(false);


            viewHolder.binding.empContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postItem != null) {
                        if (viewHolder.binding.empRadioBtn.isChecked()) {
                            selectedEmployeeSet.remove(item.employeeCode);
                            dataBase.getAttendanceDao().deleteAttendance(item.employeeCode,Prefs.getInt(PrefsConstants.ROTA_ID, -2))
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();
                            String photoId = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + item.employeeId + "_" + AttendanceConstants.ATTENDANCE_PICTURE;
                            dataBase.getAttendancePhotoDao().deleteAttendancePhoto(photoId)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();
                            photoId = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + item.employeeId + "_" + AttendanceConstants.ATTENDANCE_SIGNATURE;
                            dataBase.getAttendancePhotoDao().deleteAttendancePhoto(photoId)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();
                            notifyItemChanged(position);
                        } else {
                            onAttendance.onEmployeeSelected(item, position);
                        }
                    } else {
                        onAttendance.onEmployeeSelected(null, position);
                    }
                }
            });


        }


    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isIsselected()) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    public void setPostItem(PostItem postItem) {
        this.postItem = postItem;
    }

    public void setSelectedEmployeeSet(Set<String> selectedEmployeeSet) {
        this.selectedEmployeeSet = selectedEmployeeSet;
    }

    interface OnAttendance {
        void onEmployeeSelected(TrainingAttendanceResponse.AttendanceResponse selectedEmp, int position);
    }

    class TrainingAttendanceViewHolder extends BaseViewHolder<TrainingAttendanceResponse.AttendanceResponse> {
        AdapterTariningAttendanceBinding binding;

        public TrainingAttendanceViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding = (AdapterTariningAttendanceBinding) itemBinding;
        }

        @Override
        public void onBind(TrainingAttendanceResponse.AttendanceResponse item) {
            binding.setAdapteritem(item);

           /* binding.getRoot().setOnClickListener(v -> {
                if (listener!= null) {
                    listener.onAttendanceItemClicked(item.employeeId);
                }
            });*/
        }
    }

    public class SelectionViewHolder extends BaseViewHolder<TrainingAttendanceResponse.AttendanceResponse> {
        AdapterItemHeaderTitleBinding binding;
        public SelectionViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding = (AdapterItemHeaderTitleBinding) itemBinding;
        }
        @Override
        public void onBind(TrainingAttendanceResponse.AttendanceResponse item) {
            binding.setAdapterItem(item);
        }
    }


    static class AttendanceDiffCallback extends DiffUtil.Callback{
        List<TrainingAttendanceResponse.AttendanceResponse> oldData;
        List<TrainingAttendanceResponse.AttendanceResponse> newData;
        private static final String TAG = "AttendanceDiffCallback";
        AttendanceDiffCallback(List<TrainingAttendanceResponse.AttendanceResponse> oldData, List<TrainingAttendanceResponse.AttendanceResponse> newData) {
            this.newData = newData;
            this.oldData = oldData;
        }

        @Override
        public int getOldListSize() {
            return oldData != null ? oldData.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newData != null ? newData.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if(oldData != null) {
                return oldData.get(oldItemPosition).employeeId == newData.get(newItemPosition).employeeId;
            }
            else
                return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if(oldData != null) {
                return  true;//oldData.get(oldItemPosition).employeeName.equals(newData.get(newItemPosition).employeeName);
            }
            else
                return false;
        }
    }


}