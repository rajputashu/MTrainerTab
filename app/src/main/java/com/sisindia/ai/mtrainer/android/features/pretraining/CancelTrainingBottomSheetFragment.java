package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.base.MTrainerBottomSheetDialogFragment;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityPreTrainingReviewBinding;
import com.sisindia.ai.mtrainer.android.databinding.BottomSheetCancelTrainingBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static androidx.camera.core.CameraX.getContext;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.IRQ_PRE_TRAINING_REVIEW;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.TRAINING_CANCEL;

public class CancelTrainingBottomSheetFragment extends MTrainerBaseActivity {
    private BottomSheetCancelTrainingBinding binding;
    private PreTrainingReviewViewModel viewModel;
    String val="";
    MtrainerDataBase dataBase;



    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, CancelTrainingBottomSheetFragment.class);
        //dataBase = MtrainerDataBase.getDatabase(activity);
        return intent;
    }

    @Override
    protected void extractBundle() {




    }



    @Override
    protected void initViewModel() {
        viewModel= (PreTrainingReviewViewModel) getAndroidViewModel(PreTrainingReviewViewModel.class);

    }

  /*  @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding= (BottomSheetCancelTrainingBinding) bindFragmentView(getLayoutResource(),inflater,container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }
*/
    @Override
    protected void initViewState() {

    }

   /* @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }*/


    @Override
    protected void onCreated() {

        binding.rgIssue.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.rb_1){
                    val="Mobilization Problem";
                    binding.otherIsue.setVisibility(View.GONE);
                }
                else if (checkedId==R.id.rb_2){
                    val="Training Officer on leave";
                    binding.otherIsue.setVisibility(View.GONE);


                }else if (checkedId==R.id.rb_3){
                    val="Driver on leave";
                    binding.otherIsue.setVisibility(View.GONE);


                }else if (checkedId==R.id.rb_4){
                    val="Weather Problem";
                    binding.otherIsue.setVisibility(View.GONE);


                }else if (checkedId==R.id.rb_5){
                    val="Van Issue";
                    binding.otherIsue.setVisibility(View.GONE);


                }else if (checkedId==R.id.rb_6){
                    val="Distance Issues";
                    binding.otherIsue.setVisibility(View.GONE);


                }else if (checkedId==R.id.rb_7){
                    val="Client Permission";
                    binding.otherIsue.setVisibility(View.GONE);


                }else if (checkedId==R.id.rb_8){
                    val ="Other Issue if any";
                    val="";
                    binding.otherIsue.setVisibility(View.VISIBLE);


                }
            }
        });


        binding.okOtherissue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(binding.rgIssue.getCheckedRadioButtonId()!=-1) {
                    String otherReason = "";
                    Intent i = new Intent();

                    i.putExtra("Value", val);
                    if(binding.rgIssue.getCheckedRadioButtonId() == R.id.rb_8) {
                        if(binding.otherIsue.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(),"Please Type Reason", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            otherReason = binding.otherIsue.getText().toString();
                        }
                    }
                    i.putExtra("Val", otherReason);
                    Prefs.putString(PrefsConstants.CANCEL_TRAINING,val);
                    setResult(RESULT_OK, i);

                    MtrainerDataBase.getDatabase(CancelTrainingBottomSheetFragment.this).getTrainingCalenderDao().updateInProgressStatus(Prefs.getInt(PrefsConstants.IS_PROGRESS_ROTAID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() ->{
                                Prefs.edit().remove(PrefsConstants.IS_PROGRESS_ROTAID).apply();
                                finish();}
                            );
                }else {
                    Toast.makeText(getApplicationContext(),"Please Select any one !", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void initViewBinding() {
        binding = (BottomSheetCancelTrainingBinding) bindActivityView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.bottom_sheet_cancel_training;
    }



    @Override
    public void onBackPressed() {
      // this.finish();
        startActivityForResult(PreTrainingReviewActivity.newIntent(this), IRQ_PRE_TRAINING_REVIEW);
        super.onBackPressed();
    }


}
