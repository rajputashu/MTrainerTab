package com.sisindia.ai.mtrainer.android.di.module;

import android.app.Application;
import android.content.Context;

import com.droidcommons.dagger.qualifier.ActivityScoped;
import com.sisindia.ai.mtrainer.android.MTrainerApplication;
import com.sisindia.ai.mtrainer.android.features.AppTest;
import com.sisindia.ai.mtrainer.android.features.addrota.AddRotaActivity;
import com.sisindia.ai.mtrainer.android.features.assessment.AssessmentReportActivity;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceActivity;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendancePictureActivity;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceSignatureActvity;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingPicAndSignActivity;
import com.sisindia.ai.mtrainer.android.features.barcode.LiveBarcodeScanningActivity;
import com.sisindia.ai.mtrainer.android.features.choosetopics.ChooseTopicsActivity;
import com.sisindia.ai.mtrainer.android.features.clientreport.ClientReportActivity;
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardActivity;
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackActivity;
import com.sisindia.ai.mtrainer.android.features.location.UserLocationActivity;
import com.sisindia.ai.mtrainer.android.features.myconveyance.MyconvenceActivity;
import com.sisindia.ai.mtrainer.android.features.onboard.OnBoardActivity;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.features.pretraining.TrainingItemDetailActivity;
import com.sisindia.ai.mtrainer.android.features.reports.ReportsActivity;
import com.sisindia.ai.mtrainer.android.features.rota.WebviewActivity;
import com.sisindia.ai.mtrainer.android.features.rplform.RplFormActivity;
import com.sisindia.ai.mtrainer.android.features.server.ServerService;
import com.sisindia.ai.mtrainer.android.features.services.LocationService;
import com.sisindia.ai.mtrainer.android.features.spi.SpiMainActivity;
import com.sisindia.ai.mtrainer.android.features.spi.basicinformation.BasicInfoActvity;
import com.sisindia.ai.mtrainer.android.features.spi.clientapproval.ClientApprovalActivity;
import com.sisindia.ai.mtrainer.android.features.spi.design.DesignActivity;
import com.sisindia.ai.mtrainer.android.features.spi.draftapproval.DraftApprovalActivity;
import com.sisindia.ai.mtrainer.android.features.spi.draftspi.DraftSpiActivity;
import com.sisindia.ai.mtrainer.android.features.spi.mounted.MountedActivity;
import com.sisindia.ai.mtrainer.android.features.spi.printing.PrintingActivity;
import com.sisindia.ai.mtrainer.android.features.starttraining.StartTraningActivity;
import com.sisindia.ai.mtrainer.android.features.takeassessment.AssessmentActivity;
import com.sisindia.ai.mtrainer.android.features.takeassessment.AssessmentQuestionActivity;
import com.sisindia.ai.mtrainer.android.features.takeassessment.ChooseAssessmentFragment;
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsActivity;
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsActivityV2;
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCoursesActivity;
import com.sisindia.ai.mtrainer.android.features.trainingimages.TrainingImageActivity;
import com.sisindia.ai.mtrainer.android.features.trainingkit.SOPLandingPageActivity;
import com.sisindia.ai.mtrainer.android.features.trainingkit.TrainingKitActivity;
import com.sisindia.ai.mtrainer.android.features.trainingkit.TrainingSlidesActivity;
import com.sisindia.ai.mtrainer.android.features.trainingkit.TrainingVideoActivity;
import com.sisindia.ai.mtrainer.android.features.trainingkit.VideoLandingActivity;
import com.sisindia.ai.mtrainer.android.features.trainingkit.VideoPlayerActivity;
import com.sisindia.ai.mtrainer.android.features.trainingprogram.TrainingProgramActivity;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.UmbrellaReportBaseActivity;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.UmbrellaSPIReportBaseActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @Binds
    abstract Context bindContext(MTrainerApplication application);

    @Binds
    abstract Application bindApplication(MTrainerApplication application);


    @ActivityScoped
    @ContributesAndroidInjector
    abstract AppTest bindAppTest();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract OnBoardActivity bindOnBoardActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract DashBoardActivity bindDashBoardActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract MyconvenceActivity bindCoveyanceActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract PreTrainingReviewActivity bindPreTrainingReviewActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract StartTraningActivity bindStartTraningActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingAttendanceActivity bindTrainingAttendance();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract ClientReportActivity bindClientReport();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingItemDetailActivity bindTrainingItemDetailActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract ChooseTopicsActivity bindChooseTopicsActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract FeedbackActivity bindFeedbackActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingTopicsActivity bindTrainingTopicsActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingCoursesActivity bindTrainingCoursesActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract ReportsActivity bindReportsActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract AssessmentActivity bindTakeAssessmentActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingImageActivity bindImageCaptureActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingAttendancePictureActivity bindTrainingAttendancePictureActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingAttendanceSignatureActvity bindTrainingAttendanceSignatureActvity();

    @ContributesAndroidInjector
    abstract TrainingKitActivity bindTrainingKitActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract SOPLandingPageActivity bindSOPLandingPageActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingVideoActivity bindTrainingVideoActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingSlidesActivity bindTrainingSlidesActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract VideoLandingActivity bindVideoLandingActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract VideoPlayerActivity bindVideoPlayerActivity();

    /*@ActivityScoped
    @ContributesAndroidInjector
    abstract WebViewActivity bindWebViewActivity();*/

    @ActivityScoped
    @ContributesAndroidInjector
    abstract WebviewActivity bindWebActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract ServerService bindServerService();


    @ActivityScoped
    @ContributesAndroidInjector
    abstract RplFormActivity bindRplFormActivity();


    @ActivityScoped
    @ContributesAndroidInjector
    abstract SpiMainActivity bindSpiMainActivity();


    /*

    @ActivityScoped
    @ContributesAndroidInjector
    abstract AttendancePictureActivity bindAttendancePictureActivity();
*/
    @ActivityScoped
    @ContributesAndroidInjector
    abstract LocationService userLocationService();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract UserLocationActivity userLocationActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingPicAndSignActivity bindTrainingPicAndSignActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract AddRotaActivity bindAddRotaActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract AssessmentQuestionActivity bindAssessmentQuestionActivity();


    @ActivityScoped
    @ContributesAndroidInjector
    abstract LiveBarcodeScanningActivity bindLiveBarcodeScanningActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract DraftSpiActivity bindDraftSpiActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract BasicInfoActvity bindBasicInfoFragment();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract DesignActivity bindDesignFragment();


    @ActivityScoped
    @ContributesAndroidInjector
    abstract ClientApprovalActivity bindClientApprovalFragment();


    @ActivityScoped
    @ContributesAndroidInjector
    abstract DraftApprovalActivity bindDraftApprovalFragment();


    @ActivityScoped
    @ContributesAndroidInjector
    abstract MountedActivity bindMountedFragment();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract PrintingActivity bindPrintingFragment();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract UmbrellaReportBaseActivity bindUmbrellaReportBaseActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract UmbrellaSPIReportBaseActivity bindspiUmbrellaReportBaseActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract ChooseAssessmentFragment chooseAssessmentActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract AssessmentReportActivity assessmentReportActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingProgramActivity trainingProgramActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract TrainingTopicsActivityV2 topicsActivityV2();
}
