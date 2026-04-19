package com.sisindia.ai.mtrainer.android.features.myconveyance

import android.content.Intent
import android.util.Log
import androidx.lifecycle.Observer
import com.droidcommons.base.BaseActivity
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants
import com.sisindia.ai.mtrainer.android.databinding.ActivityMyconvenceBinding
import com.sisindia.ai.mtrainer.android.models.ConveyanceDailyData
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyData

class MyconvenceActivity : MTrainerBaseActivity() {

    lateinit var myconveyanceviewmodel:MyconveyanceactivityViewmodel
    lateinit var binding:ActivityMyconvenceBinding

    companion object{
        fun newInstance(baseActivity:BaseActivity):Intent{
            val intent = Intent(baseActivity,MyconvenceActivity::class.java)
            return intent
        }
    }

    override fun extractBundle() {

    }

    override fun initViewState() {
        liveData.observe(this, Observer {
            when(it.what){
                NavigationConstants.OPEN_CONVEYANCEDAILY->{
                    Log.d("dsfdgvgdf","opendaily")
                    openConveyancedailyScreen(it.obj as ConveyanceMonthlyData)
                }
                NavigationConstants.OPEN_CONVEYANCEMONTHLY->{
                    openConveyancemonthlyScreen()
                }
                NavigationConstants.OPEN_CONVEYANCETIMELINE->{
                    openConveyancetimelineScreen(it.obj as ConveyanceDailyData)
                }
            }
        })
    }

    override fun onCreated() {
        openConveyancemonthlyScreen()
    }

    override fun initViewBinding() {
        binding = bindActivityView(this, layoutResource) as ActivityMyconvenceBinding
        binding.vmviewmodel = myconveyanceviewmodel
        binding.executePendingBindings()
    }

    override fun initViewModel() {
        myconveyanceviewmodel = getAndroidViewModel(MyconveyanceactivityViewmodel::class.java) as MyconveyanceactivityViewmodel
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_myconvence
    }

    private fun openConveyancemonthlyScreen() {
        loadFragment(R.id.conveyancecontainer, MyConveyanceFragment.newInstance(), FRAGMENT_REPLACE, false)
    }

    private fun openConveyancedailyScreen(conveyanceMonthlyData: ConveyanceMonthlyData) {
        loadFragment(R.id.conveyancecontainer, MyConveyanceDailyFragment.newInstance(conveyanceMonthlyData), FRAGMENT_REPLACE, true)
    }

    private fun openConveyancetimelineScreen(conveyanceDailyData: ConveyanceDailyData) {
        loadFragment(R.id.conveyancecontainer, ConveyanceTimeLineFragment.newInstance(conveyanceDailyData), FRAGMENT_REPLACE, true)
    }


}