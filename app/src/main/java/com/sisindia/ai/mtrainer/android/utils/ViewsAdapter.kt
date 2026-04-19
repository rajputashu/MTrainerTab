package com.sisindia.ai.mtrainer.android.utils

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsAdapterV2
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsViewListeners
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCourseViewListeners
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCoursesAdapterV2
import com.sisindia.ai.mtrainer.android.features.trainingprogram.TrainingProgramAdapter
import com.sisindia.ai.mtrainer.android.features.trainingprogram.TrainingProgramListener

@BindingAdapter(value = ["mailAction"])
fun AppCompatTextView.setSendMail(mailStatus: String?) {
    if (!mailStatus.isNullOrEmpty()) {
        when (mailStatus) {
            "NA" -> {
                setTextColor(ContextCompat.getColor(context, R.color.navyBlue))
                text = "N.A."
            }

            "MailSent" -> {
                setTextColor(ContextCompat.getColor(context, R.color.pendingTextYellow))
                text = "Mail Sent"
            }

            "SendMail" -> {
                setTextColor(ContextCompat.getColor(context, R.color.colorLightRed))
                text = "Send Mail"
            }
        }
    } else {
        setTextColor(ContextCompat.getColor(context, R.color.navyBlue))
        text = "N.A."
    }
}

@BindingAdapter("imageUrl")
fun AppCompatImageView.loadImage(url: String?) {
    Glide.with(context)
        .load(url)
        .placeholder(android.R.drawable.alert_dark_frame)
        .error(android.R.drawable.alert_dark_frame)
        .into(this)
}

@BindingAdapter(value = ["trainingTopicAdapter", "trainingTopicListener"])
fun RecyclerView.bindTopicRV(
    recyclerAdapter: TrainingProgramAdapter?, listener: TrainingProgramListener?
) {
    if (layoutManager == null) {
        layoutManager = GridLayoutManager(context, 3)
        isNestedScrollingEnabled = false
    }
    adapter = recyclerAdapter
    recyclerAdapter?.initListener(listener)
}

@BindingAdapter(value = ["trainingCourseAdapter", "trainingCourseListener"])
fun RecyclerView.bindCoursesRV(
    recyclerAdapter: TrainingCoursesAdapterV2?, listener: TrainingCourseViewListeners?
) {
    if (layoutManager == null) {
        layoutManager = GridLayoutManager(context, 3)
        isNestedScrollingEnabled = false
    }
    adapter = recyclerAdapter
    recyclerAdapter?.initListener(listener)
}

@BindingAdapter(value = ["trainingTopicAdapter", "trainingTopicListener"])
fun RecyclerView.bindTopicRV(
    recyclerAdapter: TrainingTopicsAdapterV2?, listener: TrainingTopicsViewListeners?
) {
    if (layoutManager == null) {
        layoutManager = GridLayoutManager(context, 3)
        isNestedScrollingEnabled = false
    }
    adapter = recyclerAdapter
    recyclerAdapter?.initListener(listener)
}