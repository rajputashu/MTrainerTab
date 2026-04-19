package com.sisindia.ai.mtrainer.android.features.topicslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.droidcommons.base.recycler.BaseRecyclerAdapter
import com.droidcommons.base.recycler.BaseViewHolder
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.databinding.RowTrainingTopicsBinding
import com.sisindia.ai.mtrainer.android.models.online.TrainingTopicDataResponseMO

class TrainingTopicsAdapterV2 : BaseRecyclerAdapter<TrainingTopicDataResponseMO>() {

    private var listener: TrainingTopicsViewListeners? = null

    fun initListener(listener: TrainingTopicsViewListeners?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: RowTrainingTopicsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.row_training_topics,
            parent,
            false
        )
        return TrainingTopicsVH(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as TrainingTopicsVH
        val item = getItem(position)
        viewHolder.onBind(item)
    }

    inner class TrainingTopicsVH(
        itemBinding: ViewDataBinding
    ) : BaseViewHolder<TrainingTopicDataResponseMO>(itemBinding) {

        private val binding = itemBinding as RowTrainingTopicsBinding

        override fun onBind(item: TrainingTopicDataResponseMO) {
            binding.adapterItem = item

            binding.root.setOnClickListener {
                listener?.onTopicClick(item)
            }
        }
    }
}