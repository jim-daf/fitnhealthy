package com.example.fitnhealthy;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class WorkoutExercises extends AppCompatActivity{
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String username,user_theme,gender,experience,target;
    ArrayList<String> audioList,datesList,workoutTimesList;
    ArrayList<Integer> caloriesList;
    float[] avgHeartRatesList;
    private long age;
    private float weight,height;
    ScrollView workoutExercisesLayout;
    ImageView plankHipDips,plankUpDown,plankSide,mountainClimber,dolphinPose;
    LinearLayout cardContainer, info;
    DisplayMetrics displayMetrics;
    LayoutInflater inflater;
    Button videoBtn,animationBtn,guidanceBtn,startBtn;
    ImageView gif;
    WebView video;
    String videoEmbedHipDips,videoEmbedRegularPlank;
    Parcelable[] workoutExercises;
    WorkoutExerciseSample[] workoutExerciseSamples;
    WorkoutSample workout;
    TextView titleOfWorkout,durationOfWorkout,numOfWorkouts;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_workout_exercises);

        auth = FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        displayMetrics=  new DisplayMetrics();

        currentUser.reload();

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

        /*
        // Put gifs to exercises
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

         */

        workoutExercises =(Parcelable[]) intent.getParcelableArrayExtra("WorkoutExercises");
        workoutExerciseSamples=new WorkoutExerciseSample[workoutExercises.length];
        workout=(WorkoutSample) intent.getParcelableExtra("Workout");
        titleOfWorkout=findViewById(R.id.titleOfWorkout);
        durationOfWorkout=findViewById(R.id.durationOfWorkout);

        if (workout.getTitleOfWorkout().equals("Planks workout challenge")){
            titleOfWorkout.setText("Planks workout challenge");
            durationOfWorkout.setText("11 mins | 10 workouts");
        } else if (workout.getTitleOfWorkout().equals("Cycling")) {
            titleOfWorkout.setText("Cycling");
            durationOfWorkout.setText("");
        } else if (workout.getTitleOfWorkout().equals("Walking")) {
            titleOfWorkout.setText("Walking");
            durationOfWorkout.setText("");
        }else if (workout.getTitleOfWorkout().equals("Running")) {
            titleOfWorkout.setText("Running");
            durationOfWorkout.setText("");
        }else if (workout.getTitleOfWorkout().equals("Tennis")) {
            titleOfWorkout.setText("Tennis");
            durationOfWorkout.setText("");
        }else if (workout.getTitleOfWorkout().equals("Volleyball")) {
            titleOfWorkout.setText("Volleyball");
            durationOfWorkout.setText("");
        }
        else {
            //TODO
        }
        cardContainer=findViewById(R.id.workoutLinearLayout);
        int counter = 0;
        for (Parcelable workout : workoutExercises){
            if (workout instanceof WorkoutExerciseSample){
                workoutExerciseSamples[counter] = (WorkoutExerciseSample) workout;
                String title = workoutExerciseSamples[counter].getTitle();
                String duration = workoutExerciseSamples[counter].getDuration();


                // Create a new CardView instance
                LinearLayout customCardView = new LinearLayout(this);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                customCardView.setLayoutParams(layoutParams);
                customCardView.setBackgroundColor(Color.parseColor("#DAE4D6D6"));

                // Inflate the custom CardView layout and attach it to the CardView
                getLayoutInflater().inflate(R.layout.custom_exercise_card, customCardView, true);
                TextView titleTxtview = customCardView.findViewById(R.id.exerciseTitle);
                TextView durationTxtview = customCardView.findViewById(R.id.exerciseDuration);
                titleTxtview.setText(title);
                durationTxtview.setText(duration);
                ImageView imageInCard = customCardView.findViewById(R.id.exerciseGifAnimation);
                if (workoutExerciseSamples[counter].getTitle().equals("Plank hip dips")){
                    Glide.with(this).asGif().load(R.drawable.gif_plank_hip_dips).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Dolphin pose")) {
                    Glide.with(this).asGif().load(R.drawable.dolphin_pose).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Mountain Climber")) {
                    Glide.with(this).asGif().load(R.drawable.mountain_climber).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Plank jacks")) {
                    Glide.with(this).asGif().load(R.drawable.plank_jacks).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Side plank")) {
                    Glide.with(this).asGif().load(R.drawable.side_plank_left).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Regular plank")) {
                    Glide.with(this).asGif().load(R.drawable.regular_plank).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Spiderman plank")) {
                    Glide.with(this).asGif().load(R.drawable.spiderman_plank).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Single leg plank")) {
                    Glide.with(this).asGif().load(R.drawable.single_leg_plank).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Single arm plank")) {
                    Glide.with(this).asGif().load(R.drawable.burpees).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Commando plank")) {
                    Glide.with(this).asGif().load(R.drawable.plank_up_down).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Cycling")) {
                    Glide.with(this).asGif().load(R.drawable.cycling_anim).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Walking")) {
                    Glide.with(this).asGif().load(R.drawable.walking_anim).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Running")) {
                    Glide.with(this).asGif().load(R.drawable.running_anim).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Tennis")) {
                    Glide.with(this).asGif().load(R.drawable.tennis_anim).into(imageInCard);
                } else if (workoutExerciseSamples[counter].getTitle().equals("Volleyball")) {
                    Glide.with(this).asGif().load(R.drawable.volley_anim).into(imageInCard);
                }
                cardContainer.addView(customCardView);
                if (counter==workoutExercises.length-1){
                    // Create the third LinearLayout
                    LinearLayout linearLayoutgap = new LinearLayout(this);

                    LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 0, 0, 30); // Add a gap of 32 pixels at the top
                    linearLayoutgap.setLayoutParams(layoutParams3);
                    cardContainer.addView(linearLayoutgap);
                } else if (counter==0) {
                    // Create the third LinearLayout
                    LinearLayout linearLayoutgap = new LinearLayout(this);

                    LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 30, 0, 0); // Add a gap of 32 pixels at the top
                    linearLayoutgap.setLayoutParams(layoutParams3);
                    cardContainer.addView(linearLayoutgap);
                }

            }
            counter+=1;



        }




        setSingleEvent(cardContainer);
        //setToggleEvent(cardContainer);
        startBtn=findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleOfWorkout.getText().equals("Cycling") || titleOfWorkout.getText().equals("Walking") || titleOfWorkout.getText().equals("Running") || titleOfWorkout.getText().equals("Tennis") || titleOfWorkout.getText().equals("Volleyball")){
                    Intent intent = new Intent(WorkoutExercises.this, OtherWorkouts.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putExtra("WorkoutExercises",workoutExercises);
                    intent.putExtra("Workout",workout);
                    intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(WorkoutExercises.this, SingleWorkout.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putExtra("WorkoutExercises",workoutExercises);
                    intent.putExtra("Workout",workout);
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
        videoEmbedRegularPlank="";
        videoEmbedHipDips="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/em4gADvYvMA?si=42fam1s14od_NuNI\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Create an intent to start a new activity
                Intent intent = new Intent(WorkoutExercises.this, Workouts.class);
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
            if (cardContainer.getChildAt(i) instanceof LinearLayout) {
                LinearLayout cardView = (LinearLayout) cardContainer.getChildAt(i);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint({"InflateParams", "MissingInflatedId"})
                    @Override
                    public void onClick(View view) {
                        for (int j = 0; j < cardView.getChildCount(); j++) {
                            TextView title = (TextView) cardView.findViewById(R.id.exerciseTitle);
                            if (title.getText().equals("Plank hip dips")) {
                                exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/em4gADvYvMA?si=42fam1s14od_NuNI\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"
                                        ,R.drawable.gif_plank_hip_dips,
                                        "Starting position: ",
                                        "Begin in a standard plank position with the forearms on the ground, elbows directly under the shoulders and toes touching the floor. Keep the body in a straight line from head to heels, engaging the core and glutes.",
                                        "Hip dip movement: ",
                                        "Slowly rotate the hips to one side, bringing them closer to the ground without touching it. The rest of the body should remain stable and straight. Pause briefly at the bottom of the movement.",
                                        "Return to center:",
                                        "Lift your hips back to the starting position, and repeat the hip dip movement on the opposite side.",
                                        "Repetition: ",
                                        "Perform the exercise for the desired number of repetitions or time intervals.",
                                        "Core strength: ",
                                        "The exercise effectively targets and strengthens the entire core, enhancing stability and reducing risk of back pain and injuries.",
                                        "Improved balance and coordination:",
                                        "The rotational aspect of the hip dip challenges balance and coordination, prompting better proprioception and overall body control.",
                                        "Enhanced flexibility: ",
                                        "Performing plank hip dips can lead to improved hip and spinal flexibility, especially as you gradually increase your range of motion.",
                                        "Time efficiency:",
                                        "As a compound exercise, plank hip dips work multiple muscle groups simultaneously, making it a time-efficient addition to your workout routine.",
                                        "Plank hip dips are an effective and dynamic variation of the traditional plank, offering a comprehensive workout for the core and other major muscle groups(glutes, shoulders, upper back, quadriceps, lower back).",
                                        "https://www.sportskeeda.com/health-and-fitness/plank-dips-a-dynamic-core-strengthening-exercise"
                                        );

                            }
                                 else if (title.getText().equals("Mountain Climber")) {
                                    exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/kLh-uczlPLg?si=K1CW6-Hi5PiMcsRS\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
                                            ,R.drawable.mountain_climber,
                                            "Starting position: ",
                                            "Start in a high plank position with your body in a straight line, your back straight and your shoulders over your wrists.",
                                            "Mountain climber movement: ",
                                            "Bring your right knee into your chest, keeping your left leg extended behind. As you return your right leg to the starting position, switch and bring your left knee into your chest.",
                                            "Repetition: ",
                                            "Continue alternating bringing your knees into your chest, ensuring your hips stay down in a plank position.\n" +
                                                    "If you feel comfortable, you can increase the speed of motion, ensuring proper form is maintained throughout.",
                                            "",
                                            "",
                                            "Full-body strength ",
                                            "The exercise effectively targets your core, shoulders, arms, and legs to overall strengthen your muscles and get the heart rate up.",
                                            "Improved cardiovascular endurance:",
                                            "Mountain climbers increase your heart rate to improve both your endurance and your cardiovascular health.",
                                            "Boosted balance and coordination:",
                                            "The action of putting your weight on one leg at a time helps develop balance and coordination over time.",
                                            "Low-impact exercise",
                                            "This exercise can be a good choice for those who can’t do high-impact exercises. Having at least one foot on the ground at all times puts less stress on your joints.",
                                            "Mountain climbers are a great full-body workout because they engage the entire body for a high-intensity, low-impact workout.","https://www.wellandgood.com/mountain-climbers-muscles-worked/"
                                    );
                                } else if (title.getText().equals("Regular plank")) {
                                exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/kLh-uczlPLg?si=K1CW6-Hi5PiMcsRS\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
                                        ,R.drawable.regular_plank,
                                        "Starting position: ",
                                        "Start in a high plank position with your body in a straight line, your back straight and your shoulders over your wrists.",
                                        "Mountain climber movement: ",
                                        "Bring your right knee into your chest, keeping your left leg extended behind. As you return your right leg to the starting position, switch and bring your left knee into your chest.",
                                        "Repetition: ",
                                        "Continue alternating bringing your knees into your chest, ensuring your hips stay down in a plank position.\n" +
                                                "If you feel comfortable, you can increase the speed of motion, ensuring proper form is maintained throughout.",
                                        "",
                                        "",
                                        "Full-body strength ",
                                        "The exercise effectively targets your core, shoulders, arms, and legs to overall strengthen your muscles and get the heart rate up.",
                                        "Improved cardiovascular endurance:",
                                        "Mountain climbers increase your heart rate to improve both your endurance and your cardiovascular health.",
                                        "Boosted balance and coordination:",
                                        "The action of putting your weight on one leg at a time helps develop balance and coordination over time.",
                                        "Low-impact exercise",
                                        "This exercise can be a good choice for those who can’t do high-impact exercises. Having at least one foot on the ground at all times puts less stress on your joints.",
                                        "Mountain climbers are a great full-body workout because they engage the entire body for a high-intensity, low-impact workout.","https://www.wellandgood.com/mountain-climbers-muscles-worked/"
                                );
                            } else if (title.getText().equals("Dolphin pose")) {
                                    exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/5wsKWZWnnwA?si=55LaKTGc77QF4FMx\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
                                            ,R.drawable.dolphin_pose,
                                            "Starting position: ",
                                            "Begin on all fours in a tabletop position with your wrists directly under your shoulders and your knees under your hips.",
                                            "Secondary position: ",
                                            "Lower your forearms to the ground, keeping your elbows directly under your shoulders.",
                                            "Return to normal plank:",
                                            "Step your feet back one at a time, coming into a plank pose with your body in a straight line from your head to your heels.",
                                            "Coming out of dolphin pose: ",
                                            "To come out of the pose, lower your knees to the ground and release into child’s pose.",
                                            "Core strength: ",
                                            "Dolphin plank pose engages the entire core, including the abs, back muscles, and glutes. Regular practice of this pose can help strengthen and tone the core muscles, improving posture and stability.",
                                            "Upper body strength:",
                                            "This pose requires a lot of strength in the arms, shoulders, and upper back. Practicing dolphin plank regularly can help build strength and endurance in these muscles.",
                                            "Balance improvement:",
                                            "Dolphin plank requires a lot of focus and balance to maintain the pose. Regular practice can help improve balance and stability, which can be useful for other yoga poses and activities in daily life.",
                                            "Stretches the hamstrings:",
                                            "Dolphin plank also stretches the hamstrings, which can help reduce tightness and improve flexibility in the legs.",
                                            "Dolphin Plank Pose is a yoga pose that helps to strengthen the core, arms, and shoulders, while also stretching the hamstrings, calves, and arches of the feet.","https://www.vinyasayogaashram.com/blog/dolphin-plank-pose-forearm-plank-how-to-do-it-benefits-precautions/");

                                } else if (title.getText().equals("Plank jacks")) {
                                    exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/L4oFJRDAU4Q?si=RNAKDb6V33eM9fM_\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
                                            ,R.drawable.plank_jacks,
                                            "Step 1",
                                            "Start in a plank position, with your wrists under your shoulders and your feet hip-width apart.",
                                            "Step 2",
                                            "Bend your left arm, place your left elbow on the mat and then bend your right arm and place your right elbow on the mat.",
                                            "Step 3",
                                            "Place your left hand on the mat, straighten your left arm and then place your right hand on the mat and straighten your right arm.",
                                            "Step 4",
                                            "Switch sides and repeat this up and down movement until the set is complete.",
                                            "Core strength: ",
                                            "The exercise effectively targets and strengthens the entire core, enhancing stability and reducing risk of back pain and injuries.",
                                            "Upper body strength: ",
                                            "The up down plank targets your shoulders, chest, and triceps, helping to build strength and definition in these areas.",
                                            "Increased calorie burn: ",
                                            "The movement of the up down plank raises your heart rate, helping to burn more calories than a traditional plank.",
                                            "Improved body posture: ",
                                            "The motions you’ll use during the exercise will add substantial strength and suppleness to your lower back, which will, in turn, help your posture.",
                                            "The up and down plank strengthens and tones your core, glutes, arms, wrists, and shoulders. This exercise helps to improve your posture, tightens the midsection, and boosts weight loss."
                                            ,"https://www.spotebi.com/exercise-guide/up-down-plank/");

                                } else if (title.getText().equals("Side plank")) {
                                    exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Oe9Tp9SvTCE?si=VtGNJ4ShQewwruHy\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
                                            ,R.drawable.side_plank_left,
                                            "Step 1",
                                            "Lie on your right side with your legs straight and feet stacked on top of each other. Place your right elbow under your right shoulder with your forearm pointing away from you and your hand balled into a fist. The pinky side of your hand should be in contact with the ground.",
                                            "Step 2",
                                            "With your neck neutral, breathe out and brace your core.",
                                            "Step 3",
                                            "Lift your hips off the mat so that you’re supporting your weight on your elbow and the side of your right foot. Your body should be in a straight line from your ankles to your head.",
                                            "Step 4",
                                            "Hold this position for the duration of the exercise. Depending on your fitness level, aim for between 15 to 60 seconds.",
                                            "Strengthens three muscle groups at once: ",
                                            " To keep you stabilized in a side plank position, the muscles in your shoulders, hips, and sides of your core all have to fire and work together.",
                                            "Improved balance and coordination:",
                                            "As a balancing exercise, a side plank can help improve your sense of balance and coordination.",
                                            "Protects your spine: ",
                                            "Side planks work the deep spinal stabilizing muscle quadratus lumborumTrusted Source. Keeping this muscle strong can help reduce your risk of a back injury.",
                                            "Strengthens your core without stressing your back:",
                                            "Unlike crunches and situps, side planks don’t put pressure on your lower back. Yet, this exercise does an excellent job of boosting your core strength.",
                                            "The side plank is one of the easiest ways to work the two layers of muscle along the sides of your core, known as your obliques. These muscles help you rotate and bend your trunk, and they also play a role in helping to protect your spine.","https://www.healthline.com/health/side-plank");
                                } else if (title.getText().equals("Single arm plank")) {
                                exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Oe9Tp9SvTCE?si=VtGNJ4ShQewwruHy\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
                                        ,R.drawable.burpees,
                                        "Step 1",
                                        "Lie on your right side with your legs straight and feet stacked on top of each other. Place your right elbow under your right shoulder with your forearm pointing away from you and your hand balled into a fist. The pinky side of your hand should be in contact with the ground.",
                                        "Step 2",
                                        "With your neck neutral, breathe out and brace your core.",
                                        "Step 3",
                                        "Lift your hips off the mat so that you’re supporting your weight on your elbow and the side of your right foot. Your body should be in a straight line from your ankles to your head.",
                                        "Step 4",
                                        "Hold this position for the duration of the exercise. Depending on your fitness level, aim for between 15 to 60 seconds.",
                                        "Strengthens three muscle groups at once: ",
                                        " To keep you stabilized in a side plank position, the muscles in your shoulders, hips, and sides of your core all have to fire and work together.",
                                        "Improved balance and coordination:",
                                        "As a balancing exercise, a side plank can help improve your sense of balance and coordination.",
                                        "Protects your spine: ",
                                        "Side planks work the deep spinal stabilizing muscle quadratus lumborumTrusted Source. Keeping this muscle strong can help reduce your risk of a back injury.",
                                        "Strengthens your core without stressing your back:",
                                        "Unlike crunches and situps, side planks don’t put pressure on your lower back. Yet, this exercise does an excellent job of boosting your core strength.",
                                        "The side plank is one of the easiest ways to work the two layers of muscle along the sides of your core, known as your obliques. These muscles help you rotate and bend your trunk, and they also play a role in helping to protect your spine.","https://www.healthline.com/health/side-plank");
                            }else if (title.getText().equals("Single leg plank")) {
                                exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Oe9Tp9SvTCE?si=VtGNJ4ShQewwruHy\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
                                        ,R.drawable.single_leg_plank,
                                        "Step 1",
                                        "Lie on your right side with your legs straight and feet stacked on top of each other. Place your right elbow under your right shoulder with your forearm pointing away from you and your hand balled into a fist. The pinky side of your hand should be in contact with the ground.",
                                        "Step 2",
                                        "With your neck neutral, breathe out and brace your core.",
                                        "Step 3",
                                        "Lift your hips off the mat so that you’re supporting your weight on your elbow and the side of your right foot. Your body should be in a straight line from your ankles to your head.",
                                        "Step 4",
                                        "Hold this position for the duration of the exercise. Depending on your fitness level, aim for between 15 to 60 seconds.",
                                        "Strengthens three muscle groups at once: ",
                                        " To keep you stabilized in a side plank position, the muscles in your shoulders, hips, and sides of your core all have to fire and work together.",
                                        "Improved balance and coordination:",
                                        "As a balancing exercise, a side plank can help improve your sense of balance and coordination.",
                                        "Protects your spine: ",
                                        "Side planks work the deep spinal stabilizing muscle quadratus lumborumTrusted Source. Keeping this muscle strong can help reduce your risk of a back injury.",
                                        "Strengthens your core without stressing your back:",
                                        "Unlike crunches and situps, side planks don’t put pressure on your lower back. Yet, this exercise does an excellent job of boosting your core strength.",
                                        "The side plank is one of the easiest ways to work the two layers of muscle along the sides of your core, known as your obliques. These muscles help you rotate and bend your trunk, and they also play a role in helping to protect your spine.","https://www.healthline.com/health/side-plank");
                            }else if (title.getText().equals("Spiderman plank")) {
                                exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Oe9Tp9SvTCE?si=VtGNJ4ShQewwruHy\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
                                        ,R.drawable.spiderman_plank,
                                        "Step 1",
                                        "Lie on your right side with your legs straight and feet stacked on top of each other. Place your right elbow under your right shoulder with your forearm pointing away from you and your hand balled into a fist. The pinky side of your hand should be in contact with the ground.",
                                        "Step 2",
                                        "With your neck neutral, breathe out and brace your core.",
                                        "Step 3",
                                        "Lift your hips off the mat so that you’re supporting your weight on your elbow and the side of your right foot. Your body should be in a straight line from your ankles to your head.",
                                        "Step 4",
                                        "Hold this position for the duration of the exercise. Depending on your fitness level, aim for between 15 to 60 seconds.",
                                        "Strengthens three muscle groups at once: ",
                                        " To keep you stabilized in a side plank position, the muscles in your shoulders, hips, and sides of your core all have to fire and work together.",
                                        "Improved balance and coordination:",
                                        "As a balancing exercise, a side plank can help improve your sense of balance and coordination.",
                                        "Protects your spine: ",
                                        "Side planks work the deep spinal stabilizing muscle quadratus lumborumTrusted Source. Keeping this muscle strong can help reduce your risk of a back injury.",
                                        "Strengthens your core without stressing your back:",
                                        "Unlike crunches and situps, side planks don’t put pressure on your lower back. Yet, this exercise does an excellent job of boosting your core strength.",
                                        "The side plank is one of the easiest ways to work the two layers of muscle along the sides of your core, known as your obliques. These muscles help you rotate and bend your trunk, and they also play a role in helping to protect your spine.","https://www.healthline.com/health/side-plank");
                            }else if (title.getText().equals("Commando plank")) {
                                exerciseInstructions("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Oe9Tp9SvTCE?si=VtGNJ4ShQewwruHy\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
                                        ,R.drawable.plank_up_down,
                                        "Step 1",
                                        "Lie on your right side with your legs straight and feet stacked on top of each other. Place your right elbow under your right shoulder with your forearm pointing away from you and your hand balled into a fist. The pinky side of your hand should be in contact with the ground.",
                                        "Step 2",
                                        "With your neck neutral, breathe out and brace your core.",
                                        "Step 3",
                                        "Lift your hips off the mat so that you’re supporting your weight on your elbow and the side of your right foot. Your body should be in a straight line from your ankles to your head.",
                                        "Step 4",
                                        "Hold this position for the duration of the exercise. Depending on your fitness level, aim for between 15 to 60 seconds.",
                                        "Strengthens three muscle groups at once: ",
                                        " To keep you stabilized in a side plank position, the muscles in your shoulders, hips, and sides of your core all have to fire and work together.",
                                        "Improved balance and coordination:",
                                        "As a balancing exercise, a side plank can help improve your sense of balance and coordination.",
                                        "Protects your spine: ",
                                        "Side planks work the deep spinal stabilizing muscle quadratus lumborumTrusted Source. Keeping this muscle strong can help reduce your risk of a back injury.",
                                        "Strengthens your core without stressing your back:",
                                        "Unlike crunches and situps, side planks don’t put pressure on your lower back. Yet, this exercise does an excellent job of boosting your core strength.",
                                        "The side plank is one of the easiest ways to work the two layers of muscle along the sides of your core, known as your obliques. These muscles help you rotate and bend your trunk, and they also play a role in helping to protect your spine.","https://www.healthline.com/health/side-plank");
                            }else if (title.getText().equals("Cycling")) {

                            }else if (title.getText().equals("Walking")) {

                            }else if (title.getText().equals("Running")) {

                            }else if (title.getText().equals("Tennis")) {

                            }else if (title.getText().equals("Volleyball")) {

                            }
                            }
                        }
                });
            }




        }
    }










    public void exerciseInstructions(String videoEmbed,Integer drawableGif,String startingPositionTitleText,String startingPositionAnalysisText,String secondaryPositionTitleText,String secondaryPositionAnalysisText,String thirdPositionTitleText,String thirdPositionAnalysisText,String finalPositionTitleText,String finalPositionAnalysisText,String firstBenefitTitleText,String firstBenefitAnalysisText,String secondBenefitTitleText,String secondBenefitAnalysisText,String thirdBenefitTitleText,String thirdBenefitAnalysisText,String fourthBenefitTitleText,String fourthBenefitAnalysisText,String overallAnalysisText,String websiteLinkText){
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutExercises.this, R.style.CustomDialogTheme);
        inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_exercise_instructions, null);
        builder.setView(v);
        builder.setNegativeButton("Close", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();
        // Set the custom background drawable
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.dialog_background);
        }
        dialog.show();

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.93);
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);


        Button closeBtn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        closeBtn.setTextSize(12);
        closeBtn.setGravity(Gravity.CENTER);


        closeBtn.setTextColor(Color.parseColor("#ffffff"));
        closeBtn.setBackground(getResources().getDrawable(R.drawable.close_btn, getTheme()));
        closeBtn.setPaddingRelative(50, -30, 50, -30);
        // Set layout parameters for the close button
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        buttonLayoutParams.setMargins(0, 0, 0, 5); // Set bottom margin here, e.g., 16 pixels
        closeBtn.setLayoutParams(buttonLayoutParams);
        closeBtn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        animationBtn = v.findViewById(R.id.animationBtn);
        guidanceBtn = v.findViewById(R.id.guidanceBtn);
        videoBtn = v.findViewById(R.id.videoBtn);
        //Content
        gif = v.findViewById(R.id.instructionsAnimationGif);
        video = v.findViewById(R.id.videoInstructions);
        info = v.findViewById(R.id.exerciseInfo);

        // Edit text of info
        TextView startingPositionTitle = v.findViewById(R.id.startingPositionTitle);
        TextView secondaryPositionTitle = v.findViewById(R.id.SecondaryPositionTitle);
        TextView thirdPositionTitle = v.findViewById(R.id.ThirdPositionTitle);
        TextView finalPositionTitle = v.findViewById(R.id.FinalPositionTitle);
        TextView startingPositionAnalysis = v.findViewById(R.id.startingPositionAnalysis);
        TextView secondaryPositionAnalysis = v.findViewById(R.id.SecondaryPositionAnalysis);
        TextView thirdPositionAnalysis = v.findViewById(R.id.ThirdPositionAnalysis);
        TextView finalPositionAnalysis = v.findViewById(R.id.FinalPositionAnalysis);
        TextView firstBenefitTitle = v.findViewById(R.id.FirstBenefitTitle);
        TextView secondBenefitTitle = v.findViewById(R.id.SecondBenefitTitle);
        TextView thirdBenefitTitle = v.findViewById(R.id.ThirdBenefitTitle);
        TextView fourthBenefitTitle = v.findViewById(R.id.FourthBenefitTitle);
        TextView firstBenefitAnalysis = v.findViewById(R.id.FirstBenefitAnalysis);
        TextView secondBenefitAnalysis = v.findViewById(R.id.SecondBenefitAnalysis);
        TextView thirdBenefitAnalysis = v.findViewById(R.id.ThirdBenefitAnalysis);
        TextView fourthBenefitAnalysis = v.findViewById(R.id.FourthBenefitAnalysis);
        TextView overallAnalysis = v.findViewById(R.id.Overall);
        TextView websiteLink = v.findViewById(R.id.websiteLinkForExercise);
        startingPositionTitle.setText(startingPositionTitleText);
        startingPositionAnalysis.setText(startingPositionAnalysisText);
        secondaryPositionTitle.setText(secondaryPositionTitleText);
        secondaryPositionAnalysis.setText(secondaryPositionAnalysisText);
        thirdPositionTitle.setText(thirdPositionTitleText);
        thirdPositionAnalysis.setText(thirdPositionAnalysisText);
        finalPositionTitle.setText(finalPositionTitleText);
        finalPositionAnalysis.setText(finalPositionAnalysisText);
        firstBenefitTitle.setText(firstBenefitTitleText);
        firstBenefitAnalysis.setText(firstBenefitAnalysisText);
        secondBenefitTitle.setText(secondBenefitTitleText);
        secondBenefitAnalysis.setText(secondBenefitAnalysisText);
        thirdBenefitTitle.setText(thirdBenefitTitleText);
        thirdBenefitAnalysis.setText(thirdBenefitAnalysisText);
        fourthBenefitTitle.setText(fourthBenefitTitleText);
        fourthBenefitAnalysis.setText(fourthBenefitAnalysisText);
        overallAnalysis.setText(overallAnalysisText);
        websiteLink.setText(websiteLinkText);

        // Download gif and put it to the imageview
        Glide.with(WorkoutExercises.this).asGif().load(drawableGif).into(gif);

        // Setup youtube video to webview

        video.loadData(videoEmbed, "text/html", "utf-8");
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
                info.setVisibility(View.GONE);


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
                info.setVisibility(View.GONE);


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
                info.setVisibility(View.VISIBLE);


            }
        });

    }











}
