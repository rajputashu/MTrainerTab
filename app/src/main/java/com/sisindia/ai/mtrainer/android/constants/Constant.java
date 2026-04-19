package com.sisindia.ai.mtrainer.android.constants;

import com.sisindia.ai.mtrainer.android.BuildConfig;

public interface Constant {

    //    public static final int NOTIFICATION_ID = 100;
    //    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    //    public static final String PUSH_NOTIFICATION = "pushNotification";
    //    The account name : If changed, change in stings.xml also
    String ACCOUNT = "MTRAINER2.0";

    // An account type, in the form of a domain name: If changed, change in stings.xml also
    String ACCOUNT_TYPE = "com.sisindia.ai.mtrainer.android";

    // if changed, change in stings.xml also
    String AUTHORITY = "com.sisindia.ai.mtrainer.android.android.syncadpter.provider";

    //--------------- COMMON CONSTANTS USED IN APP ------------------//
    public static String PORT = "7082";
    public static String IP_ADDRESS = "127.1.1.1";
//    String META_DATA = "meta_data";
//    int IMAGE_HEIGHT = 960;
//    int IMAGE_WIDTH = 540;

    String EXTRA_LINK_URL = "link_url";
    String LEARNING_APP_PACKAGE_NAME = "excel.sis_mlearn";

    // String BASE_REPORT_PAGE_URL = "http://50.31.147.142/MtrainerV2AppApi/TrainingReport?TrainerId=";
    //String BASE_REPORT_PAGE_TESTING_URL = "http://50.31.147.142/MtrainerV2AppApi/TrainingReport?TrainerId=";
    // String BASE_REPORT_PAGE_URL = BuildConfig.MTRAINER_HOST + "TrainingReport/Index?TrainerId=";
    // String BASE_REPORT_PAGE_URL = "http://14.140.115.130:8080/MtrainerV2AppApi/TrainingReport/Index?TrainerId=";
    String BASE_REPORT_PAGE_URL = BuildConfig.MTRAINER_HOST + "TrainingReport/Index?TrainerId=";
    String BASE_RAISING_PAGE_URL = BuildConfig.MTRAINER_HOST + "NewRaisingUnitDetails/Index?TrainerId=";
    String WEB_VIEW_SIS_REPORT_URL = "https://mtrainer2-uat.azurewebsites.net/SISReport?TrainerId=";
    int FOREGROUND_SERVICE_ID = 1001;
    int OJT = 3;
    int CLASSROOM = 4;
    public String WEB_VIEW_URL_KEY = "WebViewUrl";
    String SELECTED_PROGRAM_ID = "SelectedProgramId";
    String SELECTED_COURSE_TYPE_ID = "SelectedCourseTypeId";
    String SELECTED_TRAINING_TOPICS_BODY_MO = "TrainingTopicsBodyMo";

}
