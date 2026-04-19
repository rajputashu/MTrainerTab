package com.sisindia.ai.mtrainer.android.features.clientreport;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.features.attendancemodule.OnQuery;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.features.trainingimages.ToggleListener;

public class ClientReportViewBinding {
    @BindingAdapter(value = {"clientReportRecyclerAdapter"})
    public static void setClientReportRecyclerAdapter(RecyclerView recyclerView, ClientReportRecylerAdapter recyclerAdapter/*TrainingAttendanceViewListeners viewListeners*/) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @BindingAdapter(value = {"ccTextListner"})
    public static void setCcTextListner(EditText editText, TextChange textChange) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textChange.onCcTextChange(s.toString());
            }
        });
    }

    @BindingAdapter(value = {"toggleListener"})
    public static void setToggleListener(Switch switchView, ToggleListener toggleListener) {
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleListener.onToggle(isChecked);
            }
        });
    }
}
