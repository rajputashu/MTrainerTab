package com.sisindia.ai.mtrainer.android.features.spi;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaiselrahman.hintspinner.HintSpinner;
import com.jaiselrahman.hintspinner.HintSpinnerAdapter;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.SpiBranchData;

import java.util.List;


public class SitePostViewBinding {

    @BindingAdapter(value = {"setSitePostRecyclerAdapter","mySpiViewListener"})
    public static void setSitePostReclerAdpter(RecyclerView recyclerView, SitePostRecyclerAdapter recyclerAdapter,SpiViewListeners spiViewListeners) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(spiViewListeners);
    }

    @BindingAdapter("setSpiSpinnerItemSelector")
    public static void onSpinnerItemSelect(Spinner spinner, AddSpinnerViewListerner taskViewListeners){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taskViewListeners.onItemSpinnerSelected(parent.getId(), position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @BindingAdapter("spibranchEntries")
    public static void setBranchEntries(HintSpinner spinner, List<SpiBranchData> unitList) {
        spinner.setAdapter(new HintSpinnerAdapter<SpiBranchData>(spinner.getContext(), unitList, "Select Branch") {
            @Override
            public String getLabelFor(SpiBranchData object) {
                return object.branchName;
            }
        });

    }



}
