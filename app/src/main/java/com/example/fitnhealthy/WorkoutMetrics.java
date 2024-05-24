package com.example.fitnhealthy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkoutMetrics extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ImageView profileImage;
    DatabaseReference databaseReference;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    String user_theme,target,experience,frequencyOfTraining;
    ScrollView homeLayout, workoutOptionsLayout, workoutsLayout, profileSetupLayout, settingsLayout, physicalDataLayout,workoutMetricsLayout;
    public DrawerLayout drawer;
    Toolbar toolbar;
    TextView titleTxtView,levelOfExperienceTxtView,targetsTxtView,frequencyOfTrainingTxtView;
    Spinner spinnerExperienceLevels,spinnerTargets,spinnerFrequencyOfTraining;
    LinearLayout workoutMetricsLinearLayout;

    public ActionBarDrawerToggle toggle;
    Button saveBtn;

    Boolean allFieldsSaved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_nav_drawer);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        //Initialize layout visibilities
        workoutsLayout = (ScrollView) findViewById(R.id.workoutsLayoutLight);
        workoutsLayout.setVisibility(View.GONE);

        settingsLayout = (ScrollView) findViewById(R.id.settingsLayout);
        settingsLayout.setVisibility(View.GONE);

        workoutOptionsLayout = (ScrollView) findViewById(R.id.selectionCategoriesLayout);
        workoutOptionsLayout.setVisibility(View.GONE);

        homeLayout = (ScrollView) findViewById(R.id.homeScreenLayout);
        homeLayout.setVisibility(View.GONE);

        profileSetupLayout = (ScrollView) findViewById(R.id.profileSetupLayout);
        profileSetupLayout.setVisibility(View.GONE);

        physicalDataLayout = (ScrollView) findViewById(R.id.updatePhysicalDataLayout);
        physicalDataLayout.setVisibility(View.GONE);

        workoutMetricsLayout=(ScrollView) findViewById(R.id.workoutMetricsLayout);
        workoutMetricsLayout.setVisibility(View.VISIBLE);



        // Nav Drawer setup
        drawer = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_hamburger_menu);
        toolbar.setTitle("Workout Metrics");

        toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open,
                R.string.nav_close);


        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);


        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitleTextColor(Color.parseColor("#000000"));

        // Profile image to navdrawer
        databaseReference = FirebaseDatabase.getInstance().getReference("/Users");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid());
        profileImage = findViewById(R.id.nav_header_image);

        workoutMetricsLinearLayout=findViewById(R.id.workoutMetricsLinearLayout);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("profileImageUrl")) {
                        if (!child.getValue().equals("") && !child.getValue().equals("null") && child.getValue() != null) {
                            profileImage = findViewById(R.id.nav_header_image);
                            Glide.with(WorkoutMetrics.this)
                                    .load(child.getValue())
                                    .into(profileImage);

                        }


                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // TextViews
        titleTxtView = findViewById(R.id.title_workoutMetrics);
        levelOfExperienceTxtView=findViewById(R.id.levelOfExperienceTxtView);
        targetsTxtView=findViewById(R.id.targetsTxtView);
        frequencyOfTrainingTxtView=findViewById(R.id.frequencyTxtView);

        //Button
        saveBtn=findViewById(R.id.saveWorkoutData);

        // Spinners setup
        spinnerExperienceLevels = (Spinner) findViewById(R.id.spinner_experienceLevel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.experience_values, R.layout.spinner_selected_physical_attributes);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_physical_attributes);
        spinnerExperienceLevels.setAdapter(adapter);

        spinnerTargets = (Spinner) findViewById(R.id.spinner_targets);
        ArrayAdapter<CharSequence> adapterTargets = ArrayAdapter.createFromResource(this,
                R.array.targets, R.layout.spinner_selected_physical_attributes);
        adapterTargets.setDropDownViewResource(R.layout.spinner_dropdown_physical_attributes);
        spinnerTargets.setAdapter(adapterTargets);

        spinnerFrequencyOfTraining = (Spinner) findViewById(R.id.spinnerFrequencyOfTraining);
        ArrayAdapter<CharSequence> adapterFrequency = ArrayAdapter.createFromResource(this,
                R.array.frequency_values, R.layout.spinner_selected_physical_attributes);
        adapterFrequency.setDropDownViewResource(R.layout.spinner_dropdown_physical_attributes);
        spinnerFrequencyOfTraining.setAdapter(adapterFrequency);

        //Get user theme
        Intent intent = getIntent();
        user_theme = intent.getStringExtra("selected_theme");

        if (user_theme.equals("light")) {
            // Toolbar
            toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
            toolbar.setTitleTextColor(Color.parseColor("#000000"));


            spinnerExperienceLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.parseColor("#B5000000"));

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            spinnerTargets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.parseColor("#B5000000"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinnerFrequencyOfTraining.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.parseColor("#B5000000"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } else if (user_theme.equals("dark")) {

            // Toolbar
            toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
            toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

            // TextViews
            titleTxtView.setTextColor(Color.parseColor("#FFFFFF"));
            levelOfExperienceTxtView.setTextColor(Color.parseColor("#FFFFFF"));
            targetsTxtView.setTextColor(Color.parseColor("#FFFFFF"));
            frequencyOfTrainingTxtView.setTextColor(Color.parseColor("#FFFFFF"));


            // Input Fields
            for (int i = 0; i < workoutMetricsLinearLayout.getChildCount(); i++) {
                if (workoutMetricsLinearLayout.getChildAt(i) instanceof CardView){
                    CardView cardView = (CardView) workoutMetricsLinearLayout.getChildAt(i);
                    cardView.setCardBackgroundColor(Color.parseColor("#59585a"));
                }

            }


            // Button
            saveBtn.setTextColor(Color.parseColor("#FFFFFF"));
            saveBtn.setBackgroundColor(Color.parseColor("#59585a"));

            // Spinners
            spinnerExperienceLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinnerExperienceLevels.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

            spinnerTargets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinnerTargets.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

            spinnerFrequencyOfTraining.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinnerFrequencyOfTraining.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

            // Screen Background Color
            workoutMetricsLayout.setBackgroundColor(Color.parseColor("#DA000000"));


        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue().equals("light")) {


                        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        toolbar.setTitleTextColor(Color.parseColor("#000000"));


                    } else if (snapshot.getValue().equals("dark")) {

                        FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice").setValue("dark");

                        // Toolbar
                        toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
                        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

                        // TextViews
                        titleTxtView.setTextColor(Color.parseColor("#B5000000"));

                        // Screen Background Color
                        workoutMetricsLayout.setBackgroundColor(Color.parseColor("#DA000000"));


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allFieldsSaved=true;
                target=spinnerTargets.getSelectedItem().toString();
                experience=spinnerExperienceLevels.getSelectedItem().toString();
                frequencyOfTraining=spinnerFrequencyOfTraining.getSelectedItem().toString();
                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("Target").setValue(target).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("User Target","Target successfully saved to database");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User's Target","Target failed to get saved to database");
                        //Toast.makeText(PhysicalAttributes.this, "An error occured saving the field age", Toast.LENGTH_SHORT).show();
                        allFieldsSaved=false;
                    }
                });
                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("Experience").setValue(experience).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("User experience","Experience successfully saved to database");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User's experience","Experience failed to get saved to database");
                        //Toast.makeText(PhysicalAttributes.this, "An error occured saving the field age", Toast.LENGTH_SHORT).show();
                        allFieldsSaved=false;
                    }
                });
                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("FrequencyOfTraining").setValue(frequencyOfTraining).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("User's Frequency of training","Successfully saved to database");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User's Frequency of training","Failed to get saved to database");
                        //Toast.makeText(PhysicalAttributes.this, "An error occured saving the field age", Toast.LENGTH_SHORT).show();
                        allFieldsSaved=false;
                    }
                });
                if (allFieldsSaved){
                    Toast.makeText(WorkoutMetrics.this, "Data saved", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(WorkoutMetrics.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    // Get overflow menu
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
    public Boolean onNavigationItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.nav_home) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutMetrics.this, Home.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();
                }
            }, 410); // Delay in milliseconds
        } else if (item.getItemId() == R.id.nav_profile_setup) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutMetrics.this, ProfileSetup.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId() == R.id.nav_workout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutMetrics.this, Home.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId() == R.id.nav_stats) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutMetrics.this, Home.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId() == R.id.nav_quiz) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutMetrics.this, Home.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId() == R.id.nav_logout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    auth.signOut();
                    Intent intent = new Intent(WorkoutMetrics.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId() == R.id.nav_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutMetrics.this, com.example.fitnhealthy.Settings.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);


                }
            }, 410);

        } else if (item.getItemId() == R.id.nav_diet) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WorkoutMetrics.this, Home.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    public void onConfigurationChanged(@NonNull Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        toggle.syncState();
    }
    @Override
    public void onBackPressed() {
        // Create an intent to start a new activity

        Intent intent = new Intent(WorkoutMetrics.this, Settings.class);
        intent.putExtra("selected_theme",user_theme);
        startActivity(intent);
        // Finish the current activity
        finish();
        super.onBackPressed();
    }
}
