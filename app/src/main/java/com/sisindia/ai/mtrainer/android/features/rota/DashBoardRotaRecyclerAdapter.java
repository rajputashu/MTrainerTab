package com.sisindia.ai.mtrainer.android.features.rota;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemDashBoardRotaBinding;
import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashBoardRotaRecyclerAdapter extends BaseRecyclerAdapter<TrainingCalendarResponse.TrainingCalendar> {

    private DashBoardRotaViewListeners listener;
    private Context context;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 5000;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterItemDashBoardRotaBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_item_dash_board_rota, parent, false);
        return new DashBoardRotaViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DashBoardRotaViewHolder viewHolder = (DashBoardRotaViewHolder) holder;
        final TrainingCalendarResponse.TrainingCalendar item = getItem(position);
        // Prefs.putString(PrefsConstants.STATUS, String.valueOf(item.getTaskStatusId()));
        // Prefs.putDouble(PrefsConstants.LAT, item.getLattitude());
        // Prefs.putDouble(PrefsConstants.LONGI,item.getLongitude());
        if (item.trainingTypeId == 2) {
            viewHolder.binding.trainingTypeImage.setImageResource(R.drawable.ic_van);
        } else {
            viewHolder.binding.trainingTypeImage.setImageResource(R.drawable.ic_tab);
        }
