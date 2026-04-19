package com.sisindia.ai.mtrainer.android.features.clientreport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityClientReportBinding;
import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportCc;
import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportTo;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.TrainingAttendanceActivity;
import com.sisindia.ai.mtrainer.android.models.Report.ReportDbCcResponse;
import com.sisindia.ai.mtrainer.android.models.Report.ReportDbResponse;
import com.sisindia.ai.mtrainer.android.utils.StringUtils;
import com.sisindia.ai.mtrainer.android.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ClientReportActivity extends MTrainerBaseActivity {
    private ClientReportViewModel viewModel;
    private ActivityClientReportBinding binding;
    private int viewCounter;
    private HashSet<Short> incorrectEmailList = new HashSet<>();

    public static Intent newIntent(Activity activity) {
        return new Intent(activity, TrainingAttendanceActivity.class);
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewState() {
       /* liveTimerEvent.observe(this, message -> {
            switch (message.what) {
                case REVIEW_INFORMATION_TIME_SPENT:
                    bindReviewInformationTime(message.arg1);
                    break;
            }
        });*/

        liveData.observe(this, message -> {
            switch (message.what) {
                case NavigationConstants.UPDATE_CLIENT_REPORT_VIEW:
                    updateFab();
                    break;
                case NavigationConstants.CLOSE_CLIENT_REPORT_VIEW:
                    Intent data = new Intent();
                    Prefs.putBooleanOnMainThread(PrefsConstants.CAN_SHOW, viewModel.canShow);
                    data.putExtra("CAN_SHOW", viewModel.canShow);
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                case NavigationConstants.SAVE_CLIENT_REPORT:
                    saveFinalData();
                    break;
                case NavigationConstants.INFLATE_CC_CLIENT_REPORT_VIEW:
                    inflateCcView((List<ReportDbCcResponse>) message.obj);
                    break;
                case NavigationConstants.INFLATE_TO_CLIENT_REPORT_VIEW:
                    inflateToView((List<ReportDbResponse>) message.obj);
                    break;
            }
        });

    }

    private void bindReviewInformationTime(int seconds) {
        binding.includeTimeSpent.tvTimeSpent.setText(TimeUtils.convertIntSecondsToHHMMSS(seconds));
    }

    @Override
    protected void onCreated() {
        setupToolBarForBackArrow(binding.tbTakeAttendance);
        viewModel.getContactMaster();
        binding.includeTimeSpent.tvTimeSpent.setText(Prefs.getString(PrefsConstants.STARTED_TIME));

        binding.flButtonTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewModel.isMultipleSession.get()) {
                    viewModel.onDataSaved();
                    return;
                }

                if (incorrectEmailList.size() > 0) {
                    //showToast("Please Select Client");
                } else {
                    viewModel.SaveReportData();
                }
            }
        });

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.add.setVisibility(View.GONE);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.to_item, null);
                viewCounter++;
                view1.setId(viewCounter);
                AppCompatEditText editText = view1.findViewById(R.id.client_name);
                ImageView image = view1.findViewById(R.id.image);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (binding.linTomails.getChildCount() != 1) {
                            incorrectEmailList.remove((short) view1.getId());
                            binding.linTomails.removeView(view1);
                            updateFab();
                        }
                    }
                });

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable != null && StringUtils.isEmailValid(editable.toString())) {
                            binding.add.setVisibility(View.VISIBLE);
                            editText.setTag(editable.toString());
                            incorrectEmailList.remove((short) view1.getId());
                            //editText.setError(null);
                        } else {
                            incorrectEmailList.add((short) view1.getId());
                            binding.add.setVisibility(View.GONE);
                            //editText.setError("Please Enter Valid Email");
                        }
                        updateFab();
                    }
                });
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus && incorrectEmailList.contains((short) view1.getId()))
                            editText.setError("Please Enter Valid Email");
                    }
                });

                binding.linTomails.addView(view1);
            }
        });

        binding.addCc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.addCc.setVisibility(View.GONE);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.to_item, null);
                viewCounter++;
                view1.setId(viewCounter);
                AppCompatEditText editText = view1.findViewById(R.id.client_name);
                ImageView image = view1.findViewById(R.id.image);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (binding.linCcmails.getChildCount() != 1) {
                            incorrectEmailList.remove((short) view1.getId());
                            binding.linCcmails.removeView(view1);
                            updateFab();
                        }
                    }
                });

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable != null && StringUtils.isEmailValid(editable.toString())) {
                            binding.addCc.setVisibility(View.VISIBLE);
                            editText.setTag(editable.toString());
                            incorrectEmailList.remove((short) view1.getId());
                            //editText.setError(null);
                        } else {
                            incorrectEmailList.add((short) view1.getId());
                            binding.addCc.setVisibility(View.GONE);
                            //editText.setError("Please Enter Valid Email");
                        }
                        updateFab();
                    }
                });

                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus && incorrectEmailList.contains((short) view1.getId()))
                            editText.setError("Please Enter Valid Email");
                    }
                });
                binding.linCcmails.addView(view1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewCounter = Prefs.getInt(PrefsConstants.VIEW_COUNT, 0);
        viewModel.getClientSavedData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_take_assessment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (ClientReportViewModel) getAndroidViewModel(ClientReportViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_client_report;
    }

    private void updateFab() {
        if (viewModel.isMultipleSession.get()) {
            binding.flButtonTraining.setAlpha(1f);
            return;
        }
        if (incorrectEmailList.size() > 0) {
            binding.flButtonTraining.setAlpha(0.3f);
        } else {
            binding.flButtonTraining.setAlpha(1f);
        }
    }

    private void saveFinalData() {
        List<SavedClientReportTo> savedReportTo = getTomails();
        List<SavedClientReportCc> reportCc = getCcmails();
        viewModel.emailFinalSaveList(savedReportTo, reportCc);
    }


    private ArrayList<SavedClientReportTo> getTomails() {

        ArrayList<SavedClientReportTo> tomails = new ArrayList<>();
        for (int i = 0; i < binding.linTomails.getChildCount(); i++) {

            if (binding.linTomails.getChildAt(i) instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) binding.linTomails.getChildAt(i);
                for (int j = 0; j < layout.getChildCount(); j++) {
                    if (layout.getChildAt(j) instanceof AppCompatEditText) {

                        EditText editText = (EditText) layout.getChildAt(j);
                        if (editText.getTag() != null) {

                            SavedClientReportTo savedClientReportTo = new SavedClientReportTo();
                            savedClientReportTo.id = layout.getId() + "_" + Prefs.getInt(PrefsConstants.ROTA_ID);
                            savedClientReportTo.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
                            savedClientReportTo.email = editText.getTag().toString();
                            tomails.add(savedClientReportTo);
                            Log.d("toMails", editText.getTag().toString());
                        }
                    }
                }
            }

        }
        return tomails;
    }

    private ArrayList<SavedClientReportCc> getCcmails() {

        ArrayList<SavedClientReportCc> tomails = new ArrayList<>();
        for (int i = 0; i < binding.linCcmails.getChildCount(); i++) {

            if (binding.linCcmails.getChildAt(i) instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) binding.linCcmails.getChildAt(i);
                for (int j = 0; j < layout.getChildCount(); j++) {
                    if (layout.getChildAt(j) instanceof AppCompatEditText) {
                        EditText editText = (EditText) layout.getChildAt(j);
                        if (editText.getTag() != null) {
                            SavedClientReportCc clientReportCc = new SavedClientReportCc();
                            clientReportCc.id = layout.getId() + "_" + Prefs.getInt(PrefsConstants.ROTA_ID);
                            clientReportCc.cc = editText.getTag().toString();
                            clientReportCc.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
                            tomails.add(clientReportCc);
                            Log.d("ccMails", editText.getTag().toString());
                        }
                    }
                }
            }

        }
        return tomails;
    }


    private void inflateCcView(List<ReportDbCcResponse> dbResponses) {
        if (!dbResponses.isEmpty()) {
            for (ReportDbCcResponse response : dbResponses) {
                binding.addCc.setVisibility(View.GONE);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.to_item, null);
                view1.setId(getSplitId(response.id));
                AppCompatEditText editText = view1.findViewById(R.id.client_name);
                editText.setText(response.cc);
                editText.setTag(response.cc);
                ImageView image = view1.findViewById(R.id.image);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (binding.linCcmails.getChildCount() != 1) {
                            incorrectEmailList.remove((short) view1.getId());
                            String id = view1.getId() + "_" + Prefs.getInt(PrefsConstants.ROTA_ID);
                            viewModel.removeCc(id);
                            Log.v("Id", " CC : " + id);
                            binding.linCcmails.removeView(view1);
                            updateFab();
                        }
                    }
                });

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable != null && StringUtils.isEmailValid(editable.toString())) {
                            binding.addCc.setVisibility(View.VISIBLE);
                            editText.setTag(editable.toString());
                            incorrectEmailList.remove((short) view1.getId());
                            //editText.setError(null);
                        } else {
                            incorrectEmailList.add((short) view1.getId());
                            binding.addCc.setVisibility(View.GONE);
                            //editText.setError("Please Enter Valid Email");
                        }
                        updateFab();
                    }
                });
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus && incorrectEmailList.contains((short) view1.getId()))
                            editText.setError("Please Enter Valid Email");
                    }
                });
                binding.linCcmails.addView(view1);
            }
            binding.addCc.performClick();
        } else {
            binding.addCc.performClick();
            viewModel.setIsLoading(false);
        }
    }

    private void inflateToView(List<ReportDbResponse> dbResponses) {
        if (!dbResponses.isEmpty()) {
            for (ReportDbResponse response : dbResponses) {
                binding.add.setVisibility(View.GONE);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.to_item, null);
                view1.setId(getSplitId(response.id));

                AppCompatEditText editText = view1.findViewById(R.id.client_name);
                editText.setText(response.email);
                editText.setTag(response.email);
                ImageView image = view1.findViewById(R.id.image);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (binding.linTomails.getChildCount() != 1) {
                            incorrectEmailList.remove((short) view1.getId());
                            String id = view1.getId() + "_" + Prefs.getInt(PrefsConstants.ROTA_ID);
                            viewModel.removeTo(id);
                            Log.v("Id", " TO : " + id);
                            binding.linTomails.removeView(view1);
                            updateFab();
                        }
                    }
                });
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable != null && StringUtils.isEmailValid(editable.toString())) {
                            Log.v("Email", "" + editable.toString());
                            binding.add.setVisibility(View.VISIBLE);
                            editText.setTag(editable.toString().trim());
                            incorrectEmailList.remove((short) view1.getId());
                            //editText.setError(null);
                        } else {
                            incorrectEmailList.add((short) view1.getId());
                            binding.add.setVisibility(View.GONE);
                            //editText.setError("Please Enter Valid Email");
                        }
                        updateFab();

                    }
                });

                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus && incorrectEmailList.contains((short) view1.getId()))
                            editText.setError("Please Enter Valid Email");
                    }
                });

                binding.linTomails.addView(view1);
            }
            binding.add.performClick();
        } else
            binding.add.performClick();
    }

    private int getSplitId(String id) {
        String[] ids = id.split("_");
        return Integer.parseInt(ids[0].trim());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Prefs.putIntOnMainThread(PrefsConstants.VIEW_COUNT, viewCounter);
    }
}
