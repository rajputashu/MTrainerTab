package com.sisindia.ai.mtrainer.android.di.module;

import android.content.Context;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.uimodels.NavigationUIModel;
import com.sisindia.ai.mtrainer.android.uimodels.YearUIModel;
import com.sisindia.ai.mtrainer.android.utils.TimeUtils;

import java.util.ArrayList;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;


@Module
public class MTrainerUiModule {
    @Provides
    @Named("StartTraining")
    ArrayList<NavigationUIModel> provideNavigationDataForStartTraining(Context context) {
        ArrayList<NavigationUIModel> list = new ArrayList<>();

        list.add(new NavigationUIModel(
                context.getString(R.string.navigation_text_take_attendance),
                R.drawable.ic_group_19,
                false,
                true,
                NavigationUIModel.NavigationUiViewType.TAKE_ATTENDANCE));

        list.add(new NavigationUIModel(
                context.getString(R.string.navigation_text_choose_topics_trained),
                R.drawable.ic_choose_topics_trained,
                false,
                true,
                NavigationUIModel.NavigationUiViewType.CHOOSE_TOPICS_TRAINED));

        list.add(new NavigationUIModel(
                context.getString(R.string.navigation_text_click_training_photos), R.drawable.ic_click_training_photos,
                false,
                true,
                NavigationUIModel.NavigationUiViewType.TAKE_TRAINING_PHOTOS));

        if (!(Prefs.getString(PrefsConstants.ROLE).equals("Training Champ") || Prefs.getString(PrefsConstants.ROLE).equals("Unit Commander"))) {
            list.add(new NavigationUIModel(
                    context.getString(R.string.navigation_text_take_assessment),
                    R.drawable.ic_take_assessment,
                    false,
                    true,
                    NavigationUIModel.NavigationUiViewType.TAKE_ASSESSMENT));
        }

        /*list.add(new NavigationUIModel(
                context.getString(R.string.navigation_text_rpl_form),
                R.drawable.ic_rpl1_form_rpl,
                false,
                true,
                NavigationUIModel.NavigationUiViewType.TAKE_RPL));*/

        list.add(new NavigationUIModel(context.getString(R.string.navigation_assessment_report),
                R.drawable.ic_on_paper,
                false,
                true,
                NavigationUIModel.NavigationUiViewType.ASSESSMENT_REPORTS));

        // below code for showing only client report for other then sis company
        if (!(Prefs.getString(PrefsConstants.COMPANY_ID).equals("2") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("9") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("7") || Prefs.getString(PrefsConstants.ROLE).equals("Training Champ"))) {
            list.add(new NavigationUIModel(
                    "Client Report",
                    R.drawable.ic_on_paper,
                    false,
                    true,
                    NavigationUIModel.NavigationUiViewType.CLIENT_REPORT));
        }

        list.add(new NavigationUIModel(
                "Remarks",
                R.drawable.ic_on_paper,
                false,
                true,
                NavigationUIModel.NavigationUiViewType.REMARKS));

        return list;
    }

    @Provides
    @Named("Year")
    ArrayList<YearUIModel> provideNavigationYear() {
        ArrayList<YearUIModel> list = new ArrayList<>();

        list.add(new YearUIModel("" + TimeUtils.getYear(), 0));

        list.add(new YearUIModel("" + (TimeUtils.getYear() - 1), 0));

        list.add(new YearUIModel("" + (TimeUtils.getYear() - 2), 0));

        list.add(new YearUIModel("" + (TimeUtils.getYear() - 3), 0));

        list.add(new YearUIModel("" + (TimeUtils.getYear() - 4), 0));

        return list;
    }

    @Provides
    @Named("Month")
    ArrayList<YearUIModel> provideNavigationMonth() {
        ArrayList<YearUIModel> list = new ArrayList<>();

        list.add(new YearUIModel("January", 1));

        list.add(new YearUIModel("February", 2));

        list.add(new YearUIModel("March", 3));

        list.add(new YearUIModel("April", 4));

        list.add(new YearUIModel("May", 5));
        list.add(new YearUIModel("June", 6));
        list.add(new YearUIModel("July", 7));
        list.add(new YearUIModel("August", 8));
        list.add(new YearUIModel("September", 9));
        list.add(new YearUIModel("October", 10));
        list.add(new YearUIModel("November", 11));
        list.add(new YearUIModel("December", 12));

        return list;
    }


}
