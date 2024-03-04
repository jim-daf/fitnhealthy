package com.example.fitnhealthy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class WorkoutOptions extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;

    ImageView profileImage;
    DatabaseReference databaseReference;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchTheme;

    ScrollView homeLayout, workoutOptionsLayout,workoutsLayout;

    GridLayout gridLayout;
    public DrawerLayout drawer;
    Toolbar toolbar;

    public ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_nav_drawer);

        auth = FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        switchTheme=findViewById(R.id.switchTheme);
        //Get username from database
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue().equals("light")){
                    switchTheme.setChecked(true);
                } else if (snapshot.getValue().equals("dark")) {
                    switchTheme.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("/Users");
        DatabaseReference imageUri=FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid());



        // Create a reference to the file you want to access
        profileImage=findViewById(R.id.nav_header_image);

        imageUri.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("profileImageUri")) {
                        if (!child.getValue().equals("") && !child.getValue().equals("null") && child.getValue() != null) {
                            profileImage=findViewById(R.id.nav_header_image);
                            Glide.with(WorkoutOptions.this)
                                    .load(child.getValue())
                                    .into(profileImage);
                            //profileImage.setImageURI(Uri.parse(child.getValue().toString()));
                        }


                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Initialize layout visibilities


        workoutOptionsLayout =(ScrollView) findViewById(R.id.selectionCategoriesLayout);
        workoutOptionsLayout.setVisibility(View.VISIBLE);

        homeLayout=(ScrollView) findViewById(R.id.homeScreenLayout);
        homeLayout.setVisibility(View.GONE);

        workoutsLayout=(ScrollView) findViewById(R.id.workoutsLayoutLight);
        workoutsLayout.setVisibility(View.GONE);


        // Nav Drawer
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        @SuppressLint("CutPasteId")
        NavigationView navView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawerLayout);
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setNavigationIcon(R.drawable.ic_hamburger_menu);
        toolbar.setTitle("Workout options");

        toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open,
                R.string.nav_close);

        // toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        //Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger_menu);



        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Dashboard

        gridLayout=(GridLayout) findViewById(R.id.gridLayoutForWorkoutOptions);
        setSingleEvent(gridLayout);
        currentUser.reload();
        //setToggleEvent(gridLayout);


        // Retrieve username from database based on the user email
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar);
                    FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice").setValue("light");


                    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    toolbar.setTitleTextColor(Color.parseColor("#000000"));




                    for (int i = 0; i < gridLayout.getChildCount(); i++) {
                        //You can see , all child item is CardView , so we just cast object to CardView
                        if (gridLayout.getChildAt(i) instanceof CardView) {
                            CardView cardView = (CardView) gridLayout.getChildAt(i);
                            cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                            for (int j = 0; j < cardView.getChildCount(); j++) {
                                if (cardView.getChildAt(j) instanceof LinearLayout) {
                                    LinearLayout layout=(LinearLayout) cardView.getChildAt(j);
                                    for (int k = 0; k < layout.getChildCount(); k++) {
                                        if (layout.getChildAt(k) instanceof TextView) {
                                            TextView txtView=(TextView) layout.getChildAt(k);
                                            txtView.setTextColor(Color.parseColor("#B5000000"));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    workoutOptionsLayout.setBackgroundColor(Color.parseColor("#DAE4D6D6"));

                }else {
                    FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice").setValue("dark");
                    //setTheme(androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dark);
                    toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
                    toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

                    for (int i = 0; i < gridLayout.getChildCount(); i++) {
                        //You can see , all child item is CardView , so we just cast object to CardView
                        if (gridLayout.getChildAt(i) instanceof CardView) {
                            CardView cardView = (CardView) gridLayout.getChildAt(i);
                            cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                            for (int j = 0; j < cardView.getChildCount(); j++) {
                                if (cardView.getChildAt(j) instanceof LinearLayout) {
                                    LinearLayout layout=(LinearLayout) cardView.getChildAt(j);
                                    for (int k = 0; k < layout.getChildCount(); k++) {
                                        if (layout.getChildAt(k) instanceof TextView) {
                                            TextView txtView=(TextView) layout.getChildAt(k);
                                            txtView.setTextColor(Color.parseColor("#FFFFFF"));
                                        }
                                    }


                                }
                            }
                        }
                    }

                    workoutOptionsLayout.setBackgroundColor(Color.parseColor("#DA000000"));
                }

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);


        return true;

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public Boolean onNavigationItemSelected(MenuItem item){

        if(item.getItemId()==R.id.nav_home){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, Home.class);
                    startActivity(intent);
                    finish();
                }
            }, 410); // Delay in milliseconds
        }else if (item.getItemId()==R.id.nav_profile_setup) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, ProfileSetup.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_workout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, Home.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_stats) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, Home.class);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId()==R.id.nav_quiz) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, Home.class);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        }else if (item.getItemId()==R.id.nav_logout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    auth.signOut();
                    Intent intent = new Intent(WorkoutOptions.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_delete_acc) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, Login.class);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId()==R.id.nav_diet) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, Home.class);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){

        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }
    public void onConfigurationChanged(@NonNull Configuration newConfiguration){
        super.onConfigurationChanged(newConfiguration);
        toggle.syncState();
    }


    private void setSingleEvent(GridLayout gridLayout) {
        //Loop all child item of Main.java Grid
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            if (gridLayout.getChildAt(i) instanceof CardView) {
                CardView cardView = (CardView) gridLayout.getChildAt(i);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (cardView.getId() == findViewById(R.id.CreateYourOwnWorkoutCard).getId() ) {

                            Intent intent = new Intent(WorkoutOptions.this, Home.class);
                            startActivity(intent);
                            finish();

                        } else if (cardView.getId() == findViewById(R.id.SelectAWorkoutCard).getId()) {
                            Intent intent = new Intent(WorkoutOptions.this, Workouts.class);
                            startActivity(intent);
                            finish();
                        } else if (cardView.getId() == findViewById(R.id.GenerateWorkoutCard).getId()) {
                            Intent intent = new Intent(WorkoutOptions.this, Home.class);
                            startActivity(intent);
                            finish();

                        }else {
                            //Toast.makeText(AdminDashboard.this,"Wrong Tap",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }




        }
    }
    @Override
    public void onBackPressed() {
        // Create an intent to start a new activity

        Intent intent = new Intent(WorkoutOptions.this, Home.class);
        startActivity(intent);
        // Finish the current activity
        finish();
        super.onBackPressed();
    }
}
