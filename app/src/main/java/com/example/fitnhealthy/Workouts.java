package com.example.fitnhealthy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Workouts extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ImageView profileImage;
    DatabaseReference databaseReference;
    ArrayList<String> audioList,datesList,workoutTimesList;
    ArrayList<Integer> caloriesList;
    float[] avgHeartRatesList;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    String username,user_theme,gender,experience,target;
    private long age;
    private float weight,height;
    ScrollView homeLayout, workoutOptionsLayout, workoutsLayout, profileSetupLayout,settingsLayout, physicalDataLayout,workoutMetricsLayout;
    GridLayout gridLayout;
    LinearLayout cardContainer,cardContainer2,shortTimeWorkouts,otherWorkouts,myWorkouts;
    WorkoutExerciseSample[] plankWorkoutExercises,cyclingWorkoutExercises,walkingWorkoutExercises,runningWorkoutExercises,tennisWorkoutExercises,volleyWorkoutExercises;
    WorkoutSample plankWorkout,cyclingWorkout,walkingWorkout,runningWorkout,tennisWorkout,volleyWorkout;
    List<String> plankWorkoutExercisesTitles,plankWorkoutExercisesDurations;


    public DrawerLayout drawer;
    Toolbar toolbar;

    public ActionBarDrawerToggle toggle;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_nav_drawer);

        auth = FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();

        //Initialize layout visibilities
        workoutsLayout=(ScrollView) findViewById(R.id.workoutsLayoutLight);
        workoutsLayout.setVisibility(View.VISIBLE);

        workoutOptionsLayout =(ScrollView) findViewById(R.id.selectionCategoriesLayout);
        workoutOptionsLayout.setVisibility(View.GONE);

        homeLayout=(ScrollView) findViewById(R.id.homeScreenLayout);
        homeLayout.setVisibility(View.GONE);

        profileSetupLayout = (ScrollView) findViewById(R.id.profileSetupLayout);
        profileSetupLayout.setVisibility(View.GONE);

        settingsLayout = (ScrollView) findViewById(R.id.settingsLayout);
        settingsLayout.setVisibility(View.GONE);

        physicalDataLayout = (ScrollView) findViewById(R.id.updatePhysicalDataLayout);
        physicalDataLayout.setVisibility(View.GONE);

        workoutMetricsLayout=(ScrollView) findViewById(R.id.workoutMetricsLayout);
        workoutMetricsLayout.setVisibility(View.VISIBLE);

        // Nav Drawer
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


        // Handle clicks on cards
        setSingleEvent(shortTimeWorkouts,otherWorkouts,myWorkouts);

        currentUser.reload();

        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitleTextColor(Color.parseColor("#000000"));

        // Profile image to navdrawer
        databaseReference = FirebaseDatabase.getInstance().getReference("/Users");
        DatabaseReference imageUri=FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid());
        profileImage=findViewById(R.id.nav_header_image);

        imageUri.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("profileImageUrl")) {
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

        cyclingWorkout=new WorkoutSample();
        cyclingWorkout.setTitleOfWorkout("Cycling");
        cyclingWorkout.setDuration("");

        cyclingWorkoutExercises=new WorkoutExerciseSample[1];
        cyclingWorkoutExercises[0]=new WorkoutExerciseSample();
        cyclingWorkoutExercises[0].setTitle("Cycling");
        cyclingWorkoutExercises[0].setDuration("");

        walkingWorkout=new WorkoutSample();
        walkingWorkout.setTitleOfWorkout("Walking");
        walkingWorkout.setDuration("");

        walkingWorkoutExercises=new WorkoutExerciseSample[1];
        walkingWorkoutExercises[0]=new WorkoutExerciseSample();
        walkingWorkoutExercises[0].setTitle("Walking");
        walkingWorkoutExercises[0].setDuration("");

        runningWorkout=new WorkoutSample();
        runningWorkout.setTitleOfWorkout("Running");
        runningWorkout.setDuration("");

        runningWorkoutExercises=new WorkoutExerciseSample[1];
        runningWorkoutExercises[0]=new WorkoutExerciseSample();
        runningWorkoutExercises[0].setTitle("Running");
        runningWorkoutExercises[0].setDuration("");

        tennisWorkout=new WorkoutSample();
        tennisWorkout.setTitleOfWorkout("Tennis");
        tennisWorkout.setDuration("");

        tennisWorkoutExercises=new WorkoutExerciseSample[1];
        tennisWorkoutExercises[0]=new WorkoutExerciseSample();
        tennisWorkoutExercises[0].setTitle("Tennis");
        tennisWorkoutExercises[0].setDuration("");

        volleyWorkout=new WorkoutSample();
        volleyWorkout.setTitleOfWorkout("Volleyball");
        volleyWorkout.setDuration("");

        volleyWorkoutExercises=new WorkoutExerciseSample[1];
        volleyWorkoutExercises[0]=new WorkoutExerciseSample();
        volleyWorkoutExercises[0].setTitle("Volleyball");
        volleyWorkoutExercises[0].setDuration("");

        plankWorkout= new WorkoutSample();
        plankWorkout.setTitleOfWorkout("Planks workout challenge");
        plankWorkout.setNumOfExercises("10 workouts");
        plankWorkout.setDuration("11 minutes");
        plankWorkoutExercises = new WorkoutExerciseSample[10];

        plankWorkoutExercises[0] = new WorkoutExerciseSample();
        plankWorkoutExercises[0].setTitle("Regular plank");
        plankWorkoutExercises[0].setDuration("45 sec");
        plankWorkoutExercises[1] = new WorkoutExerciseSample();
        plankWorkoutExercises[1].setTitle("Mountain Climber");
        plankWorkoutExercises[1].setDuration("45 sec");
        plankWorkoutExercises[2] = new WorkoutExerciseSample();
        plankWorkoutExercises[2].setTitle("Dolphin pose");
        plankWorkoutExercises[2].setDuration("45 sec");
        plankWorkoutExercises[3] = new WorkoutExerciseSample();
        plankWorkoutExercises[3].setTitle("Single leg plank");
        plankWorkoutExercises[3].setDuration("45 sec");
        plankWorkoutExercises[4] = new WorkoutExerciseSample();
        plankWorkoutExercises[4].setTitle("Plank hip dips");
        plankWorkoutExercises[4].setDuration("45 sec");
        plankWorkoutExercises[5] = new WorkoutExerciseSample();
        plankWorkoutExercises[5].setTitle("Side plank");
        plankWorkoutExercises[5].setDuration("45 sec");
        //plankWorkoutExercises[0].setGif(getResources().getDrawable(R.drawable.gif_plank_hip_dips,getTheme()));
        plankWorkoutExercises[6] = new WorkoutExerciseSample();
        plankWorkoutExercises[6].setTitle("Spiderman plank");
        plankWorkoutExercises[6].setDuration("45 sec");
        //plankWorkoutExercises[1].setGif(getResources().getDrawable(R.drawable.mountain_climber,getTheme()));

        plankWorkoutExercises[7] = new WorkoutExerciseSample();
        plankWorkoutExercises[7].setTitle("Single arm plank");
        plankWorkoutExercises[7].setDuration("45 sec");
        //plankWorkoutExercises[2].setGif(getResources().getDrawable(R.drawable.dolphin_pose,getTheme()));
        plankWorkoutExercises[8] = new WorkoutExerciseSample();
        plankWorkoutExercises[8].setTitle("Commando plank");
        plankWorkoutExercises[8].setDuration("45 sec");
        //plankWorkoutExercises[3].setGif(getResources().getDrawable(R.drawable.plank_up_down,getTheme()));

        plankWorkoutExercises[9] = new WorkoutExerciseSample();
        plankWorkoutExercises[9].setTitle("Plank jacks");
        plankWorkoutExercises[9].setDuration("45 sec");
        //plankWorkoutExercises[4].setGif(getResources().getDrawable(R.drawable.side_plank_left,getTheme()));
        /*
        for (int i = 0; i < plankWorkoutExercises.length; i++) {
            plankWorkoutExercisesTitles.add(plankWorkoutExercises[i].getTitle());
            plankWorkoutExercisesDurations.add(plankWorkoutExercises[i].getDuration());
        }

         */


        // Spinner setup
        Spinner spinner = (Spinner) findViewById(R.id.spinner_difficulty);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_values, R.layout.spinner_selected_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Intent intent = getIntent();
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

        if (user_theme.equals("light")){
            toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
            toolbar.setTitleTextColor(Color.parseColor("#000000"));
        } else if (user_theme.equals("dark")) {
            toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
            toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

            // Set spinner selected item color
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

            //CheckedTextView spinnerDropdownItemTxt = (CheckedTextView) findViewById(R.id.spinnerDropdownItemTxt);

            TextView bodyFocusTitle= (TextView) findViewById(R.id.bodyFocustxt);
            bodyFocusTitle.setTextColor(Color.parseColor("#ffffff"));
            gridLayout=(GridLayout) findViewById(R.id.gridLayoutWorkouts);
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                CardView cardView = (CardView) gridLayout.getChildAt(i);
                cardView.setCardBackgroundColor(Color.parseColor("#393737"));
                for (int j = 0; j < cardView.getChildCount(); j++) {
                    LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                    for (int k = 0; k < linearLayout.getChildCount(); k++) {
                        if (linearLayout.getChildAt(k) instanceof TextView){
                            TextView txtView = (TextView) linearLayout.getChildAt(k);
                            txtView.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                }

            }
            cardContainer=(LinearLayout) findViewById(R.id.cardContainer);
            for (int i = 0; i < cardContainer.getChildCount(); i++) {

                CardView cardView=(CardView) cardContainer.getChildAt(i);
                cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                for (int j = 0; j < cardView.getChildCount(); j++) {
                    LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                    for (int k = 0; k < linearLayout.getChildCount(); k++) {
                        if (linearLayout.getChildAt(k) instanceof TextView){
                            TextView txtView = (TextView) linearLayout.getChildAt(k);
                            txtView.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                }

            }
            cardContainer2=(LinearLayout) findViewById(R.id.cardContainer2);
            for (int i = 0; i < cardContainer2.getChildCount(); i++) {

                CardView cardView=(CardView) cardContainer2.getChildAt(i);
                cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                for (int j = 0; j < cardView.getChildCount(); j++) {
                    LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                    for (int k = 0; k < linearLayout.getChildCount(); k++) {
                        if (linearLayout.getChildAt(k) instanceof TextView){
                            TextView txtView = (TextView) linearLayout.getChildAt(k);
                            txtView.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                }

            }
            shortTimeWorkouts=(LinearLayout) findViewById(R.id.shortTimeWorkoutsContainer);
            for (int i = 0; i < shortTimeWorkouts.getChildCount(); i++) {
                CardView cardView=(CardView) shortTimeWorkouts.getChildAt(i);
                cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                for (int j = 0; j < cardView.getChildCount(); j++) {
                    LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                    for (int k = 0; k < linearLayout.getChildCount(); k++) {
                        if (linearLayout.getChildAt(k) instanceof TextView){
                            TextView txtView = (TextView) linearLayout.getChildAt(k);
                            txtView.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                }
            }
            otherWorkouts=(LinearLayout) findViewById(R.id.otherWorkoutsLinearLayout);
            for (int i = 0; i < otherWorkouts.getChildCount(); i++) {
                CardView cardView=(CardView) otherWorkouts.getChildAt(i);
                cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                for (int j = 0; j < cardView.getChildCount(); j++) {
                    LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                    for (int k = 0; k < linearLayout.getChildCount(); k++) {
                        if (linearLayout.getChildAt(k) instanceof TextView){
                            TextView txtView = (TextView) linearLayout.getChildAt(k);
                            txtView.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                }
            }
            myWorkouts=(LinearLayout) findViewById(R.id.myWorkoutsLinearLayout);
            for (int i = 0; i < myWorkouts.getChildCount(); i++) {
                if (myWorkouts.getChildAt(i) instanceof CardView){
                    CardView cardView=(CardView) myWorkouts.getChildAt(i);
                    cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                    for (int j = 0; j < cardView.getChildCount(); j++) {
                        LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                        for (int k = 0; k < linearLayout.getChildCount(); k++) {
                            if (linearLayout.getChildAt(k) instanceof TextView){
                                TextView txtView = (TextView) linearLayout.getChildAt(k);
                                txtView.setTextColor(Color.parseColor("#FFFFFF"));
                            }
                        }
                    }
                }

            }
            workoutsLayout.setBackgroundColor(Color.parseColor("#DA000000"));

        } else {
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue().equals("light")){
                        //switchTheme.setChecked(true);




                        //spinnerDropdownItemTxt.setTextColor(Color.parseColor("#FFFFFF"));

                        //spinnerDropdownItemTxt.setBackgroundColor(Color.parseColor("#393737"));

                        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        toolbar.setTitleTextColor(Color.parseColor("#000000"));



                    } else if (snapshot.getValue().equals("dark")) {

                        FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice").setValue("dark");

                        toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
                        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
                        TextView spinnerSelectedItemTxt = (TextView) findViewById(R.id.spinnerSelectedItemTxt);
                        spinnerSelectedItemTxt.setTextColor(Color.parseColor("#FFFFFF"));
                        spinner.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

                        //CheckedTextView spinnerDropdownItemTxt = (CheckedTextView) findViewById(R.id.spinnerDropdownItemTxt);

                        TextView bodyFocusTitle= (TextView) findViewById(R.id.bodyFocustxt);
                        bodyFocusTitle.setTextColor(Color.parseColor("#ffffff"));
                        gridLayout=(GridLayout) findViewById(R.id.gridLayoutWorkouts);
                        for (int i = 0; i < gridLayout.getChildCount(); i++) {
                            CardView cardView = (CardView) gridLayout.getChildAt(i);
                            cardView.setCardBackgroundColor(Color.parseColor("#393737"));
                            for (int j = 0; j < cardView.getChildCount(); j++) {
                                LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                                for (int k = 0; k < linearLayout.getChildCount(); k++) {
                                    if (linearLayout.getChildAt(k) instanceof TextView){
                                        TextView txtView = (TextView) linearLayout.getChildAt(k);
                                        txtView.setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                }
                            }

                        }
                        cardContainer=(LinearLayout) findViewById(R.id.cardContainer);
                        for (int i = 0; i < cardContainer.getChildCount(); i++) {

                            CardView cardView=(CardView) cardContainer.getChildAt(i);
                            cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                            for (int j = 0; j < cardView.getChildCount(); j++) {
                                LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                                for (int k = 0; k < linearLayout.getChildCount(); k++) {
                                    if (linearLayout.getChildAt(k) instanceof TextView){
                                        TextView txtView = (TextView) linearLayout.getChildAt(k);
                                        txtView.setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                }
                            }

                        }
                        cardContainer2=(LinearLayout) findViewById(R.id.cardContainer2);
                        for (int i = 0; i < cardContainer2.getChildCount(); i++) {

                            CardView cardView=(CardView) cardContainer2.getChildAt(i);
                            cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                            for (int j = 0; j < cardView.getChildCount(); j++) {
                                LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                                for (int k = 0; k < linearLayout.getChildCount(); k++) {
                                    if (linearLayout.getChildAt(k) instanceof TextView){
                                        TextView txtView = (TextView) linearLayout.getChildAt(k);
                                        txtView.setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                }
                            }

                        }
                        shortTimeWorkouts=(LinearLayout) findViewById(R.id.shortTimeWorkoutsContainer);
                        for (int i = 0; i < shortTimeWorkouts.getChildCount(); i++) {
                            CardView cardView=(CardView) shortTimeWorkouts.getChildAt(i);
                            cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                            for (int j = 0; j < cardView.getChildCount(); j++) {
                                LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                                for (int k = 0; k < linearLayout.getChildCount(); k++) {
                                    if (linearLayout.getChildAt(k) instanceof TextView){
                                        TextView txtView = (TextView) linearLayout.getChildAt(k);
                                        txtView.setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                }
                            }
                        }
                        otherWorkouts=(LinearLayout) findViewById(R.id.otherWorkoutsLinearLayout);
                        for (int i = 0; i < otherWorkouts.getChildCount(); i++) {
                            CardView cardView=(CardView) otherWorkouts.getChildAt(i);
                            cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                            for (int j = 0; j < cardView.getChildCount(); j++) {
                                LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                                for (int k = 0; k < linearLayout.getChildCount(); k++) {
                                    if (linearLayout.getChildAt(k) instanceof TextView){
                                        TextView txtView = (TextView) linearLayout.getChildAt(k);
                                        txtView.setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                }
                            }
                        }
                        myWorkouts=(LinearLayout) findViewById(R.id.myWorkoutsLinearLayout);
                        for (int i = 0; i < myWorkouts.getChildCount(); i++) {
                            if (myWorkouts.getChildAt(i) instanceof CardView){
                                CardView cardView=(CardView) myWorkouts.getChildAt(i);
                                cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                                for (int j = 0; j < cardView.getChildCount(); j++) {
                                    LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(j);
                                    for (int k = 0; k < linearLayout.getChildCount(); k++) {
                                        if (linearLayout.getChildAt(k) instanceof TextView){
                                            TextView txtView = (TextView) linearLayout.getChildAt(k);
                                            txtView.setTextColor(Color.parseColor("#FFFFFF"));
                                        }
                                    }
                                }
                            }

                        }
                        workoutsLayout.setBackgroundColor(Color.parseColor("#DA000000"));


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Create an intent to start a new activity
                Intent intent = new Intent(Workouts.this, WorkoutOptions.class);
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
                // Finish the current activity
                finish();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);


        return true;

    }

    // Handle clicks on overflow menu
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle clicks on navigation drawer
    public Boolean onNavigationItemSelected(MenuItem item){

        if(item.getItemId()==R.id.nav_home){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Workouts.this, Home.class);
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
                    Intent intent = new Intent(Workouts.this, ProfileSetup.class);
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
                    Intent intent = new Intent(Workouts.this, Home.class);
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
                    Intent intent = new Intent(Workouts.this, Home.class);
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
                    Intent intent = new Intent(Workouts.this, Home.class);
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
                    Intent intent = new Intent(Workouts.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Workouts.this, com.example.fitnhealthy.Settings.class);
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
                    Intent intent = new Intent(Workouts.this, Home.class);
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


    // Handle clicks on cards
    private void setSingleEvent(LinearLayout shortTimeWorkouts,LinearLayout otherWorkouts,LinearLayout myWorkouts) {
        CardView chestCard=(CardView) findViewById(R.id.chestCard);
        CardView absCard=(CardView) findViewById(R.id.absCard);
        CardView legsCard=(CardView) findViewById(R.id.legsCard);
        CardView armsCard=(CardView) findViewById(R.id.armsCard);
        
        
        chestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Workouts.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        absCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Workouts.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        legsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Workouts.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        armsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Workouts.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        shortTimeWorkouts=(LinearLayout) findViewById(R.id.shortTimeWorkoutsContainer);
        for (int i = 0; i < shortTimeWorkouts.getChildCount(); i++) {
            CardView cardView = (CardView) shortTimeWorkouts.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cardView.getId()==R.id.planks10mincard){
                        Intent intent = new Intent(Workouts.this, WorkoutExercises.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        intent.putExtra("WorkoutExercises",plankWorkoutExercises);
                        intent.putExtra("Workout",plankWorkout);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        startActivity(intent);
                        finish();
                    } else if (cardView.getId()==R.id.tabata4Card) {
                        Intent intent = new Intent(Workouts.this, WorkoutOptions.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putExtra("WorkoutExercises",plankWorkoutExercises);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        startActivity(intent);
                        finish();
                    } else if (cardView.getId()==R.id.fatBurning10Card) {
                        Intent intent = new Intent(Workouts.this, WorkoutOptions.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putExtra("WorkoutExercises",plankWorkoutExercises);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }
        otherWorkouts=(LinearLayout) findViewById(R.id.otherWorkoutsLinearLayout);
        for (int i = 0; i < otherWorkouts.getChildCount(); i++) {
            CardView cardView = (CardView) otherWorkouts.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cardView.getId()==R.id.volleyCard){
                        Intent intent = new Intent(Workouts.this, WorkoutExercises.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        intent.putExtra("WorkoutExercises",volleyWorkoutExercises);
                        intent.putExtra("Workout",volleyWorkout);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        startActivity(intent);
                        finish();
                    } else if (cardView.getId()==R.id.tennisCard) {
                        Intent intent = new Intent(Workouts.this, WorkoutExercises.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        intent.putExtra("WorkoutExercises",tennisWorkoutExercises);
                        intent.putExtra("Workout",tennisWorkout);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        startActivity(intent);
                        finish();
                    } else if (cardView.getId()==R.id.walkingCard) {
                        Intent intent = new Intent(Workouts.this, WorkoutExercises.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        intent.putExtra("WorkoutExercises",walkingWorkoutExercises);
                        intent.putExtra("Workout",walkingWorkout);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        startActivity(intent);
                        finish();
                    } else if (cardView.getId()==R.id.runningCard) {
                        Intent intent = new Intent(Workouts.this, WorkoutExercises.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        intent.putExtra("WorkoutExercises",runningWorkoutExercises);
                        intent.putExtra("Workout",runningWorkout);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        startActivity(intent);
                        finish();
                    } else if (cardView.getId()==R.id.cyclingCard) {
                        Intent intent = new Intent(Workouts.this, WorkoutExercises.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        intent.putExtra("WorkoutExercises",cyclingWorkoutExercises);
                        intent.putExtra("Workout",cyclingWorkout);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }
        myWorkouts=(LinearLayout) findViewById(R.id.myWorkoutsLinearLayout);
        for (int i = 0; i < myWorkouts.getChildCount(); i++) {

            if (myWorkouts.getChildAt(i) instanceof CardView){
                CardView cardView = (CardView) myWorkouts.getChildAt(i);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (cardView.getId()==R.id.planks10mincard){
                            Intent intent = new Intent(Workouts.this, WorkoutOptions.class);
                            intent.putExtra("selected_theme",user_theme);
                            intent.putExtra("age",age);
                            intent.putExtra("weight",weight);
                            intent.putExtra("height",height);
                            intent.putExtra("username",username);
                            intent.putExtra("gender",gender);
                            intent.putExtra("experience",experience);
                            intent.putExtra("target",target);
                            intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                            intent.putStringArrayListExtra("workoutExercisesTitles", (ArrayList<String>) plankWorkoutExercisesTitles);
                            intent.putStringArrayListExtra("workoutExercisesDurations", (ArrayList<String>) plankWorkoutExercisesTitles);
                            intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                            intent.putExtra("caloriesList",caloriesList);
                            intent.putExtra("workoutTimesList",workoutTimesList);
                            intent.putExtra("datesList",datesList);
                            startActivity(intent);
                            finish();
                        } else if (cardView.getId()==R.id.tabata4Card) {
                            Intent intent = new Intent(Workouts.this, WorkoutOptions.class);
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
                        } else if (cardView.getId()==R.id.fatBurning10Card) {
                            Intent intent = new Intent(Workouts.this, WorkoutOptions.class);
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
                    }
                });
            }


        }
    }


}
