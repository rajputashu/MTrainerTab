package com.sisindia.ai.mtrainer.android.features.myconveyance

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaiselrahman.hintspinner.HintSpinner
import com.jaiselrahman.hintspinner.HintSpinnerAdapter
import com.sisindia.ai.mtrainer.android.features.myunits.BranchViewListeners
import com.sisindia.ai.mtrainer.android.features.reports.ReortsViewListeners
import com.sisindia.ai.mtrainer.android.features.reports.ReportsAdapter
import com.sisindia.ai.mtrainer.android.uimodels.YearUIModel
import com.sisindia.ai.mtrainer.android.utils.TimeUtils

open class MyconveyanceBinding {

    companion object{
        @BindingAdapter(value = ["setmyconveneceadapter", "setmyconvencelistener"])
        @JvmStatic
        open fun setMyconveyanceAdapter(
            recyclerView: RecyclerView,
            conveyanceadapter: MyConveyanceMonthlyRecyclerAdapter,
            listeners: MyconvenceListeners
        ){
            val linearLayoutManager = LinearLayoutManager(recyclerView.context)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = conveyanceadapter
            conveyanceadapter.setListener(listeners)
        }


        @BindingAdapter(value = ["setmyreportsemployeeadapter", "setmyemployeereportslistener"])
        @JvmStatic
        open fun setMyEmployeeReportsAdapter(
            recyclerView: RecyclerView,
            conveyanceadapter: ReportsAdapter,
            listeners: ReortsViewListeners
        ){
            val linearLayoutManager = LinearLayoutManager(recyclerView.context)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = conveyanceadapter
            conveyanceadapter.setListener(listeners)
        }

        @BindingAdapter(value = ["setmyconvenecedailyadapter", "setmyconvencedailylistener"])
        @JvmStatic
        open fun setMyconveyancedailyAdapter(
            recyclerView: RecyclerView,
            conveyanceadapter: MyConveyanceDialyRecyclerAdapter,
            listeners: MyconvencedailyListeners
        ){
            val linearLayoutManager = LinearLayoutManager(recyclerView.context)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = conveyanceadapter
            conveyanceadapter.setListener(listeners)
        }

        @BindingAdapter(value = ["setmyconvenecetimelineadapter", "setmyconvencetimelinelistener"])
        @JvmStatic
        open fun setMyconveyancetimelineAdapter(
            recyclerView: RecyclerView,
            conveyanceadapter: MyConveyanceTimelineRecyclerAdapter,
            listeners: MyconvencetimelineListeners
        ){
            val linearLayoutManager = LinearLayoutManager(recyclerView.context)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = conveyanceadapter
            conveyanceadapter.setListener(listeners)
        }


        @BindingAdapter(value = ["yearEntriesUnits","setSpinnerItemSelectorYear"])
        @JvmStatic
        open fun setyearEntries(spinner: HintSpinner, unitList: List<YearUIModel?>?, taskViewListeners: YearListeners) {
            spinner.setAdapter(object : HintSpinnerAdapter<YearUIModel>(spinner.context, unitList, "Year") {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)
                    (v as TextView).setTextColor(Color.BLACK)
                    return v
                }

                override fun getLabelFor(`object`: YearUIModel): String {

                    //  TextView textView;
                    //  textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                    return `object`.year
                }
            })

            spinner.setSelection(1)

            try {
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        try {
                            (parent?.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                            // parent.setTextColor(context.getResources().getColor(R.color.color_hint));
                            taskViewListeners.onyearitemclick(position)
                        }catch (e:Exception){

                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        try {
                            (parent?.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                        }catch (e:Exception){

                        }
                    }
                }

            }catch (e:Exception){

            }

        }

        @BindingAdapter(value = ["monthEntriesUnits","setSpinnerItemSelectorMonth"])
        @JvmStatic
        open fun setmonthEntries(spinner: HintSpinner, unitList: List<YearUIModel?>?, taskViewListeners: MonthListeners) {
            spinner.setAdapter(object : HintSpinnerAdapter<YearUIModel>(spinner.context, unitList, "Month") {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)
                    (v as TextView).setTextColor(Color.BLACK)
                    return v
                }

                override fun getLabelFor(`object`: YearUIModel): String {
                    //  TextView textView;
                    //  textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                    return `object`.year
                }
            })
            spinner.setSelection(TimeUtils.getMonthNum())

            try {
                spinner.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        try {
                            (parent?.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                            // parent.setTextColor(context.getResources().getColor(R.color.color_hint));
                            taskViewListeners.onmonthitemclick(position)
                        }catch (e:Exception){

                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        try {
                            (parent?.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                        }catch (e:Exception){

                        }
                    }
                }
            }catch (e:Exception){

            }
        }


/*
        @BindingAdapter("setSpinnerItemSelectorYear")
        @JvmStatic
        open fun onSpinnerItemSelectyear(spinner: Spinner, taskViewListeners: YearListeners) {
        }
*/

/*
        @BindingAdapter("setSpinnerItemSelectorMonth")
        @JvmStatic
        open fun onSpinnerItemSelectmonth(spinner: Spinner, taskViewListeners: MonthListeners) {
        }
*/


    }



}