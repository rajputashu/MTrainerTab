package com.sisindia.ai.mtrainer.android.features.spi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterSitePostBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;

public class SitePostRecyclerAdapter extends BaseRecyclerAdapter<SpiTableDetailsResponse.SpiTableDetailsData> {
    private SpiViewListeners listeners;
    MtrainerDataBase dataBase;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 5000;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterSitePostBinding binding= DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_site_post,parent,false);
        return new SitePostItemViewHolder(binding);

    }
    public SitePostRecyclerAdapter(MtrainerDataBase dataBase){
        this.dataBase=dataBase;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SitePostItemViewHolder viewHolder= (SitePostItemViewHolder) holder;
        final SpiTableDetailsResponse.SpiTableDetailsData item= getItem(position);
        int i=position+1;
        viewHolder.binding.slNo.setText(""+i);
        viewHolder.onBind(item);



        viewHolder.binding.tvView.setOnClickListener(v -> {
            if (listeners != null) {

                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }

                listeners.onSpiItemClick(item);




            }
            else {

            }

        });

        /*public void chooseAssessment(View view) {
            if(status.eauls("pending")) {
                message.what = NavigationConstants.ON_OPEN_BASIC_INFO_SCREEN;
                liveData.postValue(message);
            } else {
                showToast("Please Select Employee");
            }
        }*/

    }
    public void setListener(SpiViewListeners listener) {
        this.listeners = listener;
    }

    private class SitePostItemViewHolder extends BaseViewHolder<SpiTableDetailsResponse.SpiTableDetailsData> {
        private  final RecyclerAdapterSitePostBinding binding;
        public SitePostItemViewHolder(RecyclerAdapterSitePostBinding itemBinding) {
            super(itemBinding);
            this.binding=itemBinding;
        }


        @Override
        public void onBind(SpiTableDetailsResponse.SpiTableDetailsData  item) {
            binding.setAdapterItem(item);
        }
    }
    void activateClick() {
        mLastClickTime = mLastClickTime - CLICK_TIME_INTERVAL;
    }
}
