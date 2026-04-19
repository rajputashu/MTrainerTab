package com.sisindia.ai.mtrainer.android.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.sisindia.ai.mtrainer.android.db.dao.AdhocSavedTopicsDao;
import com.sisindia.ai.mtrainer.android.db.dao.AdhocTopicsDao;
import com.sisindia.ai.mtrainer.android.db.dao.AssessmentDao;
import com.sisindia.ai.mtrainer.android.db.dao.AttendanceDao;
import com.sisindia.ai.mtrainer.android.db.dao.AttendancePhotoDao;
import com.sisindia.ai.mtrainer.android.db.dao.BranchDao;
import com.sisindia.ai.mtrainer.android.db.dao.CalendrRotaCompletdDao;
import com.sisindia.ai.mtrainer.android.db.dao.ClientApprovalDao;
import com.sisindia.ai.mtrainer.android.db.dao.ClientListDao;
import com.sisindia.ai.mtrainer.android.db.dao.DesignSpiDao;
import com.sisindia.ai.mtrainer.android.db.dao.DesignStatusDao;
import com.sisindia.ai.mtrainer.android.db.dao.DraftApprovalDao;
import com.sisindia.ai.mtrainer.android.db.dao.DraftSpiDao;
import com.sisindia.ai.mtrainer.android.db.dao.DraftSpiPhotoDao;
import com.sisindia.ai.mtrainer.android.db.dao.EmployeeApiDao;
import com.sisindia.ai.mtrainer.android.db.dao.GpsPingDao;
import com.sisindia.ai.mtrainer.android.db.dao.GpsTokenDao;
import com.sisindia.ai.mtrainer.android.db.dao.MasterAttendanceDao;
import com.sisindia.ai.mtrainer.android.db.dao.MasterPostDao;
import com.sisindia.ai.mtrainer.android.db.dao.MountedDao;
import com.sisindia.ai.mtrainer.android.db.dao.MountedSpiPhotoDao;
import com.sisindia.ai.mtrainer.android.db.dao.MountedStatusDao;
import com.sisindia.ai.mtrainer.android.db.dao.MtrainerMLCVideoDao;
import com.sisindia.ai.mtrainer.android.db.dao.MtrainerRotaTaskDao;
import com.sisindia.ai.mtrainer.android.db.dao.MtrainerVideoDao;
import com.sisindia.ai.mtrainer.android.db.dao.MyModuleDao;
import com.sisindia.ai.mtrainer.android.db.dao.MyUnitsDao;
import com.sisindia.ai.mtrainer.android.db.dao.PerformanceDao;
import com.sisindia.ai.mtrainer.android.db.dao.PresenatationDao;
import com.sisindia.ai.mtrainer.android.db.dao.PrintingSpiDao;
import com.sisindia.ai.mtrainer.android.db.dao.QuestionDao;
import com.sisindia.ai.mtrainer.android.db.dao.RatingDataDao;
import com.sisindia.ai.mtrainer.android.db.dao.RatingQuestionDao;
import com.sisindia.ai.mtrainer.android.db.dao.ReasonDao;
import com.sisindia.ai.mtrainer.android.db.dao.RegionDao;
import com.sisindia.ai.mtrainer.android.db.dao.ReuploadDraftSpiDao;
import com.sisindia.ai.mtrainer.android.db.dao.RplFormDao;
import com.sisindia.ai.mtrainer.android.db.dao.RplFormPhotoDao;
import com.sisindia.ai.mtrainer.android.db.dao.SavedClientReportCcDao;
import com.sisindia.ai.mtrainer.android.db.dao.SavedClientReportToDao;
import com.sisindia.ai.mtrainer.android.db.dao.SavedFeedbackDao;
import com.sisindia.ai.mtrainer.android.db.dao.SavedFeedbackReasonDao;
import com.sisindia.ai.mtrainer.android.db.dao.SignatureAttchmentDao;
import com.sisindia.ai.mtrainer.android.db.dao.Site1Dao;
import com.sisindia.ai.mtrainer.android.db.dao.SiteDao;
import com.sisindia.ai.mtrainer.android.db.dao.SlideModuleDao;
import com.sisindia.ai.mtrainer.android.db.dao.SpiBasicInfoDao;
import com.sisindia.ai.mtrainer.android.db.dao.SpiBranchDao;
import com.sisindia.ai.mtrainer.android.db.dao.SpiPostsDao;
import com.sisindia.ai.mtrainer.android.db.dao.SpiTableDetailsDao;
import com.sisindia.ai.mtrainer.android.db.dao.StarTrainingMasterDao;
import com.sisindia.ai.mtrainer.android.db.dao.SubModuleDao;
import com.sisindia.ai.mtrainer.android.db.dao.TimeLineDao;
import com.sisindia.ai.mtrainer.android.db.dao.TrainingFinalSubmitDao;
import com.sisindia.ai.mtrainer.android.db.dao.TrainingPhotoAttachmentDao;
import com.sisindia.ai.mtrainer.android.db.dao.TrainingcalinderDao;
import com.sisindia.ai.mtrainer.android.db.dao.UnitListDao;
import com.sisindia.ai.mtrainer.android.db.entities.AdhocSavedTopics;
import com.sisindia.ai.mtrainer.android.db.entities.AssementEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.CourseEntity;
import com.sisindia.ai.mtrainer.android.db.entities.DraftSpiPhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.EmployeeApiEntity;
import com.sisindia.ai.mtrainer.android.db.entities.GpsTokenEntity;
import com.sisindia.ai.mtrainer.android.db.entities.LanguageEntity;
import com.sisindia.ai.mtrainer.android.db.entities.LocationDetailsEntity;
import com.sisindia.ai.mtrainer.android.db.entities.MountedSpiPhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.MtrainerRotaTaskEntity;
import com.sisindia.ai.mtrainer.android.db.entities.Presenatation;
import com.sisindia.ai.mtrainer.android.db.entities.ProgramEntity;
import com.sisindia.ai.mtrainer.android.db.entities.ReasonEntity;
import com.sisindia.ai.mtrainer.android.db.entities.RplFormEntity;
import com.sisindia.ai.mtrainer.android.db.entities.RplFormPhotoEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportCc;
import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportTo;
import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedback;
import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedbackReason;
import com.sisindia.ai.mtrainer.android.db.entities.SignatureAttachmentEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SiteEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SlideModuleEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SubModules;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;
import com.sisindia.ai.mtrainer.android.db.entities.TopicEntity;
import com.sisindia.ai.mtrainer.android.db.entities.TrainingPhotoAttachmentEntity;
import com.sisindia.ai.mtrainer.android.db.entities.VideoClick;
import com.sisindia.ai.mtrainer.android.features.spi.model.DesignStatusResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.MountedStatusResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.ReuploadDraftSpiResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostResponse;
import com.sisindia.ai.mtrainer.android.features.trainingkit.VideoDetailsModel;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaMaster;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaMasterDao;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaPost;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaPostDao;
import com.sisindia.ai.mtrainer.android.models.AdhocEmployeesResponse;
import com.sisindia.ai.mtrainer.android.models.AdhocEmployeesSaved;
import com.sisindia.ai.mtrainer.android.models.AdhocTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.AssesmentQustions;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.CalendarResponse;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.ClientApprovalResponse;
import com.sisindia.ai.mtrainer.android.models.ContactListResponse;
import com.sisindia.ai.mtrainer.android.models.DesignSpiResponse;
import com.sisindia.ai.mtrainer.android.models.DraftSpiResponse;
import com.sisindia.ai.mtrainer.android.models.MLCVideoDetailsModel;
import com.sisindia.ai.mtrainer.android.models.MyModule;
import com.sisindia.ai.mtrainer.android.models.MyUnitsResponse;
import com.sisindia.ai.mtrainer.android.models.RatingMasterResponse;
import com.sisindia.ai.mtrainer.android.models.RegionData;
import com.sisindia.ai.mtrainer.android.models.SiteData;
import com.sisindia.ai.mtrainer.android.models.SitePostResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBranchData;
import com.sisindia.ai.mtrainer.android.models.SpiPrintingResponse;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;
import com.sisindia.ai.mtrainer.android.models.TrainerPerformanceResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingFinalSubmitResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;
import com.sisindia.ai.mtrainer.android.models.UnitListResponse;
import com.sisindia.ai.mtrainer.android.models.spi.DraftApprovalResponse;
import com.sisindia.ai.mtrainer.android.models.spi.MountedResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ChooseTopicsResponse.TopicsResponse.class, SavedTopic.class,
        TrainingCalendarResponse.TrainingCalendar.class,
        AttendanceEntity.class, TrainingAttendanceResponse.AttendanceResponse.class,
        TrainingPhotoAttachmentEntity.class,
        SignatureAttachmentEntity.class,
        SitePostResponse.PostResponse.class,
        SavedFeedback.class, SavedFeedbackReason.class,
        ContactListResponse.ClientData.class,
        AttendancePhotoEntity.class,
        RatingMasterResponse.RatingData.class,
        MtrainerRotaTaskEntity.class,
        TrainingFinalSubmitResponse.TrainingSubmitResponse.class,
        SiteEntity.class,
        UnitListResponse.Unit.class, AssesmentQustions.class,
        ReasonEntity.class, AssementEntity.class,
        TrainerPerformanceResponse.PerformanceResponse.class,
        SavedClientReportCc.class, SavedClientReportTo.class,
        TimeLineEntity.class,
        RplFormEntity.class,
        RplFormPhotoEntity.class,
        VideoDetailsModel.VideoDetailsModellist.class,
        Presenatation.class,
        SubModules.MySubModuleslist.class,
        VideoClick.class,
        SlideModuleEntity.SlideModuleEntityList.class,
        MyModule.MyModuleslist.class,
        CalendarResponse.CalendarRotaResponse.class,
        MyUnitsResponse.MyUnitsDetailList.class,
        AdhocTopicsResponse.AdhocTopics.class,
        AdhocSavedTopics.class,
        AdhocEmployeesResponse.AdhocEmployees.class,
        AdhocEmployeesSaved.class,
        EmployeeApiEntity.class,
        GpsTokenEntity.class, LocationDetailsEntity.class,
        UmbrellaPost.class,
        SpiBranchData.class,
        SpiTableDetailsResponse.SpiTableDetailsData.class,
        DraftSpiResponse.DraftSpiDetailsData.class,
        SpiBasicInfoResponse.SpiBasicInfoDetailsData.class,
        DraftSpiPhotoEntity.class,
        DesignSpiResponse.DesignSpiData.class,
        ClientApprovalResponse.ClientApprovalStatus.class,
        SpiPrintingResponse.SpiPrintingStatus.class,
        MountedResponse.MountedData.class,
        DraftApprovalResponse.DraftApprovalTableDetailsData.class,
        MountedSpiPhotoEntity.class,
        DesignStatusResponse.designStatus.class,
        MountedStatusResponse.mountedStatus.class,
        SpiPostResponse.SpiPostdata.class,
        UmbrellaMaster.class,
        ReuploadDraftSpiResponse.ReuploadDraftSpiData.class,
        MLCVideoDetailsModel.MlcVideoDetailsModellist.class,
        TrainingTopicsDataModel.class,
        ProgramEntity.class,
        LanguageEntity.class,
        CourseEntity.class,
        TopicEntity.class,
        RatingMasterResponse.RatingQuestionData.class,
        RegionData.class,
        BranchData.class,
        SiteData.class},
        version = 56, exportSchema = false)
