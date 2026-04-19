package com.sisindia.ai.mtrainer.android.rest;

import com.sisindia.ai.mtrainer.android.features.spi.basicinformation.model.BasicInfoResponse;
import com.sisindia.ai.mtrainer.android.features.spi.basicinformation.model.CompletedPostResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.DesignStatusResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.MountedStatusResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.ReuploadDraftSpiRequest;
import com.sisindia.ai.mtrainer.android.features.spi.model.ReuploadDraftSpiResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostReFetchRequest;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostRequest;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsModel;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.db.UmbrellaPostResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.PostRequest;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.UmbrellaDetailRequest;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.UmbrellaDetailResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaDataResponse;
import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.sync.UmbrellaRequest;
import com.sisindia.ai.mtrainer.android.models.AddRotaMainRequest;
import com.sisindia.ai.mtrainer.android.models.AssessmentModel;
import com.sisindia.ai.mtrainer.android.models.AssessmentResponseModel;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.BranchDetails;
import com.sisindia.ai.mtrainer.android.models.BranchRegionRequest;
import com.sisindia.ai.mtrainer.android.models.BranchRequest;
import com.sisindia.ai.mtrainer.android.models.BranchResponse;
import com.sisindia.ai.mtrainer.android.models.BranchWiseSummaryResponse;
import com.sisindia.ai.mtrainer.android.models.CalendarRequest;
import com.sisindia.ai.mtrainer.android.models.CalendarResponse;
import com.sisindia.ai.mtrainer.android.models.CancelTrainingRequest;
import com.sisindia.ai.mtrainer.android.models.CancelTrainingResponse;
import com.sisindia.ai.mtrainer.android.models.CheckInRequest;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsRequest;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.ClientApprovalRequest;
import com.sisindia.ai.mtrainer.android.models.ClientApprovalResponse;
import com.sisindia.ai.mtrainer.android.models.ContactListRequest;
import com.sisindia.ai.mtrainer.android.models.ContactListResponse;
import com.sisindia.ai.mtrainer.android.models.ConveyanceDailyResponse;
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyRequest;
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyResponse;
import com.sisindia.ai.mtrainer.android.models.ConveyanceTimelineRequest;
import com.sisindia.ai.mtrainer.android.models.ConveyanceTimelineResponse;
import com.sisindia.ai.mtrainer.android.models.CsatOycCountRequest;
import com.sisindia.ai.mtrainer.android.models.CsatOycCountResponse;
import com.sisindia.ai.mtrainer.android.models.DashboardRequest;
import com.sisindia.ai.mtrainer.android.models.DesignSpiRequest;
import com.sisindia.ai.mtrainer.android.models.DesignSpiResponse;
import com.sisindia.ai.mtrainer.android.models.EmployeeReportsRequest;
import com.sisindia.ai.mtrainer.android.models.EmployeeReportsResponsedata;
import com.sisindia.ai.mtrainer.android.models.EmployeeSearchRequest;
import com.sisindia.ai.mtrainer.android.models.EmployeeSearchResponse;
import com.sisindia.ai.mtrainer.android.models.FeedBackOtpRequest;
import com.sisindia.ai.mtrainer.android.models.FeedBackOtpResponse;
import com.sisindia.ai.mtrainer.android.models.FeedBackVerifyOtpRequest;
import com.sisindia.ai.mtrainer.android.models.FeedBackVerifyOtpResponse;
import com.sisindia.ai.mtrainer.android.models.ImageUploadRequest;
import com.sisindia.ai.mtrainer.android.models.ImageUploadResponse;
import com.sisindia.ai.mtrainer.android.models.MyUnitsRequest;
import com.sisindia.ai.mtrainer.android.models.MyUnitsResponse;
import com.sisindia.ai.mtrainer.android.models.RatingMasterRequest;
import com.sisindia.ai.mtrainer.android.models.RatingMasterResponse;
import com.sisindia.ai.mtrainer.android.models.RefreshRequest;
import com.sisindia.ai.mtrainer.android.models.RefreshResponse;
import com.sisindia.ai.mtrainer.android.models.RegionResponse;
import com.sisindia.ai.mtrainer.android.models.RplFormEmployeeResponse;
import com.sisindia.ai.mtrainer.android.models.RplFormEmplyoeeNoRequest;
import com.sisindia.ai.mtrainer.android.models.SasTokenResponse;
import com.sisindia.ai.mtrainer.android.models.SectorRequest;
import com.sisindia.ai.mtrainer.android.models.SectorTypeResponse;
import com.sisindia.ai.mtrainer.android.models.SitePostRequest;
import com.sisindia.ai.mtrainer.android.models.SitePostResponse;
import com.sisindia.ai.mtrainer.android.models.SiteResponse;
import com.sisindia.ai.mtrainer.android.models.SiteResponseModel;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoRequest;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBranchRequest;
import com.sisindia.ai.mtrainer.android.models.SpiBranchResponse;
import com.sisindia.ai.mtrainer.android.models.SpiPrintingRequest;
import com.sisindia.ai.mtrainer.android.models.SpiPrintingResponse;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsRequest;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;
import com.sisindia.ai.mtrainer.android.models.TrainerPerformanceResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCourseUpdateRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingCourseUpdateResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsRequestModel;
import com.sisindia.ai.mtrainer.android.models.UnitAttendanceRequest;
import com.sisindia.ai.mtrainer.android.models.UnitAttendanceResponse;
import com.sisindia.ai.mtrainer.android.models.UnitDetailsRequest;
import com.sisindia.ai.mtrainer.android.models.UnitListResponse;
import com.sisindia.ai.mtrainer.android.models.UnitdetailsData;
import com.sisindia.ai.mtrainer.android.models.UpdateSite;
import com.sisindia.ai.mtrainer.android.models.VanResponse;
import com.sisindia.ai.mtrainer.android.models.VanRunningStatusRequest;
import com.sisindia.ai.mtrainer.android.models.VersionCheckResponse;
import com.sisindia.ai.mtrainer.android.models.assesments.AssignTrainingCourseBody;
import com.sisindia.ai.mtrainer.android.models.assesments.CourseListResponse;
import com.sisindia.ai.mtrainer.android.models.assesments.CourseStatusResponse;
import com.sisindia.ai.mtrainer.android.models.assesments.CourseTypeResponseV2;
import com.sisindia.ai.mtrainer.android.models.assesments.ProgramResponseV2;
import com.sisindia.ai.mtrainer.android.models.assesments.TopicListResponse;
import com.sisindia.ai.mtrainer.android.models.assesments.TopicsStatusResponse;
import com.sisindia.ai.mtrainer.android.models.calender.SendMailBodyMO;
import com.sisindia.ai.mtrainer.android.models.gps.GpsRequest;
import com.sisindia.ai.mtrainer.android.models.gps.GpsResponse;
import com.sisindia.ai.mtrainer.android.models.master.StarProgramMasterBodyMO;
import com.sisindia.ai.mtrainer.android.models.master.StarTrainingMasterResponseMO;
import com.sisindia.ai.mtrainer.android.models.online.TrainingCourseBodyMO;
import com.sisindia.ai.mtrainer.android.models.online.TrainingCoursesResponseMO;
import com.sisindia.ai.mtrainer.android.models.online.TrainingProgramTypeBodyMO;
import com.sisindia.ai.mtrainer.android.models.online.TrainingProgramTypeResponseMO;
import com.sisindia.ai.mtrainer.android.models.online.TrainingTopicResponseMO;
import com.sisindia.ai.mtrainer.android.models.online.TrainingTopicsBodyMO;
import com.sisindia.ai.mtrainer.android.models.previous.PreviousTrainingResponse;
import com.sisindia.ai.mtrainer.android.models.spi.DraftApprovalRequest;
import com.sisindia.ai.mtrainer.android.models.spi.DraftApprovalResponse;
import com.sisindia.ai.mtrainer.android.models.spi.MountedResponse;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface DashBoardApi {

    String TRAINER_PERFORMANCE = "api/TrainingOperation/TraningPerformance";
    String TRAINING_CALENDAR = "api/TrainingOperation/RotaDashboard";//http://50.31.147.142/MtrainerV2AppApi/api/TrainingOperation/TrainingCalendar
    // String ATTENDANCE="api/TrainingOperation/SiteEmployee";
    String MASTER_EMPLOYEE = "api/TrainingOperation/EmployeeDetails";// http://14.140.115.130:8080/MTrainerV2AppAPIVersion1/api/TrainingOperation/SiteEmployee
    String CHOOSE_TOPICS = "api/TrainingOperation/getTopics";
    String CANCEL_TRAINING = "api/TrainingOperation/CancelTraning";
    String CLIENT_RAITING = "api/TrainingOperation/GetFeedbackResponceQuestionList";
    String COURSE_LIST = "api/CourseLMS/GetCourseList";
    String EMPLOYEELIST_BY_TRAINERID = "api/EmployeeData/GetNewEmployeeListByTrainerId";
    String SAS_TOKEN = "api/CourseLMS/GetSASToken";

    String CLIENT_CONTACT = "api/TrainingOperation/getClientContacts";
    String POST_NAME = "api/TrainingOperation/getSitePost";
    //String FEED_BACK_OTP = "api/LoginOperation//GetFeedbackOtp";
    String SIS_FEED_BACK_OTP = "api/LoginOperation/SISGetFeedbackOtp";
    String SIS_FEED_BACK_VERIFY_OTP = "api/LoginOperation/SISGetFeedbackOtpVerify";
    String SYNC_DATA_TO_SERVER = "api/SyncData/AddTrainingResult";
    String POST_IMAGE = "api/TrainingOperation/PostRotaImage";
    String POST_IMAGE1 = "api/UploadImages/UploadFile";
    String COURSE_CONTENT_TRACKER = "api/CourseLMS/SetCourseContentTracker";
    String UPLOAD_IMAGE = "api/TrainingOperation/UpdateRotaImages";
    String UNIT_LIST = "api/SyncData/GetUnit";
    String ADD_ROTA = "api/SyncData/AddRota";
    String DELETE_ROTA = "api/TrainingOperation/DeleteRota";
    String REFRESH = "api/TrainingOperation/GetUpdatedSite";
    String ASSESMENT_QUESTION = "api/TrainingOperation/AssessmentQuestion";
    String VERSION_CHECK = "api/ConfigSetting/getVersionInfo";
    String RPL_FORM = "api/EmployeeData/getEmployeeByRegNo";
    String CHECK_IN = "api/TrainingOperation/Checkin";

    String BRANCHREGION = "api/TrainingOperation/NationalTrainingHead";
    String PREVIOUS_TRAINING = "api/TrainingOperation/TrainingDetails";

    String UNIT_ATTENDANCE = "api/TrainingOperation/SiteEmployee";
    String SEARCH_EMPLOYEE = "api/TrainingOperation/SearchEmployeeDetails";
    String MASTER_UNIT = "api/TrainingOperation/MasterSiteDetails";
    String MY_UNITS = "api/TrainingOperation/GetTrainingSiteDetailsForTab";
    String COMPLETED_CALENDAR_ROTA = "api/TrainingOperation/GetTrainingDetailsForTab";

    String GPS_PING = "api/DutyOnOff/AddDutyOnOff";
    String BRANCH_DATA = "Default/BranchDDL";
    String UNIT_DETAILS_DATA = "Default/UnitDetails";
    String UPDATE_UNIT = "Default/Save";
    String BranchWiseSummary = "Default/GetBranchWiseSummary";
    String UPLOAD_SPI_UMBRELLA_DATA = "api/UmbrellaTracker/SPIPosterData";

    String VANRUNNINGSTATUS = "api/TrainingOperation/VanRunningStatus";
    String SPIBRANCH = "api/SPITracker/Branchddl";
    String CSATOYCCOUNT = "api/SPITracker/CsatOycCount";
    String TABLEDETAILS = "api/SPITracker/TableDetails";
    String BASICINFO = "api/SPITracker/UpdateSPIBasicInfo";
    String REFETCH_SPI_POST = "api/SPITracker/SPIRefetchPostDetails";
    String REFETCH_BASIC_INFO = "api/SPITracker/SPIBasicInfoDetails";
    String COMPLETED_SPI_POST = "api/SPITracker/SPIPostDetails";
    String DRAFT_APPROVAL = "api/SPITracker/SPIDraftApproval";
    String SPI_DESIGN = "api/SPITracker/SPIDesign";
    String CLIENT_APPROVAL = "api/SPITracker/SPIClientApproval";
    String SPI_PRINTING = "api/SPITracker/SPIPrintingStatus";
    String DESIGN_STATUS = "api/SPITracker/SPIPrintingStatus";
    String MOUNTED_STATUS = "api/SPITracker/SPITblMounted";
    String SPI_STATUS = "api/SPITracker/SPIUpdateBackButton";
    String SPI_POSTS = "api/SPITracker/SPIPostInfo";

    String REUPLOAD_DRAFT_SPI = "api/SPITracker/SPIRejectedDraftApproval";
    String UMBRELLA_POST_LIST = "api/UmbrellaTracker/GetPostDetails";
    String COURSE_TOPICS_LIST = "api/CourseLMS/GetCourseContentList";
    String UMBRELLA_DETAILS = "/api/UmbrellaTracker/GetDetails";
    String CONVEYANCE_MONTHLY_DETAILS = "/api/TrainerConveyance/GetConveyanceMonthly";
    String CONVEYANCE_DAILY_DETAILS = "/api/TrainerConveyance/GetConveyanceDaily";
    String CONVEYANCE_TIMELINE_DETAILS = "/api/TrainerConveyance/GetConveyanceTimeLine";
    String SEND_MAIL = "/api/TrainingOperation/SendTrainingMail";
    String SECTOR_TYPE = "/Default/SectorList";
    String GET_COURSE_TYPE_LIST = "api/SyncData/GetCourseTypeList";
    String GET_PROGRAM_LIST = "api/SyncData/GetProgramList";
    String GET_COURSE_LIST = "api/SyncData/GetCourseListByProgramId";
    String GET_COURSE_TOPIC_LIST = "api/SyncData/GetTopicListByCourseId";
    //    String ADD_TRAINING_COURSE_MAPPING = "api/SyncData/AddTrainingCourseMapping";
    String ADD_TRAINING_TOPIC_MAPPING = "api/SyncData/AddTrainingTopicMapping";
    String GET_COURSE_ASSESSMENT_REPORT = "api/SyncData/GetCourseAssessmentReportList";
    String GET_TOPICS_ASSESSMENT_REPORT = "api/SyncData/GetTopicAssessmentReportList";
    String PROGRAM_TYPE_LIST = "api/CourseLMS/GetStarProgramTypeList";
    String COURSE_LIST_V2 = "api/CourseLMS/GetStarCourseList";
    String COURSE_TOPICS_V2 = "api/CourseLMS/GetStarCourseContentList";
    String ONLINE_TRAINING_MASTER_DATA = "api/CourseLMS/GetStarProgramMasterList";

    @Multipart
    @POST(POST_IMAGE1)
    Call<ImageUploadResponse> uploadImages(@Part MultipartBody.Part file, @Query("CompanyId") int companyId);

    @POST(COURSE_CONTENT_TRACKER)
    Single<TrainingCourseUpdateResponse> sendcoursecontenttracker(@Body TrainingCourseUpdateRequest request);

    @POST(SECTOR_TYPE)
    Single<SectorTypeResponse> getSectorList(@Body SectorRequest request);

    @POST(TRAINER_PERFORMANCE)
    Single<TrainerPerformanceResponse> getTrainerPerformance(@Body DashboardRequest request);

    @POST(UMBRELLA_POST_LIST)
    Single<UmbrellaPostResponse> getUmbrellaPostList(@Body PostRequest request);

    @POST(COURSE_TOPICS_LIST)
    Single<TrainingTopicsModel> getTopicslist(@Body TrainingTopicsRequestModel request);

    @GET(SAS_TOKEN)
    Single<SasTokenResponse> getsastoken(@Query("SegmentType") String url);

    @POST(UMBRELLA_DETAILS)
    Single<UmbrellaDetailResponse> getUmbrellaDetails(@Body UmbrellaDetailRequest request);

    @POST(VERSION_CHECK)
    Single<VersionCheckResponse> getAppVersion();

    /*@GET
    Call<ResponseBody> downloadLatestApk(@Url String fileUrl);*/

    @POST(ADD_ROTA)
    Single<TrainingCalendarResponse> requestRota(@Body AddRotaMainRequest request);

    @POST(TRAINING_CALENDAR)
    Single<TrainingCalendarResponse> getTrainingCalendar(@Body DashboardRequest request);

    @POST(MASTER_EMPLOYEE)
    Single<TrainingAttendanceResponse> getAttendance(@Body TrainingAttendanceRequest request);

    @POST(CHOOSE_TOPICS)
    Single<ChooseTopicsResponse> getChooseTopics(@Body ChooseTopicsRequest request);

    @POST(CANCEL_TRAINING)
    Single<CancelTrainingResponse> getCancelTraining(@Body CancelTrainingRequest request);

    @POST(UPLOAD_SPI_UMBRELLA_DATA)
    Single<UmbrellaDataResponse> uploadspiUmbrelladata(@Body UmbrellaRequest request);

    @POST(COURSE_LIST)
    Single<TrainingCoursesResponse> getCourseslist(@Body TrainingCoursesRequest request);

    @POST(EMPLOYEELIST_BY_TRAINERID)
    Single<EmployeeReportsResponsedata> getEmployeereport(@Body EmployeeReportsRequest request);

    @POST(CLIENT_RAITING)
    Single<RatingMasterResponse> getRattingQuestionList(@Body RatingMasterRequest request);

    @POST(CLIENT_CONTACT)
    Single<ContactListResponse> getClientContactList(@Body ContactListRequest request);

    @POST(POST_NAME)
    Single<SitePostResponse> getSitePostName(@Body SitePostRequest request);

    @POST(SIS_FEED_BACK_OTP)
    Single<FeedBackOtpResponse> getFeedBackOtp(@Body FeedBackOtpRequest request);


    @POST(SIS_FEED_BACK_VERIFY_OTP)
    Single<FeedBackVerifyOtpResponse> getFeedBackverifyotp(@Body FeedBackVerifyOtpRequest request);

    @GET(UNIT_LIST)
    Single<UnitListResponse> getUnitList(@Query("CompanyId") String companyId);

    @POST(REFRESH)
    Single<RefreshResponse> refresh(@Body RefreshRequest refreshRequest);

    @POST(CONVEYANCE_MONTHLY_DETAILS)
    Single<ConveyanceMonthlyResponse> getconveyancemonthdata(@Body ConveyanceMonthlyRequest refreshRequest);

    @POST(CONVEYANCE_DAILY_DETAILS)
    Single<ConveyanceDailyResponse> getconveyancedailydata(@Body ConveyanceMonthlyRequest refreshRequest);

    @POST(CONVEYANCE_TIMELINE_DETAILS)
    Single<ConveyanceTimelineResponse> getconveyancetimelinedata(@Body ConveyanceTimelineRequest refreshRequest);

    @POST(DELETE_ROTA)
    Single<BaseApiResponse> deleteRota(@Query("RotaId") int rotaId);

    /*@POST(SYNC_DATA_TO_SERVER)
    Single<SyncApiResponse> SubmitTrainingReport(@Body TrainingFinalSubmitRequest request);

    @POST(POST_IMAGE)
    Single<ImageUploadResponse> postImages(@Part MultipartBody.Part file);*/

    @POST(UPLOAD_IMAGE)
    Single<ImageUploadResponse> uploadImages(@Body ImageUploadRequest request);

    @POST(ASSESMENT_QUESTION)
    Single<AssessmentResponseModel> getAssessmentQuestion(@Body AssessmentModel assessmentModel);


    @POST(CHECK_IN)
    Single<CancelTrainingResponse> getCheckIn(@Body CheckInRequest request);

    @Headers("Content-Type: application/json")
    @POST(RPL_FORM)
    Single<RplFormEmployeeResponse> getEmployeeRegNo(@Body RplFormEmplyoeeNoRequest employeeNo);

    @POST(BRANCHREGION)
    Single<RegionResponse> getRegion(@Body BranchRegionRequest branchRegionRequest);

    @POST(BRANCHREGION)
    Single<BranchResponse> getBranch(@Body BranchRegionRequest branchRegionRequest);

    @POST(BRANCH_DATA)
    Single<BranchDetails> getBranches(@Body BranchRequest branchRegionRequest);

    @POST(UNIT_DETAILS_DATA)
    Single<UnitdetailsData> getunitdetails(@Body UnitDetailsRequest branchRegionRequest);

    @POST(UPDATE_UNIT)
    Single<SiteResponseModel> updateunit(@Body UpdateSite branchRegionRequest);

    @POST(BranchWiseSummary)
    Single<BranchWiseSummaryResponse> getbranchsummarydata(@Body UnitDetailsRequest branchRegionRequest);

    @POST(BRANCHREGION)
    Single<SiteResponse> getSite(@Body BranchRegionRequest branchRegionRequest);

    @POST(PREVIOUS_TRAINING)
    Single<PreviousTrainingResponse> getPreviousTraining(@Body DashboardRequest request);

    @POST(UNIT_ATTENDANCE)
    Single<UnitAttendanceResponse> getUnitAttendance(@Body UnitAttendanceRequest request);

    @POST(SEARCH_EMPLOYEE)
    Single<EmployeeSearchResponse> searchEmployee(@Body EmployeeSearchRequest request);

    @POST(MASTER_UNIT)
    Single<UnitListResponse> getMasterUnitList(@Body TrainingAttendanceRequest request);

    @POST(MY_UNITS)
    Single<MyUnitsResponse> getMyUnitsDetails(@Body MyUnitsRequest request);

    @POST(COMPLETED_CALENDAR_ROTA)
    Single<CalendarResponse> getCalendarRotaDetails(@Body CalendarRequest request);

    @POST(GPS_PING)
    Single<GpsResponse> sendGpsPing(@Body GpsRequest request);

    @POST(VANRUNNINGSTATUS)
    Single<VanResponse> getVanRunningStatus(@Body VanRunningStatusRequest request);


    @POST(SPIBRANCH)
    Single<SpiBranchResponse> getSpiBranch(@Body SpiBranchRequest SpiBanchRequest);

    @POST(CSATOYCCOUNT)
    Single<CsatOycCountResponse> getCastOycCount(@Body CsatOycCountRequest csatOycCountRequest);

    @POST(TABLEDETAILS)
    Single<SpiTableDetailsResponse> getSpiTableDetails(@Body SpiTableDetailsRequest csatOycCountRequest);

    @POST(BASICINFO)
    Single<SpiBasicInfoResponse> getSpiBasicInfo(@Body SpiBasicInfoRequest csatOycCountRequest);

    @POST(REFETCH_SPI_POST)
    Single<SpiBasicInfoResponse> reFetchPostInfo(@Body SpiPostReFetchRequest postReFetchRequest);

    @POST(REFETCH_BASIC_INFO)
    Single<BasicInfoResponse> reFetchBasicInfo(@Body SpiPostReFetchRequest postReFetchRequest);

    @POST(COMPLETED_SPI_POST)
    Single<CompletedPostResponse> completedPostInfo(@Body SpiPostReFetchRequest postReFetchRequest);

    @POST(SPI_DESIGN)
    Single<DesignSpiResponse> getSpiDesign(@Body DesignSpiRequest design);

    @POST(CLIENT_APPROVAL)
    Single<ClientApprovalResponse> getClientApproval(@Body ClientApprovalRequest clientApprovalRequest);

    @POST(SPI_PRINTING)
    Single<SpiPrintingResponse> getSPiPrinting(@Body SpiPrintingRequest printingRequest);

    @POST(DRAFT_APPROVAL)
    Single<DraftApprovalResponse> getDraftApproval(@Body DraftApprovalRequest draftApprovalRequest);

    @POST(SPI_DESIGN)
    Single<MountedResponse> getMounted(@Body DesignSpiRequest design);

    @POST(DESIGN_STATUS)
    Single<DesignStatusResponse> getDesignStatus(@Body SpiPrintingRequest printingRequest);

    @POST(MOUNTED_STATUS)
    Single<MountedStatusResponse> getMountedStatus(@Body SpiPrintingRequest printingRequest);

    @POST(SPI_STATUS)
    Single<BaseApiResponse> getSpiStatus(@Body SpiStatusRequest spiStatusRequest);

    @POST(SPI_POSTS)
    Single<SpiPostResponse> getSpiPosts(@Body SpiPostRequest spiPostRequest);

    @POST(REUPLOAD_DRAFT_SPI)
    Single<ReuploadDraftSpiResponse> getReuploadDraftSpi(@Body ReuploadDraftSpiRequest reuploadDraftSpiRequest);

    @POST(REUPLOAD_DRAFT_SPI)
    Single<SpiBasicInfoResponse> getReuploadToBasic(@Body ReuploadDraftSpiRequest reuploadDraftSpiRequest);

    @POST(SEND_MAIL)
    Single<BaseApiResponse> sendMail(@Body SendMailBodyMO body);

    @GET(GET_COURSE_TYPE_LIST)
    Single<CourseTypeResponseV2> getCourseTypeList();

    @GET(GET_PROGRAM_LIST)
    Single<ProgramResponseV2> getProgramList(@Query("CourseTypeId") int courseTypeId,
                                             @Query("CompanyId") int companyId);

    @GET(GET_COURSE_LIST)
    Single<CourseListResponse> getCourseList(@Query("CourseTypeId") int courseTypeId,
                                             @Query("CompanyId") int companyId,
                                             @Query("ProgramId") int programId);

    @GET(GET_COURSE_TOPIC_LIST)
    Single<TopicListResponse> getTopicsList(@Query("CourseId") int courseId,
                                            @Query("CompanyId") int companyId);

    @POST(ADD_TRAINING_TOPIC_MAPPING)
    Single<BaseApiResponse> mapTrainingCourseTopic(@Body AssignTrainingCourseBody body);

    @GET(GET_COURSE_ASSESSMENT_REPORT)
    Single<CourseStatusResponse> getCourseStatus(@Query("TrainingId") int trainingId);

    @GET(GET_TOPICS_ASSESSMENT_REPORT)
    Single<TopicsStatusResponse> getTopicsStatus(@Query("TrainingId") int trainingId);

    @POST(PROGRAM_TYPE_LIST)
    Single<TrainingProgramTypeResponseMO> getStarProgramTypeList(@Body TrainingProgramTypeBodyMO body);

    @POST(COURSE_LIST_V2)
    Single<TrainingCoursesResponseMO> getStarCourseList(@Body TrainingCourseBodyMO request);

    @POST(COURSE_TOPICS_V2)
    Single<TrainingTopicResponseMO> getCourseContentList(@Body TrainingTopicsBodyMO request);

    @POST(ONLINE_TRAINING_MASTER_DATA)
    Single<StarTrainingMasterResponseMO> getTrainingMasterData(@Body StarProgramMasterBodyMO request);

}
