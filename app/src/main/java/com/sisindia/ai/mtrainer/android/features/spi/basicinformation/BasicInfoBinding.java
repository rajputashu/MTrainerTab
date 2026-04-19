package com.sisindia.ai.mtrainer.android.features.spi.basicinformation;

import androidx.databinding.BindingAdapter;

import com.jaiselrahman.hintspinner.HintSpinner;
import com.jaiselrahman.hintspinner.HintSpinnerAdapter;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostResponse;

import java.util.List;

public class BasicInfoBinding {
    @BindingAdapter("spipostEntries")
    public static void setBranchEntries(HintSpinner spinner, List<SpiPostResponse.SpiPostdata> unitList) {

        spinner.setAdapter(new HintSpinnerAdapter<SpiPostResponse.SpiPostdata>(spinner.getContext(), unitList, "") {

            @Override
            public String getLabelFor(SpiPostResponse.SpiPostdata object) {


                return object.postName;
            }
        });

    }
}
