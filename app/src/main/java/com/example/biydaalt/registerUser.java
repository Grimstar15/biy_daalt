package com.example.biydaalt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class registerUser extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    EditText editname, editage, editmail, editpass, editcpass, editphone, editaddress;
    ProgressBar progressBar;
    Button register;
    ImageView userPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        getSupportActionBar().setTitle("Sign Up");
        mAuth = FirebaseAuth.getInstance();

        register = (Button) findViewById(R.id.registerUser);
        register.setOnClickListener(this);

        editname = (EditText) findViewById(R.id.fullName);
        editage = (EditText) findViewById(R.id.age);
        editmail = (EditText) findViewById(R.id.email);
        editpass = (EditText) findViewById(R.id.password);
        editcpass = (EditText) findViewById(R.id.cpassword);
        editphone = (EditText) findViewById(R.id.phone);
        editaddress = (EditText) findViewById(R.id.address);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        userPhoto = findViewById(R.id.userPhoto);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    checkAndRequestForPermission();
                    openGallery();

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registerUser:
                registerUser();
                break;
            case R.id.userPhoto:
                photo();
                break;
        }
    }

    private void photo() {

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(registerUser.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(registerUser.this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(registerUser.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }

            else {
                ActivityCompat.requestPermissions(registerUser.this,
                                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                     PReqCode);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){
            imageUri = data.getData();
            userPhoto.setImageURI(imageUri);
        }

    }

    private void registerUser(){
        String email = editmail.getText().toString().trim();
        String password = editpass.getText().toString().trim();
        String cpassword = editcpass.getText().toString().trim();
        String name = editname.getText().toString().trim();
        String age = editage.getText().toString().trim();
        String phone = editphone.getText().toString().trim();
        String address = editaddress.getText().toString().trim();
        ImageView img = findViewById(R.id.userPhoto);
        if(img.equals(null)){
            Toast.makeText(registerUser.this,"Please add Image",Toast.LENGTH_LONG).show();
            return;
        }
        if(name.isEmpty()){
            editname.setError("Name is required");
            editname.requestFocus();
            return;
        }
        if(age.isEmpty()){
            editage.setError("Age is required");
            editage.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editmail.setError("Email is required");
            editmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editmail.setError("Email is invalid");
            editmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editpass.setError("Password is required");
            editpass.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            editpass.setError("Phone is required");
            editpass.requestFocus();
            return;
        }
        if(address.isEmpty()){
            editpass.setError("Address is required");
            editpass.requestFocus();
            return;
        }
        if(password.length() <6){
            editpass.setError("Enter strong password!");
            editpass.requestFocus();
            return;
        }
        if(!password.equals(cpassword)){
            editcpass.setError("Password not match!");
            editcpass.requestFocus();
            return;
        }
        if(cpassword.isEmpty()){
            editcpass.setError("Confrim password!");
            editcpass.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.INVISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(name, age, email, phone, address);
                            updatePhoto(name, imageUri,mAuth.getCurrentUser());
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(registerUser.this,"User registered!",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(registerUser.this,MainActivity.class));
                                    }
                                    else{
                                        Toast.makeText(registerUser.this,"Failed! Try again!",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else{
                            Toast.makeText(registerUser.this,"Failed! Try again!",Toast.LENGTH_LONG).show();
                            register.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void updateUI(){

    }
    private void updatePhoto(String name, Uri imageUri, FirebaseUser currentUser){
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        StorageReference imagePath = mStorage.child(mAuth.getCurrentUser().getUid());
        imagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.d("TAG", "onComplete: +successful");
                                            updateUI();
                                        }
                                    }
                                });


                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }
    private void savePreferences() {
        SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
        String usermail = editmail.getText().toString().trim();
        String userpass = editpass.getText().toString().trim();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mail", usermail);
        editor.putString("pass", userpass);
        editor.commit();
    }
}