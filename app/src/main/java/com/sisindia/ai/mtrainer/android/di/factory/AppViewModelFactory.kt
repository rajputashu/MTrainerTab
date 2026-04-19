package com.sisindia.ai.mtrainer.android.di.factory


import androidx.collection.ArrayMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sisindia.ai.mtrainer.android.AppTestViewModel
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel
import com.sisindia.ai.mtrainer.android.di.component.AppViewModelSubComponent
import com.sisindia.ai.mtrainer.android.features.addrota.AddRotaViewmodel
import com.sisindia.ai.mtrainer.android.features.assessment.AssessmentReportViewModel
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendancePictureViewModel
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceSignatureViewModel
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceViewModel
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingPicAndSignatureViewModel
import com.sisindia.ai.mtrainer.android.features.barcode.barcodedetection.BarCodeResultViewModel
import com.sisindia.ai.mtrainer.android.features.checkin.CheckInViewModel
import com.sisindia.ai.mtrainer.android.features.choosetopics.ChooseTopicsViewModel
import com.sisindia.ai.mtrainer.android.features.clientreport.ClientReportViewModel
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardViewModel
import com.sisindia.ai.mtrainer.android.features.feedback.ClientNAViewModel
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackActivityViewModel
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackBottomSheetViewModel
import com.sisindia.ai.mtrainer.android.features.feedback.FeedbackFinalViewModel
import com.sisindia.ai.mtrainer.android.features.feedback.MainFeedbackViewModel
import com.sisindia.ai.mtrainer.android.features.login.UserViewModel
import com.sisindia.ai.mtrainer.android.features.myattendance.MyAttendanceViewModel
import com.sisindia.ai.mtrainer.android.features.myconveyance.MyConveyanceViewModel
import com.sisindia.ai.mtrainer.android.features.myconveyance.MyconveyanceactivityViewmodel
import com.sisindia.ai.mtrainer.android.features.myunits.MyUnitsViewModel
import com.sisindia.ai.mtrainer.android.features.myunits.MyUnitstabViewModel
import com.sisindia.ai.mtrainer.android.features.notifications.NotificationsViewModel
import com.sisindia.ai.mtrainer.android.features.onboard.OnBoardViewModel
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewViewModel
import com.sisindia.ai.mtrainer.android.features.pretraining.TrainingItemDetailViewModel
import com.sisindia.ai.mtrainer.android.features.pretraining.TrainingTopicsViewModel
import com.sisindia.ai.mtrainer.android.features.reports.ReportsViewmodel
import com.sisindia.ai.mtrainer.android.features.rota.DashBoardRotaViewModel
import com.sisindia.ai.mtrainer.android.features.rplform.RplFormViewModel
import com.sisindia.ai.mtrainer.android.features.spi.SpiViewModel
import com.sisindia.ai.mtrainer.android.features.spi.basicinformation.BasicInformationViewModel
import com.sisindia.ai.mtrainer.android.features.spi.clientapproval.ClientApprovalViewModel
import com.sisindia.ai.mtrainer.android.features.spi.design.DesignViewModel
import com.sisindia.ai.mtrainer.android.features.spi.draftapproval.DraftApprovalViewModel
import com.sisindia.ai.mtrainer.android.features.spi.draftspi.DraftSpiViewModel
import com.sisindia.ai.mtrainer.android.features.spi.mounted.MountedViewModel
import com.sisindia.ai.mtrainer.android.features.spi.printing.PrintingViewModel
import com.sisindia.ai.mtrainer.android.features.splash.SplashViewModel
import com.sisindia.ai.mtrainer.android.features.starttraining.StartTrainingViewModel
import com.sisindia.ai.mtrainer.android.features.takeassessment.AssesmentQuestionViewModel
import com.sisindia.ai.mtrainer.android.features.takeassessment.AssessmentViewModel
import com.sisindia.ai.mtrainer.android.features.takeassessment.ChooseAssessmentViewModel
import com.sisindia.ai.mtrainer.android.features.takeassessment.TakeAssessmentViewModel
import com.sisindia.ai.mtrainer.android.features.tickets.TicketsViewModel
import com.sisindia.ai.mtrainer.android.features.timeline.TimeLineViewModel
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsViewmodel
import com.sisindia.ai.mtrainer.android.features.trainingcalendar.TrainingCalendarViewModel
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCoursesViewmodel
import com.sisindia.ai.mtrainer.android.features.trainingimages.TrainingImageViewModel
import com.sisindia.ai.mtrainer.android.features.trainingkit.TrainingKitViewModel
import com.sisindia.ai.mtrainer.android.features.trainingprogram.TrainingProgramViewModel
import com.sisindia.ai.mtrainer.android.features.umbrellareport.UmbrellaReportViewModel
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppViewModelFactory @Inject
constructor(viewModelSubComponent: AppViewModelSubComponent) : ViewModelProvider.Factory {

    private val creators: ArrayMap<Class<*>, Callable<out ViewModel>> = ArrayMap()

    init {
        // View models cannot be injected directly because they won't be bound to the owner's view model scope.

        creators[AppTestViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.appTestViewModel() }

        creators[MTrainerViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmMTrainerViewModel() }

        creators[OnBoardViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmOnBoardViewModel() }

        creators[SplashViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmSplashViewModel() }

        creators[UserViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmUserViewModel() }

        creators[DashBoardViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmDashBoardViewModel() }


        creators[CheckInViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmCheckInViewModel() }

        creators[MyAttendanceViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmMyAttendanceViewModel() }

        creators[MyConveyanceViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmMyConveyanceViewModel() }

        creators[MyconveyanceactivityViewmodel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmMyconveyanceViewmodel() }

        creators[MyUnitsViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmMyUnitsViewModel() }

        creators[MyUnitstabViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmMyUnitsTabViewModel() }

        creators[NotificationsViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmNotificationsViewModel() }

        creators[DashBoardRotaViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmDashBoardRotaViewModel() }

        creators[RplFormViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmRplFormViewModel() }

        creators[TicketsViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTicketsViewModel() }

        creators[TimeLineViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTimeLineViewModel() }

        creators[TrainingCalendarViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingCalendarViewModel() }

        creators[TrainingKitViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingKitViewModel() }

        creators[PreTrainingReviewViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmPreTrainingReviewViewModel() }

        creators[StartTrainingViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmStartTrainingViewModel() }

        creators[TrainingAttendanceViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingAttendanceViewModel() }

        creators[ClientReportViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmClientReportViewModel() }

        creators[TrainingTopicsViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingTopicsViewModel() }

        creators[TrainingItemDetailViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingItemDetailViewModel() }

        creators[ChooseTopicsViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmChooseTopicsViewModel() }

        creators[AssessmentViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmAssessmentViewModel() }
        creators[TakeAssessmentViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTakeAssessmentViewModel() }

        creators[AssesmentQuestionViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmAssesmentQuestionViewModel() }

        creators[ChooseAssessmentViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmChooseAssessmentViewModel() }

        creators[TrainingImageViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmLiveImageCameraViewModel() }

        creators[TrainingAttendanceSignatureViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingAttendanceSignatureViewModel() }



        creators[TrainingAttendancePictureViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingAttendancePictureViewModel() }

        creators[TrainingCoursesViewmodel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingCoursesViewModel() }
        creators[ReportsViewmodel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmReportsViewModel() }



        creators[TrainingPicAndSignatureViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingPicAndSignatureViewModel() }

        creators[FeedbackActivityViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmFeedbackActivityViewModel() }

        creators[MainFeedbackViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmMainFeedbackViewModel() }

        creators[ClientNAViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmClientNAViewModel() }

        creators[FeedbackFinalViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmFeedbackFinalViewModel() }

        creators[FeedbackBottomSheetViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmFeedbackBottomSheetViewModel() }

        creators[TrainingTopicsViewmodel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingTopicsViewl() }


        creators[AddRotaViewmodel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmAddRotaViewmodel() }

        creators[BarCodeResultViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmBarCodeResultViewModel() }

        creators[SpiViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmSpiViewModel() }
        creators[BasicInformationViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmBasicInformationViewModel() }

        creators[ClientApprovalViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmClientApprovalViewModel() }

        creators[DesignViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmDesignViewModel() }

        creators[DraftApprovalViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmDraftApprovalViewModel() }

        creators[DraftSpiViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmDraftSpiViewModel() }


        creators[MountedViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmMountedViewModel() }

        creators[PrintingViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmPrintingViewModel() }

        creators[UmbrellaReportViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmUmbrellaReportViewModel() }

        creators[AssessmentReportViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmAssessmentReport() }

        creators[TrainingProgramViewModel::class.java] =
            Callable<ViewModel> { viewModelSubComponent.vmTrainingProgram() }

        /*

                creators[AttendancePictureViewModel::class.java] =
                    Callable<ViewModel> { viewModelSubComponent. vmAttendancePictureViewModel() }


                creators[AttendanceSignatureViewModel::class.java] =
                    Callable<ViewModel> { viewModelSubComponent. vmAttendanceSignatureViewModel() }
        */
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        if (creator == null) {
            throw IllegalArgumentException("Unknown model. class $modelClass")
        }
        try {
            return creator.call() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}