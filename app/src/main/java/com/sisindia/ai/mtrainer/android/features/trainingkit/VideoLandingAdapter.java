package com.sisindia.ai.mtrainer.android.features.trainingkit;

import static com.sisindia.ai.mtrainer.android.utils.Utils.getExternalSdCardPath;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.WholeData;
import com.sisindia.ai.mtrainer.android.utils.Utils;

import java.io.File;
import java.util.List;

public class VideoLandingAdapter extends RecyclerView.Adapter<VideoLandingAdapter.TrainingKitViewHolder> {

    private final Context context;
    List<WholeData> wholeDataList;
    MtrainerDataBase mTrainerDB;
    String count;
    String path = "";
    private final Utils utils;

    public VideoLandingAdapter(Activity context, List<WholeData> wholeDataList, String count) {
        this.context = context;
        utils = new Utils(context);
        mTrainerDB = MtrainerDataBase.getDatabase(context);
        this.wholeDataList = wholeDataList;
        this.count = count;
    }

    @NonNull
    @Override
    public TrainingKitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.content_scrolling, parent, false);
        return new TrainingKitViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TrainingKitViewHolder holder, int position) {
        final WholeData wholeData = wholeDataList.get(position);
        if (wholeData.isIssubmodule()) {
            holder.submodule_no.setText((wholeData.getId()));
            holder.submodule_name.setText(wholeData.getName());
            holder.lecture1_heading.setVisibility(View.GONE);
            holder.section1_header.setVisibility(View.VISIBLE);
        } else {
            String seconds = ((wholeData.getDuration()));
            holder.lecture1_text.setText("Lecture " + wholeData.getId());
            holder.subheading_title.setText(wholeData.getName());
            holder.duration.setText("Video-" + Utils.timeConversion(Integer.parseInt(seconds)));
            holder.section1_header.setVisibility(View.GONE);
            holder.lecture1_heading.setVisibility(View.VISIBLE);
            if (wholeData.getPath() != null) {
                path = wholeData.getPath().replaceAll("\\\\", "/");
            }
        }
        final File file;

        if (utils.getDocRoot() == null) {
            file = new File(getExternalSdCardPath() + "/mtrain_enc/");
            Log.d("filepath00", file.getPath());
        } else {
            file = new File(utils.getDocRoot() + "/" + path);
            Log.d("filepath11", file.getPath());
        }

        holder.lecture1_heading.setOnClickListener(v -> {
            Log.e("filepath22", file.getPath());
            if (file.exists()) {
                //  if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                Intent i = new Intent(context, VideoPlayerActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("name", wholeData.getName());
                i.putExtra("videoname", wholeData.getPath());
                i.putExtra("duration", wholeData.getDuration());
                i.putExtra("videoId", wholeData.getVideoId());
                i.putExtra("ismlcvideoslist", "No");
                context.startActivity(i);
            } else {
                new Utils(context).showAlert("File not found");
            }
        });
    }

    /*private String getDocRoot() {
        StoragePath storagePath;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            storagePath = new StoragePath(context.getExternalFilesDirs(null));
        } else {
            storagePath = new StoragePath();
        }

        String[] storages;
        try {
            storages = storagePath.getDeviceStorages();
            Log.e("VidLandingAdapterStorage Size", String.valueOf(storages.length));
            if (storages.length > 1) {
                return storages[1] + "/mtrain_enc/";
            } else {
                return storages[0] + "/Download/mtrain_enc/";
            }
        } catch (Exception e) {
            return null;
        }
    }*/

    @Override
    public int getItemCount() {
        return wholeDataList.size();
    }

    public static class TrainingKitViewHolder extends RecyclerView.ViewHolder {
        TextView submodule_no, submodule_name, lecture1_text, subheading_title, duration;
        RelativeLayout lecture1_heading, section1_header;

        public TrainingKitViewHolder(final View itemView) {
            super(itemView);
            subheading_title = itemView.findViewById(R.id.subheading_title);
            lecture1_text = itemView.findViewById(R.id.lecture1_text);
            submodule_name = itemView.findViewById(R.id.submodule_name);
            section1_header = itemView.findViewById(R.id.submodule_layout);
            lecture1_heading = itemView.findViewById(R.id.video_layout);
            submodule_no = itemView.findViewById(R.id.submodule_no);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}
