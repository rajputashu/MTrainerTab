package com.sisindia.ai.mtrainer.android.features.trainingprogram

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.droidcommons.base.recycler.BaseRecyclerAdapter
import com.droidcommons.base.recycler.BaseViewHolder
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.databinding.RowTrainingProgramBinding
import com.sisindia.ai.mtrainer.android.models.online.TrainingProgramTypeDataResponseMO

class TrainingProgramAdapter : BaseRecyclerAdapter<TrainingProgramTypeDataResponseMO>() {

    private var listener: TrainingProgramListener? = null

    fun initListener(listener: TrainingProgramListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: RowTrainingProgramBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.row_training_program,
            parent,
            false
        )
        return TrainingProgramVH(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as TrainingProgramVH
        val item = getItem(position)
        viewHolder.onBind(item)
    }

    inner class TrainingProgramVH(
        itemBinding: ViewDataBinding
    ) : BaseViewHolder<TrainingProgramTypeDataResponseMO>(itemBinding) {

        private val binding = itemBinding as RowTrainingProgramBinding

        override fun onBind(item: TrainingProgramTypeDataResponseMO) {
            binding.adapterItem = item

            binding.root.setOnClickListener {
                listener?.onItemClick(item)
            }
        }
    }
}