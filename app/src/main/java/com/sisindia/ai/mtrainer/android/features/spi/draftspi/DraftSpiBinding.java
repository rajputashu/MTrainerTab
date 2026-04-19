package com.sisindia.ai.mtrainer.android.features.spi.draftspi;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DraftSpiBinding {

    @BindingAdapter(value = {"setDraftSpiRecyclerAdapter","myDraftSpiViewListener"})
    public static void setMyUnitsReclerAdpter(RecyclerView recyclerView, DraftSpiRecyclerAdapter recyclerAdapter,DraftSpiViewlistener draftSpiViewlistener) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(draftSpiViewlistener);
    }
}
