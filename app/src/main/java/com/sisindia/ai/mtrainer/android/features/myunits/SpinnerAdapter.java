package com.sisindia.ai.mtrainer.android.features.myunits;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.sisindia.ai.mtrainer.android.R;

import java.io.Serializable;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter implements Serializable {
    private List<String> items;
    private Context context;

    @SuppressWarnings("unchecked")
    public SpinnerAdapter(Context context, List<String> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items==null?0:items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    public View createItemView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.row_my_spinner, parent, false);
        TextView name = (TextView) view.findViewById(R.id.text);
        String category = items.get(position);
        name.setText(category);
        return view;
    }

}
