package com.sisindia.ai.mtrainer.android.features.dashboard;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;

import com.google.android.material.navigation.NavigationView;
import com.jaiselrahman.hintspinner.HintSpinner;
import com.jaiselrahman.hintspinner.HintSpinnerAdapter;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.features.addrota.AddTaskViewListeners;

import java.util.ArrayList;

public class DashBoardDataBinding {
    /*  @BindingAdapter("android:src")
      public static void setImageDrawable(FloatingActionButton view, Drawable drawable) {
          view.setImageDrawable(drawable);
      }

      @BindingAdapter("android:src")
      public static void setImageResource(FloatingActionButton imageView, int resource){
          imageView.setImageResource(resource);
      }*/
    @BindingAdapter("appImageDrawable")
    public static void setAppImageDrawable(AppCompatImageView view, String companyId) {
        Log.v("appImage ", "Com. Id : " + companyId);
        switch (companyId) {
            case "1":
                view.setImageResource(R.drawable.dtss_logo);
                break;
            case "2":
                view.setImageResource(R.drawable.company_logo);
                break;
            case "4":
                view.setImageResource(R.drawable.rare_logo);
                break;
            case "7":
                view.setImageResource(R.drawable.ic_slv_logo);
                break;
            case "8":
                view.setImageResource(R.drawable.smc_logo);
                break;
            case "9":
                view.setImageResource(R.drawable.ic_uniq_logo);
                break;
        }

    }

    @BindingAdapter("companyText")
    public static void setCompnayText(AppCompatTextView view, String companyId) {
        switch (companyId) {
            case "1":
                view.setText("DTSS");
                break;
            case "2":
                view.setText("SIS(India Ltd)");
                break;
            case "4":
                view.setText("RARE");
                break;
            case "7":
                view.setText("SISCO");
                break;
            case "8":
                view.setText("SMC");
                break;
            case "9":
                view.setText("UNIQ");
                break;
        }

    }

    @BindingAdapter("homeMenu")
    public static void setHomeMenu(NavigationView navigationView, int id) {
        navigationView.inflateMenu(id);
    }


    @BindingAdapter("setSpinnerItemStatusSelector")
    public static void onSpinnerItemSelect(Spinner spinner, AddTaskViewListeners taskViewListeners) {
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


    @BindingAdapter("trainingThroughType")
    public static void setTrainingConductType(HintSpinner spinner, int dummy) {
        ArrayList<String> type = new ArrayList<>(2);
        type.add("VAN");
        type.add("TAB");
        spinner.setAdapter(new HintSpinnerAdapter<String>(spinner.getContext(), type, "Select"));
    }

    @BindingAdapter("trainingOnRoad")
    public static void setTrainingOnRoad(HintSpinner spinner, int dummy) {
        ArrayList<String> type = new ArrayList<>(2);
        type.add("YES");
        type.add("NO");
        spinner.setAdapter(new HintSpinnerAdapter<String>(spinner.getContext(), type, "Select"));
    }

    @BindingAdapter("trainingReason")
    public static void setTrainingReason(HintSpinner spinner, int dummy) {
        ArrayList<String> type = new ArrayList<>(5);
        type.add("Van servicing");
        type.add("Weather problem");
        type.add("Driver Leave");
        type.add("Van not working");
        type.add("Others");
        spinner.setAdapter(new HintSpinnerAdapter<String>(spinner.getContext(), type, "Select"));
    }


}
