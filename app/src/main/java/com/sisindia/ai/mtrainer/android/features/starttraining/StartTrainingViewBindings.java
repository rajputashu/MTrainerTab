package com.sisindia.ai.mtrainer.android.features.starttraining;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.commons.NavigationUiRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.commons.NavigationViewListeners;

public class StartTrainingViewBindings {

    @BindingAdapter(value = {"navigationRecyclerAdapter", "navigationViewListeners", "adapterFilter"})
    public static void setRecyclerAdapterForTopNavigation(RecyclerView recyclerView, NavigationUiRecyclerAdapter adapter, NavigationViewListeners listeners, String companyId) {
        //LinearLayoutManager manager = new LinearLayoutManager(recyclerView.getContext(), RecyclerView.HORIZONTAL, false);
        GridLayoutManager manager;
        /*if(companyId.equals("1"))*/
            manager = new GridLayoutManager(recyclerView.getContext(), 5);
        /*else
            manager = new GridLayoutManager(recyclerView.getContext(), 4);*/
        recyclerView.setLayoutManager(manager);
        //recyclerView.setHasFixedSize(true);
        adapter.setViewListeners(listeners);
        recyclerView.setAdapter(adapter);
    }
}
