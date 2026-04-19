package com.sisindia.ai.mtrainer.android.features.spi.mounted;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterMountedBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.spi.draftspi.DraftSpiViewlistener;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiImage;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;
import com.sisindia.ai.mtrainer.android.models.spi.MountedResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MountedRecyclerAdapter extends BaseRecyclerAdapter<SpiBasicInfoResponse.SpiBasicInfoDetailsData> {

    MtrainerDataBase dataBase;
    public MountedViewListener listeners;
    public MountedRecyclerAdapter mountedRecyclerAdapter;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 3000;
    private Map<String, List<SpiImage>> spiImageList;
    List<SpiImage> spiImages;
    private Picasso picasso;
    Context context;
    ZoomImageView imageView;
    AppCompatImageButton imageButton;

    SpiImage image;
    String uniqueId;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterMountedBinding binding= DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_mounted,parent,false);
        return new MountedRecyclerAdapter.MountedItemViewHolder(binding);
    }
    public MountedRecyclerAdapter(MtrainerDataBase dataBase, Picasso picasso, Context context){
        this.dataBase=dataBase;
        this.picasso=Picasso.get();
        this.context=context;
    }
    public void updateSpiImageList(Map<String, List<SpiImage>> imageList, int position) {
        this.spiImageList = imageList;
        /*  *//* if(position != -1)
            notifyItemChanged(position);*//*
      //  else*/
        //notifyItemChanged(position);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MountedItemViewHolder viewHolder= (MountedItemViewHolder) holder;
        final SpiBasicInfoResponse.SpiBasicInfoDetailsData item=getItem(position);
        int i=position+1;
        viewHolder.binding.slNo.setText(""+i);
        viewHolder.binding.postName.setText(item.postName);
        viewHolder.onBind(item);
        viewHolder.binding.spiImg1.setVisibility(View.INVISIBLE);
        viewHolder.binding.takeMountImage.setVisibility(View.VISIBLE);

        if(spiImageList==null || spiImageList.get(item.uniqueId) == null) {
            viewHolder.binding.takeMountImage.setVisibility(View.VISIBLE);

        } else {

            spiImages = spiImageList.get(item.uniqueId);
            for(SpiImage image : spiImages) {
                if(image.position == 1){
                    viewHolder.binding.spiImg1.setVisibility(View.VISIBLE);
                    File    image1 = new File(image.imageUrl);
                    if(image1.exists()) {

                    Bitmap myBitmap = BitmapFactory.decodeFile(image1.getAbsolutePath());
                    viewHolder.binding.spiImg1.setImageBitmap(myBitmap);
                    }
                    viewHolder.binding.takeMountImage.setVisibility(View.GONE);
                  /*  picasso.load(new File(image.imageUrl))
                            .into(viewHolder.binding.spiImg1);*/
                }

            }
        }


        viewHolder.binding.spiImg1.setOnClickListener(v->{

            AlertDialog.Builder alertDialog= new AlertDialog.Builder(v.getRootView().getContext());
            View dialogview=LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.spi_image_dailog,null);
            AlertDialog alert = alertDialog.create();
            imageView= dialogview.findViewById(R.id.spi_image);
            imageButton=dialogview.findViewById(R.id.spi_close_btn);
            // imageView.setImageResource(spiImages.get(position).imageUrl);
            spiImages = spiImageList.get(item.uniqueId);
            for(SpiImage image : spiImages) {
                if(image.position == 1) {
                    viewHolder.binding.spiImg1.setVisibility(View.VISIBLE);
                    picasso.load(new File(image.imageUrl))
                            .into(imageView);
                } }
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



        viewHolder.binding.takeMountImage.setOnClickListener(v -> {
            if (listeners != null) {

                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }

                if(((spiImageList == null || spiImageList.get(item.uniqueId) == null) ? 0 : spiImageList.get(item.uniqueId).size()) <=0)
                    listeners.onMountedItemClick(item, position);

            }
            else {
                Toast.makeText(context, "only one image u can take", Toast.LENGTH_SHORT).show();

            }


        });

    }


    public void setListener(MountedViewListener listener) {
        this.listeners = listener;
    }

    public class MountedItemViewHolder extends BaseViewHolder<SpiBasicInfoResponse.SpiBasicInfoDetailsData> {
        private  final  RecyclerAdapterMountedBinding binding;
        public MountedItemViewHolder(RecyclerAdapterMountedBinding itemBinding) {
            super(itemBinding);
            this.binding=itemBinding;
        }


        @Override
        public void onBind(SpiBasicInfoResponse.SpiBasicInfoDetailsData item) {
            binding.setAdapterItem(item);

        }
    }





}
