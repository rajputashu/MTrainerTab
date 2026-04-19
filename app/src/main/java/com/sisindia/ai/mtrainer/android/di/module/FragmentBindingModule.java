package com.sisindia.ai.mtrainer.android.di.module;


import com.droidcommons.dagger.qualifier.FragmentScoped;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.AddEmployeeFragment;
import com.sisindia.ai.mtrainer.android.features.barcode.barcodedetection.BarcodeResultFragment;
import com.sisindia.ai.mtrainer.android.features.checkin.CheckInFragment;
import com.sisindia.ai.mtrainer.android.features.feedback.ClientNABottomSheet;
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackBottomSheet;
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackFinalFragment;
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackOtpBottomSheet;
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackRatingFragment;
import com.sisindia.ai.mtrainer.android.features.feedback.MainFeedbackFragment;
import com.sisindia.ai.mtrainer.android.features.login.LoginWithMobileNumberFragment;
import com.sisindia.ai.mtrainer.android.features.myattendance.MyAttendanceFragment;
import com.sisindia.ai.mtrainer.android.features.myconveyance.ConveyanceTimeLineFragment;
import com.sisindia.ai.mtrainer.android.features.myconveyance.MyConveyanceDailyFragment;
import com.sisindia.ai.mtrainer.android.features.myconveyance.MyConveyanceFragment;
import com.sisindia.ai.mtrainer.android.features.myunits.MyUnitsFragment;
import com.sisindia.ai.mtrainer.android.features.myunits.MyUnitstabFragment;
import com.sisindia.ai.mtrainer.android.features.notifications.NotificationsFragment;
import com.sisindia.ai.mtrainer.android.features.otp.LoginOtpFragment;
import com.sisindia.ai.mtrainer.android.features.pretraining.CancelTrainingBottomSheetFragment;
import com.sisindia.ai.mtrainer.android.features.rota.DashBoardRotaFragment;
import com.sisindia.ai.mtrainer.android.features.rplform.RplFormFragment;
import com.sisindia.ai.mtrainer.android.features.splash.SplashFragment;
import com.sisindia.ai.mtrainer.android.features.takeassessment.TakeAssessmentFragment;
import com.sisindia.ai.mtrainer.android.features.tickets.TicketsFragment;
import com.sisindia.ai.mtrainer.android.features.timeline.TimeLineFragment;
import com.sisindia.ai.mtrainer.android.features.trainingcalendar.TrainingCalendarFragment;
import com.sisindia.ai.mtrainer.android.features.trainingimages.CaptureImageFragmentKt;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBindingModule {


    @ContributesAndroidInjector
    @FragmentScoped
    abstract SplashFragment bindSplashFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract LoginWithMobileNumberFragment bindLoginWithMobileNumberFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract LoginOtpFragment bindLoginOtpFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract DashBoardRotaFragment bindDashBoardRotaFragment();


    @ContributesAndroidInjector
    @FragmentScoped
    abstract CheckInFragment bindCheckInFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract MyAttendanceFragment bindMyAttendanceFragment();


    @ContributesAndroidInjector
    @FragmentScoped
    abstract MyConveyanceFragment bindMyConveyanceFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract ConveyanceTimeLineFragment bindConveyanceTimelineFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract MyConveyanceDailyFragment bindConveyanceMonthlyFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract MyUnitsFragment bindMyUnitsFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract MyUnitstabFragment bindMyUnitstabFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract NotificationsFragment bindNotificationsFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract RplFormFragment bindRplFormFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract TicketsFragment bindTicketsFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract TimeLineFragment bindTimeLineFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract TrainingCalendarFragment bindTrainingCalendarFragment();
/*

    @ContributesAndroidInjector
    @FragmentScoped
    abstract TrainingKitFragment bindTrainingKitFragment();
*/
/*

    @ContributesAndroidInjector
    @FragmentScoped
    abstract QRScannerFragment bindQRScannerFragment();
*/

    @ContributesAndroidInjector
    @FragmentScoped
    abstract TakeAssessmentFragment bindTaseAssessmentFragment();

    /*@ContributesAndroidInjector
    @FragmentScoped
    abstract ChooseAssessmentFragment bindChooseAssessmentFragment();*/

    @ContributesAndroidInjector
    @FragmentScoped
    abstract CaptureImageFragmentKt bindCaptureImageFragmentKt();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract CancelTrainingBottomSheetFragment bindCancelTrainingBottomSheetFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract FeedbackBottomSheet bindFeedbackBottomSheet();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract ClientNABottomSheet bindClientNABottomSheet();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract FeedbackOtpBottomSheet bindFeedbackOtpBottomSheet();


    @ContributesAndroidInjector
    @FragmentScoped
    abstract MainFeedbackFragment bindMainFeedbackFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract FeedbackRatingFragment bindFeedbackRatingFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract FeedbackFinalFragment bindFeedbackFinalFragment();


    @ContributesAndroidInjector
    @FragmentScoped
    abstract AddEmployeeFragment bindAddEmployeeFragment();

    @ContributesAndroidInjector
    @FragmentScoped
    abstract BarcodeResultFragment bindBarcodeResultFragment();


}
