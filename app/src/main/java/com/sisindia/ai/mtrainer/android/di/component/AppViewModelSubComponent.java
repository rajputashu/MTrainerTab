package com.sisindia.ai.mtrainer.android.di.component;

import com.sisindia.ai.mtrainer.android.AppTestViewModel;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.features.addrota.AddRotaViewmodel;
import com.sisindia.ai.mtrainer.android.features.assessment.AssessmentReportViewModel;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendancePictureViewModel;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceSignatureViewModel;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceViewModel;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingPicAndSignatureViewModel;
import com.sisindia.ai.mtrainer.android.features.barcode.barcodedetection.BarCodeResultViewModel;
import com.sisindia.ai.mtrainer.android.features.checkin.CheckInViewModel;
import com.sisindia.ai.mtrainer.android.features.choosetopics.ChooseTopicsViewModel;
import com.sisindia.ai.mtrainer.android.features.clientreport.ClientReportViewModel;
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardViewModel;
import com.sisindia.ai.mtrainer.android.features.feedback.ClientNAViewModel;
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackActivityViewModel;
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackBottomSheetViewModel;
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackFinalViewModel;
import com.sisindia.ai.mtrainer.android.features.feedback.MainFeedbackViewModel;
import com.sisindia.ai.mtrainer.android.features.login.UserViewModel;
import com.sisindia.ai.mtrainer.android.features.myattendance.MyAttendanceViewModel;
import com.sisindia.ai.mtrainer.android.features.myconveyance.MyConveyanceViewModel;
import com.sisindia.ai.mtrainer.android.features.myconveyance.MyconveyanceactivityViewmodel;
import com.sisindia.ai.mtrainer.android.features.myunits.MyUnitsViewModel;
import com.sisindia.ai.mtrainer.android.features.myunits.MyUnitstabViewModel;
import com.sisindia.ai.mtrainer.android.features.notifications.NotificationsViewModel;
import com.sisindia.ai.mtrainer.android.features.onboard.OnBoardViewModel;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewViewModel;
import com.sisindia.ai.mtrainer.android.features.pretraining.TrainingItemDetailViewModel;
import com.sisindia.ai.mtrainer.android.features.pretraining.TrainingTopicsViewModel;
import com.sisindia.ai.mtrainer.android.features.reports.ReportsViewmodel;
import com.sisindia.ai.mtrainer.android.features.rota.DashBoardRotaViewModel;
import com.sisindia.ai.mtrainer.android.features.rplform.RplFormViewModel;
import com.sisindia.ai.mtrainer.android.features.spi.SpiViewModel;
import com.sisindia.ai.mtrainer.android.features.spi.basicinformation.BasicInformationViewModel;
import com.sisindia.ai.mtrainer.android.features.spi.clientapproval.ClientApprovalViewModel;
import com.sisindia.ai.mtrainer.android.features.spi.design.DesignViewModel;
import com.sisindia.ai.mtrainer.android.features.spi.draftapproval.DraftApprovalViewModel;
import com.sisindia.ai.mtrainer.android.features.spi.draftspi.DraftSpiViewModel;
import com.sisindia.ai.mtrainer.android.features.spi.mounted.MountedViewModel;
import com.sisindia.ai.mtrainer.android.features.spi.printing.PrintingViewModel;
import com.sisindia.ai.mtrainer.android.features.splash.SplashViewModel;
import com.sisindia.ai.mtrainer.android.features.starttraining.StartTrainingViewModel;
import com.sisindia.ai.mtrainer.android.features.takeassessment.AssesmentQuestionViewModel;
import com.sisindia.ai.mtrainer.android.features.takeassessment.AssessmentViewModel;
import com.sisindia.ai.mtrainer.android.features.takeassessment.ChooseAssessmentViewModel;
import com.sisindia.ai.mtrainer.android.features.takeassessment.TakeAssessmentViewModel;
import com.sisindia.ai.mtrainer.android.features.tickets.TicketsViewModel;
import com.sisindia.ai.mtrainer.android.features.timeline.TimeLineViewModel;
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsViewmodel;
import com.sisindia.ai.mtrainer.android.features.trainingcalendar.TrainingCalendarViewModel;
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCoursesViewmodel;
import com.sisindia.ai.mtrainer.android.features.trainingimages.TrainingImageViewModel;
import com.sisindia.ai.mtrainer.android.features.trainingkit.TrainingKitViewModel;
import com.sisindia.ai.mtrainer.android.features.trainingprogram.TrainingProgramViewModel;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.UmbrellaReportViewModel;

