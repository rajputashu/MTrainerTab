package com.sisindia.ai.mtrainer.android.features.myunits;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaiselrahman.hintspinner.HintSpinner;
import com.jaiselrahman.hintspinner.HintSpinnerAdapter;
import com.sisindia.ai.mtrainer.android.features.addrota.AddTaskViewListeners;
import com.sisindia.ai.mtrainer.android.features.reports.InductionFilterViewListeners;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.BranchDataModel;

import java.util.List;

public class MyUnitsViewBinding {

  /*  @BindingAdapter("branchEntries1")
    public static void setBranchEntries(HintSpinner spinner, List<MyUnitsResponse.MyUnitsDetailList> unitList) {
        spinner.setAdapter(new HintSpinnerAdapter<MyUnitsResponse.MyUnitsDetailList>(spinner.getContext(), unitList, "Select Branch") {
            @Override
            public String getLabelFor(MyUnitsResponse.MyUnitsDetailList object) {
                return object.branchName;
            }
        });
    }*/

    @BindingAdapter(value = {"setMyUnitsRecyclerAdapter", "myUnitsViewListener"})
    public static void setMyUnitsReclerAdpter(RecyclerView recyclerView, MyUnitsRecyclerAdapter recyclerAdapter, UnitsiteViewListeners unitsiteViewListeners) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if (recyclerView.getItemDecorationCount() == 0) {
            DividerItemDecoration divider =
                    new DividerItemDecoration(
                            recyclerView.getContext(),
                            linearLayoutManager.getOrientation()
                    );
            recyclerView.addItemDecoration(divider);
        }

        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(unitsiteViewListeners);
    }


    @BindingAdapter("branchtabEntriesUnits")
    public static void setBranchtabEntries(HintSpinner spinner, List<BranchDataModel> unitList) {
        spinner.setAdapter(new HintSpinnerAdapter<BranchDataModel>(spinner.getContext(), unitList, "Select Branch") {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }

            @Override
            public String getLabelFor(BranchDataModel object) {

                //  TextView textView;
                //  textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                return object.branchName;
            }
        });
    }

    @BindingAdapter("unitStatusList")
    public static void bindUnitStatusSpinner(HintSpinner spinner, List<String> unitList) {
//         = List.of("Active", "Disbanded");
        spinner.setAdapter(new HintSpinnerAdapter<>(spinner.getContext(), unitList,
                "Select Unit Status") {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }

            @Override
            public String getLabelFor(String status) {
                return status;
            }
        });
    }

    @BindingAdapter("filterEntries")
    public static void setFilterEntries(HintSpinner spinner, List<String> unitList) {
        spinner.setAdapter(new HintSpinnerAdapter<String>(spinner.getContext(), unitList, "Select Status") {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }

            @Override
            public String getLabelFor(String object) {

                //  TextView textView;
                //  textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                return object;
            }
        });
    }


    @BindingAdapter("branchEntriesUnits")
    public static void setBranchEntries(HintSpinner spinner, List<BranchData> unitList) {
        spinner.setAdapter(new HintSpinnerAdapter<BranchData>(spinner.getContext(), unitList, "Select Branch") {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.parseColor("#ffffff"));
                return v;
            }

            @Override
            public String getLabelFor(BranchData object) {

                //  TextView textView;
                //  textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                return object.branchName;
            }
        });
    }

    @BindingAdapter("setSpinnerItemSelectorMyUnits")
    public static void onSpinnerItemSelect1(Spinner spinner, AddTaskViewListeners taskViewListeners) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                // parent.setTextColor(context.getResources().getColor(R.color.color_hint));
                taskViewListeners.onItemSpinnerSelected(parent.getId(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

            }
        });
    }

    @BindingAdapter("setSpinnerItemSelectorMyUnitsTab")
    public static void onSpinnerItemSelecttab1(Spinner spinner, BranchViewListeners taskViewListeners) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                // parent.setTextColor(context.getResources().getColor(R.color.color_hint));
                taskViewListeners.onItemSpinnerSelected(parent.getId(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

            }
        });
    }

    @BindingAdapter("siteStatusListener")
    public static void onSiteStatusSpinner(Spinner spinner, SiteStatusListener listener) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                listener.onItemSpinnerSelected(parent.getId(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            }
        });
    }

    @BindingAdapter("setSpinnerItemSelectorInductionFilter")
    public static void onSpinnerItemSelectorFilter(Spinner spinner, InductionFilterViewListeners taskViewListeners) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                // parent.setTextColor(context.getResources().getColor(R.color.color_hint));
                taskViewListeners.onItemSpinnerSelected(parent.getId(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

            }
        });
    }

}
