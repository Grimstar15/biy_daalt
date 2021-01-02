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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class place_workers extends Fragment {
    View v;
    DatabaseReference reference, userreference;
    Bundle bundle;
    String gazar;
    ArrayList<worked> arrayList = new ArrayList<>();
    final ArrayList<String> key = new ArrayList<>();
    final ArrayList<String> names = new ArrayList<>();
    int i;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_place_workers, container, false);
        bundle = getArguments();
        gazar = bundle.getString("place");
        getActivity().setTitle(gazar);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,key);
        ListView listView = (ListView) v.findViewById(R.id.place_worker);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                key.add(s);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayAdapter.notifyDataSetChanged();
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
        if (key != null) {
            Log.d("TAG", "onCreateView: " +"notnull");
//            ListView listView = (ListView) v.findViewById(R.id.place_worker);

            Log.d("TAG", "onCreateView: " + arrayList.size() + key.size());

        }else {
            Log.d("TAG", "onCreateView: null");
        }
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                place_worked_adapter adapter = new place_worked_adapter(getActivity(),R.layout.place_worked_list,arrayList);
//            listView.setAdapter(adapter);
                for( i =0; i < key.size() ; i++){
                    final String[] name = new String[1];
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
                            String place = work.getPlace();

                            if (gazar.equals(place)) {
                                if (work.getPermission().equals("true")) {
                                    work.setUsername(name[0]);
                                    arrayList.add(work);
                                }
                                adapter.notifyDataSetChanged();
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