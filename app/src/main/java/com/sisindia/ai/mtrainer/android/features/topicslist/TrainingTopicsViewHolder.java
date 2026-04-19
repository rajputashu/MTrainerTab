package com.sisindia.ai.mtrainer.android.features.topicslist;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemTrainingTopicsBinding;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class TrainingTopicsViewHolder extends BaseViewHolder<TrainingTopicsDataModel> {
    RecyclerAdapterItemTrainingTopicsBinding binding;

    public TrainingTopicsViewHolder(@NonNull RecyclerAdapterItemTrainingTopicsBinding itemBinding) {
        super(itemBinding);
        binding = itemBinding;
    }
    @Override
    public void onBind(TrainingTopicsDataModel item) {
        binding.setAdapterItem(item);
        Glide.with(binding.tvDuration.getContext()).load(item.getCourseContentThumbnailURL()+"?"+ Prefs.getString(PrefsConstants.SAS_TOKEN)).into(binding.imvTopicimg);
        if (item.getLastseen() != null) {
            SimpleDateFormat inputFormatWithMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            SimpleDateFormat inputFormatWithoutMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String lastSeen = "";
            try {
                // First, try to parse with milliseconds
                Date date = inputFormatWithMillis.parse(item.getLastseen());
                lastSeen = outputFormat.format(date);
            } catch (ParseException e) {
                // If parsing with milliseconds fails, try without milliseconds
                try {
                    Date date = inputFormatWithoutMillis.parse(item.getLastseen());
                    lastSeen = outputFormat.format(date);
                } catch (ParseException ex) {
                    // Handle the case where parsing fails for both formats
                    ex.printStackTrace();
                }
            }
            binding.lastseentext.setText("Last Seen: " + lastSeen);
            binding.startedtext.setText("Started");
            binding.startedtext.setTextColor(Color.parseColor("#008000"));
        } else {
            binding.lastseentext.setText("");
            binding.startedtext.setText("Not Started yet");
            binding.startedtext.setTextColor(Color.parseColor("#FF4433"));
        }


    }
}

