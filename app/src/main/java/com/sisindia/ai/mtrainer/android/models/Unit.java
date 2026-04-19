package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;

public class Unit {
    public int unitId;
    public String unitName;

    @NonNull
    @Override
    public String toString() {
        return unitName;
    }
}
