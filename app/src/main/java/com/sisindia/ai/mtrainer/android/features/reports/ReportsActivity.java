package com.sisindia.ai.mtrainer.android.features.reports;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingCourses1Binding;
import com.sisindia.ai.mtrainer.android.databinding.LayoutReportsBinding;
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsActivity;
import com.sisindia.ai.mtrainer.android.models.EmployeeReportsResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportsActivity extends MTrainerBaseActivity {

    LayoutReportsBinding binding;
    private Calendar startDate, endDate;
    boolean isStartDate;
    DatePickerDialog datePickerDialog;

    @Override
    protected void extractBundle() {}

    @Override
    protected void initViewState() {
        liveData.observe(this, new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
                Intent intent = new Intent(ReportsActivity.this, TrainingTopicsActivity.class);
                Bundle bundle =  new Bundle();
                bundle.putParcelable("trainingCoursesModel",(Parcelable) message.obj);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreated() {
        binding.relative37.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdatepickerdialog(true);
            }
        });

        binding.relative38.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdatepickerdialog(false);
            }
        });

/*
        binding.cbCoursenotfound.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                List<EmployeeReportsResponse> list = new ArrayList<>();
                if(!viewModel.trainingcourses.get().isEmpty()){
                    for (EmployeeReportsResponse data : viewModel.trainingcourses.get()) {
                    if(data.equals("NotStarted")){
                        list.add(data);
                    }
                    }
                viewModel.adapter.clearAndSetItems(list);
                    viewModel.adapter.notifyDataSetChanged();
                }
            }else{
                    viewModel.adapter.clearAndSetItems(viewModel.trainingcourses.get());
                    viewModel.adapter.notifyDataSetChanged();
            }
        });
*/


/*binding.backtodashbtn.setOnClickListener(view -> {
    Intent intent = new Intent(TrainingCoursesActivity.this, DashBoardActivity.class);
    startActivity(intent);

});*/


//        binding.logoutbtn.setOnClickListener(
//                view -> {
//
//                    // Create the object of AlertDialog Builder class
//                    Dialog dialog = new Dialog(TrainingCoursesActivity.this);
//
//                    // Set the message show for the Alert time
//
//                    dialog.setContentView(R.layout.custom_logout_dialog);
//                    dialog.setCancelable(false);
//                    dialog.setCanceledOnTouchOutside(false);
//
//                    TextView yes = dialog.findViewById(R.id.yesbtn);
//                    TextView no = dialog.findViewById(R.id.nobtn);
//
//                    yes.setOnClickListener(view1 -> {
//                        viewModel.clearAllTableData();
//                        Intent i = new Intent(this, OnBoardActivity.class);
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(i);
//                        finish();
//                    });
//
//                    no.setOnClickListener(view1 -> {
//                        dialog.dismiss();
//                    });
//
//                    dialog.show();
//
//
//                        /*// Set Alert Title
//                        builder.setTitle("Alert !");
//
//                        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
//                        builder.setCancelable(false);
//
//                        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
//                        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//                            viewModel.clearAllTableData();
//                            Intent i = new Intent(this, OnBoardActivity.class);
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(i);
//                            finish();
//                        });
//
//                        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
//                        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
//                            // If user click no then dialog box is canceled.
//                            dialog.dismiss();
//
//                        });
//
//                        // Create the Alert dialog
//                        AlertDialog alertDialog = builder.create();
//                        // Show the Alert Dialog box
//                        alertDialog.show();*/
//
//                }
//
//        );
    }

    private void showdatepickerdialog(boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

         datePickerDialog = new DatePickerDialog(
                ReportsActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        if (isStartDate) {
                            startDate = selectedDate;
                            viewModel.selectedstartdate.set(android.text.format.DateFormat.format("yyyy-MM-dd", startDate).toString());
                            viewModel.selectedenddate.set("");
                            binding.spMonth.setText("");
                        } else {
                            endDate = selectedDate;
                            if(startDate.compareTo(endDate)>0){
                                showToast("End Date Should be more than start date");
                                endDate = null;
                                viewModel.selectedenddate.set("");
                                datePickerDialog.dismiss();
                            }else{
                                viewModel.selectedenddate.set(android.text.format.DateFormat.format("yyyy-MM-dd", endDate).toString());
                            }
                        }

                       /* if(startDate==null){
                            showToast("Select Start Date");
                            datePickerDialog.dismiss();
                        }else {
                            viewModel.selectedstartdate.set(new SimpleDateFormat("yyyy-MM-dd").format(startDate));
                        }*/

                    }
                },
                year, month, day
        );

        if (isStartDate && endDate != null) {
            datePickerDialog.getDatePicker().setMaxDate(endDate.getTimeInMillis());
        } else if (!isStartDate && startDate != null) {
            datePickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
        }

        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    @Override
    protected void initViewBinding() {
        binding = (LayoutReportsBinding) bindActivityView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();

    }

    @Override
    protected void initViewModel() {
        viewModel = (ReportsViewmodel) getAndroidViewModel(ReportsViewmodel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_reports;
    }

    private ReportsViewmodel viewModel;


}