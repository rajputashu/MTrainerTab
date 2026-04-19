package com.sisindia.ai.mtrainer.android.features.addrota;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaiselrahman.hintspinner.HintSpinner;
import com.jaiselrahman.hintspinner.HintSpinnerAdapter;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.features.choosetopics.ChooseTopicsRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.CourseTypeData;
import com.sisindia.ai.mtrainer.android.models.RegionData;
import com.sisindia.ai.mtrainer.android.models.SiteData;
import com.sisindia.ai.mtrainer.android.models.TrainingType;
import com.sisindia.ai.mtrainer.android.models.Unit;
import com.sisindia.ai.mtrainer.android.models.UnitListResponse;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

import static org.threeten.bp.DayOfWeek.SUNDAY;
import static org.threeten.bp.temporal.TemporalAdjusters.next;

public class AddTaskViewBinding {

    @BindingAdapter(value = {"setCreateTaskDatePicker", "viewListeners"})
    public static void setCreateTaskDatePicker(AppCompatTextView textView, LocalDate date, AddTaskViewListeners listeners) {
        int selectedYear = date.getYear();
        int selectedMonth = date.getMonthValue() - 1;
        int selectedDayOfMonth = date.getDayOfMonth();

        final LocalDate currentSunday = LocalDate.now().with(next(SUNDAY));
        final LocalDate nextSunday = currentSunday.with(next(SUNDAY));
        Calendar maxDate = Calendar.getInstance();
        /*maxDate.set(Calendar.YEAR, nextSunday.getYear());
        maxDate.set(Calendar.MONTH, nextSunday.getMonthValue() - 1);
        maxDate.set(Calendar.DAY_OF_MONTH, nextSunday.getDayOfMonth());*/

        textView.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(textView.getContext(), (view, year, month, dayOfMonth) -> {
                LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                listeners.onDateChanged(v.getId() , selectedDate);
            }, selectedYear, selectedMonth, selectedDayOfMonth);
            datePickerDialog.getDatePicker().setMinDate(Instant.now().toEpochMilli());
            //datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
            datePickerDialog.show();
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        });
    }

    @BindingAdapter(value = {"setCreateTaskStartTimePicker", "viewListeners"})
    public static void setCreateTaskStartTimePicker(AppCompatTextView textView, LocalTime startTime, AddTaskViewListeners listeners) {
        textView.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(textView.getContext(), (view, hour, minute) -> {
                LocalTime selectedTime = LocalTime.of(hour, minute);
                listeners.onStartTimeSelected(selectedTime);
            }, startTime.getHour(), startTime.getMinute(), false);
            timePickerDialog.show();
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
    }

    @BindingAdapter(value = {"setCreateTaskEndTimePicker", "viewListeners"})
    public static void setCreateTaskEndTimePicker(AppCompatTextView textView, LocalTime endTime, AddTaskViewListeners listeners) {
        textView.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(textView.getContext(), (view, hour, minute) -> {
                LocalTime selectedTime = LocalTime.of(hour, minute);
                listeners.onEndTimeSelected(selectedTime);
            }, endTime.getHour(), endTime.getMinute(), false);

            timePickerDialog.show();
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
    }

    @BindingAdapter("unitEntries")
    public static void setUnitEntries(HintSpinner spinner, List<UnitListResponse.Unit> unitList) {
        spinner.setAdapter(new HintSpinnerAdapter<UnitListResponse.Unit>(spinner.getContext(), unitList, "Select Site Name") {
            @Override
            public String getLabelFor(UnitListResponse.Unit object) {
                return object.unitName + " (" + object.unitCode + ")";
            }
        });
        /*adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/
    }




    @BindingAdapter("regionEntries")
    public static void setRegionEntries(HintSpinner spinner, List<RegionData> unitList) {
        spinner.setAdapter(new HintSpinnerAdapter<RegionData>(spinner.getContext(), unitList, "Select Region") {
            @Override
            public String getLabelFor(RegionData object) {
                return object.regionName;
            }
        });
        /*adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/
    }

    @BindingAdapter("branchEntries")
    public static void setBranchEntries(HintSpinner spinner, List<BranchData> unitList) {
        spinner.setAdapter(new HintSpinnerAdapter<BranchData>(spinner.getContext(), unitList, "Select Branch") {
            @Override
            public String getLabelFor(BranchData object) {
                return object.branchName;
            }
        });
        /*adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/
    }
    @BindingAdapter("siteEntries")
    public static void setSiteEntries(SearchableSpinner spinner, List<SiteData> unitList) {
        spinner.setTitle("Select Site");
        Context context = spinner.getContext();
        ArrayAdapter<SiteData> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, unitList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        /*spinner.setAdapter(new HintSpinnerAdapter<SiteData>(spinner.getContext(), unitList, "Select Site") {
            @Override
            public String getLabelFor(SiteData object) {
                return object.siteName;
            }
        });*/
        /*adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/
    }


    @BindingAdapter("typeEntries")
    public static void setTypeEntries(Spinner spinner, List<TrainingType> trainingTypeList) {
        ArrayAdapter<TrainingType> adapter = new ArrayAdapter<TrainingType>(spinner.getContext(), android.R.layout.simple_spinner_item, trainingTypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @BindingAdapter("coursetypeEntries")
    public static void setCoursetypeEntries(Spinner spinner, List<CourseTypeData> trainingTypeList) {
        ArrayAdapter<CourseTypeData> adapter = new ArrayAdapter<CourseTypeData>(spinner.getContext(), android.R.layout.simple_spinner_item, trainingTypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @BindingAdapter(value = {"setRotaTopicAdapter"})
    public static void setTopicAdapter(RecyclerView recyclerView, AddRotaTopicAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @BindingAdapter("setSpinnerItemSelector")
    public static void onSpinnerItemSelect(Spinner spinner, AddTaskViewListeners taskViewListeners){
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

    @BindingAdapter("setCourseTypeSpinnerItemSelector")
    public static void onCourseSpinnerItemSelect(Spinner spinner, AddTaskViewListeners taskViewListeners){
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

    @BindingAdapter(value = {"rxListener"})
    public static void setRxListener(SearchView searchView, PublishSubject<String> queryListener) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryListener.onNext(newText);
                return false;
            }
        });
    }

    @BindingAdapter("trainingConductType")
    public static void setTrainingConductType(HintSpinner spinner, int dummy) {
        ArrayList<String> type = new ArrayList<>(2);
        type.add("OJT");
        type.add("Classroom");
        spinner.setAdapter(new HintSpinnerAdapter<String>(spinner.getContext(), type, "Select Training Type"));
    }
}