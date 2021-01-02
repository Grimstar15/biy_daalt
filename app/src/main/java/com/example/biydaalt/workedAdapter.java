package com.example.biydaalt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class workedAdapter extends ArrayAdapter<worked> {
    private Context mContext;
    private int mResource;

    public workedAdapter(@NonNull Context context, int resource, @NonNull ArrayList<worked> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView date = convertView.findViewById(R.id.list_date);
        TextView place = convertView.findViewById(R.id.list_place);
        TextView value = convertView.findViewById(R.id.list_value);
        TextView salary = convertView.findViewById(R.id.list_salary);


        int tsalin = getItem(position).getSalary();
        date.setText(getItem(position).getDate());
        place.setText(getItem(position).getPlace());
        value.setText(getItem(position).getValue());
        salary.setText(String.valueOf(tsalin));
        return convertView;
    }
}
