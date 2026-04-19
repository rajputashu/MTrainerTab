package com.sisindia.ai.mtrainer.android.features.trainingkit;

import static com.sisindia.ai.mtrainer.android.utils.Utils.getExternalSdCardPath;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.sisindia.ai.mtrainer.android.models.MLCVideoDetailsModel;
import com.sisindia.ai.mtrainer.android.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


class MlcVideoAdapter extends RecyclerView.Adapter<MlcVideoAdapter.ViewHolder> implements Filterable {
    //    private Context context;
    List<MLCVideoDetailsModel.MlcVideoDetailsModellist> models;
    private List<MLCVideoDetailsModel.MlcVideoDetailsModellist> mFilteredList;
    String path = "";
    private final Utils utils;

    public MlcVideoAdapter(Activity context, List<MLCVideoDetailsModel.MlcVideoDetailsModellist> models) {
//        this.context = context;
        utils = new Utils(context);
        this.models = models;
        this.mFilteredList = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.training_kit_single_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.textview.setText(models.get(position).getEnglishName());
        holder.imageicon.setImageResource(R.drawable.ic_slideshow_grey_700_36dp);
        if (models.get(position).getPath() != null) {
            path = models.get(position).getPath().replaceAll("\\\\", "/");
        }

        final File file;
        if (utils.getDocRoot() == null) {
            file = new File(getExternalSdCardPath() + "/mtrain_enc/");
        } else {
            file = new File(utils.getDocRoot() + "/" + path);
            Log.d("MLCVidAdapter", file.getPath());
        }

        holder.relative_trainingkit.setOnClickListener(view -> {
            if (file.exists()) {
                Intent i = new Intent(view.getContext(), VideoPlayerActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("name", models.get(position).getEnglishName());
                i.putExtra("videoname", models.get(position).getPath());
                i.putExtra("duration", models.get(position).getDuration());
                i.putExtra("videoId", models.get(position).getVideoId());
                i.putExtra("ismlcvideoslist", "Yes");
                view.getContext().startActivity(i);
            } else {
                new Utils(view.getContext()).showAlert("File not found");
            }
        });
    }

    /*private String getDocRoot() {
        final String state = Environment.getExternalStorageState();
        Log.e("state", state);
//        if(state.equals("mounted"))
//        {
        StoragePath storagePath;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            storagePath = new StoragePath(context.getExternalFilesDirs(null));
        } else {
            storagePath = new StoragePath();
        }

        String[] storages;
        if (storagePath != null) {
            try {
                storages = storagePath.getDeviceStorages();
                for (int i = 0; i < storages.length; i++) {
                    Log.e("paths" + i, storages[i]);
                }
                if (storages.length > 1) {
                    return storages[1] + "/mtrain_enc/";
                } else {
                    return storages[0] + "/Download/mtrain_enc/";
                }
            } catch (Exception e) {
                return null;
            }

        } else {
            return getExternalSdCardPath() + "/mtrain_enc/";
        }
    }*/

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

                    ArrayList<MLCVideoDetailsModel.MlcVideoDetailsModellist> filteredList = new ArrayList<>();

                    for (MLCVideoDetailsModel.MlcVideoDetailsModellist androidVersion : models) {

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
                mFilteredList = (List<MLCVideoDetailsModel.MlcVideoDetailsModellist>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
