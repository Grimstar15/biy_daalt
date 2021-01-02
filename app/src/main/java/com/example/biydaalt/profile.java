package com.example.biydaalt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends Fragment {
    private FirebaseUser user;
    private DatabaseReference reference;
    private StorageReference storageReference;

    private View view;
    private String userID;
    private Button logout;
    private TextView txtname, txtage, txtmail, txtphone, txtaddress, txtcount, txtsalary;
    private Bundle bundle;
    private User myUser;
    private ArrayList<worked> arrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile,container,false);
        getActivity().setTitle("Profile");
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        txtname= view.findViewById(R.id.nametitle) ;
        txtage= view.findViewById(R.id.agetitle) ;
        txtmail= view.findViewById(R.id.emailtitle) ;
        txtphone= view.findViewById(R.id.profile_phone) ;
        txtaddress= view.findViewById(R.id.profile_address) ;
        txtcount= view.findViewById(R.id.profile_count);
        txtsalary = view.findViewById(R.id.profile_salary);
        bundle = this.getArguments();
        storageReference = FirebaseStorage.getInstance().getReference("users_photos").child(userID);
        try{
            final File imagefile = File.createTempFile(userID,"jpg");
            storageReference.getFile(imagefile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
                    CircleImageView img = view.findViewById(R.id.profile_image);
                    if(img!=null) {
                        img.setImageBitmap(bitmap);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailuress: "+e);
                }
            });
        }
        catch (IOException i){
            i.printStackTrace();
        }

        if(bundle != null){
            myUser = bundle.getParcelable("data");
            txtname.append(myUser.getName());
            txtage.append(myUser.getAge());
            txtmail.append(myUser.getEmail());
            txtphone.append(myUser.getPhone());
            txtaddress.append(myUser.getAddress());
            storageReference = FirebaseStorage.getInstance().getReference("users_photos").child(myUser.getUserId());
            try {
                File imagefile = File.createTempFile(myUser.getUserId(), "jpeg");
                storageReference.getFile(imagefile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
                        CircleImageView img = view.findViewById(R.id.profile_image);

                        img.setImageBitmap(bitmap);

                    }
                });
            }
            catch (IOException i){
                i.printStackTrace();
            }

        }else {
            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if (userProfile != null) {
                        String name = userProfile.name;
                        String email = userProfile.email;
                        String age = userProfile.age;
                        String phone = userProfile.phone;
                        String address = userProfile.address;

                        txtname.append(name);
                        txtage.append(age);
                        txtmail.append(email);
                        txtphone.append(phone);
                        txtaddress.append(address);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Cant get data", Toast.LENGTH_LONG).show();
                }
            });
            reference.child(userID).child("dates").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    worked work = snapshot.getValue(worked.class);
                    if (work.getPermission().equals("true")) {
                        arrayList.add(work);
                        int count = arrayList.size();
                        txtcount.setText(String.valueOf(count));
                        txtsalary.setText(String.valueOf(count * arrayList.get(0).getSalary()));
                    }
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
        }
        return view;
    }
}
