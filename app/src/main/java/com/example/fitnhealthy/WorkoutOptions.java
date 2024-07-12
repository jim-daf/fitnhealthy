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

import androidx.activity.OnBackPressedCallback;
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

import java.util.ArrayList;
import java.util.List;


public class WorkoutOptions extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ImageView profileImage;
    DatabaseReference databaseReference;

    ArrayList<String> audioList,datesList,workoutTimesList;
    ArrayList<Integer> caloriesList;
    float[] avgHeartRatesList;

    String username,user_theme,gender,experience,target;
    private long age;
    private float weight,height;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchTheme;
    ScrollView homeLayout, workoutOptionsLayout,workoutsLayout,profileSetupLayout,settingsLayout, physicalDataLayout,workoutMetricsLayout;
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
        workoutOptionsLayout =(ScrollView) findViewById(R.id.selectionCategoriesLayout);
        workoutOptionsLayout.setVisibility(View.VISIBLE);

        homeLayout=(ScrollView) findViewById(R.id.homeScreenLayout);
        homeLayout.setVisibility(View.GONE);

        workoutsLayout=(ScrollView) findViewById(R.id.workoutsLayoutLight);
        workoutsLayout.setVisibility(View.GONE);

        profileSetupLayout = (ScrollView) findViewById(R.id.profileSetupLayout);
        profileSetupLayout.setVisibility(View.GONE);

        settingsLayout = (ScrollView) findViewById(R.id.settingsLayout);
        settingsLayout.setVisibility(View.GONE);

        physicalDataLayout = (ScrollView) findViewById(R.id.updatePhysicalDataLayout);
        physicalDataLayout.setVisibility(View.GONE);

        workoutMetricsLayout=(ScrollView) findViewById(R.id.workoutMetricsLayout);
        workoutMetricsLayout.setVisibility(View.GONE);

        // Nav Drawer
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


        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        gridLayout=(GridLayout) findViewById(R.id.gridLayoutForWorkoutOptions);
        setSingleEvent(gridLayout);
        currentUser.reload();

        //UI Theme
        Intent intent =  getIntent();

        caloriesList=new ArrayList<>();
        workoutTimesList=new ArrayList<>();
        datesList=new ArrayList<>();

        user_theme = intent.getStringExtra("selected_theme");
        age=intent.getLongExtra("age",0L);
        weight=intent.getFloatExtra("weight",0F);
        height=intent.getFloatExtra("height",0F);
        experience=intent.getStringExtra("experience");
        target=intent.getStringExtra("target");
        gender=intent.getStringExtra("gender");
        username=intent.getStringExtra("username");
        audioList=new ArrayList<>();
        audioList=intent.getStringArrayListExtra("savedAudioList");
        datesList=intent.getStringArrayListExtra("datesList");
        caloriesList=intent.getIntegerArrayListExtra("caloriesList");
        workoutTimesList=intent.getStringArrayListExtra("workoutTimesList");
        avgHeartRatesList=new float[datesList.size()];
        avgHeartRatesList=intent.getFloatArrayExtra("avgHeartRatesList");

        if(user_theme.equals("light")){

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


        } else if (user_theme.equals("dark")) {

            toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
            toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

            /*
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

             */
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("/Users");
        DatabaseReference imageUri=FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid());



        // Upload profile image to nav header
        profileImage=findViewById(R.id.nav_header_image);

        imageUri.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("profileImageUrl")) {
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







        //setToggleEvent(gridLayout);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(WorkoutOptions.this, Home.class);
                intent.putExtra("selected_theme",user_theme);
                intent.putExtra("age",age);
                intent.putExtra("weight",weight);
                intent.putExtra("height",height);
                intent.putExtra("username",username);
                intent.putExtra("gender",gender);
                intent.putExtra("experience",experience);
                intent.putExtra("target",target);
                intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                intent.putExtra("caloriesList",caloriesList);
                intent.putExtra("workoutTimesList",workoutTimesList);
                intent.putExtra("datesList",datesList);
                startActivity(intent);
                finish();
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
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
                    startActivity(intent);
                    finish();
                }
            }, 410); // Delay in milliseconds
        }else if (item.getItemId()==R.id.nav_profile_setup) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, ProfileSetup.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_workout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, WorkoutOptions.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_stats) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, Home.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId()==R.id.nav_quiz) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, Home.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
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
        } else if (item.getItemId()==R.id.nav_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, com.example.fitnhealthy.Settings.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
                    startActivity(intent);



                }
            }, 410);

        } else if (item.getItemId()==R.id.nav_diet) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutOptions.this, Home.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
                    startActivity(intent);

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
                            intent.putExtra("selected_theme",user_theme);
                            intent.putExtra("age",age);
                            intent.putExtra("weight",weight);
                            intent.putExtra("height",height);
                            intent.putExtra("username",username);
                            intent.putExtra("gender",gender);
                            intent.putExtra("experience",experience);
                            intent.putExtra("target",target);
                            intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                            intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                            intent.putExtra("caloriesList",caloriesList);
                            intent.putExtra("workoutTimesList",workoutTimesList);
                            intent.putExtra("datesList",datesList);
                            startActivity(intent);
                            finish();

                        } else if (cardView.getId() == findViewById(R.id.SelectAWorkoutCard).getId()) {
                            Intent intent = new Intent(WorkoutOptions.this, Workouts.class);
                            intent.putExtra("selected_theme",user_theme);
                            intent.putExtra("age",age);
                            intent.putExtra("weight",weight);
                            intent.putExtra("height",height);
                            intent.putExtra("username",username);
                            intent.putExtra("gender",gender);
                            intent.putExtra("experience",experience);
                            intent.putExtra("target",target);
                            intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                            intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                            intent.putExtra("caloriesList",caloriesList);
                            intent.putExtra("workoutTimesList",workoutTimesList);
                            intent.putExtra("datesList",datesList);
                            startActivity(intent);
                            finish();
                        } else if (cardView.getId() == findViewById(R.id.GenerateWorkoutCard).getId()) {
                            Intent intent = new Intent(WorkoutOptions.this, Home.class);
                            intent.putExtra("selected_theme",user_theme);
                            intent.putExtra("age",age);
                            intent.putExtra("weight",weight);
                            intent.putExtra("height",height);
                            intent.putExtra("username",username);
                            intent.putExtra("gender",gender);
                            intent.putExtra("experience",experience);
                            intent.putExtra("target",target);
                            intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                            intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                            intent.putExtra("caloriesList",caloriesList);
                            intent.putExtra("workoutTimesList",workoutTimesList);
                            intent.putExtra("datesList",datesList);
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

}