public abstract class MtrainerDataBase extends RoomDatabase {
    public abstract TopicDao getTopicDao();

    public abstract AssessmentDao getAssessmentDao();

    public abstract UnitListDao getUnitListDao();

    public abstract SavedTopicDao getSavedTopicDao();

    public abstract TrainingcalinderDao getTrainingCalenderDao();

    public abstract AttendanceDao getAttendanceDao();

    public abstract TrainingPhotoAttachmentDao getPhotoAttachmentDao();

    public abstract SignatureAttchmentDao getSignatureAttachmentDao();

    public abstract AttendancePhotoDao getAttendancePhotoDao();

    public abstract RatingQuestionDao getRatingQuestionDao();

    public abstract RatingDataDao getRatingDataDao();

    public abstract ClientListDao getClientListDao();

    public abstract SavedFeedbackDao getSavedFeedbackDao();

    public abstract SavedFeedbackReasonDao getSavedFeedbackReasonDao();

    public abstract MasterPostDao getMasterPostDao();

    public abstract QuestionDao getQuestionDao();

    public abstract MtrainerRotaTaskDao getMtrainerRotaTaskDao();

    public abstract SiteDao getSiteDao();

    public abstract ReasonDao getReasonDao();

    public abstract PerformanceDao getPerformanceDao();

