package com.example.biydaalt;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class worked_date extends Fragment {
    private View v;
    private String date, datetime, userID;
    private TextView datetext;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private Spinner spinner, dayy;
    private DatabaseReference reference,users;
    private ArrayList<String> arrayList = new ArrayList<>(), days = new ArrayList<>();
    private ArrayList<worked> list = new ArrayList<worked>();
    private Button upload;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseUser user;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_worked_date, container, false);
        getActivity().setTitle("Add date");
        calendar = Calendar.getInstance();
        datetext = v.findViewById(R.id.date);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(calendar.getTime());
        datetext.setText(date);
        reference = FirebaseDatabase.getInstance().getReference("places");
        users = FirebaseDatabase.getInstance().getReference("Users");
        spinner = v.findViewById(R.id.spinner);
        dayy = v.findViewById(R.id.day);
        upload = v.findViewById(R.id.upload);
        days.add("day");
        days.add("night");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        ArrayAdapter<String> work = new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item,days);
        dayy.setAdapter(work);
        ListView listView = (ListView) v.findViewById(R.id.list_worked);
        workedAdapter workedAdapter = new workedAdapter(getActivity(),R.layout.worked_list,list);
        listView.setAdapter(workedAdapter);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot item: snapshot.getChildren()){
                    arrayList.add(item.getValue(String.class));
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item,arrayList);
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        datetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                datetime = year + "-" + month + "-" + dayOfMonth;
                datetext.setText(datetime);
            }
        };
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String days, place, value;

                days = datetext.getText().toString();
                place = spinner.getSelectedItem().toString();
                value = dayy.getSelectedItem().toString();
                worked work = new worked(days,place,value,10000);
                users.child(userID).child("dates").child(days).setValue(work).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity()," Waiting permission" ,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        users.child(userID).child("dates").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                worked value = snapshot.getValue(worked.class);
                if (value.getPermission().equals("true")) {
                    list.add(value);
                }
                workedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                workedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return v;
    }
}