package com.example.biydaalt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class date_of_work extends Fragment {
    View v;
    DatabaseReference reference;
    final ArrayList<String> key = new ArrayList<>();
    ArrayList<worked> arrayList = new ArrayList<>();
    int i;
    String time, date, value;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_date_of_work, container, false);
        ListView listView = (ListView) v.findViewById(R.id.permission_list);
        Bundle data = getArguments();
        time = data.getString("time");
        date = data.getString("date");
        value = data.getString("value");
        getActivity().setTitle(date + " " +time);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                key.add(s);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
                permissionAdapter adapter = new permissionAdapter(getActivity(),R.layout.date_permisson_list,arrayList);
//            listView.setAdapter(adapter);
                for( i =0; i < key.size() ; i++){
                    final String[] name = new String[1];
                    final String userId;
                    userId = key.get(i);
                    reference.child(key.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User data = snapshot.getValue(User.class);
                            name[0] = data.name;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    reference.child(key.get(i)).child("dates").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            worked work = snapshot.getValue(worked.class);
                            String permission = work.getPermission();

                            if (permission.equals(value)) {
                                String values = work.getValue();
                                String dates = work.getDate();
                                if (dates.equals(date)) {
                                    if (values.equals(time)) {
                                        work.setUsername(name[0]);
                                        work.setUserId(userId);
                                        arrayList.add(work);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            adapter.notifyDataSetChanged();
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
                    listView.setAdapter(adapter);
                }



            }
        }, 500);
        return v;
    }
}