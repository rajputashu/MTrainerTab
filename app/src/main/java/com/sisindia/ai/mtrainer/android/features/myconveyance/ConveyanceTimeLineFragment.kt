package com.sisindia.ai.mtrainer.android.features.myconveyance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment
import com.sisindia.ai.mtrainer.android.databinding.FragmentConveyanceTimeLineBinding
import com.sisindia.ai.mtrainer.android.models.ConveyanceDailyData

/**
 * A simple [Fragment] subclass.
 * Use the [ConveyanceTimeLineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ConveyanceTimeLineFragment : MTrainerBaseFragment() {

    lateinit var binding:FragmentConveyanceTimeLineBinding
    lateinit var viewModel:MyConveyanceViewModel
    lateinit var conveyanceDailyData: ConveyanceDailyData


    override fun extractBundle() {
        conveyanceDailyData = arguments?.get("dailydata") as ConveyanceDailyData
    }

    override fun initViewModel() {
        viewModel = getAndroidViewModel(MyConveyanceViewModel::class.java) as MyConveyanceViewModel
    }

    override fun initViewBinding(inflater: LayoutInflater?, container: ViewGroup?): View {
        binding = bindFragmentView(layoutResource, inflater, container) as FragmentConveyanceTimeLineBinding
        binding.vm = viewModel
        viewModel.selecteddate.set(conveyanceDailyData.date)
        viewModel.fetchconveyencetimelinedata()
        binding.executePendingBindings()
        return binding.root
    }

    override fun initViewState() {
    }

    override fun onCreated() {
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_conveyance_time_line
    }

    companion object {
        fun newInstance(conveyanceDailyData: ConveyanceDailyData) =
            ConveyanceTimeLineFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("dailydata", conveyanceDailyData)
                }
            }
    }
}