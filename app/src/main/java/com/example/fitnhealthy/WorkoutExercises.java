package com.example.fitnhealthy;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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

import org.w3c.dom.Text;

import java.util.Objects;


public class WorkoutExercises extends AppCompatActivity{
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String user_theme;
    ScrollView workoutExercisesLayout;
    ImageView plankHipDips,plankUpDown,plankSide,mountainClimber,dolphinPose;
    LinearLayout cardContainer,hipDipInfo;
    DisplayMetrics displayMetrics;
    LayoutInflater inflater;
    Button videoBtn,animationBtn,guidanceBtn,startBtn;
    ImageView gif;
    WebView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_workout_exercises);

        auth = FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        displayMetrics=  new DisplayMetrics();

        currentUser.reload();

        Intent intent = getIntent();
        user_theme = intent.getStringExtra("selected_theme");

        plankHipDips=findViewById(R.id.plankHipDips);
        Glide.with(this).asGif().load(R.drawable.gif_plank_hip_dips).into(plankHipDips);
        plankSide=findViewById(R.id.plankSide);
        Glide.with(this).asGif().load(R.drawable.side_plank_left).into(plankSide);
        plankUpDown=findViewById(R.id.plankUpDown);
        Glide.with(this).asGif().load(R.drawable.plank_up_down).into(plankUpDown);
        mountainClimber=findViewById(R.id.plankMountainClimber);
        Glide.with(this).asGif().load(R.drawable.mountain_climber).into(mountainClimber);
        dolphinPose=findViewById(R.id.plankDolphin);
        Glide.with(this).asGif().load(R.drawable.dolphin_pose).into(dolphinPose);

        cardContainer=findViewById(R.id.planksLinearLayout);

        setSingleEvent(cardContainer);
        //setToggleEvent(cardContainer);
        startBtn=findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkoutExercises.this, Workouts.class);
                intent.putExtra("selected_theme",user_theme);
                startActivity(intent);
                finish();
            }
        });



    }

