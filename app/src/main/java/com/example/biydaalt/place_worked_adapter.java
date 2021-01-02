package com.example.biydaalt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class place_worked_adapter extends ArrayAdapter<worked> {
    private Context mContext;
    private int mResource;

    public place_worked_adapter(@NonNull Context context, int resource, @NonNull ArrayList<worked> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView username  = convertView.findViewById(R.id.pworked_username);
        TextView date = convertView.findViewById(R.id.pworkd_date);
        TextView value = convertView.findViewById(R.id.pworked_time);
        TextView salary = convertView.findViewById(R.id.pworked_salary);

        int tsalin = getItem(position).getSalary();
        username.setText(getItem(position).getUsername());
        date.setText(getItem(position).getDate());
        value.setText(getItem(position).getValue());
        salary.setText(String.valueOf(tsalin));
        return convertView;
    }
}

