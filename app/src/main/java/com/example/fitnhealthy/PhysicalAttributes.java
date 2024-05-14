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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PhysicalAttributes extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ImageView profileImage;
    Boolean allFilledFieldsSaved;
    DatabaseReference databaseReference;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    String user_theme,age,height,weight;
    ScrollView homeLayout, workoutOptionsLayout, workoutsLayout, profileSetupLayout, settingsLayout, physicalDataLayout;
    public DrawerLayout drawer;
    Toolbar toolbar;
    TextView titleTxtView;
    TextInputEditText ageInputField, weightInputField, heightInputField;
    public ActionBarDrawerToggle toggle;
    Button saveBtn;


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
        physicalDataLayout.setVisibility(View.VISIBLE);

        // Nav Drawer setup
        drawer = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_hamburger_menu);
        toolbar.setTitle("Physical Attributes");

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

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("profileImageUrl")) {
                        if (!child.getValue().equals("") && !child.getValue().equals("null") && child.getValue() != null) {
                            profileImage = findViewById(R.id.nav_header_image);
                            Glide.with(PhysicalAttributes.this)
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
        titleTxtView = findViewById(R.id.changePhysicalAttributes);

        // Input Fields
        ageInputField = findViewById(R.id.newAge);
        weightInputField = findViewById(R.id.newWeight);
        heightInputField = findViewById(R.id.newHeight);

        //Button
        saveBtn=findViewById(R.id.savePhysicalData);

        // Spinner setup
        Spinner spinner = (Spinner) findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, R.layout.spinner_selected_physical_attributes);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_physical_attributes);

        spinner.setAdapter(adapter);

        //Get user theme
        Intent intent = getIntent();
        user_theme = intent.getStringExtra("selected_theme");

        if (user_theme.equals("light")) {
            // Toolbar
            toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
            toolbar.setTitleTextColor(Color.parseColor("#000000"));

            // Spinner
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

            // Input Fields
            ageInputField.setBackground(getResources().getDrawable(R.drawable.input_background_dark));
            weightInputField.setBackground(getResources().getDrawable(R.drawable.input_background_dark));
            heightInputField.setBackground(getResources().getDrawable(R.drawable.input_background_dark));

            ageInputField.setHintTextColor(Color.parseColor("#DCFFFFFF"));
            weightInputField.setHintTextColor(Color.parseColor("#DCFFFFFF"));
            heightInputField.setHintTextColor(Color.parseColor("#DCFFFFFF"));

            ageInputField.setTextColor(Color.parseColor("#FFFFFF"));
            weightInputField.setTextColor(Color.parseColor("#FFFFFF"));
            heightInputField.setTextColor(Color.parseColor("#FFFFFF"));

            // Button
            saveBtn.setTextColor(Color.parseColor("#FFFFFF"));
            saveBtn.setBackgroundColor(Color.parseColor("#59585a"));

            // Spinner
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner.getBackground().setColorFilter(Color.parseColor("#59585a"), PorterDuff.Mode.SRC_ATOP);

            // Screen Background Color
            physicalDataLayout.setBackgroundColor(Color.parseColor("#DA000000"));


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

                        // Input Fields
                        ageInputField.setBackground(getResources().getDrawable(R.drawable.input_background_dark));
                        weightInputField.setBackground(getResources().getDrawable(R.drawable.input_background_dark));
                        heightInputField.setBackground(getResources().getDrawable(R.drawable.input_background_dark));

                        ageInputField.setHintTextColor(Color.parseColor("#DCFFFFFF"));
                        weightInputField.setHintTextColor(Color.parseColor("#DCFFFFFF"));
                        heightInputField.setHintTextColor(Color.parseColor("#DCFFFFFF"));

                        ageInputField.setTextColor(Color.parseColor("#FFFFFF"));
                        weightInputField.setTextColor(Color.parseColor("#FFFFFF"));
                        heightInputField.setTextColor(Color.parseColor("#FFFFFF"));

                        // Screen Background Color
                        physicalDataLayout.setBackgroundColor(Color.parseColor("#DA000000"));

                        // Button
                        saveBtn.setTextColor(Color.parseColor("#FFFFFF"));
                        saveBtn.setBackgroundColor(Color.parseColor("#59585a"));


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
                age=ageInputField.getText().toString();
                weight=weightInputField.getText().toString();
                height=heightInputField.getText().toString();
                if(TextUtils.isEmpty(age) && TextUtils.isEmpty(weight) && TextUtils.isEmpty(height)){
                    Toast.makeText(PhysicalAttributes.this, "All fields are empty", Toast.LENGTH_SHORT).show();
                }
                else if (!age.isEmpty() && (Integer.valueOf(age)<=0 || Integer.valueOf(age)>100)){
                    Toast.makeText(PhysicalAttributes.this, "Invalid input for field age", Toast.LENGTH_SHORT).show();
                }else if (!height.toString().isEmpty() && (Float.valueOf(height)<0.50 || Float.valueOf(height)>2.40)) {
                    Toast.makeText(PhysicalAttributes.this, "Invalid input for field height", Toast.LENGTH_SHORT).show();
                }else if (!weight.toString().isEmpty() && (Float.valueOf(weight)<10 || Float.valueOf(weight)>350)) {
                    Toast.makeText(PhysicalAttributes.this, "Invalid input for field weight", Toast.LENGTH_SHORT).show();
                }else{
                    allFilledFieldsSaved=true;
                    if (!TextUtils.isEmpty(age)){
                        FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("age").setValue(Integer.valueOf(age)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("User Age","Age successfully saved to database");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("User Age","Age value failed to get saved to database");
                                //Toast.makeText(PhysicalAttributes.this, "An error occured saving the field age", Toast.LENGTH_SHORT).show();
                                allFilledFieldsSaved=false;
                            }
                        });
                    }
                    if(!TextUtils.isEmpty(weight.toString())){
                        String weightFormatted = String.valueOf(BigDecimal.valueOf(Double.valueOf(weight)).setScale(2, RoundingMode.HALF_UP));
                        FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("weight").setValue(Double.valueOf(weightFormatted)).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                allFilledFieldsSaved=false;
                            }
                        });
                    }
                    if(!TextUtils.isEmpty(height.toString())){
                        String heightFormatted = String.valueOf(BigDecimal.valueOf(Double.valueOf(height)).setScale(2, RoundingMode.HALF_UP));
                        FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("height").setValue(Double.valueOf(heightFormatted)).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                allFilledFieldsSaved=false;
                            }
                        });
                    }
                    FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("gender").setValue(spinner.getSelectedItem().toString()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            allFilledFieldsSaved=false;
                        }
                    });
                    if (allFilledFieldsSaved){
                        Toast.makeText(PhysicalAttributes.this, "Data saved", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PhysicalAttributes.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

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
                    Intent intent = new Intent(PhysicalAttributes.this, Home.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();
                }
            }, 410); // Delay in milliseconds
        } else if (item.getItemId() == R.id.nav_profile_setup) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(PhysicalAttributes.this, ProfileSetup.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId() == R.id.nav_workout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(PhysicalAttributes.this, Home.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId() == R.id.nav_stats) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(PhysicalAttributes.this, Home.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId() == R.id.nav_quiz) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(PhysicalAttributes.this, Home.class);
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
                    Intent intent = new Intent(PhysicalAttributes.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId() == R.id.nav_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(PhysicalAttributes.this, Settings.class);
                    intent.putExtra("selected_theme", user_theme);
                    startActivity(intent);
                    finish();

                }
            }, 410);

        } else if (item.getItemId() == R.id.nav_diet) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(PhysicalAttributes.this, Home.class);
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

        Intent intent = new Intent(PhysicalAttributes.this, Settings.class);
        intent.putExtra("selected_theme",user_theme);
        startActivity(intent);
        // Finish the current activity
        finish();
        super.onBackPressed();
    }
}