package com.sisindia.ai.mtrainer.android.commons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemTopNavigationBinding;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.uimodels.NavigationUIModel;


public class NavigationUiRecyclerAdapter extends BaseRecyclerAdapter<NavigationUIModel> {

    private NavigationViewListeners viewListeners;
    private Context context;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterItemTopNavigationBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_top_navigation, parent, false);
        return new NavigationUiViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NavigationUIModel model = getItem(position);
        final NavigationUiViewHolder viewHolder = (NavigationUiViewHolder) holder;
        viewHolder.onBind(model);
        viewHolder.binding.startTrainingContainer.setAlpha(1);
       // if(PreTrainingReviewActivity.selectedEmpID.isEmpty() && !model.getBottomTxt().equals(context.getResources().getString(R.string.navigation_text_take_attendance)))
            if(Prefs.getInt(PrefsConstants.SELECTED_EMP_COUNT, 0) == 0 && !model.getBottomTxt().equals(context.getResources().getString(R.string.navigation_text_take_attendance)))

            viewHolder.binding.startTrainingContainer.setAlpha(0.3f);
    }

    public void setViewListeners(NavigationViewListeners viewListeners) {
        this.viewListeners = viewListeners;
    }

    class NavigationUiViewHolder extends BaseViewHolder<NavigationUIModel> {
        public AdapterItemTopNavigationBinding binding;

        public NavigationUiViewHolder(@NonNull AdapterItemTopNavigationBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }

        @Override
        public void onBind(NavigationUIModel item) {
            binding.setAdapterItem(item);

            binding.getRoot().setOnClickListener(v -> {
                if (viewListeners != null) {
                    viewListeners.onNavigationItemClick(item);
                }
            });
        }
    }
}
