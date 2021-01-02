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

public class monthAdapter extends ArrayAdapter<worked> {
    private Context mContext;
    private int mResource;

    public monthAdapter(@NonNull Context context, int resource, @NonNull ArrayList<worked> objects) {
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
        TextView count = convertView.findViewById(R.id.list_count);
        TextView salary = convertView.findViewById(R.id.list_salary);

        int tsalin = getItem(position).getSalary();
        date.setText(getItem(position).getDate());
        count.setText(String.valueOf(getItem(position).getCount()));
        salary.setText(String.valueOf(tsalin));
        return convertView;
    }
}


