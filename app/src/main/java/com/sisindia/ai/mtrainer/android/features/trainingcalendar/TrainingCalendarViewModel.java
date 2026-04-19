package com.sisindia.ai.mtrainer.android.features.trainingcalendar;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.addrota.AddTaskViewListeners;
import com.sisindia.ai.mtrainer.android.models.CalendarRequest;
import com.sisindia.ai.mtrainer.android.models.CalendarResponse;
import com.sisindia.ai.mtrainer.android.models.calender.SendMailBodyMO;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TrainingCalendarViewModel extends MTrainerViewModel {

    public TrainingCalendarRecyclerAdapter trainingCalendarAdapter;
    public ObservableField<LocalDate> startDate = new ObservableField<>(LocalDate.now());
    public ObservableField<LocalDate> endDate = new ObservableField<>(LocalDate.now());
    //    public ObservableField<String> noCompleteRota = new ObservableField<>();
    // public ObservableField<LocalTime> taskStartTime = new ObservableField<>(LocalTime.now());
    // public ObservableField<LocalTime> taskEndTime = new ObservableField<>(LocalTime.now().plusMinutes(15));
    @Inject
    DashBoardApi dashBoardApi;
    String startdate, enddate;
    MtrainerDataBase dataBase;
    public ObservableBoolean isDataAvailable = new ObservableBoolean(false);
    private List<CalendarResponse.CalendarRotaResponse> localCalenderResponse;
    private int selectedMailPosition = -1;

    @Inject
    public TrainingCalendarViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
        trainingCalendarAdapter = new TrainingCalendarRecyclerAdapter(dataBase);

        /*dataBase.getCalendarRotaCompleted().flushTrainingCalendar()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();*/
    }

    public AddTaskViewListeners viewListeners = new AddTaskViewListeners() {

        @Override
        public void onDateChanged(int viewId, LocalDate date) {
            switch (viewId) {
                case R.id.add_rota_start_date:
                    startDate.set(date);
                    startdate = String.valueOf(startDate);
                    break;

                case R.id.add_rota_end_date:
                    endDate.set(date);
                    enddate = String.valueOf(endDate);
                    break;
            }
        }

        @Override
        public void onStartTimeSelected(LocalTime time) {
        }

        @Override
        public void onEndTimeSelected(LocalTime time) {
        }

        @Override
        public void onItemSpinnerSelected(int viewId, int position) {

        }

        @Override
        public void onSendEmailClicked(int position) {

            if (!localCalenderResponse.isEmpty()) {
                String mailAction = localCalenderResponse.get(position).mailSendAction;
                if (mailAction.equalsIgnoreCase("NA"))
                    showToast("Can't send mail when N.A.");
                else if (mailAction.equalsIgnoreCase("MailSent"))
                    showToast("Mail already sent");
                else {
                    selectedMailPosition = position;
                    message.what = NavigationConstants.OPEN_SEND_MAIL_DIALOG;
                    liveData.postValue(message);
                }
            }
        }
    };

    /*void refreshCalendarView(List<CalendarResponse.CalendarRotaResponse> data) {
        // trainingCalendarAdapter.clear();
        trainingCalendarAdapter.clearAndSetItems(data);
        trainingCalendarAdapter.notifyDataSetChanged();
    }*/

    public void getRotaApply(View view) {
        //Commenting below flushTrainingCalendar as it is not needed
        /*dataBase.getCalendarRotaCompleted().flushTrainingCalendar()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();*/

//        trainingCalendarAdapter.clear();
//        trainingCalendarAdapter.notifyDataSetChanged();
//        List<CalendarResponse.CalendarRotaResponse> calendarRotaResponses = new ArrayList<>();

        /*if (calendarRotaResponses.size() != 0) {
            addDisposable(dataBase.getCalendarRotaCompleted()
                    .flushTrainingCalendar()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
            getTrainingAttendance();
        } else {
            getTrainingAttendance();
        }*/

        // Removed above condition as it will never satisfy {Hence removed if condition}
        getTrainingAttendance();
    }

    void getTrainingAttendance() {
        setIsLoading(true);
        String empId = (Prefs.getString(PrefsConstants.EMPLOYEE_ID));
        LocalDate sDate = startDate.get();
        LocalDate eDate = endDate.get();
        String format = "yyyy-MM-dd";
        CalendarRequest request = new CalendarRequest(sDate.format(DateTimeFormatter.ofPattern(format)), eDate.format(DateTimeFormatter.ofPattern(format)), empId);
        addDisposable(dashBoardApi.getCalendarRotaDetails(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCalendarRotaSuccess, this::onApiError));
    }

    private void onCalendarRotaSuccess(CalendarResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

            /*addDisposable(dataBase.getCalendarRotaCompleted()
                    .insertCalendarRota(response.calendarRotaResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());*/
            localCalenderResponse = response.calendarRotaResponses;
            if (response.calendarRotaResponses.isEmpty()) {
                showToast("Rota not available");
                isDataAvailable.set(false);
            } else {
                isDataAvailable.set(true);
                showToast("Updating list");
                trainingCalendarAdapter.clearAndSetItems(response.calendarRotaResponses);
            }
        }
    }

    public void sendEmailViaAPI() {
        if (selectedMailPosition > -1) {
            int trainingId = localCalenderResponse.get(selectedMailPosition).trainingId;
            addDisposable(dashBoardApi.sendMail(new SendMailBodyMO(trainingId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.statusCode == SUCCESS_RESPONSE) {
                            showToast("Mail sent successfully");
                            trainingCalendarAdapter.getItem(selectedMailPosition).mailSendAction = "MailSent";
                            trainingCalendarAdapter.notifyItemChanged(selectedMailPosition);
                        }
                    }, this::onApiError));
        }
    }

    /*public LiveData<List<CalendarResponse.CalendarRotaResponse>> getCalendarRota() {
        return dataBase.getCalendarRotaCompleted().getCalendarList();
    }*/
}
