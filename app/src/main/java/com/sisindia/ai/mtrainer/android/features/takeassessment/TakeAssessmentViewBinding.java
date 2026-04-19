package com.sisindia.ai.mtrainer.android.features.takeassessment;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.commons.NavigationUiRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.commons.NavigationViewListeners;
import com.sisindia.ai.mtrainer.android.features.assessment.AssessmentReportAdapter;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.OnQuery;
import com.sisindia.ai.mtrainer.android.features.pretraining.TextListRecyclerAdapter;

public class TakeAssessmentViewBinding {
    @BindingAdapter(value = {"navigationRecyclerAdapter"})
    public static void setRecyclerAdapterForTopNavigation(RecyclerView recyclerView, NavigationUiRecyclerAdapter adapter) {
        LinearLayoutManager manager = new LinearLayoutManager(recyclerView.getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter(value = {"setTakeAssessmentRecyclerAdapter", "assessmentItemClickListener"})
    public static void setTakeAssessmentReclerAdpter(RecyclerView recyclerView, TakeAssessmentRecyclerAdapter recyclerAdapter, AssessmentItemListener listener) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(listener);
    }

    @BindingAdapter(value = {"assessmentReportAdapter"})
    public static void setAssessmentReport(RecyclerView recyclerView, AssessmentReportAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @BindingAdapter(value = {"assessmentQueryListener"})
    public static void setAssessmentQueryListener(SearchView searchView, AssessmentQueryListener queryListener) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryListener.onAssessmentQuery(newText);
                return false;
            }
        });
    }
}