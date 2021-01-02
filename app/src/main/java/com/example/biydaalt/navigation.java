package com.example.biydaalt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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

public class navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private FirebaseUser user;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private String userID, position;
    private TextView username;
    final ArrayList<String> key = new ArrayList<>();
    final ArrayList<String> userz = new ArrayList<>();
    ArrayList<worked> arrayList = new ArrayList<>();
    public static int badge_count_number = 0;
    public static TextView badge_counter, counter;
    int i, n = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new profile()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference("users_photos").child(userID);
        try{
            final File imagefile = File.createTempFile(userID,"jpg");
            storageReference.getFile(imagefile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
                    ImageView img = findViewById(R.id.nav_image);
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
//        if(uri!=null) {
//            Glide.with(this).load(user.getPhotoUrl()).into(image);
//            image.setImageURI(uri);
//        }


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                     position = userProfile.position;
                    if(position.equals("boss")){
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.boss_menu);
                        navigationView.setCheckedItem(R.id.nav_profile);
                        LayoutInflater li = LayoutInflater.from(navigation.this);
                        badge_counter = (TextView) li.inflate(R.layout.notification_badge,null);
                        counter = (TextView) li.inflate(R.layout.notification_badge,null);
                        navigationView.getMenu().findItem(R.id.nav_bdate).setActionView(badge_counter);
                        navigationView.getMenu().findItem(R.id.nav_users).setActionView(counter);

                    }
                    username = findViewById(R.id.nav_username);
                    username.setText( userProfile.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(navigation.this,"Cant get data",Toast.LENGTH_LONG).show();
            }
        });
        // badge heseg
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User data = snapshot.getValue(User.class);
                    Log.d("TAG", "onChildAdded: " + data.getPermission());
                    if (data.getPermission().equals("false")) {
                        userz.add(data.getPermission());
                        if (position.equals("boss"))
                        counter.setText(String.valueOf(userz.size()));
                    }
                    String s = snapshot.getKey();
                    key.add(s);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User data = snapshot.getValue(User.class);
                    Log.d("TAG", "onChildAdded: " + data.getPermission());
                    if (data.getPermission().equals("false")) {
                        userz.add(data.getPermission());
                        if (position.equals("boss"))
                        counter.setText(String.valueOf(userz.size()));
                    }
                    String s = snapshot.getKey();
                    key.add(s);
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
                    for (i = 0; i < key.size(); i++) {

                        reference.child(key.get(i)).child("dates").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                worked work = snapshot.getValue(worked.class);
                                String permission = work.getPermission();
                                if (permission.equals("false")) {
                                    arrayList.add(work);
                                    if (position.equals("boss"))
                                    show_counter(arrayList.size());


                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                worked work = snapshot.getValue(worked.class);
                                String permission = work.getPermission();
                                if (permission.equals("false")) {
                                    arrayList.add(work);
                                    if (position.equals("boss"))
                                        show_counter(arrayList.size());


                                }
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


                }
            }, 2000);
        }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new profile()).commit();
                break;
            case R.id.nav_date:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new worked_date()).commit();
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.nav_users:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new users()).commit();
                break;
            case R.id.nav_places:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new places()).commit();
                break;
            case R.id.nav_salary:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new salary()).commit();
                break;
            case R.id.nav_bdate:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new boss_date()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(navigation.this,MainActivity.class));

        SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("permission", "false");
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
    public static void show_counter(int count){
        if (count > 0){
            badge_counter.setText(count+"");
        }
        else {
            badge_counter.setText("");
        }
    }

}