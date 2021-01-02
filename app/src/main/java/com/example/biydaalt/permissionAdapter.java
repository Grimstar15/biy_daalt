package com.example.biydaalt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class permissionAdapter extends ArrayAdapter<worked> {
    private Context mContext;
    private int mResource;
    private DatabaseReference reference;
    private String userId, date, check;

    public permissionAdapter(@NonNull Context context, int resource, @NonNull ArrayList<worked> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView place = convertView.findViewById(R.id.permission_place);
        TextView value = convertView.findViewById(R.id.permission_value);
        TextView username = convertView.findViewById(R.id.permission_username);
        SwitchCompat switchCompat = convertView.findViewById(R.id.permission_switchh);

        username.setText(getItem(position).getUsername());
        place.setText(getItem(position).getPlace());
        value.setText(getItem(position).getValue());
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = getItem(position).getUserId();
        date = getItem(position).getDate();
        Log.d("TAG", "getView: "+ date + " " + userId);

        check = getItem(position).getPermission();
        if (check.equals("true")) {
            switchCompat.setChecked(true);
        }
        else {
            switchCompat.setChecked(false);
        }
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchCompat.isChecked()){
                    Log.d("TAG", "getView: trie");
                    reference.child(userId).child("dates").child(date).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext()," true" ,Toast.LENGTH_LONG).show();
                        }
                    });

                }else {
                    Toast.makeText(getContext()," false" ,Toast.LENGTH_LONG).show();
                    reference.child(userId).child("dates").child(date).child("permission").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"false",Toast.LENGTH_LONG).show();
                        }
                    });
//            switchCompat.setChecked(false);
                }
            }
        });


        return convertView;
    }
}
