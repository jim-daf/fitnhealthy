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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class Settings extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ImageView moon,sun,profileImage;
    LinearLayout backgroundProfile,backgroundBiometric,mainBackground,backgroundWorkoutMetrics;
    DatabaseReference databaseReference;
    ArrayList<String> audioList,datesList,workoutTimesList;
    ArrayList<Integer> caloriesList;
    float[] avgHeartRatesList;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    String username,user_theme,gender,experience,target;
    private long age;
    private float weight,height;
    ScrollView homeLayout, workoutOptionsLayout, workoutsLayout, profileSetupLayout, settingsLayout, physicalDataLayout,workoutMetricsLayout;
    public DrawerLayout drawer;
    Toolbar toolbar;
    TextView updatePhysicalData, updateProfileData, notificationsTxtView, notificationsOff, notificationsOn, healthAndConnectTxtView,connectOff,connectOn,settingsTheme,languageTxtView,updateWorkoutData;
    public ActionBarDrawerToggle toggle;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchTheme,switchNotifs, switchHealthConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_nav_drawer);

        auth = FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();

        //Initialize layout visibilities
        workoutsLayout=(ScrollView) findViewById(R.id.workoutsLayoutLight);
        workoutsLayout.setVisibility(View.GONE);

        settingsLayout=(ScrollView) findViewById(R.id.settingsLayout);
        settingsLayout.setVisibility(View.VISIBLE);

        workoutOptionsLayout =(ScrollView) findViewById(R.id.selectionCategoriesLayout);
        workoutOptionsLayout.setVisibility(View.GONE);

        homeLayout=(ScrollView) findViewById(R.id.homeScreenLayout);
        homeLayout.setVisibility(View.GONE);

        profileSetupLayout = (ScrollView) findViewById(R.id.profileSetupLayout);
        profileSetupLayout.setVisibility(View.GONE);

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
        toolbar.setTitle("Settings");

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
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid());
        profileImage=findViewById(R.id.nav_header_image);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("profileImageUrl")) {
                        if (!child.getValue().equals("") && !child.getValue().equals("null") && child.getValue() != null) {
                            profileImage=findViewById(R.id.nav_header_image);
                            Glide.with(Settings.this)
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

        // Spinner setup
        Spinner spinner = (Spinner) findViewById(R.id.spinner_languages);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, R.layout.spinner_selected_settings);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_settings);

        spinner.setAdapter(adapter);

        // Switches
        switchTheme= findViewById(R.id.theme_switch_settings);
        switchNotifs= findViewById(R.id.notifications_switch);
        switchHealthConnect=findViewById(R.id.healthandconnect_switch);

        // ImageViews
        sun=findViewById(R.id.sun_settings);
        moon=findViewById(R.id.moon_settings);

        // TextViews
        notificationsTxtView=findViewById(R.id.notificationsTxtView);
        notificationsOn=findViewById(R.id.notificationsOn);
        notificationsOff=findViewById(R.id.notificationsOff);
        healthAndConnectTxtView=findViewById(R.id.healthandconnectTxtView);
        connectOff=findViewById(R.id.connectOff);
        connectOn=findViewById(R.id.connectOn);
        settingsTheme=findViewById(R.id.settingsTheme);
        languageTxtView=findViewById(R.id.languageTxtView);
        updatePhysicalData =findViewById(R.id.updateBiometricData);
        updateProfileData=findViewById(R.id.updateProfileData);
        updateWorkoutData=findViewById(R.id.updateWorkoutData);

        // Backgrounds
        backgroundProfile=findViewById(R.id.backgroundOfProfileData);
        backgroundBiometric=findViewById(R.id.backgroundOfBiometricData);
        mainBackground=findViewById(R.id.mainBackground);
        backgroundWorkoutMetrics=findViewById(R.id.backgroundOfWorkoutData);

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
            //Toolbar
            toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
            toolbar.setTitleTextColor(Color.parseColor("#000000"));

            //Switches
            switchTheme.setChecked(true);
            switchTheme.getThumbDrawable().setColorFilter(Color.parseColor("#B5000000"), PorterDuff.Mode.MULTIPLY);
            switchTheme.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

            switchNotifs.getThumbDrawable().setColorFilter(Color.parseColor("#B5000000"), PorterDuff.Mode.MULTIPLY);
            switchNotifs.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

            switchHealthConnect.getThumbDrawable().setColorFilter(Color.parseColor("#B5000000"), PorterDuff.Mode.MULTIPLY);
            switchHealthConnect.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

            // ImageViews
            sun.setImageResource(R.drawable.ic_sun_settings);
            moon.setImageResource(R.drawable.ic_moon_settings);

            // Spinner
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner.getBackground().setColorFilter(Color.parseColor("#8c8c8c"), PorterDuff.Mode.SRC_ATOP);


            //TextViews
            notificationsTxtView.setTextColor(Color.parseColor("#000000"));
            notificationsOff.setTextColor(Color.parseColor("#000000"));
            notificationsOn.setTextColor(Color.parseColor("#000000"));
            healthAndConnectTxtView.setTextColor(Color.parseColor("#000000"));
            connectOff.setTextColor(Color.parseColor("#000000"));
            connectOn.setTextColor(Color.parseColor("#000000"));
            settingsTheme.setTextColor(Color.parseColor("#000000"));
            languageTxtView.setTextColor(Color.parseColor("#000000"));
            updateProfileData.setTextColor(Color.parseColor("#000000"));
            updatePhysicalData.setTextColor(Color.parseColor("#000000"));
            updateWorkoutData.setTextColor(Color.parseColor("#000000"));

            //Drawable arrows
            updatePhysicalData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow,getTheme()),null);
            updateProfileData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow,getTheme()),null);
            updateWorkoutData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow,getTheme()),null);

            //Backgrounds
            mainBackground.setBackground(getResources().getDrawable(R.drawable.input_background2));
            backgroundProfile.setBackground(getResources().getDrawable(R.drawable.input_background2));
            backgroundBiometric.setBackground(getResources().getDrawable(R.drawable.input_background2));
            backgroundWorkoutMetrics.setBackground(getResources().getDrawable(R.drawable.input_background2));

            // settingsScreenBackground
            settingsLayout.setBackgroundColor(Color.parseColor("#DAE4D6D6"));

        } else if (user_theme.equals("dark")) {

            // Toolbar
            toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
            toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

            // Switches
            switchTheme.setChecked(false);
            switchTheme.getThumbDrawable().setColorFilter(Color.parseColor("#8c8c8c"), PorterDuff.Mode.MULTIPLY);
            switchTheme.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

            switchNotifs.getThumbDrawable().setColorFilter(Color.parseColor("#8c8c8c"), PorterDuff.Mode.MULTIPLY);
            switchNotifs.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

            switchHealthConnect.getThumbDrawable().setColorFilter(Color.parseColor("#8c8c8c"), PorterDuff.Mode.MULTIPLY);
            switchHealthConnect.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

            // ImageViews
            sun.setImageResource(R.drawable.ic_sun_settings_light);
            moon.setImageResource(R.drawable.ic_moon_settings_light);

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

            spinner.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

            //TextViews
            notificationsTxtView.setTextColor(Color.parseColor("#ffffff"));
            notificationsOff.setTextColor(Color.parseColor("#ffffff"));
            notificationsOn.setTextColor(Color.parseColor("#ffffff"));
            healthAndConnectTxtView.setTextColor(Color.parseColor("#ffffff"));
            connectOff.setTextColor(Color.parseColor("#ffffff"));
            connectOn.setTextColor(Color.parseColor("#ffffff"));
            settingsTheme.setTextColor(Color.parseColor("#ffffff"));
            languageTxtView.setTextColor(Color.parseColor("#ffffff"));
            updateProfileData.setTextColor(Color.parseColor("#ffffff"));
            updatePhysicalData.setTextColor(Color.parseColor("#ffffff"));
            updateWorkoutData.setTextColor(Color.parseColor("#ffffff"));

            //Drawable arrows
            updatePhysicalData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow_white,getTheme()),null);
            updateProfileData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow_white,getTheme()),null);
            updateWorkoutData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow_white,getTheme()),null);

            //Backgrounds
            mainBackground.setBackground(getResources().getDrawable(R.drawable.input_background_dark_settings));
            backgroundProfile.setBackground(getResources().getDrawable(R.drawable.input_background_dark_settings));
            backgroundBiometric.setBackground(getResources().getDrawable(R.drawable.input_background_dark_settings));
            backgroundWorkoutMetrics.setBackground(getResources().getDrawable(R.drawable.input_background_dark_settings));

            // settingsScreenBackground
            settingsLayout.setBackgroundColor(Color.parseColor("#DA000000"));


        }


        updatePhysicalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, PhysicalAttributes.class);
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
        updateProfileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, ProfileSetup.class);
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
                //intent.putExtra("navigatedFrom","settingsScreen");
                startActivity(intent);
                
            }
        });
        updateWorkoutData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, WorkoutMetrics.class);
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

        //Handle switch events
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    //Set theme to light
                    FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice").setValue("light");
                    user_theme="light";


                    //Toolbar
                    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    toolbar.setTitleTextColor(Color.parseColor("#000000"));

                    //Switches
                    switchTheme.setChecked(true);
                    switchTheme.getThumbDrawable().setColorFilter(Color.parseColor("#B5000000"), PorterDuff.Mode.MULTIPLY);
                    switchTheme.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

                    switchNotifs.getThumbDrawable().setColorFilter(Color.parseColor("#B5000000"), PorterDuff.Mode.MULTIPLY);
                    switchNotifs.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

                    switchHealthConnect.getThumbDrawable().setColorFilter(Color.parseColor("#B5000000"), PorterDuff.Mode.MULTIPLY);
                    switchHealthConnect.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

                    // ImageViews
                    sun.setImageResource(R.drawable.ic_sun_settings);
                    moon.setImageResource(R.drawable.ic_moon_settings);

                    // Spinner
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Settings.this,
                            R.array.languages, R.layout.spinner_selected_settings);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_settings);

                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    spinner.getBackground().setColorFilter(Color.parseColor("#8c8c8c"), PorterDuff.Mode.SRC_ATOP);

                    //TextViews
                    notificationsTxtView.setTextColor(Color.parseColor("#000000"));
                    notificationsOff.setTextColor(Color.parseColor("#000000"));
                    notificationsOn.setTextColor(Color.parseColor("#000000"));
                    healthAndConnectTxtView.setTextColor(Color.parseColor("#000000"));
                    connectOff.setTextColor(Color.parseColor("#000000"));
                    connectOn.setTextColor(Color.parseColor("#000000"));
                    settingsTheme.setTextColor(Color.parseColor("#000000"));
                    languageTxtView.setTextColor(Color.parseColor("#000000"));
                    updateProfileData.setTextColor(Color.parseColor("#000000"));
                    updatePhysicalData.setTextColor(Color.parseColor("#000000"));
                    updateWorkoutData.setTextColor(Color.parseColor("#000000"));

                    //Drawable arrows
                    updatePhysicalData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow,getTheme()),null);
                    updateProfileData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow,getTheme()),null);
                    updateWorkoutData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow,getTheme()),null);


                    // Backgrounds
                    mainBackground.setBackground(getResources().getDrawable(R.drawable.input_background_settings));
                    backgroundProfile.setBackground(getResources().getDrawable(R.drawable.input_background_settings));
                    backgroundBiometric.setBackground(getResources().getDrawable(R.drawable.input_background_settings));
                    backgroundWorkoutMetrics.setBackground(getResources().getDrawable(R.drawable.input_background_settings));

                    // settingsScreenBackground
                    settingsLayout.setBackgroundColor(Color.parseColor("#DAE4D6D6"));


                }else {

                    // Set theme to dark
                    FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("ui_theme_choice").setValue("dark");
                    user_theme="dark";

                    // Toolbar
                    toolbar.setBackgroundColor(Color.parseColor("#E62E2D2D"));
                    toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

                    // Switches
                    switchTheme.setChecked(false);
                    switchTheme.getThumbDrawable().setColorFilter(Color.parseColor("#8c8c8c"), PorterDuff.Mode.MULTIPLY);
                    switchTheme.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

                    switchNotifs.getThumbDrawable().setColorFilter(Color.parseColor("#8c8c8c"), PorterDuff.Mode.MULTIPLY);
                    switchNotifs.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

                    switchHealthConnect.getThumbDrawable().setColorFilter(Color.parseColor("#8c8c8c"), PorterDuff.Mode.MULTIPLY);
                    switchHealthConnect.getTrackDrawable().setColorFilter(Color.parseColor("#8c8c8c"),PorterDuff.Mode.MULTIPLY);

                    // ImageViews
                    sun.setImageResource(R.drawable.ic_sun_settings_light);
                    moon.setImageResource(R.drawable.ic_moon_settings_light);

                    // Spinner
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Settings.this,
                            R.array.languages, R.layout.spinner_selected_settings);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_settings);

                    spinner.setAdapter(adapter);
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

                    //TextViews
                    notificationsTxtView.setTextColor(Color.parseColor("#ffffff"));
                    notificationsOff.setTextColor(Color.parseColor("#ffffff"));
                    notificationsOn.setTextColor(Color.parseColor("#ffffff"));
                    healthAndConnectTxtView.setTextColor(Color.parseColor("#ffffff"));
                    connectOff.setTextColor(Color.parseColor("#ffffff"));
                    connectOn.setTextColor(Color.parseColor("#ffffff"));
                    settingsTheme.setTextColor(Color.parseColor("#ffffff"));
                    languageTxtView.setTextColor(Color.parseColor("#ffffff"));
                    updateProfileData.setTextColor(Color.parseColor("#ffffff"));
                    updatePhysicalData.setTextColor(Color.parseColor("#ffffff"));
                    updateWorkoutData.setTextColor(Color.parseColor("#ffffff"));

                    //Drawable arrows
                    updatePhysicalData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow_white,getTheme()),null);
                    updateProfileData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow_white,getTheme()),null);
                    updateWorkoutData.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.forward_arrow_white,getTheme()),null);

                    //Backgrounds
                    mainBackground.setBackground(getResources().getDrawable(R.drawable.input_background_dark_settings));
                    backgroundProfile.setBackground(getResources().getDrawable(R.drawable.input_background_dark_settings));
                    backgroundBiometric.setBackground(getResources().getDrawable(R.drawable.input_background_dark_settings));
                    backgroundWorkoutMetrics.setBackground(getResources().getDrawable(R.drawable.input_background_dark_settings));

                    // settingsScreenBackground
                    settingsLayout.setBackgroundColor(Color.parseColor("#DA000000"));

                }

            }
        });
        /*
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //TODO
            }
        });

         */


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
                    Intent intent = new Intent(Settings.this, Home.class);
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
                    Intent intent = new Intent(Settings.this, ProfileSetup.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                    //intent.putExtra("navigatedFrom","settingsScreen");
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
                    startActivity(intent);

                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_workout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Settings.this, Home.class);
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
                    Intent intent = new Intent(Settings.this, Home.class);
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
                    Intent intent = new Intent(Settings.this, Home.class);
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
                    Intent intent = new Intent(Settings.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Nothing

                }
            }, 410);

        } else if (item.getItemId()==R.id.nav_diet) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Settings.this, Home.class);
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
}
