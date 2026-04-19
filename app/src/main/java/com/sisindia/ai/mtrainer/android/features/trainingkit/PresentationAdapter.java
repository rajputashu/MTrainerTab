package com.sisindia.ai.mtrainer.android.features.trainingkit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.SubModules;

import java.util.ArrayList;
import java.util.List;

class PresentationAdapter extends RecyclerView.Adapter<PresentationAdapter.ViewHolder> implements Filterable {

    //List<com.sisindia.ai.mtrainer.android.db.entities.SubModules.MySubModuleslist> models;
    private List<SubModules.MySubModuleslist> mFilteredList;
    List<com.sisindia.ai.mtrainer.android.db.entities.SubModules.MySubModuleslist> models;
    private Context context;
    private MtrainerDataBase dataBase;

    public PresentationAdapter(Activity context, List<com.sisindia.ai.mtrainer.android.db.entities.SubModules.MySubModuleslist> models) {
        this.context = context;
        this.models = models;
        this.mFilteredList = models;
        dataBase = MtrainerDataBase.getDatabase(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.training_kit_single_row, parent, false);
        return new PresentationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.textview.setText(models.get(position).getEnglishName());
        holder.imageicon.setImageResource(R.drawable.ic_stat_name);
        holder.count.setVisibility(View.VISIBLE);
        int count = dataBase.getSlideModuleDao().getSlideModuleCount(models.get(position).getModuleNo(), models.get(position).getSubModuleNo());
        if (count == 1 || count == 0) {
            holder.count.setText("0 SLIDE");
        } else {
            holder.count.setText(count + " SLIDES");
        }

        holder.relative_trainingkit.setOnClickListener(view -> {
            Intent i = new Intent(context, TrainingSlidesActivity.class);
            i.putExtra("id", models.get(position).getModuleNo());
            i.putExtra("submodule", models.get(position).getSubModuleNo());
            i.putExtra("videoname", models.get(position).getEnglishName());
            // Log.e("videoame", "its" + models.get(position).getEnglishName());
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
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    mFilteredList = models;
                } else {
                    ArrayList<SubModules.MySubModuleslist> filteredList = new ArrayList<>();
                    for (com.sisindia.ai.mtrainer.android.db.entities.SubModules.MySubModuleslist androidVersion : models) {
                        if (androidVersion.getEnglishName().toLowerCase().contains(charString) || androidVersion.getModuleNo().toLowerCase().contains(charString) || androidVersion.getHindiName().toLowerCase().contains(charString)) {
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
                mFilteredList = (List<com.sisindia.ai.mtrainer.android.db.entities.SubModules.MySubModuleslist>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textview, count;
        ImageView imageicon;
        RelativeLayout relative_trainingkit;

        public ViewHolder(View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.fireandsafety);
            imageicon = itemView.findViewById(R.id.imageicon);
            count = itemView.findViewById(R.id.count);
            relative_trainingkit = itemView.findViewById(R.id.relative_trainingkit);
        }
    }
}