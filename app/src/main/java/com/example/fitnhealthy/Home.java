package com.example.fitnhealthy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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



public class Home extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String username,user_theme;
    ImageView profileImage;
    DatabaseReference databaseReference;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchTheme;
    TextView homeTitleTextView;
    ScrollView homeLayout,workoutOptionsLayout,workoutsLayout,profileSetupLayout,settingsLayout, physicalDataLayout,workoutMetricsLayout;

    ImageView sun,moon;
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

        //Initialize layout visibilities
        homeLayout=(ScrollView) findViewById(R.id.homeScreenLayout);
        workoutsLayout=(ScrollView) findViewById(R.id.workoutsLayoutLight);
        workoutOptionsLayout =(ScrollView) findViewById(R.id.selectionCategoriesLayout);
        profileSetupLayout = (ScrollView) findViewById(R.id.profileSetupLayout);
        settingsLayout = (ScrollView) findViewById(R.id.settingsLayout);


        workoutOptionsLayout.setVisibility(View.GONE);
        workoutsLayout.setVisibility(View.GONE);
        profileSetupLayout.setVisibility(View.GONE);
        homeLayout.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.GONE);

        physicalDataLayout = (ScrollView) findViewById(R.id.updatePhysicalDataLayout);
        physicalDataLayout.setVisibility(View.GONE);

        workoutMetricsLayout=(ScrollView) findViewById(R.id.workoutMetricsLayout);
        workoutMetricsLayout.setVisibility(View.VISIBLE);


        gridLayout=(GridLayout) findViewById(R.id.gridLayout);
        // Nav Drawer
        drawer = findViewById(R.id.drawerLayout);
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_hamburger_menu);
        toolbar.setTitle("Home");

        toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open,
                R.string.nav_close);


        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);


        //UI Theme
        Intent intent =  getIntent();
        user_theme = intent.getStringExtra("selected_theme");
        switchTheme=findViewById(R.id.switchTheme);
        if(user_theme.equals("light")){
            switchTheme.setChecked(true);
            switchTheme.getThumbDrawable().setColorFilter(Color.parseColor("#B5000000"), PorterDuff.Mode.MULTIPLY);
            switchTheme.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);
            toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
            toolbar.setTitleTextColor(Color.parseColor("#000000"));

            moon=findViewById(R.id.moon);
            sun=findViewById(R.id.sun);
            sun.setImageResource(R.drawable.ic_sun);
            moon.setImageResource(R.drawable.ic_moon);


            TextView title=findViewById(R.id.homeScreenTitleText);
            title.setTextColor(Color.parseColor("#B5000000"));
            for (int i = 0; i < gridLayout.getChildCount(); i++) {

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

            homeLayout.setBackgroundColor(Color.parseColor("#DAE4D6D6"));
        } else if (user_theme.equals("dark")) {
            switchTheme.setChecked(false);
        } else {
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
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("/Users");
        Query emailQuery = databaseReference.orderByChild("email").equalTo(currentUser.getEmail());
        DatabaseReference imageUri=FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid());
        // Get an instance of FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a reference to the storage root
        StorageReference storageRef = storage.getReference();

        // Create a reference to the file you want to access
        StorageReference fileRef = storageRef.child("/profileImages").child(currentUser.getUid());
        profileImage=findViewById(R.id.nav_header_image);

        imageUri.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("profileImageUrl")) {
                        if (!child.getValue().equals("") && !child.getValue().equals("null") && child.getValue() != null) {
                            profileImage=findViewById(R.id.nav_header_image);
                            Glide.with(Home.this)
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

        homeTitleTextView=findViewById(R.id.homeScreenTitleText);
        emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    assert user != null;
                    username = user.getUsername();

                    homeTitleTextView.setText("Hello "+username);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", error.getMessage()); //Never ignore potential errors!

            }
        });







        // Handle switch events
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar);
                    FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice").setValue("light");
                    user_theme="light";


                    switchTheme.getThumbDrawable().setColorFilter(Color.parseColor("#B5000000"), PorterDuff.Mode.MULTIPLY);
                    switchTheme.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);
                    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    toolbar.setTitleTextColor(Color.parseColor("#000000"));

                    moon=findViewById(R.id.moon);
                    sun=findViewById(R.id.sun);
                    sun.setImageResource(R.drawable.ic_sun);
                    moon.setImageResource(R.drawable.ic_moon);


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

                    homeLayout.setBackgroundColor(Color.parseColor("#DAE4D6D6"));


                }else {
                    FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice").setValue("dark");
                    user_theme="dark";

                    toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
                    toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

                    switchTheme.getThumbDrawable().setColorFilter(Color.parseColor("#8c8c8c"), PorterDuff.Mode.MULTIPLY);
                    switchTheme.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);
                    moon=findViewById(R.id.moon);
                    sun=findViewById(R.id.sun);
                    sun.setImageResource(R.drawable.ic_sun_light);
                    moon.setImageResource(R.drawable.ic_moon_light);


                    TextView title=findViewById(R.id.homeScreenTitleText);
                    title.setTextColor(Color.parseColor("#FFFFFF"));
                    for (int i = 0; i < gridLayout.getChildCount(); i++) {

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



        setSingleEvent(gridLayout);
        currentUser.reload();
        //setToggleEvent(gridLayout);




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
                    Intent intent = new Intent(Home.this, Home.class);
                    intent.putExtra("selected_theme",user_theme.toString());
                    startActivity(intent);
                    finish();
                }
            }, 410); // Delay in milliseconds
        }else if (item.getItemId()==R.id.nav_profile_setup) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Home.this, ProfileSetup.class);
                    intent.putExtra("selected_theme",user_theme.toString());
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_workout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Home.this, WorkoutOptions.class);
                    intent.putExtra("selected_theme",user_theme.toString());
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_stats) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Home.this, Home.class);
                    intent.putExtra("selected_theme",user_theme.toString());
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId()==R.id.nav_quiz) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Home.this, Home.class);
                    intent.putExtra("selected_theme",user_theme.toString());
                    startActivity(intent);
                    finish();

                }
            }, 410);

        }else if (item.getItemId()==R.id.nav_logout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    auth.signOut();
                    Intent intent = new Intent(Home.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Home.this, Settings.class);
                    intent.putExtra("selected_theme",user_theme.toString());
                    startActivity(intent);


                }
            }, 410);

        } else if (item.getItemId()==R.id.nav_diet) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Home.this, Home.class);
                    intent.putExtra("selected_theme",user_theme.toString());
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
    /*

*/
    private void setSingleEvent(GridLayout gridLayout) {
        //Loop all child item of Main.java Grid
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            if (gridLayout.getChildAt(i) instanceof CardView) {
                CardView cardView = (CardView) gridLayout.getChildAt(i);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (cardView.getId() == findViewById(R.id.HomeCard).getId() ) {

                            Intent intent = new Intent(Home.this, Home.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();

                        } else if (cardView.getId() == findViewById(R.id.StatsCard).getId()) {
                            Intent intent = new Intent(Home.this, Home.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();
                        } else if (cardView.getId() == findViewById(R.id.StepsCard).getId()) {
                            Intent intent = new Intent(Home.this, Home.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();

                        } else if (cardView.getId() == findViewById(R.id.WorkoutCard).getId()) {
                            Intent intent = new Intent(Home.this, WorkoutOptions.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();

                        } else if (cardView.getId() == findViewById(R.id.dietCard).getId()) {
                            Intent intent = new Intent(Home.this, Home.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();
                        } else if (cardView.getId()==findViewById(R.id.setProfileCard).getId()) {
                            Intent intent = new Intent(Home.this, ProfileSetup.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();
                        }else if (cardView.getId()==findViewById(R.id.logoutCard).getId()) {
                            auth.signOut();
                            Intent intent = new Intent(Home.this, Login.class);
                            startActivity(intent);
                            finish();

                        } else if (cardView.getId()==findViewById(R.id.quizCard).getId()) {
                            Intent intent = new Intent(Home.this, Home.class);
                            intent.putExtra("selected_theme",user_theme.toString());
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

        Intent intent = new Intent(Home.this, Home.class);
        intent.putExtra("selected_theme",user_theme);
        startActivity(intent);
        // Finish the current activity
        finish();
        super.onBackPressed();
    }
}

