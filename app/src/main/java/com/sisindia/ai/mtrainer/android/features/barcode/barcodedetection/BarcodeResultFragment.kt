/*
 * Copyright 2020 KnowledgeFlex
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sisindia.ai.mtrainer.android.features.barcode.barcodedetection

import android.content.DialogInterface
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.droidcommons.preference.Prefs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sisindia.ai.mtrainer.android.features.barcode.camera.WorkflowModel
import com.sisindia.ai.mtrainer.android.features.barcode.camera.WorkflowModel.WorkflowState
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.base.MTrainerBottomSheetDialogFragment
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants.*
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants
import com.sisindia.ai.mtrainer.android.databinding.BarcodeBottomSheetBinding
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/** Displays the bottom sheet to present barcode fields contained in the detected barcode.  */
class BarcodeResultFragment : MTrainerBottomSheetDialogFragment() {
    private lateinit var name: String
    private lateinit var empCode: String
    private lateinit var database : MtrainerDataBase
    private lateinit var viewModel : BarCodeResultViewModel
    private lateinit var binding: BarcodeBottomSheetBinding
    lateinit var model: TrainingAttendanceViewModel

    override fun applyStyle() {

    }

    override fun extractBundle() {
        val arguments = arguments
        if (arguments?.containsKey(ARG_BARCODE_NAME_FIELD) == true && arguments.containsKey(
                ARG_BARCODE_EMPCODE_FIELD
            )) {
            name = arguments.getString(ARG_BARCODE_NAME_FIELD) ?: ""
            empCode = arguments.getString(ARG_BARCODE_EMPCODE_FIELD) ?: ""
        } else {
            Log.e(TAG, "Unable to fetch info from QRCode")
        }
    }

    override fun initViewState() {
        liveData.observe(this,
            Observer<Message> {
                when(it.what) {
                    CLOSE_BARCODE_DETAIL_FRAGMENT -> dismissAllowingStateLoss()
                }
            })
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        activity?.let {
            // Back to working state after the bottom sheet is dismissed.
            ViewModelProviders.of(it).get(WorkflowModel::class.java).setWorkflowState(WorkflowState.DETECTING)
        }
        super.onDismiss(dialogInterface)
    }

    override fun onCreated() {
        binding.tvName.text = name
        binding.tvEmpcode.text = empCode
        viewModel.empcode = empCode
        model = ViewModelProvider(this).get(TrainingAttendanceViewModel::class.java)
    }

    override fun initViewModel() {
        viewModel = getAndroidViewModel(BarCodeResultViewModel::class.java) as BarCodeResultViewModel
    }

    override fun initViewBinding(inflater: LayoutInflater?, container: ViewGroup?): View {
        binding =
            bindFragmentView(layoutResource, inflater, container) as BarcodeBottomSheetBinding
        binding.vm = viewModel
        binding.executePendingBindings()
        return binding.root
    }

    override fun getLayoutResource(): Int {
        return R.layout.barcode_bottom_sheet
    }

    companion object {

        private const val TAG = "BarcodeResultFragment"
        private const val ARG_BARCODE_NAME_FIELD = "arg_barcode_name_field"
        private const val ARG_BARCODE_EMPCODE_FIELD = "arg_barcode_empcode_field"

        fun show(fragmentManager: FragmentManager, name : String, empCode : String) {
            val barcodeResultFragment = BarcodeResultFragment()
            barcodeResultFragment.arguments = Bundle().apply {
                putString(ARG_BARCODE_NAME_FIELD, name)
                putString(ARG_BARCODE_EMPCODE_FIELD, empCode)
            }
            barcodeResultFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as BarcodeResultFragment?)?.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog?
            val bottomSheet =
                dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }
    }

}
