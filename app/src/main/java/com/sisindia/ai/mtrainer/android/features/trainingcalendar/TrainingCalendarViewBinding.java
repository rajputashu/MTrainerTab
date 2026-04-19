package com.sisindia.ai.mtrainer.android.features.trainingcalendar;

import static org.threeten.bp.DayOfWeek.SUNDAY;
import static org.threeten.bp.temporal.TemporalAdjusters.next;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.features.addrota.AddTaskViewListeners;
import com.sisindia.ai.mtrainer.android.utils.DividerDecoration;

import org.threeten.bp.LocalDate;

import java.util.Calendar;

public class TrainingCalendarViewBinding {
    @BindingAdapter(value = {"setTrainingCalendarRecyclerAdapter", "listener"})
    public static void setTrainingCalendarReclerAdpter(RecyclerView recyclerView,
                                                       TrainingCalendarRecyclerAdapter recyclerAdapter,
                                                       AddTaskViewListeners listener) {

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter.initListener(listener);
        recyclerView.setAdapter(recyclerAdapter);

        Drawable dividerDrawable = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.rv_divider);
        if (dividerDrawable != null) {
            DividerDecoration dividerItemDecoration = new DividerDecoration(dividerDrawable);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

    @BindingAdapter(value = {"setCreateTaskDatePicker1", "viewListeners1"})
    public static void setCreateTaskDatePicker(AppCompatTextView textView, LocalDate date, AddTaskViewListeners listeners) {
        int selectedYear = date.getYear();
        int selectedMonth = date.getMonthValue() - 1;
        int selectedDayOfMonth = date.getDayOfMonth();

        final LocalDate currentSunday = LocalDate.now().with(next(SUNDAY));
        final LocalDate nextSunday = currentSunday.with(next(SUNDAY));
        Calendar maxDate = Calendar.getInstance();
    /*  maxDate.set(Calendar.YEAR, nextSunday.getYear());
        maxDate.set(Calendar.MONTH, nextSunday.getMonthValue() - 1);
        maxDate.set(Calendar.DAY_OF_MONTH, nextSunday.getDayOfMonth());
    */
        textView.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(textView.getContext(), (view, year, month, dayOfMonth) -> {
                LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                listeners.onDateChanged(v.getId(), selectedDate);
            }, selectedYear, selectedMonth, selectedDayOfMonth);
            // datePickerDialog.getDatePicker().setMinDate(Instant.now().toEpochMilli());
            //  datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
            datePickerDialog.show();
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        });
    }
}
