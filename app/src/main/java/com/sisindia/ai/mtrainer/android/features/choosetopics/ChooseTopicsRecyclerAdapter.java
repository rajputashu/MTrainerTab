package com.sisindia.ai.mtrainer.android.features.choosetopics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MediatorLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.AdapterChooseTopicsBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.SavedTopic;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChooseTopicsRecyclerAdapter extends BaseRecyclerAdapter<ChooseTopicsResponse.TopicsResponse> {
    private MtrainerDataBase dataBase;
    private Set<Integer> selectedTopicSet = new HashSet<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        AdapterChooseTopicsBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_choose_topics, parent, false);

        return new ChooseTopicsViewHolder(binding);
    }

    public ChooseTopicsRecyclerAdapter(MtrainerDataBase dataBase){
        this.dataBase = dataBase;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ChooseTopicsViewHolder viewHolder= (ChooseTopicsViewHolder) holder;
        final ChooseTopicsResponse.TopicsResponse item = getItem(position);
        viewHolder.onBind(item);

        if(selectedTopicSet.contains(item.topicId))
            viewHolder.binding.topicRadioBtn.setChecked(true);
        else
            viewHolder.binding.topicRadioBtn.setChecked(false);

        viewHolder.binding.topicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.binding.topicRadioBtn.isChecked()){
                    selectedTopicSet.remove(item.topicId);
                    dataBase.getSavedTopicDao().deleteSavedTopic(item.topicId, Prefs.getInt(PrefsConstants.ROTA_ID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    notifyItemChanged(position);
                } else {
                    SavedTopic savedTopic = new SavedTopic();
                    savedTopic.topicId = item.topicId;
                    savedTopic.topicName = item.topicName;
                    savedTopic.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
                    selectedTopicSet.add(item.topicId);
                    dataBase.getSavedTopicDao().insertSavedTopic(savedTopic)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    notifyItemChanged(position);
                }
            }
        });

    }
    public void setSelectedTopicSet(Set<Integer> selectedTopicSet) {
        this.selectedTopicSet = selectedTopicSet;
    }
    class ChooseTopicsViewHolder extends BaseViewHolder<ChooseTopicsResponse.TopicsResponse>{
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
}
