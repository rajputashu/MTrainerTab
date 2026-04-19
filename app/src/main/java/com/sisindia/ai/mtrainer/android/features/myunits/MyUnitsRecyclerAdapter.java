package com.sisindia.ai.mtrainer.android.features.myunits;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.droidcommons.base.recycler.BaseRecyclerAdapter;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemMyUnitsTabBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.UnitsData;

import java.util.ArrayList;
import java.util.List;

/*public class MyUnitsRecyclerAdapter extends BaseRecyclerAdapter<UnitsData> implements Filterable {

    private MtrainerDataBase dataBase;
    private List<UnitsData> unitsDataObservableList;
    private List<UnitsData> unitsDataObservablefilteredList;
    private UnitsiteViewListeners unitsiteViewListeners;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterItemMyUnitsTabBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_item_my_units_tab, parent, false);
        return new MyUnitsItemViewHolder(binding);
    }

    public MyUnitsRecyclerAdapter(MtrainerDataBase dataBase, List<UnitsData> unitsDataObservableList) {
        this.dataBase = dataBase;
        this.unitsDataObservableList = unitsDataObservableList;
        unitsDataObservablefilteredList = unitsDataObservableList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyUnitsItemViewHolder viewHolder = (MyUnitsItemViewHolder) holder;
        final UnitsData item = getItem(position);
        viewHolder.onBind(item, position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    unitsDataObservablefilteredList = unitsDataObservableList;
                } else {
                    List<UnitsData> filteredList = new ArrayList<>();
                    for (int j = 0; j < unitsDataObservableList.size(); j++) {
                        if (unitsDataObservableList.get(j).getUnitCode().toLowerCase().startsWith(charString.toLowerCase()) || unitsDataObservableList.get(j).getUnitName().toLowerCase().startsWith(charString.toLowerCase())) {
                            filteredList.add(unitsDataObservableList.get(j));
                        }
                    }
                    unitsDataObservablefilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = unitsDataObservablefilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                unitsDataObservablefilteredList = (ArrayList<UnitsData>) filterResults.values;
                setItems(unitsDataObservablefilteredList);
                notifyDataSetChanged();
            }
        };
    }

    public void setitems(List<UnitsData> unitsDataObservableList1) {
        unitsDataObservableList = unitsDataObservableList1;
    }

    public void setListener(UnitsiteViewListeners listener) {
        this.unitsiteViewListeners = listener;
    }


    private class MyUnitsItemViewHolder extends BaseViewHolder<UnitsData> {
        private final RecyclerAdapterItemMyUnitsTabBinding binding;

        public MyUnitsItemViewHolder(RecyclerAdapterItemMyUnitsTabBinding itemBinding) {
            super(itemBinding);
            this.binding = itemBinding;
        }

        @Override
        public void onBind(UnitsData item, int position) {
            binding.setUnitsadapterItem(item);
            binding.txtEdit.setOnClickListener(v -> {
                if (unitsDataObservablefilteredList != null) {
                    if (unitsDataObservablefilteredList.size() == unitsDataObservableList.size()) {
                        unitsiteViewListeners.onunitsiteItemClick(unitsDataObservableList.get(position), position);
                    } else {
                        for (int j = 0; j < unitsDataObservableList.size(); j++) {
                            if (unitsDataObservableList.get(j).getUnitCode().equals(unitsDataObservablefilteredList.get(position).getUnitCode())) {
                                unitsiteViewListeners.onunitsiteItemClick(unitsDataObservableList.get(j), j);
                            }
                        }
                    }
                } else {
                    unitsiteViewListeners.onunitsiteItemClick(unitsDataObservableList.get(position), position);
                }

            });
        }

        @Override
        public void onBind(UnitsData item) {

        }
    }
}*/

public class MyUnitsRecyclerAdapter extends BaseRecyclerAdapter<UnitsData> implements Filterable {

    private List<UnitsData> unitsDataObservableList;
    private List<UnitsData> unitsDataObservableFilteredList;
    private UnitsiteViewListeners unitsiteViewListeners;

    /*public MyUnitsRecyclerAdapter(MtrainerDataBase dataBase, List<UnitsData> unitsDataObservableList) {
        this.unitsDataObservableList = unitsDataObservableList;
        this.unitsDataObservableFilteredList = new ArrayList<>(unitsDataObservableList);
        setItems(this.unitsDataObservableFilteredList);
    }*/

    public MyUnitsRecyclerAdapter(MtrainerDataBase dataBase, List<UnitsData> unitsDataObservableList) {
        this.unitsDataObservableList =
                unitsDataObservableList != null ? unitsDataObservableList : new ArrayList<>();

        this.unitsDataObservableFilteredList =
                new ArrayList<>(this.unitsDataObservableList);

        setItems(this.unitsDataObservableFilteredList);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerAdapterItemMyUnitsTabBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.recycler_adapter_item_my_units_tab, parent, false);
        return new MyUnitsItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyUnitsItemViewHolder) holder).onBind(getItem(position), position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchText = constraint == null ? "" : constraint.toString().toLowerCase().trim();
                List<UnitsData> filteredList = new ArrayList<>();

                if (searchText.isEmpty()) {
                    filteredList.addAll(unitsDataObservableList);
                } else {
                    for (UnitsData unit : unitsDataObservableList) {
                        if (matchesSearch(unit, searchText)) {
                            filteredList.add(unit);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                unitsDataObservableFilteredList = (List<UnitsData>) results.values;
                setItems(unitsDataObservableFilteredList);
                notifyDataSetChanged();
            }
        };
    }

    private boolean matchesSearch(UnitsData unit, String searchText) {

        return (unit.getUnitCode() != null &&
                unit.getUnitCode().toLowerCase().contains(searchText))

                || (unit.getUnitName() != null &&
                unit.getUnitName().toLowerCase().contains(searchText))

                || (unit.getUnitType() != null &&
                unit.getUnitType().toLowerCase().contains(searchText))

                || (unit.getSectorName() != null &&
                unit.getSectorName().toLowerCase().contains(searchText));
    }

    public void setItemsList(List<UnitsData> list) {
        this.unitsDataObservableList = list;
        this.unitsDataObservableFilteredList = new ArrayList<>(list);
        setItems(this.unitsDataObservableFilteredList);
        notifyDataSetChanged();
    }

    public void setListener(UnitsiteViewListeners listener) {
        this.unitsiteViewListeners = listener;
    }

    private class MyUnitsItemViewHolder extends BaseViewHolder<UnitsData> {

        private final RecyclerAdapterItemMyUnitsTabBinding binding;

        public MyUnitsItemViewHolder(RecyclerAdapterItemMyUnitsTabBinding binding) {
            super(binding);
            this.binding = binding;
        }

        @Override
        public void onBind(UnitsData item, int position) {
            binding.setUnitsadapterItem(item);
            if (binding.unitsSNo != null) {
                binding.unitsSNo.setText(String.valueOf(position + 1));
            }

            binding.txtEdit.setOnClickListener(v -> {
                if (unitsiteViewListeners != null) {
//                    int originalPosition = unitsDataObservableList.indexOf(item);
//                    unitsiteViewListeners.onunitsiteItemClick(item, originalPosition);
                    unitsiteViewListeners.onunitsiteItemClick(item, position);
                }
            });
        }

        @Override
        public void onBind(UnitsData item) {
        }
    }
}

