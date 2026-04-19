package com.sisindia.ai.mtrainer.android.features.spi.draftapproval;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DraftApprovalViewBinding {

    @BindingAdapter(value = {"setDrafApprovalRecyclerAdapter"})
    public static void setMyUnitsReclerAdpter(RecyclerView recyclerView, DraftApprovalRecyclerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
