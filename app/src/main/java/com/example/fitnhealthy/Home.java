package com.example.fitnhealthy;


import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.Manifest;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;

import android.os.Build;

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


// If you are using a permission library, import the appropriate classes/interfaces for requesting permissions
// For example, if you are using EasyPermissions library:
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.AppSettingsDialog;

import androidx.appcompat.app.ActionBarDrawerToggle;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String username,user_theme,gender,experience,target;
    private long age;
    private float weight,height;
    ArrayList<String> audioList,datesList,workoutTimesList;
    ArrayList<Integer> caloriesList;
    float[] avgHeartRatesList;
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
        //Boolean checkStoragePermissions=checkStoragePermissions();
        // Check if the permission has been granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] perms = {Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS};
            if (EasyPermissions.hasPermissions(this, perms)) {
                // Already have permission, do the thing
                // ...
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(
                        this,
                        null,
                        1,
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.POST_NOTIFICATIONS
                );
            }

        } else {
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.POST_NOTIFICATIONS};
            if (EasyPermissions.hasPermissions(this, perms)) {
                // Already have permission, do the thing
                // ...
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(
                        this,
                        null,
                        1,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.POST_NOTIFICATIONS
                );
            }

        }
        /*
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            final int STORAGE_PERMISSION_CODE = 23;
            //Android is 11 (R) or above
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                    intent.setData(uri);
                    storageActivityResultLauncher.launch(intent);
                }catch (Exception e){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    storageActivityResultLauncher.launch(intent);
                }
            }else{
                //Below android 11
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        STORAGE_PERMISSION_CODE
                );
            }
        }

         */


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
        caloriesList=new ArrayList<>();
        workoutTimesList=new ArrayList<>();
        datesList=new ArrayList<>();


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


        //Toast.makeText(this, audioList.toString(), Toast.LENGTH_SHORT).show();


        homeTitleTextView=findViewById(R.id.homeScreenTitleText);
        homeTitleTextView.setText("Hello "+ username.toString());
        databaseReference = FirebaseDatabase.getInstance().getReference("/Users");
        //Query emailQuery = databaseReference.orderByChild("email").equalTo(currentUser.getEmail());
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


        /*emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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

         */







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


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
// Create an intent to start a new activity

                Intent intent = new Intent(Home.this, Home.class);
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
                    Intent intent = new Intent(Home.this, Home.class);
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
                    Intent intent = new Intent(Home.this, ProfileSetup.class);
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
                    Intent intent = new Intent(Home.this, WorkoutOptions.class);
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
                    Intent intent = new Intent(Home.this, Home.class);
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
                    Intent intent = new Intent(Home.this, Home.class);
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
                    Intent intent = new Intent(Home.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 410);
        } else if (item.getItemId()==R.id.nav_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Home.this, com.example.fitnhealthy.Settings.class);
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
                    Intent intent = new Intent(Home.this, Home.class);
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

                        } else if (cardView.getId() == findViewById(R.id.StatsCard).getId()) {
                            Intent intent = new Intent(Home.this, WorkoutStats.class);
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
                        } else if (cardView.getId() == findViewById(R.id.ProgressCard).getId()) {
                            Intent intent = new Intent(Home.this, Home.class);
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

                        } else if (cardView.getId() == findViewById(R.id.settingsCard).getId()) {
                            Intent intent = new Intent(Home.this, com.example.fitnhealthy.Settings.class);
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
                        else if (cardView.getId() == findViewById(R.id.WorkoutCard).getId()) {
                            if (age==0L || height==0L || weight==0L || gender.equals("")){
                                Intent intent = new Intent(Home.this, PhysicalAttributes.class);
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
                                Intent intent = new Intent(Home.this, WorkoutOptions.class);
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


                        } else if (cardView.getId() == findViewById(R.id.dietCard).getId()) {
                            Intent intent = new Intent(Home.this, Home.class);
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
                        } else if (cardView.getId()==findViewById(R.id.setProfileCard).getId()) {
                            Intent intent = new Intent(Home.this, ProfileSetup.class);
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

                        }else if (cardView.getId()==findViewById(R.id.logoutCard).getId()) {
                            auth.signOut();
                            Intent intent = new Intent(Home.this, Login.class);
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
    /*
    public boolean checkStoragePermissions(){

            //Below android 11
            int write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);


            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;

    }
    private ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>(){

                        @Override
                        public void onActivityResult(ActivityResult o) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                                //Android is 11 (R) or above
                                if(Environment.isExternalStorageManager()){
                                    //Manage External Storage Permissions Granted
                                    Log.d(TAG, "onActivityResult: Manage External Storage Permissions Granted");
                                }else{
                                    Toast.makeText(Home.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                //Below android 11

                            }
                        }
                    });



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, perform your desired action here
            } else {
                // Permission has been denied, handle accordingly
            }
        }
    }
    */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.

        }
    }


}

