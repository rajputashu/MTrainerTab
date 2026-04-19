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

package com.sisindia.ai.mtrainer.android.features.barcode

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.droidcommons.preference.Prefs
import com.google.android.gms.common.internal.Objects
import com.google.android.material.chip.Chip
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants.CURRENT_POST_ID
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants.CURRENT_POST_NAME
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceViewModel
import com.sisindia.ai.mtrainer.android.features.barcode.barcodedetection.BarcodeField
import com.sisindia.ai.mtrainer.android.features.barcode.barcodedetection.BarcodeProcessor
import com.sisindia.ai.mtrainer.android.features.barcode.barcodedetection.BarcodeResultFragment
import com.sisindia.ai.mtrainer.android.features.barcode.camera.CameraSource
import com.sisindia.ai.mtrainer.android.features.barcode.camera.CameraSourcePreview
import com.sisindia.ai.mtrainer.android.features.barcode.camera.GraphicOverlay
import com.sisindia.ai.mtrainer.android.features.barcode.camera.WorkflowModel
import com.sisindia.ai.mtrainer.android.features.barcode.camera.WorkflowModel.WorkflowState
import com.sisindia.ai.mtrainer.android.models.PostItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.*
import javax.inject.Inject

/** Barcode scanning workflow using camera preview.  */
class LiveBarcodeScanningActivity : MTrainerBaseActivity(), OnClickListener {

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var settingsButton: View? = null
    private var flashButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowState? = null
    //var postItem = HashSet<String>()
    var postItem: PostItem? = null
    var postName: PostItem? = null
    var selectedPostSet = HashSet<String>()
    var selectedEmployeeSet = HashSet<String>()
    lateinit var model: TrainingAttendanceViewModel
    private var dataBase: MtrainerDataBase?=null







    // public var postId: String?=null
    //public var postName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_live_barcode_kotlin)
        preview = findViewById(R.id.camera_preview)
        graphicOverlay = findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay).apply {
            setOnClickListener(this@LiveBarcodeScanningActivity)
            cameraSource = CameraSource(this)
        }

        promptChip = findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
            (AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter) as AnimatorSet).apply {
                setTarget(promptChip)
            }

        findViewById<View>(R.id.close_button).setOnClickListener(this)
        flashButton = findViewById<View>(R.id.flash_button).apply {
            setOnClickListener(this@LiveBarcodeScanningActivity)
        }

        setUpWorkflowModel()
    }

    override fun onResume() {
        super.onResume()
        workflowModel?.markCameraFrozen()
        settingsButton?.isEnabled = true
        currentWorkflowState = WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(BarcodeProcessor(graphicOverlay!!, workflowModel!!))
        workflowModel?.setWorkflowState(WorkflowState.DETECTING)
    }

    override fun onPostResume() {
        super.onPostResume()
        BarcodeResultFragment.dismiss(supportFragmentManager)
    }

    override fun onCreated() {
        //TODO("Not yet implemented")
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    override fun initViewBinding() {
        //TODO("Not yet implemented")
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_live_barcode_kotlin
    }

    override fun extractBundle() {
        //TODO("Not yet implemented")

        try {
                        selectedEmployeeSet = intent.getSerializableExtra("EMPLOYEE_SET") as HashSet<String>
                        selectedPostSet = intent.getSerializableExtra("POST_SET") as HashSet<String>
                    postItem = intent.getSerializableExtra("SELECTED_POST") as PostItem
                    postName = intent.getSerializableExtra("POST_Name") as PostItem

                   } catch (e : Exception) {
                       Log.d(TAG, "extractBundle: Error")
                      e.printStackTrace()
                    }
    }

    override fun initViewState() {
        //TODO("Not yet implemented")
    }

    override fun initViewModel() {
        //TODO("Not yet implemented")
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.close_button -> onBackPressed()
            R.id.flash_button -> {
                flashButton?.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                    } else {
                        it.isSelected = true
                        cameraSource!!.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    }
                }
            }
            /*R.id.settings_button -> {
                settingsButton?.isEnabled = false
                startActivity(Intent(this, SettingsActivity::class.java))
            }*/
        }
    }

    private fun startCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        val cameraSource = this.cameraSource ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                preview?.start(cameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start camera preview!", e)
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
            flashButton?.isSelected = false
            preview?.stop()
        }
    }

    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel::class.java)

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState
            Log.d(TAG, "Current workflow state: ${currentWorkflowState!!.name}")

            val wasPromptChipGone = promptChip?.visibility == View.GONE

            when (workflowState) {
                WorkflowState.DETECTING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                WorkflowState.CONFIRMING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }
                WorkflowState.SEARCHING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }
                WorkflowState.DETECTED, WorkflowState.SEARCHED -> {
                    promptChip?.visibility = View.GONE
                    stopCameraPreview()
                }
                else -> promptChip?.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation =
                              wasPromptChipGone && promptChip?.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        workflowModel?.detectedBarcode?.observe(this, Observer { barcode ->
            if (barcode != null) {
                val barcodeFieldList = ArrayList<BarcodeField>()
                barcodeFieldList.add(BarcodeField("Raw Value", barcode.rawValue ?: ""))
                val data = parseData(barcode.rawValue ?: "")
                //size written for scaning other than sis qrcode it will show error msg
                if(data.size < 2) {
                    showToast("Please Scan Correct BarCode")
                    workflowModel?.setWorkflowState(WorkflowState.DETECTING)
                } else
                    BarcodeResultFragment.show(
                     supportFragmentManager,
                     data[0].trim(),
                     data[1].trim()
                    )
            }
        })
    }

    companion object {
        private const val TAG = "LiveBarcodeActivity"
    }

    fun parseData(data : String) : List<String> {
        if(data.contains("\r\n"))
            return data.split("\r\n")
        else if(data.contains("\n"))
            return data.split("\n")
        else return emptyList()
    }


}
