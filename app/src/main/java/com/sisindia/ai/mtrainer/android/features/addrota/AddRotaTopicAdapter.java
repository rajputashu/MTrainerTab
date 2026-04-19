package com.sisindia.ai.mtrainer.android.features.addrota;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterChooseTopicsBinding;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;

public class AddRotaTopicAdapter extends BaseRecyclerAdapter<ChooseTopicsResponse.TopicsResponse> {

    private TopicSelected topicSelected;

    public AddRotaTopicAdapter(TopicSelected topicSelected) {
        this.topicSelected = topicSelected;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterChooseTopicsBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_choose_topics, parent, false);

        return new AddRotaTopicAdapter.ChooseTopicsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AddRotaTopicAdapter.ChooseTopicsViewHolder viewHolder= (AddRotaTopicAdapter.ChooseTopicsViewHolder) holder;
        final ChooseTopicsResponse.TopicsResponse item = getItem(position);
        viewHolder.onBind(item);
        if(AddRotaViewmodel.selectedTopic.contains(item.topicId))
            viewHolder.binding.topicRadioBtn.setChecked(true);
        else
            viewHolder.binding.topicRadioBtn.setChecked(false);
        viewHolder.binding.topicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewHolder.binding.topicRadioBtn.isChecked()){
                    AddRotaViewmodel.selectedTopic.remove(item.topicId);
                    notifyItemChanged(position);
                    topicSelected.topicSelected();
                } else {
                    AddRotaViewmodel.selectedTopic.add(item.topicId);
                    notifyItemChanged(position);
                    topicSelected.topicSelected();
                }
            }
        });
    }
    class ChooseTopicsViewHolder extends BaseViewHolder<ChooseTopicsResponse.TopicsResponse> {
        public AdapterChooseTopicsBinding binding;

        public ChooseTopicsViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding= (AdapterChooseTopicsBinding) itemBinding;
        }

        @Override
        public void onBind(ChooseTopicsResponse.TopicsResponse item) {
            binding.setTextItem(item);
        }
    }

    public interface TopicSelected {
        void topicSelected();
    }
}