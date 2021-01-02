package com.example.biydaalt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link salary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class salary extends Fragment {
    private View v;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ListView listView;
    private ArrayList<worked> work = new ArrayList<worked>(), workedArrayList = new ArrayList<worked>();
    private ArrayList<String > dates = new ArrayList<String>();
    private int k;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_salary, container, false);
        listView = v.findViewById(R.id.list_salary);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("dates");
//        if(!work.equals(null)) {
//            workedArrayList.add(work.get(0));
//        }
        monthAdapter monthAdapter = new monthAdapter(getActivity(),R.layout.month,workedArrayList);


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                worked value = snapshot.getValue(worked.class);
                if (value.getPermission().equals("true")) {
                    String date[] = value.getDate().toString().split("-");
                    String month = date[0] + "-" + date[1];
                    value.setDate(month);
                    work.add(value);
                    dates.add(value.getDate());
                    Collections.sort(dates);
                }

                monthAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                monthAdapter.notifyDataSetChanged();
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



        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String date1, date2;
                int m = 0;
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(dates);
                dates.clear();
                dates.addAll(hashSet);

                for (int i = 0; i < dates.size(); i++) {
                    date1= dates.get(i);
                    Log.d("TAG", "onChildAdded: " + date1);
                    for (int j = 0; j < work.size(); j++) {
                        Log.d("TAG", "onChildAdded: " + work.get(j).getDate());
                        date2= work.get(j).getDate();
                        if (date1.equals(date2)) {
                            k++;
                            m = j;
                        }
//                            if(k==0){
//                                workedArrayList.add(work.get(i));
//                                k=0;
//                                Log.d("TAG", "run: "+ work.get(i).getDate());
//                            Log.d("TAG", "onCreateViewww size des: " + workedArrayList.size());
                        }
                    int sal;
                    sal = work.get(m).getSalary() * k;
                    work.get(m).setSalary(sal);
                    work.get(m).setCount(k);
                    workedArrayList.add(work.get(m));
                    k=0;
//                    Log.d("TAG", "onCreateViewww size des: " + workedArrayList.size());
                    }

                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,dates);
                listView.setAdapter(monthAdapter);

            }
        }, 500);

        return v;

    }

}