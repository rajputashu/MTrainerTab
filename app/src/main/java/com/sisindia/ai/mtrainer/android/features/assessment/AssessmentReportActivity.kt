package com.sisindia.ai.mtrainer.android.features.assessment

import androidx.lifecycle.Observer
import com.droidcommons.preference.Prefs
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants
import com.sisindia.ai.mtrainer.android.databinding.ActivityAssessmentReportBinding

class AssessmentReportActivity : MTrainerBaseActivity() {

    lateinit var viewModel: AssessmentReportViewModel
    lateinit var binding: ActivityAssessmentReportBinding

    override fun extractBundle() {
    }

    override fun initViewState() {
        liveData.observe(this, Observer {
            when (it.what) {

            }
        })
    }

    override fun onCreated() {
        setupToolBarForBackArrow(binding.tbAssessmentReport)
        binding.includeTimeSpent?.tvTimeSpent?.text = Prefs.getString(PrefsConstants.STARTED_TIME)

        viewModel.getCourseAssessmentReportList()
    }

    override fun initViewBinding() {
        binding = bindActivityView(this, layoutResource) as ActivityAssessmentReportBinding
        binding.vm = viewModel
        binding.executePendingBindings()
    }

    override fun initViewModel() {
        viewModel =
            getAndroidViewModel(AssessmentReportViewModel::class.java) as AssessmentReportViewModel
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_assessment_report
    }
}