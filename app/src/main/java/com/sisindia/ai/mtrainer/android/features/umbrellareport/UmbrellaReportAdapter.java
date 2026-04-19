package com.sisindia.ai.mtrainer.android.features.umbrellareport;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.AdapterItemUmbrellaImageBinding;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.UmbrellaImageRvItem;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UmbrellaReportAdapter extends BaseRecyclerAdapter<UmbrellaImageRvItem> {
    private Picasso picasso;
    private ImageCaptureListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterItemUmbrellaImageBinding binding = DataBindingUtil.inflate(inflater, R.layout.adapter_item_umbrella_image, parent, false);
        return new UmbrellaReportViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final UmbrellaReportViewHolder viewHolder= (UmbrellaReportViewHolder) holder;
        viewHolder.onBind(getItem(position));
    }

    public void setListener(ImageCaptureListener listener) {
        this.listener = listener;
    }

    class UmbrellaReportViewHolder extends BaseViewHolder<UmbrellaImageRvItem> {
        public AdapterItemUmbrellaImageBinding binding;

        public UmbrellaReportViewHolder(@NonNull AdapterItemUmbrellaImageBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }

        @Override
        public void onBind(UmbrellaImageRvItem item) {
            binding.setItem(item);
            Log.d("TAG", "onBind: Picasso -> " + picasso);
            if(item.imagePath != null && !item.imagePath.isEmpty()) {
                picasso.load(new File(item.imagePath))
                        .fit().into(binding.umbrellaImage);
                binding.getRoot().setOnClickListener(null);
            } else {
                binding.umbrellaImage.setImageResource(android.R.drawable.ic_menu_camera);
                binding.getRoot().setOnClickListener((view) -> {
                    Log.d("TAG", "onBind: Clicked");
                    listener.onTakeImage(item.postId, item.postName,getLayoutPosition());
                });
            }
        }
    }

    public void setPicasso(Picasso picasso) {
        this.picasso = picasso;
    }
}