import dagger.Subcomponent;

@Subcomponent
public interface AppViewModelSubComponent {

    AppTestViewModel appTestViewModel();

    MTrainerViewModel vmMTrainerViewModel();

    OnBoardViewModel vmOnBoardViewModel();

    SplashViewModel vmSplashViewModel();

    UserViewModel vmUserViewModel();

    TrainingCoursesViewmodel vmTrainingCoursesViewModel();
    ReportsViewmodel vmReportsViewModel();


    DashBoardViewModel vmDashBoardViewModel();

    CheckInViewModel vmCheckInViewModel();

    MyAttendanceViewModel vmMyAttendanceViewModel();

    MyConveyanceViewModel vmMyConveyanceViewModel();
    MyconveyanceactivityViewmodel vmMyconveyanceViewmodel();

    MyUnitsViewModel vmMyUnitsViewModel();

    MyUnitstabViewModel vmMyUnitsTabViewModel();

    NotificationsViewModel vmNotificationsViewModel();

    DashBoardRotaViewModel vmDashBoardRotaViewModel();

    RplFormViewModel vmRplFormViewModel();

    TicketsViewModel vmTicketsViewModel();

    TrainingTopicsViewmodel vmTrainingTopicsViewl();


    TimeLineViewModel vmTimeLineViewModel();

    TrainingCalendarViewModel vmTrainingCalendarViewModel();

    TrainingKitViewModel vmTrainingKitViewModel();

    PreTrainingReviewViewModel vmPreTrainingReviewViewModel();


    StartTrainingViewModel vmStartTrainingViewModel();

    TrainingAttendanceViewModel vmTrainingAttendanceViewModel();

    ClientReportViewModel vmClientReportViewModel();

    TrainingTopicsViewModel vmTrainingTopicsViewModel();

    TrainingItemDetailViewModel vmTrainingItemDetailViewModel();
    ChooseTopicsViewModel vmChooseTopicsViewModel();

    AssessmentViewModel vmAssessmentViewModel();

    AssesmentQuestionViewModel vmAssesmentQuestionViewModel();

    TakeAssessmentViewModel vmTakeAssessmentViewModel();

    ChooseAssessmentViewModel vmChooseAssessmentViewModel();

    TrainingImageViewModel vmLiveImageCameraViewModel();

    TrainingAttendanceSignatureViewModel vmTrainingAttendanceSignatureViewModel();

    TrainingAttendancePictureViewModel   vmTrainingAttendancePictureViewModel();

    TrainingPicAndSignatureViewModel      vmTrainingPicAndSignatureViewModel();

    FeedbackActivityViewModel vmFeedbackActivityViewModel();

    MainFeedbackViewModel vmMainFeedbackViewModel();

    ClientNAViewModel vmClientNAViewModel();

    FeedbackFinalViewModel vmFeedbackFinalViewModel();

    FeedbackBottomSheetViewModel vmFeedbackBottomSheetViewModel();

    AddRotaViewmodel vmAddRotaViewmodel();
    BarCodeResultViewModel vmBarCodeResultViewModel();

    SpiViewModel vmSpiViewModel();
    BasicInformationViewModel vmBasicInformationViewModel();
    ClientApprovalViewModel vmClientApprovalViewModel();
    DesignViewModel vmDesignViewModel();
    DraftApprovalViewModel vmDraftApprovalViewModel();
    DraftSpiViewModel vmDraftSpiViewModel();
    MountedViewModel vmMountedViewModel();
    PrintingViewModel vmPrintingViewModel();

    UmbrellaReportViewModel vmUmbrellaReportViewModel();
    AssessmentReportViewModel vmAssessmentReport();
    TrainingProgramViewModel vmTrainingProgram();

    @Subcomponent.Builder
    interface Builder {
        AppViewModelSubComponent build();
    }
}
