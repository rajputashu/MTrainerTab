package com.sisindia.ai.mtrainer.android.features.trainingcourses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.droidcommons.base.recycler.BaseRecyclerAdapter
import com.droidcommons.base.recycler.BaseViewHolder
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.databinding.RowTrainingCoursesBinding
import com.sisindia.ai.mtrainer.android.models.online.TrainingCourseDataResponseMO

class TrainingCoursesAdapterV2 : BaseRecyclerAdapter<TrainingCourseDataResponseMO>() {

    private var listener: TrainingCourseViewListeners? = null

    fun initListener(listener: TrainingCourseViewListeners?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: RowTrainingCoursesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.row_training_courses,
            parent,
            false
        )
        return TrainingCourseVH(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as TrainingCourseVH
        val item = getItem(position)
        viewHolder.onBind(item)
    }

    inner class TrainingCourseVH(
        itemBinding: ViewDataBinding
    ) : BaseViewHolder<TrainingCourseDataResponseMO>(itemBinding) {

        private val binding = itemBinding as RowTrainingCoursesBinding

        override fun onBind(item: TrainingCourseDataResponseMO) {
            binding.adapterItem = item

            binding.root.setOnClickListener {
                listener?.onCourseClickV2(item)
            }
        }
    }
}