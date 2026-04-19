package com.sisindia.ai.mtrainer.android.features.trainingimages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.views.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TrainingImageViewBinding {
    @BindingAdapter("setTrainingImageAdapter")
    public static void setTrainingImageRecyclerAdapter(RecyclerView recyclerView, TrainingImageAdapter adapter) {

    }

    @BindingAdapter("setImageFromPath")
    public static void setImageFromLocalPath(RoundedImageView imageView, String imagePath) {

        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither = false;                     //Disable Dithering mode
        bfOptions.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        bfOptions.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        bfOptions.inTempStorage = new byte[32 * 1024];

        FileInputStream fs = null;
        Bitmap bm;
        try {
            fs = new FileInputStream(new File(imagePath));
                bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
                imageView.setImageBitmap(bm);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    @BindingAdapter(value = {"toggleListener"})
    public static void setToggleListener(SwitchCompat switchView, ToggleListener toggleListener) {
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleListener.onToggle(isChecked);
            }
        });
    }
}
