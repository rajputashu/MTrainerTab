package com.sisindia.ai.mtrainer.android.features.spi.design;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.bumptech.glide.Glide;
import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.otaliastudios.zoom.ZoomImageView;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterDesignBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiImage;
import com.sisindia.ai.mtrainer.android.models.DesignSpiResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class DesignRecyclerAdapter extends BaseRecyclerAdapter<DesignSpiResponse.DesignSpiData> {
    private MtrainerDataBase dataBase;
    private Picasso picasso;
    private ArrayList<DesignSpiResponse.DesignSpiData> designSpiData;
    Context context;
    ZoomImageView imageView;
    AppCompatImageButton imageButton;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterDesignBinding binding= DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_design,parent,false);
        return new DesignRecyclerAdapter.DesignItemViewHolder(binding);
    }
    public DesignRecyclerAdapter(MtrainerDataBase dataBase,Picasso picasso,Context context){
        this.dataBase=dataBase;
        this.picasso=Picasso.get();
        this.context=context;
        Log.v("log","picosso message"+this.picasso);
        Log.e("log error","picosso message"+picasso);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DesignItemViewHolder viewHolder= (DesignItemViewHolder) holder;
        final DesignSpiResponse.DesignSpiData item=getItem(position);

     //   String[] parts = item.imagesList.split(",");

      //  picasso.load(parts[0]).into(viewHolder.binding.spiImg1);
     /*   picasso.load(R.drawable.sis_logo)
                .resize(40,30)
                .into(viewHolder.binding.spiImg1);
        picasso.load(R.drawable.sis_logo)
                .resize(40,30)
                .into(viewHolder.binding.spiImg2);
        picasso.load(R.drawable.sis_logo)
                .resize(40,30)
                .into(viewHolder.binding.spiImg3);*/
        int i=position+1;
        viewHolder.binding.slNo.setText(""+i);
        viewHolder.binding.mainGate.setText(item.postName);


       // picasso.load(parts[0]).into(viewHolder.binding.spiImg1);
        //picasso.load(parts[1]).into(viewHolder.binding.spiImg2);
       // picasso.load(parts[2]).into(viewHolder.binding.spiImg3);

        if(!item.imagesList.isEmpty()) {
            String[] parts = item.imagesList.split(",");
            for (int i1 = 0; i1 < parts.length; i1++) {
                if(i1==0){
                    Picasso.get().load(parts[0]).into(viewHolder.binding.spiImg1);
                }
                if(i1>0){
                    Picasso.get().load(parts[0]).into(viewHolder.binding.spiImg1);
                    Picasso.get().load(parts[1]).into(viewHolder.binding.spiImg2);
                }
                if(i1>1){
                    Picasso.get().load(parts[0]).into(viewHolder.binding.spiImg1);
                    Picasso.get().load(parts[1]).into(viewHolder.binding.spiImg2);
                    Picasso.get().load(parts[2]).into(viewHolder.binding.spiImg3);
                }


            }
        }

        viewHolder.binding.spiImg1.setOnClickListener(v->{

            AlertDialog.Builder alertDialog= new AlertDialog.Builder(v.getRootView().getContext());
            View dialogview=LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.spi_image_dailog,null);
            AlertDialog alert = alertDialog.create();
            imageView= dialogview.findViewById(R.id.spi_image);
            imageButton=dialogview.findViewById(R.id.spi_close_btn);
            if(!item.imagesList.isEmpty()) {
                String[] parts = item.imagesList.split(",");
                for (int i1 = 0; i1 < parts.length; i1++) {
                    if(i1==0){
                        Picasso.get().load(parts[0]).into(imageView);
                    }
                    if(i1>0){
                        Picasso.get().load(parts[0]).into(imageView);
                        Picasso.get().load(parts[1]).into(imageView);
                    }
                    if(i1>1){
                        Picasso.get().load(parts[0]).into(imageView);
                        Picasso.get().load(parts[1]).into(imageView);
                        Picasso.get().load(parts[2]).into(imageView);
                    }
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


        viewHolder.binding.spiImg2.setOnClickListener(v->{

            AlertDialog.Builder alertDialog= new AlertDialog.Builder(v.getRootView().getContext());
            View dialogview=LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.spi_image_dailog,null);
            AlertDialog alert = alertDialog.create();
            imageView= dialogview.findViewById(R.id.spi_image);
            imageButton=dialogview.findViewById(R.id.spi_close_btn);
            if(!item.imagesList.isEmpty()) {
                String[] parts = item.imagesList.split(",");
                for (int i1 = 0; i1 < parts.length; i1++) {
                    if(i1>0){
                        Picasso.get().load(parts[1]).into(imageView);
                    }
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



        viewHolder.binding.spiImg3.setOnClickListener(v->{

            AlertDialog.Builder alertDialog= new AlertDialog.Builder(v.getRootView().getContext());
            View dialogview=LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.spi_image_dailog,null);
            AlertDialog alert = alertDialog.create();
            imageView= dialogview.findViewById(R.id.spi_image);
            imageButton=dialogview.findViewById(R.id.spi_close_btn);
            if(!item.imagesList.isEmpty()) {
                String[] parts = item.imagesList.split(",");
                for (int i1 = 0; i1 < parts.length; i1++) {
                    if(i1>1){
                        Picasso.get().load(parts[2]).into(imageView);
                    }
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





    /*    if(!item.imagesList.isEmpty()){
            String[] parts = item.imagesList.split(",");
            switch (parts.length){
                case 1:
                    picasso.load(parts[0]).into(viewHolder.binding.spiImg1);

                    Log.v("log picasso1","image1"+parts);
                    break;
                case 2:
                    picasso.load(parts[0])
                            .into(viewHolder.binding.spiImg1);
                    picasso.load(parts[1])
                            .into(viewHolder.binding.spiImg2);
                    Log.v("log picasso2","image1"+parts[0]);
                    Log.v("log","image1"+parts[0]);
                    break;
                case 3:
                    picasso.load(parts[0])
                            .into(viewHolder.binding.spiImg1);
                    picasso.load(parts[1])
                            .into(viewHolder.binding.spiImg2);
                    picasso.load(parts[2])
                            .into(viewHolder.binding.spiImg3);
                    notifyDataSetChanged();
                    notifyItemChanged(position);
                    Log.v("log","image1");
                    break;



            }

        }*/


        viewHolder.onBind(item);

    }

    public class DesignItemViewHolder extends BaseViewHolder<DesignSpiResponse.DesignSpiData> {
        private  final RecyclerAdapterDesignBinding binding;
        public DesignItemViewHolder(RecyclerAdapterDesignBinding itemBinding) {
            super(itemBinding);
            this.binding=itemBinding;
        }


        @Override
        public void onBind(DesignSpiResponse.DesignSpiData item) {
            binding.setAdapter(item);

        }
    }
}
