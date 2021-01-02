package com.example.biydaalt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private int mResouece;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private User myUser;

    public UserAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResouece = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResouece,parent,false);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        TextView username = convertView.findViewById(R.id.list_text);
        SwitchCompat switchCompat = convertView.findViewById(R.id.list_switch);
        String permission = getItem(position).getPermission();
        String hehe = "true";
        myUser = getItem(position);
        String userID = myUser.getUserId();
        storageReference = FirebaseStorage.getInstance().getReference("users_photos").child(userID);

        try{
            final File imagefile = File.createTempFile(userID,"jpg");

            View finalConvertView = convertView;
            storageReference.getFile(imagefile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
                    ImageView img = finalConvertView.findViewById(R.id.list_image);
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


        switchCompat.setChecked(true);
        if (permission.equals("false")){
            switchCompat.setChecked(false);
        }
//        Toast.makeText(getContext(),permission ,Toast.LENGTH_LONG).show();
//        if(permission.compareTo("true")==0){
//            switchCompat.setChecked(true);
//        }else{
//            switchCompat.setChecked(false);
//        }
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUser = getItem(position);
                if(switchCompat.isChecked()){
                    reference.child(myUser.getUserId()).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext()," true" ,Toast.LENGTH_LONG).show();
                        }
                    });
                    switchCompat.setChecked(true);
                }else {
                    reference.child(myUser.getUserId()).child("permission").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"false",Toast.LENGTH_LONG).show();
                        }
                    });
                    switchCompat.setChecked(false);
                }
            }
        });
        username.setText(getItem(position).getName());
        return convertView;
    }
}
