package com.sisindia.ai.mtrainer.android.features.clientreport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.AttendanceConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemHeaderTitleBinding;
import com.sisindia.ai.mtrainer.android.databinding.AdapterTariningAttendanceBinding;
import com.sisindia.ai.mtrainer.android.databinding.ClientReportItemBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportTo;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceViewModel;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.models.ContactListResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;

import io.reactivex.schedulers.Schedulers;

public class ClientReportRecylerAdapter extends BaseRecyclerAdapter<ContactListResponse.ClientData> {

    private ClientReportRecylerAdapter.ViewUpdate updateView;
    private MtrainerDataBase dataBase;


    public ClientReportRecylerAdapter(MtrainerDataBase dataBase, ClientReportRecylerAdapter.ViewUpdate updateView) {
        this.dataBase = dataBase;
        this.updateView = updateView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ClientReportItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.client_report_item, parent, false);
            return new ClientReportRecylerAdapter.ClientReportViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
         /*   final ClientReportRecylerAdapter.ClientReportViewHolder viewHolder = (ClientReportRecylerAdapter.ClientReportViewHolder) holder;
            final ContactListResponse.ClientData item = getItem(position);
            viewHolder.onBind(item);
            if (PreTrainingReviewActivity.selectedClientId.contains(item.clientId))
                viewHolder.binding.clientRadioBtn.setChecked(true);
            else
                viewHolder.binding.clientRadioBtn.setChecked(false);
            viewHolder.binding.clientContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if (viewHolder.binding.clientRadioBtn.isChecked()) {
                            PreTrainingReviewActivity.selectedClientId.remove(item.clientId);
                            dataBase.getSavedClientReportToDao().deleteClientEmail(item.email)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();
                            notifyItemChanged(position);
                            updateView.updateView();
                        } else {
                            PreTrainingReviewActivity.selectedClientId.add(item.clientId);
                            SavedClientReportTo reportTo = new SavedClientReportTo();
                            reportTo.email = item.email;
                            reportTo.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
                            dataBase.getSavedClientReportToDao().insertClientEmail(reportTo)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();
                            notifyItemChanged(position);
                            updateView.updateView();
                        }
                }
            });*/
    }

    interface ViewUpdate {
        void updateView();
    }

    class ClientReportViewHolder extends BaseViewHolder<ContactListResponse.ClientData> {
        ClientReportItemBinding binding;

        public ClientReportViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding = (ClientReportItemBinding) itemBinding;
        }

        @Override
        public void onBind(ContactListResponse.ClientData item) {
            binding.setAdapteritem(item);
        }
    }
}