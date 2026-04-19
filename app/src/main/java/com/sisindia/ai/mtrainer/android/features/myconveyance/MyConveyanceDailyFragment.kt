package com.sisindia.ai.mtrainer.android.features.myconveyance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.droidcommons.base.BaseFragment
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment
import com.sisindia.ai.mtrainer.android.databinding.FragmentConveyancemonthlyBinding
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyData

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */

class MyConveyanceDailyFragment : MTrainerBaseFragment() {

    private var viewModel: MyConveyanceViewModel? = null
    lateinit var binding:FragmentConveyancemonthlyBinding
    lateinit var conveyanceMonthlyData: ConveyanceMonthlyData

    override fun extractBundle() {
        conveyanceMonthlyData = arguments?.getSerializable("monthlydata") as ConveyanceMonthlyData
    }

    override fun initViewModel() {
        viewModel = getAndroidViewModel(MyConveyanceViewModel::class.java) as MyConveyanceViewModel
    }

    override fun initViewBinding(inflater: LayoutInflater?, container: ViewGroup?): View {
        binding = bindFragmentView(layoutResource,inflater,container) as FragmentConveyancemonthlyBinding
        binding.vm = viewModel
        viewModel?.selectedmonth?.set(conveyanceMonthlyData.month)
        viewModel?.selectedyear?.set(conveyanceMonthlyData.year)
        viewModel?.fetchconveyencedialydata()
        binding.executePendingBindings()
        return binding.root
    }

    override fun initViewState() {
    }

    override fun onCreated() {
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_conveyancemonthly
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConveyancemonthlyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(conveyanceMonthlyData: ConveyanceMonthlyData): BaseFragment =
            MyConveyanceDailyFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("monthlydata",conveyanceMonthlyData)
                }
            }
    }
}