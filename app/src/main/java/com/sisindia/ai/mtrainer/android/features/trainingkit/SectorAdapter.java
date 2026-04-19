package com.sisindia.ai.mtrainer.android.features.trainingkit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.R;

import java.util.List;


class SectorAdapter extends RecyclerView.Adapter<SectorAdapter.ViewHolder> {

    List<VideoDetailsModel.VideoDetailsModellist> models;
    private Context context;

    public SectorAdapter(Activity context, List<VideoDetailsModel.VideoDetailsModellist> models) {
        this.context = context;
        this.models = models;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.training_kit_single_row, parent, false);
        return new SectorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.textview.setText(models.get(position).getName());
        holder.imageicon.setImageResource(R.drawable.ic_slideshow_grey_700_36dp);
        holder.relative_trainingkit.setOnClickListener(view -> {
            Intent i = new Intent(context, SOPLandingPageActivity.class);
            i.putExtra("id", position + 1);
            i.putExtra("videoname", models.get(position).getName());
            Log.e("videoame", "its" + models.get(position).getName());
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textview;
        ImageView imageicon;
        RelativeLayout relative_trainingkit;

        public ViewHolder(View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.fireandsafety);
            imageicon = itemView.findViewById(R.id.imageicon);
            relative_trainingkit = itemView.findViewById(R.id.relative_trainingkit);
        }
    }
}

