package com.sisindia.ai.mtrainer.android.uimodels;

import androidx.annotation.DrawableRes;

import org.parceler.Parcel;

@Parcel
public class NavigationUIModel {

    private String bottomTxt;

    @DrawableRes
    private int iconResId;

    private boolean isCompleted;

    private boolean isClickable;

    private NavigationUiViewType viewType;


    public NavigationUIModel() {

    }

    public NavigationUIModel(String bottomTxt, int iconResId, boolean isCompleted, boolean isClickable, NavigationUiViewType viewType) {
        this.bottomTxt = bottomTxt;
        this.iconResId = iconResId;
        this.isCompleted = isCompleted;
        this.isClickable = isClickable;
        this.viewType = viewType;
    }

    public String getBottomTxt() {
        return bottomTxt;
    }

    public void setBottomTxt(String bottomTxt) {
        this.bottomTxt = bottomTxt;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public NavigationUiViewType getViewType() {
        return viewType;
    }

    public void setViewType(NavigationUiViewType viewType) {
        this.viewType = viewType;
    }


    public enum NavigationUiViewType {
        UNKNOWN(0),
        TAKE_ATTENDANCE(1),
        TAKE_TRAINING_PHOTOS(2),
        CHOOSE_TOPICS_TRAINED(3),
        TAKE_ASSESSMENT(4),
        TAKE_RPL(5),
        TAKE_FEEDBACK(6),
        CLIENT_REPORT(7),
        REMARKS(8),
        ASSESSMENT_REPORTS(9);
        private final int navigationViewType;

        NavigationUiViewType(int navigationViewType) {
            this.navigationViewType = navigationViewType;
        }

        public int getNavigationViewType() {
            return navigationViewType;
        }
    }

}


