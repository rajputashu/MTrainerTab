package com.sisindia.ai.mtrainer.android.features.assessment;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.view.View;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.features.takeassessment.AssessmentItemListener;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AssessmentReportViewModel extends MTrainerViewModel {

    public AssessmentItemListener listener = (item, position) -> {
    };

    public AssessmentReportAdapter reportAdapter = new AssessmentReportAdapter();

    @Inject
    DashBoardApi dashBoardApi;

    @Inject
    public AssessmentReportViewModel(Application app) {
        super(app);
    }

    // -------------------------------------------------------------------------
    // Fetch Course Assessment Report
    // -------------------------------------------------------------------------
    public void getCourseAssessmentReportList() {
        setIsLoading(true);

        int trainingId = Prefs.getInt(PrefsConstants.ROTA_ID);

        addDisposable(
                dashBoardApi.getTopicsStatus(trainingId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {

                            setIsLoading(false);

                            if (response.statusCode == SUCCESS_RESPONSE) {

                                if (response.attendanceList == null || response.attendanceList.isEmpty()) {
                                    showToast("No data found");
                                } else {
                                    onAssessmentListSuccess(response.attendanceList);
                                }

                            } else {
                                showToast(response.statusMessage);
                            }

                        }, error -> {
                            setIsLoading(false);
                            onApiError(error);
                        })
        );
    }

    // -------------------------------------------------------------------------
    // On Assessment Success - Add header letters + set to adapter
    // -------------------------------------------------------------------------
    private void onAssessmentListSuccess(List<AttendanceEntity> attendanceEntityList) {
        List<AttendanceEntity> modifiedList = getListWithLetter(attendanceEntityList);
        reportAdapter.clearAndSetItems(modifiedList);
    }

    // -------------------------------------------------------------------------
    // Add alphabetical headers to list
    // -------------------------------------------------------------------------
    private List<AttendanceEntity> getListWithLetter(List<AttendanceEntity> attendanceEntities) {

        List<AttendanceEntity> modifiedList = new ArrayList<>();

        // Sort safely by employeeName OR empName
        List<AttendanceEntity> sortedList = new ArrayList<>(attendanceEntities);

        Collections.sort(sortedList, new Comparator<AttendanceEntity>() {
            @Override
            public int compare(AttendanceEntity u1, AttendanceEntity u2) {

                String name1 = (u1.employeeName != null && !u1.employeeName.isEmpty())
                        ? u1.employeeName
                        : u1.empName;

                String name2 = (u2.employeeName != null && !u2.employeeName.isEmpty())
                        ? u2.employeeName
                        : u2.empName;

                if (name1 == null) name1 = "";
                if (name2 == null) name2 = "";

                return name1.compareToIgnoreCase(name2);
            }
        });

        String lastHeader = "";

        for (AttendanceEntity user : sortedList) {

            String name;

            if (user.employeeName != null && !user.employeeName.isEmpty()) {
                name = user.employeeName;
            } else if (user.empName != null && !user.empName.isEmpty()) {
                name = user.empName;
            } else {
                name = "#";
            }

            String header = String.valueOf(name.toUpperCase().charAt(0));

            if (!header.equals(lastHeader)) {
                lastHeader = header;

                AttendanceEntity headerEntity = new AttendanceEntity();
                headerEntity.isselected = true;
                headerEntity.header = header;

                modifiedList.add(headerEntity);
            }

            modifiedList.add(user);
        }

        return modifiedList;
    }

    // -------------------------------------------------------------------------
    // Button/UI click handler
    // -------------------------------------------------------------------------
    public void onViewClick(View view) {
        getCourseAssessmentReportList();
    }
}
