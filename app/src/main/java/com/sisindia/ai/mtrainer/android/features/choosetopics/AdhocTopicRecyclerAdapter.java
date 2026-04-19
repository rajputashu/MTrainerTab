package com.sisindia.ai.mtrainer.android.features.choosetopics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.AdapterAdhocTopicBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.SavedTopic;
import com.sisindia.ai.mtrainer.android.db.dao.AdhocSavedTopicsDao;
import com.sisindia.ai.mtrainer.android.db.entities.AdhocSavedTopics;
import com.sisindia.ai.mtrainer.android.models.AdhocTopicsResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AdhocTopicRecyclerAdapter extends BaseRecyclerAdapter<AdhocTopicsResponse.AdhocTopics> {

    MtrainerDataBase dataBase;
    private Set<Integer> selectedTopicSet = new HashSet<>();
    ArrayList<String> ids;
    ArrayList<String> name;

    public AdhocTopicRecyclerAdapter(MtrainerDataBase dataBase) {
        this.dataBase=dataBase;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterAdhocTopicBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_adhoc_topic, parent, false);

        return new AdhocTopicRecyclerAdapter.AdhocTopicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AdhocTopicViewHolder viewHolder = (AdhocTopicViewHolder) holder;
        final AdhocTopicsResponse.AdhocTopics item = getItem(position);

        viewHolder.onBind(item);
        List<AdhocTopicsResponse.AdhocTopics> topicName = new ArrayList<>();
        ids=new ArrayList<>();
        name=new ArrayList<>();

      //// viewHolder.binding.topicRadioBtn.setOnCheckedChangeListener(null);
        AdhocSavedTopics savedTopic = new AdhocSavedTopics();
       // viewHolder.binding.topicRadioBtn.setSelected(item.isIsselected());

        if(selectedTopicSet.contains(item.Id))
            viewHolder.binding.topicRadioBtn.setChecked(true);
        else
            viewHolder.binding.topicRadioBtn.setChecked(false);


        viewHolder.binding.topicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  viewHolder.binding.topicRadioBtn.setChecked(true);

                if(viewHolder.binding.topicRadioBtn.isChecked()){
                  selectedTopicSet.remove(item.Id);
                    ids.remove("" +  savedTopic.id);
                    name.remove("" +  savedTopic.topicName);
                    dataBase.getAdhocSavedTopicsDao().deleteAdhocSavedTopic(item.Id, Prefs.getInt(PrefsConstants.ROTA_ID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    notifyItemChanged(position);


                }
                else {

                    savedTopic.id = item.Id;
                    savedTopic.topicName = item.topicName;
                    savedTopic.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
                    ids.add("" +  savedTopic.id);
                    name.add("" + savedTopic.topicName);
                    selectedTopicSet.add(item.Id);
                    dataBase.getAdhocSavedTopicsDao().insertAdhocSavedTopic(savedTopic)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    notifyItemChanged(position);

                }


            }
        });



    }
    public void setSelectedAdhocSet(Set<Integer> selectedTopicSet) {
        this.selectedTopicSet = selectedTopicSet;
    }

    public class AdhocTopicViewHolder extends BaseViewHolder<AdhocTopicsResponse.AdhocTopics> {

        private final AdapterAdhocTopicBinding binding;

        public AdhocTopicViewHolder(@NonNull AdapterAdhocTopicBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }


        @Override
        public void onBind(AdhocTopicsResponse.AdhocTopics item) {
            binding.setAdapterItem(item);

        }
    }
}
