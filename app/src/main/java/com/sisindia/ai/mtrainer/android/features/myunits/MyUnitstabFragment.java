package com.sisindia.ai.mtrainer.android.features.myunits;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.ObservableList;

import com.droidcommons.base.BaseFragment;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentMyUnitstabBinding;
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardActivity;
import com.sisindia.ai.mtrainer.android.models.SectorDataResponse;
import com.sisindia.ai.mtrainer.android.models.UnitsData;
import com.sisindia.ai.mtrainer.android.models.UpdateSite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class MyUnitstabFragment extends MTrainerBaseFragment {

    private FragmentMyUnitstabBinding binding;
    private MyUnitstabViewModel viewModel;

    ArrayList<SectorDataResponse> sectorDataResponsesList = new ArrayList<SectorDataResponse>();

    private SectorDataResponse selectedSectorData;

    Dialog dialog;

    public static BaseFragment newInstance() {
        return new MyUnitstabFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_unitstab;
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (MyUnitstabViewModel) getAndroidViewModel(MyUnitstabViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentMyUnitstabBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVmtabmodel(viewModel);
        viewModel.fetchBranchList();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {

        /*binding.autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strText = s.toString();
                if (strText.isEmpty()) {
                    viewModel.myUnitsAdapter.setItemsList(viewModel.unitsDataObservableList);
                    viewModel.myUnitsAdapter.clearAndSetItems(viewModel.unitsDataObservableList);
                    viewModel.myUnitsAdapter.getFilter().filter(strText);
                    viewModel.myUnitsAdapter.notifyDataSetChanged();
                } else {
                    viewModel.myUnitsAdapter.getFilter().filter(strText);
                    viewModel.myUnitsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        binding.autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.myUnitsAdapter.getFilter().filter(s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        viewModel.mutableLiveData.observe(this, aBoolean -> {
            if (aBoolean) {
//                openEditDialog();
                openEditDialogV2();
            }
        });
        viewModel.getUpdate().observe(this, aBoolean -> {
            if (aBoolean) {
                dialog.dismiss();
                binding.autoCompleteTextView.setText("");
            }
        });
    }

    private void openEditDialogV2() {
//        int pos = viewModel.postion.get();
//        UnitsData selectedUnit = viewModel.unitsDataObservableList.get(pos);
        UnitsData selectedUnit = viewModel.selectedUnitLiveData.getValue();

        dialog = new Dialog(Objects.requireNonNull(requireActivity()));
        dialog.setContentView(R.layout.dialog_edit_unit);
        TextView tv_unitcode = dialog.findViewById(R.id.tv_unitcode);
        TextView tv_unitname = dialog.findViewById(R.id.tv_unitname);
        TextView vT_dp_spicounttext = dialog.findViewById(R.id.vT_dp_spimounted);
        Spinner vS_dp_spi = dialog.findViewById(R.id.vs_spi);
        EditText et_spitobemounted = dialog.findViewById(R.id.et_spitobemounted);
        TextView tv_spitobemountedtext = dialog.findViewById(R.id.vT_dp_spitobemounted);
        TextView tv_totalspicoveredtext = dialog.findViewById(R.id.vT_dp_totalspicovered);

        EditText et_spitotalpostcovered = dialog.findViewById(R.id.et_totalspicovered);
        EditText vE_dp_spicount = dialog.findViewById(R.id.et_spimounted);
        Spinner vS_dp_unittype = dialog.findViewById(R.id.vS_dp_unittype);
        Spinner vS_dp_sectype = dialog.findViewById(R.id.vS_dp_sectype);
        Spinner vS_dp_disbandtype = dialog.findViewById(R.id.vS_dp_disbtype);
        Button bt_dc_submit = dialog.findViewById(R.id.bt_dc_submit);
        tv_unitcode.setText(selectedUnit.getUnitCode());
        tv_unitname.setText(selectedUnit.getUnitName());
        et_spitobemounted.setText("" + selectedUnit.getSpiMountTarget());
        et_spitotalpostcovered.setText("" + selectedUnit.getSpiPostCovered());
        vE_dp_spicount.setText("" + selectedUnit.getSpiCount());
        ArrayList<String> spiarryalist = new ArrayList<>();
        spiarryalist.add("Yes");
        spiarryalist.add("No");
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), spiarryalist);
        spinnerAdapter.setDropDownViewResource(R.layout.row_my_spinner);
        vS_dp_spi.setAdapter(spinnerAdapter);

        if (selectedUnit.getSpi() == 0) {
            vE_dp_spicount.setVisibility(View.GONE);
            vT_dp_spicounttext.setVisibility(View.GONE);
            tv_spitobemountedtext.setVisibility(View.GONE);
            et_spitobemounted.setVisibility(View.GONE);
            tv_totalspicoveredtext.setVisibility(View.GONE);
            et_spitotalpostcovered.setVisibility(View.GONE);
            vS_dp_spi.setSelection(1);
        } else {
            vE_dp_spicount.setVisibility(View.VISIBLE);
            vT_dp_spicounttext.setVisibility(View.VISIBLE);
            tv_spitobemountedtext.setVisibility(View.VISIBLE);
            et_spitobemounted.setVisibility(View.VISIBLE);
            tv_totalspicoveredtext.setVisibility(View.VISIBLE);
            et_spitotalpostcovered.setVisibility(View.VISIBLE);
            vS_dp_spi.setSelection(0);
        }

        spinnerAdapter.notifyDataSetChanged();

        vS_dp_spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    vE_dp_spicount.setVisibility(View.VISIBLE);
                    vT_dp_spicounttext.setVisibility(View.VISIBLE);
                    tv_spitobemountedtext.setVisibility(View.VISIBLE);
                    et_spitobemounted.setVisibility(View.VISIBLE);
                    tv_totalspicoveredtext.setVisibility(View.VISIBLE);
                    et_spitotalpostcovered.setVisibility(View.VISIBLE);
                } else {
                    vE_dp_spicount.setVisibility(View.GONE);
                    vT_dp_spicounttext.setVisibility(View.GONE);
                    tv_spitobemountedtext.setVisibility(View.GONE);
                    et_spitobemounted.setVisibility(View.GONE);
                    tv_totalspicoveredtext.setVisibility(View.GONE);
                    et_spitotalpostcovered.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        vE_dp_spicount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_spitobemounted.getText().toString().isEmpty()) {
                    vE_dp_spicount.setText("");
                    Toast.makeText(getActivity(), "Enter SPI To Be Mounted", Toast.LENGTH_LONG).show();
                } else {
                    int ettobemounted = Integer.parseInt(et_spitobemounted.getText().toString());
                    int etmounted;
                    if (vE_dp_spicount.getText().toString().isEmpty()) {
                        etmounted = 0;
                    } else {
                        etmounted = Integer.parseInt(vE_dp_spicount.getText().toString());
                    }
                    if (ettobemounted < etmounted) {
                        vE_dp_spicount.setError("SPI Mounted should be less than SPI to be Mounted");
                        vE_dp_spicount.setText("");
                        Toast.makeText(getActivity(), "SPI Mounted should be less than SPI to be Mounted", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        ArrayList<String> unittypearryalist = new ArrayList<>();
        unittypearryalist.add("OYC");
        unittypearryalist.add("CSAT");
        unittypearryalist.add("Other");
        SpinnerAdapter spinnerAdapter1 = new SpinnerAdapter(getActivity(), unittypearryalist);
        spinnerAdapter1.setDropDownViewResource(R.layout.row_my_spinner);
        vS_dp_unittype.setAdapter(spinnerAdapter1);

        int unitTypeIndex = unittypearryalist.indexOf(selectedUnit.getUnitType());
        if (unitTypeIndex >= 0) {
            vS_dp_unittype.setSelection(unitTypeIndex);
        } else {
            vS_dp_unittype.setSelection(2);
        }

        String originalDisbanded = selectedUnit.getIsDisbanded(); // "YES" or "NO"

        ArrayList<String> disbandedlist = new ArrayList<>();
        disbandedlist.add("YES");
        disbandedlist.add("NO");
        SpinnerAdapter spinnerAdapter2 = new SpinnerAdapter(getActivity(), disbandedlist);
        spinnerAdapter2.setDropDownViewResource(R.layout.row_my_spinner);
        vS_dp_disbandtype.setAdapter(spinnerAdapter2);

        if ("YES".equalsIgnoreCase(selectedUnit.getIsDisbanded())) {
            vS_dp_disbandtype.setSelection(0);
        } else {
            vS_dp_disbandtype.setSelection(1);
        }

        /*if (selectedUnit.getIsDisbanded().equals("NO")) {
            vS_dp_unittype.setSelection(1);
        } else {
            vS_dp_unittype.setSelection(0);
        }*/

        sectorDataResponsesList = new ArrayList<>(viewModel.sectorTypeObservableList);
        //Sector Type
        SectorSpinnerAdapter spinnerAdaptersector = new SectorSpinnerAdapter(getActivity(), sectorDataResponsesList);
        spinnerAdaptersector.setDropDownViewResource(R.layout.row_my_spinner);
        vS_dp_sectype.setAdapter(spinnerAdaptersector);

        /*if (selectedUnit.getSectorId() == 0) {
            vS_dp_sectype.setSelection(0);
        } else {
            for (int j = 0; j < viewModel.sectorTypeObservableList.size(); j++) {
                if (viewModel.sectorTypeObservableList.get(j).getId() == selectedUnit.getSectorId()) {
                    vS_dp_sectype.setSelection(j);
                    spinnerAdaptersector.notifyDataSetChanged();
                }
            }
        }
        spinnerAdaptersector.notifyDataSetChanged();*/

        int sectorIndex = 0;
        for (int i = 0; i < sectorDataResponsesList.size(); i++) {
            if (sectorDataResponsesList.get(i).getId() == selectedUnit.getSectorId()) {
                sectorIndex = i;
                break;
            }
        }
        vS_dp_sectype.setSelection(sectorIndex);

        viewModel.sectorTypeObservableList.addOnListChangedCallback(
                new ObservableList.OnListChangedCallback<ObservableList<SectorDataResponse>>() {

                    @Override
                    public void onChanged(ObservableList<SectorDataResponse> sender) {
                        updateSectorSpinner(sender);
                    }

                    @Override
                    public void onItemRangeChanged(ObservableList<SectorDataResponse> sender, int positionStart, int itemCount) {

                    }

                    @Override
                    public void onItemRangeInserted(ObservableList<SectorDataResponse> sender, int positionStart, int itemCount) {
                        updateSectorSpinner(sender);
                    }

                    @Override
                    public void onItemRangeMoved(ObservableList<SectorDataResponse> sender, int fromPosition, int toPosition, int itemCount) {

                    }

                    @Override
                    public void onItemRangeRemoved(ObservableList<SectorDataResponse> sender, int positionStart, int itemCount) {
                        updateSectorSpinner(sender);
                    }


                    private void updateSectorSpinner(List<SectorDataResponse> list) {
                        sectorDataResponsesList.clear();
                        sectorDataResponsesList.addAll(list);
                        spinnerAdaptersector.notifyDataSetChanged();

                        // SET DEFAULT SELECTION FROM UNIT API
                        int selectionIndex = 0;
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getId() == selectedUnit.getSectorId()) {
                                selectionIndex = i;
                                break;
                            }
                        }

                        vS_dp_sectype.setSelection(selectionIndex, false);
                    }
                });


        vS_dp_sectype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSectorData = sectorDataResponsesList.get(i); // Save selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        /*viewModel.sectorTypeObservableList.addOnListChangedCallback(new ObservableList.OnListChangedCallback<>() {
            @Override
            public void onChanged(ObservableList<SectorDataResponse> sender) {
                if (selectedUnit.getSectorId() == 0) {
                    vS_dp_sectype.setSelection(0);
                } else {
                    for (int j = 0; j < viewModel.sectorTypeObservableList.size(); j++) {
                        if (viewModel.sectorTypeObservableList.get(j).getId() == selectedUnit.getSectorId()) {
                            vS_dp_sectype.setSelection(j);
                            spinnerAdaptersector.notifyDataSetChanged();
                        }
                    }
                }
                spinnerAdaptersector.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<SectorDataResponse> sender, int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeInserted(ObservableList<SectorDataResponse> sender, int positionStart, int itemCount) {
                if (selectedUnit.getSectorId() == 0) {
                    vS_dp_sectype.setSelection(0);
                } else {
                    for (int j = 0; j < viewModel.sectorTypeObservableList.size(); j++) {
                        if (viewModel.sectorTypeObservableList.get(j).getId() == selectedUnit.getSectorId()) {
                            vS_dp_sectype.setSelection(j);
                            spinnerAdaptersector.notifyDataSetChanged();
                        }
                    }
                }
                spinnerAdaptersector.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeMoved(ObservableList<SectorDataResponse> sender, int fromPosition, int toPosition, int itemCount) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<SectorDataResponse> sender, int positionStart, int itemCount) {
                if (selectedUnit.getSectorId() == 0) {
                    vS_dp_sectype.setSelection(0);
                } else {
                    for (int j = 0; j < viewModel.sectorTypeObservableList.size(); j++) {
                        if (viewModel.sectorTypeObservableList.get(j).getId() == selectedUnit.getSectorId()) {
                            vS_dp_sectype.setSelection(j);
                            spinnerAdaptersector.notifyDataSetChanged();
                        }
                    }
                }
                spinnerAdaptersector.notifyDataSetChanged();
            }
        });*/


        bt_dc_submit.setOnClickListener(v -> {

                    String currentDisbanded =
                            vS_dp_disbandtype.getSelectedItemPosition() == 0 ? "YES" : "NO";

                    if (!currentDisbanded.equalsIgnoreCase(originalDisbanded)) {

                        String message;

                        if ("YES".equalsIgnoreCase(currentDisbanded)) {
                            message = "Are you sure you want to disband this site?";
                        } else {
                            message = "Are you sure you want to activate this site?";
                        }

                        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                                .setTitle("Confirmation")
                                .setMessage(message)
                                .setPositiveButton("Yes", (dialogInterface, i) -> {
                                    proceedWithSubmit(
                                            et_spitotalpostcovered,
                                            et_spitobemounted,
                                            vE_dp_spicount,
                                            vS_dp_spi,
                                            vS_dp_unittype,
                                            vS_dp_disbandtype
                                    );
                                })
                                .setNegativeButton("No", null)
                                .show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                    } else {
                        proceedWithSubmit(
                                et_spitotalpostcovered,
                                et_spitobemounted,
                                vE_dp_spicount,
                                vS_dp_spi,
                                vS_dp_unittype,
                                vS_dp_disbandtype
                        );
                    }

          /*  if (et_spitotalpostcovered.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Enter Total SPI Post Covered", Toast.LENGTH_SHORT).show();
                return;
            }

            if (et_spitobemounted.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Enter SPI to be mounted", Toast.LENGTH_SHORT).show();
                return;
            }

            if (vE_dp_spicount.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Enter SPI Mounted", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSectorData == null) {
                Toast.makeText(getActivity(), "Please select sector type", Toast.LENGTH_SHORT).show();
                return;
            }

            int spiposition;
            int spicount = 0;
            int spitobemounted = 0;
            int spitotalcount = 0;

            if (vS_dp_spi.getSelectedItemPosition() == 0) {
                spiposition = 1;
                spicount = Integer.parseInt(vE_dp_spicount.getText().toString());
                spitotalcount = Integer.parseInt(et_spitotalpostcovered.getText().toString());
                spitobemounted = Integer.parseInt(et_spitobemounted.getText().toString());
                if (spitobemounted < spicount) {
                    Toast.makeText(getActivity(), "SPI Mounted should be less than SPI to be Mounted", Toast.LENGTH_LONG).show();
                    return;
                }

            } else {
                spiposition = 0;
            }

            int umbrellaposition = 0;
            int umbrellaobeinstalled = 0;
            int umbrellainstalled = 0;
            int umbrellapostcovered = 0;
            int umbrelladamagedposition = 0;
            int umbrellatobedamaged = 0;
            int umbrellareceived = 0;


            UpdateSite updateSite = new UpdateSite(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                    viewModel.branchList.get(viewModel.branchposition.get()).branchId,
                    viewModel.unitsDataObservableList.get(viewModel.postion.get()).getUnitId(),
                    spiposition, umbrellainstalled, spicount,
                    vS_dp_unittype.getSelectedItem().toString(), umbrellaposition,
                    spitobemounted, spitotalcount, umbrellaobeinstalled, umbrellapostcovered,
                    umbrellareceived, umbrelladamagedposition, umbrellatobedamaged,
                    selectedSectorData.getId(), selectedSectorData.getSectorName(),
                    vS_dp_disbandtype.getSelectedItemPosition() == 0 ? 1 : 0);
            viewModel.updatesite(updateSite);*/

                }
        );
        dialog.show();
    }

    private void proceedWithSubmit(
            EditText et_spitotalpostcovered,
            EditText et_spitobemounted,
            EditText vE_dp_spicount,
            Spinner vS_dp_spi,
            Spinner vS_dp_unittype,
            Spinner vS_dp_disbandtype
    ) {
        if (et_spitotalpostcovered.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter Total SPI Post Covered", Toast.LENGTH_SHORT).show();
            return;
        }

        if (et_spitobemounted.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter SPI to be mounted", Toast.LENGTH_SHORT).show();
            return;
        }

        if (vE_dp_spicount.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter SPI Mounted", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedSectorData == null) {
            Toast.makeText(getActivity(), "Please select sector type", Toast.LENGTH_SHORT).show();
            return;
        }

        int spiposition;
        int spicount = 0;
        int spitobemounted = 0;
        int spitotalcount = 0;

        if (vS_dp_spi.getSelectedItemPosition() == 0) {
            spiposition = 1;

            spicount = Integer.parseInt(vE_dp_spicount.getText().toString());
            spitotalcount = Integer.parseInt(et_spitotalpostcovered.getText().toString());
            spitobemounted = Integer.parseInt(et_spitobemounted.getText().toString());

            if (spitobemounted < spicount) {
                Toast.makeText(getActivity(), "SPI Mounted should be less than SPI to be Mounted", Toast.LENGTH_LONG).show();
                return;
            }

        } else {
            spiposition = 0;
        }

        // Default unused values
        int umbrellaposition = 0;
        int umbrellaobeinstalled = 0;
        int umbrellainstalled = 0;
        int umbrellapostcovered = 0;
        int umbrelladamagedposition = 0;
        int umbrellatobedamaged = 0;
        int umbrellareceived = 0;

        /*UpdateSite updateSite = new UpdateSite(
                Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                viewModel.branchList.get(viewModel.branchposition.get()).branchId,
                viewModel.unitsDataObservableList.get(viewModel.postion.get()).getUnitId(),
                spiposition,
                umbrellainstalled,
                spicount,
                vS_dp_unittype.getSelectedItem().toString(),
                umbrellaposition,
                spitobemounted,
                spitotalcount,
                umbrellaobeinstalled,
                umbrellapostcovered,
                umbrellareceived,
                umbrelladamagedposition,
                umbrellatobedamaged,
                selectedSectorData.getId(),
                selectedSectorData.getSectorName(),
                vS_dp_disbandtype.getSelectedItemPosition() == 0 ? 1 : 0
        );*/

        UnitsData selectedUnit = viewModel.selectedUnitLiveData.getValue();

        if (selectedUnit == null) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateSite updateSite = new UpdateSite(
                Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                viewModel.branchList.get(viewModel.branchposition.get()).branchId,
                selectedUnit.getUnitId(),
                spiposition,
                umbrellainstalled,
                spicount,
                vS_dp_unittype.getSelectedItem().toString(),
                umbrellaposition,
                spitobemounted,
                spitotalcount,
                umbrellaobeinstalled,
                umbrellapostcovered,
                umbrellareceived,
                umbrelladamagedposition,
                umbrellatobedamaged,
                selectedSectorData.getId(),
                selectedSectorData.getSectorName(),
                vS_dp_disbandtype.getSelectedItemPosition() == 0 ? 1 : 0
        );

        viewModel.updatesite(updateSite);
    }

    @Override
    protected void onCreated() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashBoardActivity) requireActivity()).showRefreshButton(false);
    }
}