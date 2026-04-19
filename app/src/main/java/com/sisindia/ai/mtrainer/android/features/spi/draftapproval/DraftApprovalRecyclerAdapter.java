package com.sisindia.ai.mtrainer.android.features.spi.draftapproval;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.facebook.stetho.common.ArrayListAccumulator;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterDraftApproval1Binding;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterDraftSpiBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.spi.draftspi.DraftSpiRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.models.spi.DraftApprovalResponse;

import java.util.List;

public class DraftApprovalRecyclerAdapter extends BaseRecyclerAdapter<DraftApprovalResponse.DraftApprovalTableDetailsData> {
    MtrainerDataBase dataBase;
    List<DraftApprovalResponse.DraftApprovalTableDetailsData> status=new ArrayListAccumulator<>();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterDraftApproval1Binding binding= DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_draft_approval1,parent,false);
        return new DraftApprovalRecyclerAdapter.DraftAppovalItemViewHolder(binding);
    }
    public DraftApprovalRecyclerAdapter(MtrainerDataBase dataBase){
        this.dataBase=dataBase;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DraftAppovalItemViewHolder viewHolder= (DraftAppovalItemViewHolder) holder;
        final DraftApprovalResponse.DraftApprovalTableDetailsData item = getItem(position);
        viewHolder.onBind(item);
        int i=position+1;
        viewHolder.binding.slNo.setText(""+i);
        viewHolder.binding.postName.setText(item.postName);
        viewHolder.binding.status.setText(item.status);
        viewHolder.binding.remark.setText(item.remark);


    }
    private class DraftAppovalItemViewHolder extends BaseViewHolder<DraftApprovalResponse.DraftApprovalTableDetailsData>{
        private final RecyclerAdapterDraftApproval1Binding binding;

        public DraftAppovalItemViewHolder(RecyclerAdapterDraftApproval1Binding itemBinding) {
            super(itemBinding);
            this.binding=itemBinding;
        }

        @Override
        public void onBind(DraftApprovalResponse.DraftApprovalTableDetailsData item) {
            binding.setAdapter(item);

        }
    }
}
