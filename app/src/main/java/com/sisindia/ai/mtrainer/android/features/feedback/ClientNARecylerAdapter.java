package com.sisindia.ai.mtrainer.android.features.feedback;

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
import com.sisindia.ai.mtrainer.android.databinding.ClientNaItemBinding;

import java.util.HashSet;

public class ClientNARecylerAdapter extends BaseRecyclerAdapter<String> {
    private HashSet<String> selectedOption = new HashSet<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ClientNaItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.client_na_item, parent, false);

        return new ClientNARecylerAdapter.ClientCAViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ClientNARecylerAdapter.ClientCAViewHolder viewHolder= (ClientNARecylerAdapter.ClientCAViewHolder) holder;
        final String item = getItem(position);
        viewHolder.onBind(item);
        if(selectedOption.contains(item))
            viewHolder.binding.feedbackRadioBtn.setChecked(true);
        else
            viewHolder.binding.feedbackRadioBtn.setChecked(false);
        viewHolder.binding.feedbackItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewHolder.binding.feedbackRadioBtn.isChecked()){
                    selectedOption.clear();
                    notifyDataSetChanged();
                } else {
                    selectedOption.clear();
                    selectedOption.add(item);
                    notifyDataSetChanged();
                }
            }
        });
    }
    class ClientCAViewHolder extends BaseViewHolder<String> {
        public ClientNaItemBinding binding;

        public ClientCAViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding);
            this.binding= (ClientNaItemBinding) itemBinding;
        }

        @Override
        public void onBind(String item) {
            binding.setTextItem(item);
        }
    }

    String selectedNAOption() {
        if(selectedOption.isEmpty())
            return null;
        else
            return selectedOption.iterator().next();
    }

}