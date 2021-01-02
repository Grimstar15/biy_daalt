package com.example.biydaalt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView register;
    private EditText editemail, editpass;
    private Button btn;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private String permission, position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Login");
        register = (ImageView) findViewById(R.id.signup);
        register.setOnClickListener(this);

        btn = (Button) findViewById(R.id.login);
        btn.setOnClickListener(this);

        editemail = (EditText) findViewById(R.id.email);
        editpass = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        SharedPreferences sp = getSharedPreferences("session",Context.MODE_PRIVATE);
        permission = sp.getString("permission","");
        if(permission.equals("true")) {
            startActivity(new Intent(this, navigation.class));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signup:
                startActivity(new Intent(this,registerUser.class));
                break;

            case R.id.login:
                userLogin();
                break;
        }
    }
    private void userLogin(){
        String email = editemail.getText().toString().trim();
        String pass = editpass.getText().toString().trim();

        if(email.isEmpty()){
            editemail.setError("Email is required!");
            editemail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editemail.setError("Please enter valid email!");
            editemail.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            editpass.setError("Password is required!");
            editpass.requestFocus();
            return;
        }
        btn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = mAuth.getCurrentUser();
                    permission();
                }
                else{
                    Toast.makeText(MainActivity.this,"Check your data!",Toast.LENGTH_LONG).show();
                    btn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void permission(){
        String userID = user.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    position = userProfile.position;
                    permission = userProfile.permission;
                    if(permission.equals("true")){

                            startActivity(new Intent(MainActivity.this, navigation.class));

                    }else {
                        Toast.makeText(MainActivity.this,"You need permission!",Toast.LENGTH_LONG).show();
                    }
                    btn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(MainActivity.this,"Cant get data",Toast.LENGTH_LONG).show();
        }

    });
}

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }
    private void savePreferences() {
        SharedPreferences sp = getSharedPreferences("session",Context.MODE_PRIVATE);
        String usermail = editemail.getText().toString().trim();
        String userpass = editpass.getText().toString().trim();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mail", usermail);
        editor.putString("pass", userpass);
        editor.putString("permission", permission);
        editor.commit();
    }
    private void loadPreferences() {

        SharedPreferences sp = getSharedPreferences("session",Context.MODE_PRIVATE);
        String usermail = sp.getString("mail","");
        String userpass = sp.getString("pass","");
        editemail.setText(usermail);
        editpass.setText(userpass);
    }
}