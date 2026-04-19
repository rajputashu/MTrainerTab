package com.sisindia.ai.mtrainer.android.features.trainingkit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.models.MyModule;

import java.util.ArrayList;
import java.util.List;


class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> implements Filterable {
    private Context context;
    List<MyModule.MyModuleslist> models;
    private List<MyModule.MyModuleslist> mFilteredList;

    public VideoAdapter(Activity context, List<MyModule.MyModuleslist> models) {
        this.context = context;
        this.models = models;
        this.mFilteredList = models;
    }


    @Override
    public ViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.training_kit_single_row, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.textview.setText(models.get(position).getEnglishName());
        holder.imageicon.setImageResource(R.drawable.ic_slideshow_grey_700_36dp);
        holder.relative_trainingkit.setOnClickListener(view -> {
            Intent i = new Intent(context, VideoLandingActivity.class);
            i.putExtra("id", models.get(position).getModuleNo());
            //    i.putExtra("path",models.get(position).ge)
            i.putExtra("videoname", models.get(position).getEnglishName());
            Log.e("videoame", "its" + models.get(position).getEnglishName());
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = models;
                } else {

                    ArrayList<MyModule.MyModuleslist> filteredList = new ArrayList<>();

                    for (MyModule.MyModuleslist androidVersion : models) {

                        if (androidVersion.getEnglishName().toLowerCase().contains(charString.toLowerCase()) || androidVersion.getModuleNo().toLowerCase().contains(charString.toLowerCase()) || androidVersion.getHindiName().toLowerCase().contains(charString.toLowerCase())) {

                            filteredList.add(androidVersion);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList.clear();
                mFilteredList = (List<MyModule.MyModuleslist>) filterResults.values;

                notifyDataSetChanged();
            }
        };
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
