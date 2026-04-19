package com.sisindia.ai.mtrainer.android.features.trainingimages;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.StartActivityItemConstants;
import com.sisindia.ai.mtrainer.android.databinding.TrainingPhotograpsBinding;
import com.sisindia.ai.mtrainer.android.db.entities.TempImageData;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class TrainingImageAdapter extends RecyclerView.Adapter<TrainingImageAdapter.TrainingImageViewHolder> {

    private ShowImage showImage;
    private RemoveImage removeImage;
    private List<TempImageData> imageDataList;
    private Picasso picasso;

    public TrainingImageAdapter(ShowImage showImage, RemoveImage removeImage, Picasso picasso) {
        this.showImage = showImage;
        this.removeImage = removeImage;
        this.picasso=picasso;
    }

    @NonNull
    @Override
    public TrainingImageAdapter.TrainingImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TrainingPhotograpsBinding binding = DataBindingUtil.inflate(inflater, R.layout.training_photograps, parent, false);
        return new TrainingImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingImageAdapter.TrainingImageViewHolder holder, int position) {
        final TrainingImageViewHolder viewHolder= (TrainingImageViewHolder) holder;
        //viewHolder.binding.setImagePath(imageDataList.get(position).trainingPhotoURI);
        picasso.load(new File(imageDataList.get(position).trainingPhotoURI))
                .fit()
                .into(viewHolder.binding.imagePhoto);

        if(TextUtils.isDigitsOnly(imageDataList.get(position).pictureTypeId.trim()) && Integer.parseInt(imageDataList.get(position).pictureTypeId.trim()) == StartActivityItemConstants.POSTER_PICTURE)
            viewHolder.binding.imageType.setText("SPI");
        else
            viewHolder.binding.imageType.setText("Training");

        viewHolder.binding.imagePhoto.setOnClickListener(v -> {
            showImage.onImageSelected(imageDataList.get(position).trainingPhotoURI);
        });

        viewHolder.binding.removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new MaterialAlertDialogBuilder(view.getContext(), R.style.AlertDialogTheme)
                        .setTitle("Alert")
                        .setMessage("Do you want to delete image")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              /*  removeImage.imageRemoved(PreTrainingReviewActivity.imagePathList.get(position));
                                PreTrainingReviewActivity.imagePathList.remove(position);
                                PreTrainingReviewActivity.selectedPostList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,PreTrainingReviewActivity.imagePathList.size());*/
                                removeImage.imageRemoved(imageDataList.get(position).trainingPhotoURI);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false)
                        .create();
                dialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return imageDataList==null ? 0 : imageDataList.size();
    }

    public void setImageDataList(List<TempImageData> imageDataList) {
        this.imageDataList = imageDataList;
        notifyDataSetChanged();
    }

    class TrainingImageViewHolder extends RecyclerView.ViewHolder {
        public TrainingPhotograpsBinding binding;

        public TrainingImageViewHolder(@NonNull ViewDataBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding= (TrainingPhotograpsBinding) itemBinding;
        }
    }

    public void dataChanged() {
        notifyDataSetChanged();
    }

    interface RemoveImage {
        void imageRemoved(String imageUrl);
    }
    interface ShowImage {
        void onImageSelected(String imageUrl);
    }
}