/*

        if(item.traningStatusId == 11) {
            if(Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8")) {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                viewHolder.binding.trainingCompletedParticipantLl.setLayoutParams(param);
                viewHolder.binding.trainingCompletedTimeLl.setLayoutParams(param);
                viewHolder.binding.llCompleted.setWeightSum(2f);
                viewHolder.binding.trainingCompletedRatingLl.setVisibility(View.GONE);
            }

              *//*if(item.isInProgress == 1) {
                viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_pending));
                viewHolder.binding.pending.setText("Syncing");
                //viewHolder.binding.llCompleted.setBackgroundColor(context.getResources().getColor(R.color.lightYellow));
            } else {
                viewHolder.binding.pending.setText("Completed");
                viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_completed));
                //viewHolder.binding.llCompleted.setBackgroundColor(context.getResources().getColor(R.color.green));
            }*//*
            viewHolder.binding.llCancelled.setVisibility(View.GONE);
            viewHolder.binding.llCompleted.setVisibility(View.VISIBLE);
            viewHolder.binding.pending.setText("Completed");
            viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_completed));
            viewHolder.binding.rotaChipHeader.setText("Completed training");
            viewHolder.binding.rotaChipHeader.setChipBackgroundColorResource(R.color.greenBackgroundLight);
            viewHolder.binding.trainingCompletedParticipant.setText(item.totalTrained);
            String s = item.givenRating + "/5";
            viewHolder.binding.trainingCompletedRating.setText(s);

            // Setting Start and End Time
            String s1 = item.savedStartTime + "-" + item.savedEndTime;
            viewHolder.binding.trainingCompletedTime.setText(s1);
            //viewHolder.binding.getRoot().setClickable(false);
            //viewHolder.binding.trainingCompletedParticipant.setText();
        }*/


        if (item.traningStatusId == 11) {
            if (Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8")) {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                viewHolder.binding.trainingCompletedParticipantLl.setLayoutParams(param);
                viewHolder.binding.trainingCompletedTimeLl.setLayoutParams(param);
                viewHolder.binding.llCompleted.setWeightSum(2f);
                viewHolder.binding.trainingCompletedRatingLl.setVisibility(View.GONE);
            }
            viewHolder.binding.llCancelled.setVisibility(View.GONE);
            viewHolder.binding.llCompleted.setVisibility(View.VISIBLE);
            if (item.isInProgress == 1) {
                viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_syncing));
                viewHolder.binding.pending.setText("Syncing");
                viewHolder.binding.llCompleted.setBackgroundColor(viewHolder.binding.getRoot().getContext().getResources().getColor(R.color.lightYellow));
                viewHolder.binding.llCompleted.setBackgroundTintList(viewHolder.binding.getRoot().getContext().getResources().getColorStateList(R.color.lightYellow));
            } else {
                viewHolder.binding.pending.setText("Completed");
                viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_completed));
                viewHolder.binding.llCompleted.setBackgroundColor(viewHolder.binding.getRoot().getContext().getResources().getColor(R.color.green));
                viewHolder.binding.llCompleted.setBackgroundTintList(viewHolder.binding.getRoot().getContext().getResources().getColorStateList(R.color.green));
            }
            //viewHolder.binding.pending.setText("Completed");
            //viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_completed));
            String chipText = "Completed On " + changeDateFormat(item.actualStartDatetime == null ? item.estimatedEndDatetime : item.actualStartDatetime);
            viewHolder.binding.rotaChipHeader.setText(chipText);
            viewHolder.binding.rotaChipHeader.setChipBackgroundColorResource(R.color.greenBackgroundLight);
            viewHolder.binding.trainingCompletedParticipant.setText(item.totalTrained);
            String s = item.givenRating + "/5";
            viewHolder.binding.trainingCompletedRating.setText(s);

            // Setting Start and End Time
            String s1 = item.savedStartTime + "-" + item.savedEndTime;
            viewHolder.binding.trainingCompletedTime.setText(s1);
            //viewHolder.binding.getRoot().setClickable(false);
            //viewHolder.binding.trainingCompletedParticipant.setText();
        }


        //this below code  writing for cancel training
        else if (item.traningStatusId == 12) {
            viewHolder.binding.llCompleted.setVisibility(View.GONE);
            viewHolder.binding.llCancelled.setVisibility(View.VISIBLE);
            viewHolder.binding.pending.setText("Cancelled");
            viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_cancelled));
            viewHolder.binding.rotaChipHeader.setText("Cancelled training");

            viewHolder.binding.rotaChipHeader.setChipBackgroundColorResource(R.color.colorLightPink);
            viewHolder.binding.tvCancelledReason.setText(item.reason);
        } else {

            if (item.isInProgress == 1) {
                viewHolder.binding.llCompleted.setVisibility(View.GONE);
                viewHolder.binding.llCancelled.setVisibility(View.GONE);
                viewHolder.binding.pending.setText("INPROGRESS");
                viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_pending));
                viewHolder.binding.rotaChipHeader.setText("Planned Time " + item.estimatedStartTime + " - " + item.estimatedEndTime);
                viewHolder.binding.rotaChipHeader.setChipBackgroundColorResource(R.color.lightYellow);
                /*
                viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_syncing));
                viewHolder.binding.pending.setText("In Progress");
                viewHolder.binding.llCompleted.setBackgroundColor(viewHolder.binding.getRoot().getContext().getResources().getColor(R.color.lightYellow));
                viewHolder.binding.llCompleted.setBackgroundTintList(viewHolder.binding.getRoot().getContext().getResources().getColorStateList(R.color.lightYellow));*/
            } else {
                viewHolder.binding.llCompleted.setVisibility(View.GONE);
                viewHolder.binding.llCancelled.setVisibility(View.GONE);
                viewHolder.binding.pending.setText("PENDING");
                viewHolder.binding.pendingImage.setImageResource((R.drawable.ic_pending));
                viewHolder.binding.rotaChipHeader.setText("Planned Time " + item.estimatedStartTime + " - " + item.estimatedEndTime);
                viewHolder.binding.rotaChipHeader.setChipBackgroundColorResource(R.color.lightYellow);
            }
        }

        viewHolder.onBind(item);
    }

    public void setListener(DashBoardRotaViewListeners listener) {
        this.listener = listener;
    }

    class DashBoardRotaViewHolder extends BaseViewHolder<TrainingCalendarResponse.TrainingCalendar> {

        private final RecyclerAdapterItemDashBoardRotaBinding binding;

        public DashBoardRotaViewHolder(@NonNull RecyclerAdapterItemDashBoardRotaBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }

        @Override
        public void onBind(TrainingCalendarResponse.TrainingCalendar item) {
            binding.setAdapterItem(item);
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTime = now;
                    listener.onRotaItemClick(item);

                    /*switch (item.rotaId) {
                        case 4: {
                            SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
                            String in = logDateFormat.format(new Date());

                            Date logOut = null;
                            try {
                                logOut = dateFormat4.parse(item.estimatedStartDatetime);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String out = dateFormat4.format(logOut);

                            Date lg = null;
                            try {
                                lg = dateFormat4.parse(in);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date lgo = null;
                            // Date lgo1 = null;
                            try {
                                lgo = dateFormat4.parse(out);
                                //  lgo1 = dateFormat41.parse(out1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (lgo.equals(lg)) {
                                //here onclick rota need to implement
                                listener.onRotaItemClick(item);
                                break;
                            }else if (lgo.after(lg)){
                                Toast.makeText(context, "Your Training Start time is not started", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            else if (lgo.before(lg)) {
                                Toast.makeText(context, "Your Training Start time is elapsed", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                        case 1:
                            try {
                                Toast.makeText(context,"Task is already completed",Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                            }
                            break;
                        case 10:
                            try {
                                Toast.makeText(context,"Task is already completed",Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                            }
                            break;
                    }*/
                } else {

                }
            });
        }
    }

    void activateClick() {
        mLastClickTime = mLastClickTime - CLICK_TIME_INTERVAL;
    }


    private String changeDateFormat(String date) {
        Date inputDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat result = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            inputDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return inputDate == null ? "" : result.format(inputDate);
    }
}