    public abstract TrainingFinalSubmitDao getTrainingFinalSubmitDao();

    public abstract SavedClientReportToDao getSavedClientReportToDao();

    public abstract SavedClientReportCcDao getSavedClientReportCcDao();

    public abstract TimeLineDao getTimeLineDao();

    public abstract RplFormDao getRplFormDao();

    public abstract RplFormPhotoDao getRplFormPhotoDao();

    public abstract GpsPingDao getGpsPingDao();

    public abstract GpsTokenDao getGpsTokenDao();

    public abstract MtrainerVideoDao getMtrainerVideoDao();

    public abstract MtrainerMLCVideoDao getMtrainerMLCVideoDao();

    public abstract PresenatationDao getPresenatationDao();

    public abstract MyModuleDao getMyModuleDao();

    public abstract SubModuleDao getSubModuleDao();

    public abstract SlideModuleDao getSlideModuleDao();

    public abstract RegionDao getRegionDao();

    public abstract BranchDao getBranchDao();

    //public abstract BranchDetailsDao getBranchDetailsDao();
    public abstract Site1Dao getSite1Dao();

    public abstract CalendrRotaCompletdDao getCalendarRotaCompleted();

    public abstract MyUnitsDao getMyUnitsDao();

    public abstract AdhocTopicsDao getAdhocTopicsDao();

