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

import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;

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
import com.google.firebase.database.ValueEventListener;


public class Workouts extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ImageView profileImage;
    DatabaseReference databaseReference;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchTheme;
    ScrollView homeLayout, workoutOptionsLayout, workoutsLayout, workoutsLayoutDark;
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

        workoutOptionsLayout =(ScrollView) findViewById(R.id.selectionCategoriesLayout);
        workoutOptionsLayout.setVisibility(View.GONE);

        homeLayout=(ScrollView) findViewById(R.id.homeScreenLayout);
        homeLayout.setVisibility(View.GONE);
        // Theme selection
        switchTheme=findViewById(R.id.switchTheme);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue().equals("light")){
                    //switchTheme.setChecked(true);
                    //Initialize layout visibilities
                    workoutsLayout=(ScrollView) findViewById(R.id.workoutsLayoutLight);
                    workoutsLayoutDark=(ScrollView) findViewById(R.id.workoutsLayoutDark);
                    workoutsLayout.setVisibility(View.VISIBLE);
                    workoutsLayoutDark.setVisibility(View.GONE);
                    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    toolbar.setTitleTextColor(Color.parseColor("#000000"));


                } else if (snapshot.getValue().equals("dark")) {
                    //switchTheme.setChecked(false);
                    workoutsLayout=(ScrollView) findViewById(R.id.workoutsLayoutDark);
                    workoutsLayout.setVisibility(View.VISIBLE);
                    workoutsLayout=(ScrollView) findViewById(R.id.workoutsLayoutLight);
                    workoutsLayout.setVisibility(View.GONE);
                    toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
                    toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
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
                            Glide.with(Workouts.this)
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
        toolbar.setTitle("Workouts");

        toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open,
                R.string.nav_close);


        drawer.addDrawerListener(toggle);
        toggle.syncState();





        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);


        gridLayout=(GridLayout) findViewById(R.id.gridLayout);
        setSingleEvent(gridLayout);
        currentUser.reload();

        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        /*
        // Theme
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice").setValue("light");


                    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    toolbar.setTitleTextColor(Color.parseColor("#000000"));



                    TextView title=findViewById(R.id.homeScreenTitleText);
                    title.setTextColor(Color.parseColor("#B5000000"));
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
                    TextView title=findViewById(R.id.homeScreenTitleText);
                    title.setTextColor(Color.parseColor("#FFFFFF"));
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

                    homeLayout.setBackgroundColor(Color.parseColor("#DA000000"));
                }

            }
        });

         */
        Spinner spinner = (Spinner) findViewById(R.id.spinner_difficulty);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_values, R.layout.spinner_selected_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
                    Intent intent = new Intent(Workouts.this, Home.class);
                    startActivity(intent);
                    finish();
                }
            }, 410); // Delay in milliseconds
        }else if (item.getItemId()==R.id.nav_profile_setup) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Workouts.this, ProfileSetup.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_workout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Workouts.this, Home.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_stats) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Workouts.this, Home.class);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId()==R.id.nav_quiz) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Workouts.this, Home.class);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        }else if (item.getItemId()==R.id.nav_logout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    auth.signOut();
                    Intent intent = new Intent(Workouts.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_delete_acc) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Workouts.this, Login.class);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId()==R.id.nav_diet) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Workouts.this, Home.class);
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
                LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(0);
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    if (linearLayout.getChildAt(j) instanceof HorizontalScrollView){
                        HorizontalScrollView horizontalScrollView=(HorizontalScrollView) linearLayout.getChildAt(j);
                        LinearLayout cardContainer = (LinearLayout) horizontalScrollView.getChildAt(0);
                        for (int k = 0; k < cardContainer.getChildCount(); k++) {
                            CardView card = (CardView) cardContainer.getChildAt(k);
                            card.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (card.getId() == findViewById(R.id.chestCard).getId() ) {

                                        Intent intent = new Intent(Workouts.this, Home.class);
                                        startActivity(intent);
                                        finish();

                                    } else if (card.getId() == findViewById(R.id.absCard).getId()) {
                                        Intent intent = new Intent(Workouts.this, Home.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (card.getId() == findViewById(R.id.legsCard).getId()) {
                                        Intent intent = new Intent(Workouts.this, Home.class);
                                        startActivity(intent);
                                        finish();

                                    }else if (card.getId() == findViewById(R.id.armsCard).getId()) {
                                        Intent intent = new Intent(Workouts.this, Home.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
                        }
                    }
                }

            }




        }
    }
    @Override
    public void onBackPressed() {
        // Create an intent to start a new activity

        Intent intent = new Intent(Workouts.this, WorkoutOptions.class);
        startActivity(intent);
        // Finish the current activity
        finish();
        super.onBackPressed();
    }
}
