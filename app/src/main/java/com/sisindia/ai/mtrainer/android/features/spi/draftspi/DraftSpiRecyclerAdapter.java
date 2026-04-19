package com.sisindia.ai.mtrainer.android.features.spi.draftspi;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.otaliastudios.zoom.ZoomImageView;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterDraftSpiBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.TempDraftSpiPhotoList;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiImage;
import com.sisindia.ai.mtrainer.android.models.DraftSpiResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DraftSpiRecyclerAdapter extends BaseRecyclerAdapter<SpiBasicInfoResponse.SpiBasicInfoDetailsData> {
    MtrainerDataBase dataBase;
    public RemoveDraftImage removeDraftImage;
    private Picasso picasso;
    private DraftSpiViewlistener listeners;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 3000;
    private ArrayList<DraftSpiResponse.DraftSpiDetailsData> draftSpiDetailsData;
    private List<TempDraftSpiPhotoList> imageDataList;
    Context context;
    // Map<UNIQUEID, List<SPIIMAGE>>
    // UNIQUEID -> Row Number
    // SPI IMAGE -> Images
    private Map<String, List<SpiImage>> spiImageList;
    // private  List<SpiImage> spiImages;
    // SpiImage image=new SpiImage();
    List<SpiImage> spiImages = new ArrayList<>();
    SpiImage image;
    int rowPosition;
    String uniqueId;
    File image1, image2, image3;
    private Dialog dialog;
    ZoomImageView imageView;
    AppCompatImageButton imageButton;
    private DraftSpiViewModel viewModel;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterDraftSpiBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_draft_spi, parent, false);
        return new DraftSpiItemViewHolder(binding);

    }

    public DraftSpiRecyclerAdapter(MtrainerDataBase dataBase, Picasso picasso, RemoveDraftImage removeDraftImage, Context context) {
        this.dataBase = dataBase;
        this.picasso = Picasso.get();
        this.removeDraftImage = removeDraftImage;
        this.context = context;
    }

    public void updateSpiImageList(Map<String, List<SpiImage>> imageList, int position) {
        this.spiImageList = imageList;
        /*  *//* if(position != -1)
            notifyItemChanged(position);*//*
      //  else*/
        //notifyItemChanged(position);
        // notifyItemInserted(position);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DraftSpiItemViewHolder viewHolder = (DraftSpiItemViewHolder) holder;
        final SpiBasicInfoResponse.SpiBasicInfoDetailsData item = getItem(position);
        int i = position + 1;
        viewHolder.binding.slNo.setText("" + i);
        viewHolder.binding.postName.setText(item.postName);
        viewHolder.binding.imageCount.setText((spiImageList == null || spiImageList.get(item.uniqueId) == null) ? "0" : String.valueOf(spiImageList.get(item.uniqueId).size()));
        viewHolder.onBind(item);
        rowPosition = 1;
        uniqueId = item.uniqueId;

        // viewHolder.binding.spiImg1.setVisibility(View.VISIBLE);
        viewHolder.binding.spiImg1.setVisibility(View.INVISIBLE);
        viewHolder.binding.spiImg2.setVisibility(View.INVISIBLE);
        viewHolder.binding.spiImg3.setVisibility(View.INVISIBLE);

        if (spiImageList == null || spiImageList.get(item.uniqueId) == null) {

        } else {

            spiImages = spiImageList.get(item.uniqueId);
            for (SpiImage image : spiImages) {
                if (image.position == 1) {

                    viewHolder.binding.spiImg1.setVisibility(View.VISIBLE);
                    picasso.load(new File(image.imageUrl))
                            .into(viewHolder.binding.spiImg1);
                    Log.v("picasso", "image1" + image.imageUrl);







                   /* image1 = new File(image.imageUrl);
                    if(image1.exists()) {

                    Bitmap myBitmap = BitmapFactory.decodeFile(image1.getAbsolutePath());
                    viewHolder.binding.spiImg1.setImageBitmap(myBitmap);
                    }*/
                }

                if (image.position == 2) {
                    viewHolder.binding.spiImg2.setVisibility(View.VISIBLE);
                    picasso.load(new File(image.imageUrl))
                            .into(viewHolder.binding.spiImg2);
                    Log.v("picasso", "image2" + image.imageUrl);

                   /* image2 = new File(image.imageUrl);
                    if(image2.exists()) {

                        Bitmap myBitmap = BitmapFactory.decodeFile(image2.getAbsolutePath());
                        viewHolder.binding.spiImg2.setImageBitmap(myBitmap);
                    }*/
                }
                if (image.position == 3) {
                    viewHolder.binding.spiImg3.setVisibility(View.VISIBLE);
                    picasso.load(new File(image.imageUrl))
                            .into(viewHolder.binding.spiImg3);
                    Log.v("picasso", "image3" + image.imageUrl);

                   /* image3 = new File(image.imageUrl);
                    if(image3.exists()) {

                        Bitmap myBitmap = BitmapFactory.decodeFile(image3.getAbsolutePath());
                        viewHolder.binding.spiImg3.setImageBitmap(myBitmap);
                    }*/
                }

            }
        }
        viewHolder.binding.spiImg1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //DELETE

                int position = 1;
                String unique = item.uniqueId;
                dataBase.getDraftSpiPhotoDao().deleteDraftImage(position, unique)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                notifyDataSetChanged();
                Toast.makeText(context, "image deleted successfully", Toast.LENGTH_SHORT).show();
                //viewHolder.binding.spiImg1.setVisibility(View.GONE);
                //viewHolder.binding.spiImg1.Cach
                return true;

            }
        });


        viewHolder.binding.spiImg2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //DELETE
                int position = 2;
                String unique = item.uniqueId;
                dataBase.getDraftSpiPhotoDao().deleteDraftImage(position, unique)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                notifyDataSetChanged();
                Toast.makeText(context, "image deleted successfully", Toast.LENGTH_SHORT).show();
                return true;

            }
        });


        viewHolder.binding.spiImg3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //DELETE
                int position = 3;
                String unique = item.uniqueId;
                dataBase.getDraftSpiPhotoDao().deleteDraftImage(position, unique)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                notifyDataSetChanged();
                Toast.makeText(context, "image deleted successfully", Toast.LENGTH_SHORT).show();
                return true;

            }
        });


        viewHolder.binding.spiImg1.setOnClickListener(v -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());
            View dialogview = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.spi_image_dailog, null);
            AlertDialog alert = alertDialog.create();
            imageView = dialogview.findViewById(R.id.spi_image);
            imageButton = dialogview.findViewById(R.id.spi_close_btn);

            spiImages = spiImageList.get(item.uniqueId);
            for (SpiImage image : spiImages) {
                if (image.position == 1) {
                    viewHolder.binding.spiImg1.setVisibility(View.VISIBLE);
                    picasso.load(new File(image.imageUrl))
                            .into(imageView);
                }
            }
            alertDialog.setView(dialogview);
            alertDialog.setCancelable(true);
            alertDialog.show();
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();

                }
            });

        });

        viewHolder.binding.spiImg2.setOnClickListener(v -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());
            View dialogview = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.spi_image_dailog, null);
            AlertDialog alert = alertDialog.create();
            imageView = dialogview.findViewById(R.id.spi_image);
            imageButton = dialogview.findViewById(R.id.spi_close_btn);
            // imageView.setImageResource(spiImages.get(position).imageUrl);
            spiImages = spiImageList.get(item.uniqueId);
            for (SpiImage image : spiImages) {
                if (image.position == 2) {
                    viewHolder.binding.spiImg2.setVisibility(View.VISIBLE);
                    picasso.load(new File(image.imageUrl))
                            .into(imageView);
                }
            }
            alertDialog.setView(dialogview);
            alertDialog.setCancelable(true);
            alertDialog.show();
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();

                }
            });

        });

        viewHolder.binding.spiImg3.setOnClickListener(v -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());
            View dialogview = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.spi_image_dailog, null);
            AlertDialog alert = alertDialog.create();
            imageView = dialogview.findViewById(R.id.spi_image);
            imageButton = dialogview.findViewById(R.id.spi_close_btn);
            // imageView.setImageResource(spiImages.get(position).imageUrl);
            spiImages = spiImageList.get(item.uniqueId);
            for (SpiImage image : spiImages) {
                if (image.position == 3) {
                    viewHolder.binding.spiImg3.setVisibility(View.VISIBLE);
                    picasso.load(new File(image.imageUrl))
                            .into(imageView);
                }
            }
            alertDialog.setView(dialogview);
            alertDialog.setCancelable(true);
            alertDialog.show();
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();

                }
            });

        });


        viewHolder.binding.takeDraftImage.setOnClickListener(v -> {
            if (listeners != null) {

                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }
                if (((spiImageList == null || spiImageList.get(item.uniqueId) == null) ? 0 : spiImageList.get(item.uniqueId).size()) <= 2)
                    listeners.onSpiDraftItemClick(item, position);

            } else {

            }

        });
    }

   /* @Override
    public int getItemCount() {
        return imageDataList==null ? 0 : imageDataList.size();
    }


    public void setImageDataList(List<TempDraftSpiPhotoList> imageDataList) {
        this.imageDataList = imageDataList;
        notifyDataSetChanged();
    }*/

    /*private   void open(){
        dialog = new Dialog(context.getApplicationContext());
        SpiImageDailogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.spi_image_dailog,null,false);
        dialogBinding.setVm(viewModel);



        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogBinding.closeBtn.setOnClickListener(view -> {
            if(dialog.isShowing())
                dialog.dismiss();
        });
    }*/
    public void setListener(DraftSpiViewlistener listener) {
        this.listeners = listener;
    }

    private class DraftSpiItemViewHolder extends BaseViewHolder<SpiBasicInfoResponse.SpiBasicInfoDetailsData> {
        private final RecyclerAdapterDraftSpiBinding binding;

        public DraftSpiItemViewHolder(RecyclerAdapterDraftSpiBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }


        @Override
        public void onBind(SpiBasicInfoResponse.SpiBasicInfoDetailsData item) {
            binding.setAdapterItem(item);
        }
    }

    void activateClick() {
        mLastClickTime = mLastClickTime - CLICK_TIME_INTERVAL;
    }

    interface RemoveDraftImage {
        void DarftimageRemoved(int position, String uniqueid, String imageurl);
    }

}