/*
    @SuppressLint("ClickableViewAccessibility")
    private void setToggleEvent(LinearLayout cardContainer) {
        for (int i = 0; i < cardContainer.getChildCount(); i++) {
            final CardView cardView = (CardView) cardContainer.getChildAt(i);
            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                        //cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                        animateCardColor(cardView,Color.parseColor("#ffffff"));
                        return true;
                    }

                    if (motionEvent.getAction()==MotionEvent.ACTION_CANCEL){
                        animateCardColor(cardView,Color.parseColor("#ffffff"));
                        return true;
                    }
                    return false;
                }

            });
        }
    }

 */

    private void animateCardColor(final CardView cardView, int targetColor) {
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(cardView.getCardBackgroundColor().getDefaultColor(), targetColor);
        colorAnimator.setDuration(200); // Set the animation duration (in milliseconds)
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedColor = (int) animator.getAnimatedValue();
                cardView.setCardBackgroundColor(animatedColor);
            }
        });
        colorAnimator.start();
    }
    private void setSingleEvent(LinearLayout cardContainer) {
        //Loop all child item of Main.java Grid
        for (int i = 0; i < cardContainer.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            if (cardContainer.getChildAt(i) instanceof CardView) {
                CardView cardView = (CardView) cardContainer.getChildAt(i);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint({"InflateParams", "MissingInflatedId"})
                    @Override
                    public void onClick(View view) {
                        if (cardView.getId() == findViewById(R.id.plankHipDipsCard).getId() ) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutExercises.this,R.style.CustomDialogTheme);
                            inflater=getLayoutInflater();
                            View v =inflater.inflate(R.layout.activity_exercise_instructions,null);
                            builder.setView(v);
                            builder.setNegativeButton("Close",(dialog,which) -> {});
                            AlertDialog dialog=builder.create();
                            // Set the custom background drawable
                            Window window = dialog.getWindow();
                            if (window != null) {
                                window.setBackgroundDrawableResource(R.drawable.dialog_background);
                            }
                            dialog.show();

                            WindowManager.LayoutParams layoutParams=window.getAttributes();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            layoutParams.width = (int) (displayMetrics.widthPixels * 0.93);
                            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;


                            window.setAttributes(layoutParams);


                            Button closeBtn=dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            closeBtn.setTextSize(12);
                            closeBtn.setGravity(Gravity.CENTER);



                            closeBtn.setTextColor(Color.parseColor("#ffffff"));
                            closeBtn.setBackground(getResources().getDrawable(R.drawable.close_btn,getTheme()));
                            closeBtn.setPaddingRelative(50,-30,50,-30);
                            // Set layout parameters for the close button
                            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            buttonLayoutParams.setMargins(0, 0, 0, 5); // Set bottom margin here, e.g., 16 pixels
                            closeBtn.setLayoutParams(buttonLayoutParams);
                            closeBtn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            animationBtn=v.findViewById(R.id.animationBtn);
                            guidanceBtn=v.findViewById(R.id.guidanceBtn);
                            videoBtn=v.findViewById(R.id.videoBtn);
                            //Content
                            gif=v.findViewById(R.id.plankhipDipsGifInstructions);
                            video=v.findViewById(R.id.plankHipDipsVideoInstructions);
                            hipDipInfo=v.findViewById(R.id.hip_dip_info);
                            // Download gif and put it to the imageview
                            Glide.with(WorkoutExercises.this).asGif().load(R.drawable.gif_plank_hip_dips).into(gif);

                            // Setup youtube video to webview
                            String videoEmbed="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/em4gADvYvMA?si=42fam1s14od_NuNI\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
                            video.loadData(videoEmbed,"text/html","utf-8");
                            video.getSettings().setJavaScriptEnabled(true);
                            video.setWebChromeClient(new WebChromeClient());
                            animationBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    animationBtn.setBackgroundColor(Color.parseColor("#B5000000"));
                                    animationBtn.setTextColor(Color.parseColor("#FFFFFF"));
                                    videoBtn.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                                    videoBtn.setTextColor(Color.parseColor("#000000"));
                                    guidanceBtn.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                                    guidanceBtn.setTextColor(Color.parseColor("#000000"));


                                    gif.setVisibility(View.VISIBLE);
                                    video.setVisibility(View.GONE);
                                    hipDipInfo.setVisibility(View.GONE);


                                }
                            });
                            videoBtn.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("SetJavaScriptEnabled")
                                @Override
                                public void onClick(View view) {
                                    videoBtn.setBackgroundColor(Color.parseColor("#B5000000"));
                                    videoBtn.setTextColor(Color.parseColor("#FFFFFF"));
                                    animationBtn.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                                    animationBtn.setTextColor(Color.parseColor("#000000"));
                                    guidanceBtn.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                                    guidanceBtn.setTextColor(Color.parseColor("#000000"));


                                    video.setVisibility(View.VISIBLE);
                                    gif.setVisibility(View.GONE);
                                    hipDipInfo.setVisibility(View.GONE);


                                }
                            });
                            guidanceBtn.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("SetJavaScriptEnabled")
                                @Override
                                public void onClick(View view) {
                                    guidanceBtn.setBackgroundColor(Color.parseColor("#B5000000"));
                                    guidanceBtn.setTextColor(Color.parseColor("#FFFFFF"));
                                    animationBtn.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                                    animationBtn.setTextColor(Color.parseColor("#000000"));
                                    videoBtn.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                                    videoBtn.setTextColor(Color.parseColor("#000000"));


                                    video.setVisibility(View.GONE);
                                    gif.setVisibility(View.GONE);
                                    hipDipInfo.setVisibility(View.VISIBLE);


                                }
                            });




                        } else if (cardView.getId() == findViewById(R.id.StatsCard).getId()) {
                            Intent intent = new Intent(WorkoutExercises.this, WorkoutExercises.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();
                        } else if (cardView.getId() == findViewById(R.id.StepsCard).getId()) {
                            Intent intent = new Intent(WorkoutExercises.this, WorkoutExercises.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();

                        } else if (cardView.getId() == findViewById(R.id.WorkoutCard).getId()) {
                            Intent intent = new Intent(WorkoutExercises.this, WorkoutOptions.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();

                        } else if (cardView.getId() == findViewById(R.id.dietCard).getId()) {
                            Intent intent = new Intent(WorkoutExercises.this, WorkoutExercises.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();
                        } else if (cardView.getId()==findViewById(R.id.setProfileCard).getId()) {
                            Intent intent = new Intent(WorkoutExercises.this, WorkoutExercises.class);
                            intent.putExtra("selected_theme",user_theme.toString());
                            startActivity(intent);
                            finish();
                        }else if (cardView.getId()==findViewById(R.id.logoutCard).getId()) {
                            auth.signOut();
                            Intent intent = new Intent(WorkoutExercises.this, WorkoutExercises.class);
                            startActivity(intent);
                            finish();

                        } else if (cardView.getId()==findViewById(R.id.quizCard).getId()) {
                            Intent intent = new Intent(WorkoutExercises.this, WorkoutExercises.class);
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

        Intent intent = new Intent(WorkoutExercises.this, Workouts.class);
        intent.putExtra("selected_theme",user_theme);
        startActivity(intent);
        // Finish the current activity
        finish();
        super.onBackPressed();
    }




}