    public abstract AdhocSavedTopicsDao getAdhocSavedTopicsDao();

    public abstract EmployeeApiDao getEmployeeApiDao();

    public abstract MasterAttendanceDao getMasterAttendanceDao();

    public abstract SpiBranchDao getSpiBranchDao();

    public abstract SpiTableDetailsDao getSpiTableDetailsDao();

    public abstract DraftSpiDao getDraftSpiDao();

    public abstract SpiBasicInfoDao getSpiBasicInfoDao();

    public abstract DraftSpiPhotoDao getDraftSpiPhotoDao();

    public abstract DesignSpiDao getDesignSpidao();

    public abstract ClientApprovalDao getClientApprovalDao();

    public abstract PrintingSpiDao getPrintingSpiDao();

    public abstract MountedSpiPhotoDao getMountedSpiPhotoDao();

    public abstract DraftApprovalDao getDraftApprovalDao();

    public abstract MountedDao getMountedDao();

    public abstract DesignStatusDao getDesignStatusDao();

    public abstract MountedStatusDao getMountedStatusDao();

    public abstract SpiPostsDao getSpiPostsDao();

    public abstract ReuploadDraftSpiDao getReuploadDraftSpiDao();

    public abstract UmbrellaPostDao getUmbrellaPostDao();

    public abstract UmbrellaMasterDao getUmbrellaMasterDao();

    public abstract StarTrainingMasterDao getTrainingMasterDao();

