package com.sisindia.ai.mtrainer.android.features.takeassessment

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity
import com.sisindia.ai.mtrainer.android.db.entities.LanguageEntity
import com.sisindia.ai.mtrainer.android.features.trainingprogram.LanguageSelectListener
import com.sisindia.ai.mtrainer.android.models.assesments.CourseListResponse
import com.sisindia.ai.mtrainer.android.models.assesments.CourseTypeResponseV2
import com.sisindia.ai.mtrainer.android.models.assesments.ProgramResponseV2
import com.sisindia.ai.mtrainer.android.models.assesments.TopicListResponse

@BindingAdapter(value = ["courseTypeList", "courseTypeListener"])
fun AppCompatSpinner.bindCourseTypeSpinner(
    list: List<CourseTypeResponseV2.CourseTypeDataV2>?,
    listener: CourseTypeListener?
) {
    if (list.isNullOrEmpty() || listener == null) {
        return
    }

    val courseTypeList = ArrayList<String>()
    courseTypeList.add("Select Course Type")

    for (data in list) {
        courseTypeList.add(data.courseTypeName)
    }

    val modeAdapter = ArrayAdapter(
        context,
        R.layout.support_simple_spinner_dropdown_item,
        courseTypeList
    )

    adapter = modeAdapter
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
            listener.onCourseTypeSelected(pos)
        }
    }
}


@BindingAdapter(value = ["programList", "listener"])
fun AppCompatSpinner.bindProgramSpinner(
    list: List<ProgramResponseV2.ProgramDataV2>?,
    listener: ProgramListener?
) {
    if (list.isNullOrEmpty() || listener == null) {
        return
    }

    val courseTypeList = ArrayList<String>()
    courseTypeList.add("Select Program")

    for (data in list) {
        courseTypeList.add(data.programName)
    }

    val modeAdapter = ArrayAdapter(
        context,
        R.layout.support_simple_spinner_dropdown_item,
        courseTypeList
    )

    adapter = modeAdapter
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
            listener.onProgramSelected(pos)
        }
    }
}

@BindingAdapter(value = ["courseList", "listener"])
fun AppCompatSpinner.bindCourseListSpinner(
    list: List<CourseListResponse.CourseListData>?,
    listener: CourseListener?
) {
    if (list.isNullOrEmpty() || listener == null) {
        return
    }

    val courseTypeList = ArrayList<String>()
    courseTypeList.add("Select Course")

    for (data in list) {
        courseTypeList.add(data.courseName)
    }

    val modeAdapter = ArrayAdapter(
        context,
        R.layout.support_simple_spinner_dropdown_item,
        courseTypeList
    )

    adapter = modeAdapter
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
            listener.onCourseListSelected(pos)
        }
    }
}

@BindingAdapter(value = ["topicList", "listener"])
fun AppCompatSpinner.bindCourseTopicSpinner(
    list: List<TopicListResponse.TopicListData>?,
    listener: CourseTopicListener?
) {
    if (list.isNullOrEmpty() || listener == null) {
        return
    }

    val courseTypeList = ArrayList<String>()
    courseTypeList.add("Select Topic")

    for (data in list) {
        courseTypeList.add(data.topicName)
    }

    val modeAdapter = ArrayAdapter(
        context,
        R.layout.support_simple_spinner_dropdown_item,
        courseTypeList
    )

    adapter = modeAdapter
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
            listener.onCourseTopicSelected(pos)
        }
    }
}

@BindingAdapter(value = ["courseList", "courseListener"])
fun AppCompatAutoCompleteTextView.setCourseListAdapter(
    list: List<CourseListResponse.CourseListData>?,
    listener: CourseListener?
) {
    if (!list.isNullOrEmpty()) {

        val courseList = list.map { it.courseName }

        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            courseList
        )

        setAdapter(adapter)

        setOnItemClickListener { _, _, position, _ ->
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)

            val selectedName = adapter.getItem(position)
            val selectedData = list.firstOrNull { it.courseName == selectedName }

            listener?.onCourseListSelected(position, selectedData)
            setText("")
        }
    }
}

@BindingAdapter("attendanceMO")
fun AppCompatTextView.setNameAndEmpCode(entity: AttendanceEntity?) {

    if (entity == null) {
        text = "-"
        return
    }

    // Pick proper name → priority: employeeName → empName
    val name = when {
        !entity.employeeName.isNullOrBlank() && entity.employeeName != "NA" ->
            entity.employeeName

        !entity.empName.isNullOrBlank() && entity.empName != "NA" ->
            entity.empName

        else -> null
    }

    // Handle empCode
    val code = when {
        !entity.empCode.isNullOrBlank() && entity.empCode != "NA" ->
            entity.empCode

        else -> null
    }

    // Final formatted text
    text = when {
        name != null && code != null -> "$name ($code)"
        name != null -> name
        code != null -> code
        else -> "-"
    }
}

@BindingAdapter(value = ["languageList", "listener", "selectedLanguageId"])
fun AppCompatSpinner.bindLanguageSpinner(
    list: List<LanguageEntity>?,
    listener: LanguageSelectListener?,
    selectedLanguageId: Int        // ← new: passed from ViewModel ObservableInt
) {
    if (list.isNullOrEmpty() || listener == null) return

    val languageNames = list.map { it.languageType }

    val modeAdapter = ArrayAdapter(
        context,
        R.layout.support_simple_spinner_dropdown_item,
        languageNames
    )
    adapter = modeAdapter

    // -------------------------------------------------------------------------
    // Restore previously saved selection.
    // Find the position whose languageId matches what is stored in the ViewModel.
    // Falls back to 0 if nothing was saved yet.
    // -------------------------------------------------------------------------
    val savedPosition = list.indexOfFirst { it.languageId == selectedLanguageId }
    val restorePosition = if (savedPosition >= 0) savedPosition else 0

    // Restore silently — setSelection triggers onItemSelected, so we detach the
    // listener first, restore the position, then reattach. This prevents a
    // spurious save-to-prefs call every time the screen is recreated.
    onItemSelectedListener = null
    setSelection(restorePosition, false)

    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
            listener.onLanguageSelect(list[pos].languageId)
        }
    }
}