    private static volatile MtrainerDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MtrainerDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MtrainerDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MtrainerDataBase.class, "mtrainer_database")
                            .allowMainThreadQueries()
                            .addMigrations(MIGRATION_46_51, MIGRATION_47_51, MIGRATION_48_51, MIGRATION_49_51, MIGRATION_50_51)
                            .enableMultiInstanceInvalidation()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Migration MIGRATION_46_51 = new Migration(46, 51) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            clearDB(database);
        }
    };

    private static Migration MIGRATION_47_51 = new Migration(47, 51) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            clearDB(database);
        }
    };

    private static Migration MIGRATION_48_51 = new Migration(48, 51) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            clearDB(database);
        }
    };

    private static Migration MIGRATION_49_51 = new Migration(49, 51) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    private static Migration MIGRATION_50_51 = new Migration(50, 51) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    static void clearDB(SupportSQLiteDatabase _db) {
        _db.execSQL("DELETE FROM `master_topic_table`");
        _db.execSQL("DELETE FROM `master_attendance_name`");
        _db.execSQL("DELETE FROM `signature_attachment_table`");
        _db.execSQL("DELETE FROM `post_master_table`");
        _db.execSQL("DELETE FROM `master_client_contact_table`");
        _db.execSQL("DELETE FROM `master_rating_table`");
        _db.execSQL("DELETE FROM `rota_task_table`");
        _db.execSQL("DELETE FROM `site_table`");
        _db.execSQL("DELETE FROM `master_unit_table`");
        _db.execSQL("DELETE FROM `assessment_question_master`");
        _db.execSQL("DELETE FROM `reason_table`");
        _db.execSQL("DELETE FROM `performance_table`");
        _db.execSQL("DELETE FROM `rpl_form_table`"); // Save
        _db.execSQL("DELETE FROM `rplform_photo_table`"); // Save
        _db.execSQL("DELETE FROM `video_model_table`");
        _db.execSQL("DELETE FROM `presentation_table`");
        _db.execSQL("DELETE FROM `sub_modules_table`");
        _db.execSQL("DELETE FROM `video_click_table`");
        _db.execSQL("DELETE FROM `slide_module_table`");
        _db.execSQL("DELETE FROM `modu_table`");
        _db.execSQL("DELETE FROM `calendar_rota_table`"); //
        _db.execSQL("DELETE FROM `my_units_table`");
        _db.execSQL("DELETE FROM `adhoc_topics_table`"); // Maybe
        _db.execSQL("DELETE FROM `adhoc_employees_table`"); // Maybe
        _db.execSQL("DELETE FROM `Adhoc_saved_employee_table`"); //
        _db.execSQL("DELETE FROM `employee_api`");
        _db.execSQL("DELETE FROM `gps_token_table`");
        _db.execSQL("DELETE FROM `location_details`");
        _db.execSQL("DELETE FROM `spi_branch_data`");
        _db.execSQL("DELETE FROM `Spi_table`");
        _db.execSQL("DELETE FROM `draft_spi_table`");
        _db.execSQL("DELETE FROM `Spi_basic_info_table`"); //SPI
        _db.execSQL("DELETE FROM `design_spi_table`");
        _db.execSQL("DELETE FROM `client_approval_table`");
        _db.execSQL("DELETE FROM `spi_printing_table`");
        _db.execSQL("DELETE FROM `mounted_table`");
        _db.execSQL("DELETE FROM `Spi_draft_approval_table`");
        _db.execSQL("DELETE FROM `design_status_table`");
        _db.execSQL("DELETE FROM `mounted_status_table`");
        _db.execSQL("DELETE FROM `Spi_posts_table`");
        _db.execSQL("DELETE FROM `reupload_draft_spi_table`");
        _db.execSQL("DELETE FROM `master_rating_question_table`");
        _db.execSQL("DELETE FROM `region_data`");
        _db.execSQL("DELETE FROM `branch_data`");
        _db.execSQL("DELETE FROM `site_data`");
        _db.execSQL("ALTER TABLE `umbrella_post_table` ADD COLUMN `waterMark` TEXT DEFAULT ' '");
        _db.execSQL("ALTER TABLE `umbrella_post_table` ADD COLUMN `isMark` INTEGER NOT NULL DEFAULT 0");
        //  `waterMark` TEXT DEFAULT ' ', `isMark` INTEGER NOT NULL DEFAULT 0)");
        //_db.execSQL("CREATE TABLE IF NOT EXISTS `umbrella_post_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `postId` INTEGER NOT NULL, `postName` TEXT, `siteId` INTEGER NOT NULL, `adHocPostId` TEXT, `umbrellaId` TEXT, `ImageId` TEXT, `imagePath` TEXT, `canSync` INTEGER NOT NULL, `hasUrl` INTEGER NOT NULL, `isAdhoc` INTEGER NOT NULL, `keyId` INTEGER NOT NULL, `isCompress` INTEGER NOT NULL, `waterMark` TEXT, `isMark` INTEGER NOT NULL)");
    }
}