package com.example.fitnhealthy;





import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.activity.OnBackPressedCallback;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Wearable;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.speech.tts.UtteranceProgressListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import jp.wasabeef.blurry.Blurry;

public class SingleWorkout extends AppCompatActivity implements TextToSpeech.OnInitListener,Player.Listener{
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String username,user_theme,gender,experience,target;
    ArrayList<String> audioList,datesList,workoutTimesList;
    ArrayList<Integer> caloriesList;
    float[] avgHeartRatesList;
    private long age;
    private int counter;
    private float weight,height;
    Boolean defaultChecked;
    ConstraintLayout singleUserLayout;
    ImageView helpWorkout,pauseWorkout,workoutImageView,musicImageView,voiceCommandsView,helpImageView;
    TextView titleOfWorkoutExercise,remainingTimeTxt,timeTxt;
    List<Audio> filesAudio;
    AppCompatButton pauseBtn;
    DisplayMetrics displayMetrics;
    LayoutInflater inflater;
    List<Audio> audioSelectedList;
    List<CheckBox> myMusicCheckboxes;
    Boolean checkedAudio;

    private TextToSpeech textToSpeech;
    CountDownTimer countDownTimer;
    private static final String TAG = "MainActivity";
    public int currentExercise,counterOfCheckboxSetTrue,counterOfCheckboxSetFalse;
    LinearLayout info;
    Button videoBtn,animationBtn,guidanceBtn;
    ImageView gif;
    WebView video;
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String STOP_ACTIVITY_PATH = "/stop-activity";
    private static final String WEAR_CAPABILITY = "wear";
    private String transcriptionNodeId;
    private ClientDataViewModel clientDataViewModel;
    private DataClient dataClient;
    private MessageClient messageClient;
    Boolean turnedOff;
    ExoPlayer player;
    private CapabilityClient capabilityClient;
    private List<String> mp3Files,workouts;
    Parcelable[] workoutExercises;
    private long startTime,totalMinutes,remainingSeconds;
    WorkoutExerciseSample[] workoutExerciseSamples;
    Boolean savedChoices;

    WorkoutSample workout;
    private Boolean voiceCmdsActive,exerciseBreak;
    CheckBox saveChoicesCheckbox,turnOnCheckbox,turnOffCheckbox;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));

        setContentView(R.layout.activity_single_workout);

        auth = FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        displayMetrics=   new DisplayMetrics();
        audioSelectedList=new ArrayList<>();
        myMusicCheckboxes=new ArrayList<>();
        filesAudio=fetchAudioFiles();
        counter=0;
        defaultChecked=false;
        savedChoices=false;

        player = new ExoPlayer.Builder(SingleWorkout.this).build();
        turnedOff=false;

        player.addListener(new Player.Listener() {
            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
            }
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    player.seekTo(0,0);
                    player.setPlayWhenReady(true);

                }
            }
        });

        /*
        if (musicFiles in database exist for user){
            then add them to the player
        }else{
            add 5 common gym songs
        }

         */





        clientDataViewModel = new ViewModelProvider(this).get(ClientDataViewModel.class);
        dataClient = Wearable.getDataClient(this);
        messageClient = Wearable.getMessageClient(this);
        capabilityClient = Wearable.getCapabilityClient(this);

        voiceCmdsActive=Boolean.TRUE;
        exerciseBreak=Boolean.FALSE;

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

        if (audioList.size()==1 && audioList.get(0).toString().equals("default")){
            player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music1));
            player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music2));
            player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music3));
            player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music4));
            player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music5));
            defaultChecked=true;
        } else {
            for (String audioName: audioList) {
                for (Audio audio:filesAudio) {
                    if (audio.name.toString().equals(audioName)){
                        audioSelectedList.add(audio);
                        player.addMediaItem(MediaItem.fromUri(audio.uri));
                    }
                }
            }
            if (audioList.contains("default")){
                player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music1));
                player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music2));
                player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music3));
                player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music4));
                player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music5));
                defaultChecked=true;
            }
        }

        //Layout
        singleUserLayout=(ConstraintLayout) findViewById(R.id.singleWorkoutLayout);

        //TextViews
        remainingTimeTxt=findViewById(R.id.remainingTimeTxt);
        timeTxt=findViewById(R.id.timeTxt);
        titleOfWorkoutExercise =findViewById(R.id.titleOfWorkout);

        //ImageViews
        workoutImageView=findViewById(R.id.workoutImageView);
        helpWorkout=findViewById(R.id.helpWorkout);
        pauseWorkout=findViewById(R.id.pauseWorkout);
        musicImageView=findViewById(R.id.music);
        voiceCommandsView=findViewById(R.id.voice);
        helpImageView=findViewById(R.id.helpWorkout);
        //Buttons
        pauseBtn=findViewById(R.id.pauseBtn);



        workoutExercises =(Parcelable[]) intent.getParcelableArrayExtra("WorkoutExercises");
        workoutExerciseSamples=new WorkoutExerciseSample[workoutExercises.length];
        workout=(WorkoutSample) intent.getParcelableExtra("Workout");
        currentExercise = 0;


        for (Parcelable workout : workoutExercises) {
            if (workout instanceof WorkoutExerciseSample) {
                workoutExerciseSamples[currentExercise] = (WorkoutExerciseSample) workout;
                if (workoutExerciseSamples[currentExercise].getTitle().equals("Plank hip dips")) {
                    Glide.with(this).asGif().load(R.drawable.gif_plank_hip_dips).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Plank hip dips");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Dolphin pose")) {
                    Glide.with(this).asGif().load(R.drawable.dolphin_pose).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Dolphin pose");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Mountain Climber")) {
                    Glide.with(this).asGif().load(R.drawable.mountain_climber).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Mountain Climber");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Plank jacks")) {
                    Glide.with(this).asGif().load(R.drawable.plank_jacks).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Plank jacks");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Side plank")) {
                    Glide.with(this).asGif().load(R.drawable.side_plank_left).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Side plank");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Regular plank")) {
                    Glide.with(this).asGif().load(R.drawable.regular_plank).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Regular plank");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Spiderman plank")) {
                    Glide.with(this).asGif().load(R.drawable.spiderman_plank).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Spiderman plank");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Single leg plank")) {
                    Glide.with(this).asGif().load(R.drawable.single_leg_plank).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Single leg plank");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Single arm plank")) {
                    Glide.with(this).asGif().load(R.drawable.burpees).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Single arm plank");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Commando plank")) {
                    Glide.with(this).asGif().load(R.drawable.plank_up_down).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Commando plank");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Cycling")) {
                    Glide.with(this).asGif().load(R.drawable.plank_up_down).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Cycling");
                }
                break;
            }
        }




        startWearableActivity();
        startTime = SystemClock.uptimeMillis();
        pauseWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.stop();
                stopCountdownTimer();
                player.pause();
                pauseWearableActivity();

                Blurry.with(SingleWorkout.this).radius(25).sampling(2).onto(singleUserLayout);
                AlertDialog.Builder builder = new AlertDialog.Builder(SingleWorkout.this, R.style.CustomDialogTheme);
                inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.activity_inflated_pause_workout, null);
                builder.setView(v);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                // Set the custom background drawable
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawableResource(R.drawable.dialog_transparent_bg);
                }


                dialog.show();

                WindowManager.LayoutParams layoutParams = window.getAttributes();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                layoutParams.width = (int) (displayMetrics.widthPixels * 0.5);
                layoutParams.height = (int) (displayMetrics.heightPixels * 0.5);
                window.setAttributes(layoutParams);

                // Edit text of info
                TextView continueTxtView = v.findViewById(R.id.continueTxtView);
                TextView finishWorkoutTxtView = v.findViewById(R.id.finishTxtView);
                TextView exitWorkoutTxtView = v.findViewById(R.id.exitTxtView);
                LinearLayout continueLinearLayout=v.findViewById(R.id.backgroundOfContinue);
                LinearLayout finishLayout=v.findViewById(R.id.backgroundOfFinish);

                continueTxtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startCountdownTimer(Long.parseLong(timeTxt.getText().toString())*1000);
                        player.play();
                        pauseBtn.setText("PAUSE");
                        continueWearableActivity();
                        dialog.dismiss();
                        Blurry.delete(singleUserLayout);
                    }
                });

                finishWorkoutTxtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        long endTime = SystemClock.uptimeMillis();
                        long elapsedMillis = endTime - startTime;
                        long elapsedSeconds = elapsedMillis / 1000;
                        totalMinutes = elapsedSeconds/60;
                        remainingSeconds=elapsedSeconds%60;

                        textToSpeech.stop();
                        player.stop();
                        player.release();
                        stopWearableActivity();
                        dialog.dismiss();
                        Blurry.delete(singleUserLayout);

                        // Transfer data from watch to database
                        Intent intent = new Intent(SingleWorkout.this, CurrentWorkoutStats.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        intent.putIntegerArrayListExtra("heartRateValues",MyPhoneListenerService.Companion.getArraylistOfHeartRates());
                        intent.putExtra("workoutTime",String.format("%02d min %02d sec",totalMinutes,remainingSeconds));
                        intent.putExtra("totalMinutes",totalMinutes);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        startActivity(intent);
                        finish();
                    }
                });
                exitWorkoutTxtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textToSpeech.stop();
                        player.stop();
                        player.release();
                        stopWearableActivity();
                        dialog.dismiss();
                        Intent intent = new Intent(SingleWorkout.this,Workouts.class);
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
        });
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pauseWearableActivity();
                if (pauseBtn.getText().toString().equals("CONTINUE")){
                    startCountdownTimer(Long.parseLong(timeTxt.getText().toString())*1000);
                    player.play();
                    pauseBtn.setText("PAUSE");
                    continueWearableActivity();
                } else if (pauseBtn.getText().toString().equals("PAUSE")) {
                    textToSpeech.stop();
                    stopCountdownTimer();
                    player.pause();
                    pauseBtn.setText("CONTINUE");
                    pauseWearableActivity();
                }else if(pauseBtn.getText().toString().equals("GO TO NEXT EXERCISE")){
                    pauseBtn.setText("PAUSE");
                    exerciseBreak=Boolean.FALSE;
                    //next exercise handle
                    currentExercise++;
                    int index=0;
                    for (Parcelable workout : workoutExercises) {
                        if (workout instanceof WorkoutExerciseSample && index==currentExercise) {
                            workoutExerciseSamples[currentExercise] = (WorkoutExerciseSample) workout;

                            timeTxt.setText("45");
                            stopCountdownTimer();
                            startCountdownTimer(Long.parseLong(timeTxt.getText().toString())*1000);
                            break;
                        }
                        index++;
                    }

                } else if (pauseBtn.getText().equals("FINISH WORKOUT SESSION")) {
                    long endTime = SystemClock.uptimeMillis();
                    long elapsedMillis = endTime - startTime;
                    long elapsedSeconds = elapsedMillis / 1000;
                    totalMinutes = elapsedSeconds/60;
                    remainingSeconds=elapsedSeconds%60;

                    stopWearableActivity();

                    // Transfer data from watch to database
                    Intent intent = new Intent(SingleWorkout.this, CurrentWorkoutStats.class);
                    intent.putExtra("selected_theme",user_theme);
                    intent.putExtra("age",age);
                    intent.putExtra("weight",weight);
                    intent.putExtra("height",height);
                    intent.putExtra("username",username);
                    intent.putExtra("gender",gender);
                    intent.putExtra("experience",experience);
                    intent.putExtra("target",target);
                    intent.putIntegerArrayListExtra("heartRateValues",MyPhoneListenerService.Companion.getArraylistOfHeartRates());
                    intent.putExtra("workoutTime",String.format("%02d min %02d sec",totalMinutes,remainingSeconds));
                    intent.putExtra("totalMinutes",totalMinutes);
                    intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                    intent.putExtra("caloriesList",caloriesList);
                    intent.putExtra("workoutTimesList",workoutTimesList);
                    intent.putExtra("datesList",datesList);
                    startActivity(intent);
                }


            }
        });
        musicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Pause
                textToSpeech.stop();
                stopCountdownTimer();
                player.pause();
                pauseWearableActivity();


                File directory = Environment.getExternalStorageDirectory();

                //List<String> filesAudio=fetchAudioFiles(getApplicationContext(),);
                //List<File> directories = getAllDirectories(directory);
                File[] files = directory.listFiles();


                counterOfCheckboxSetTrue=0;
                counterOfCheckboxSetFalse=0;
                audioSelectedList.clear();

                AlertDialog.Builder builder = new AlertDialog.Builder(SingleWorkout.this,R.style.CustomDialogTheme);
                inflater=getLayoutInflater();
                View v = inflater.inflate(R.layout.activity_music_list,null);
                builder.setView(v);
                builder.setNegativeButton("Cancel",(dialog,which) -> {});
                builder.setPositiveButton("Select",(dialog,which) -> {});
                saveChoicesCheckbox=(CheckBox) v.findViewById(R.id.saveMusicChoices);
                turnOnCheckbox=(CheckBox) v.findViewById(R.id.checkboxTurnMusicOn);
                turnOffCheckbox=(CheckBox) v.findViewById(R.id.checkboxTurnMusicOff);

                LinearLayout myMusicList = (LinearLayout) v.findViewById(R.id.musicList);
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

                Button selectBtn=dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button closeBtn=dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                turnOnCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if (checked){
                            turnedOff=false;
                            turnOffCheckbox.setChecked(false);
                            selectBtn.setText("Select");
                            for (int i = 0; i < myMusicList.getChildCount(); i++) {
                                myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                            }

                        }else {
                            turnedOff=true;
                            turnOffCheckbox.setChecked(true);
                            selectBtn.setText("OK");
                            for (int i = 0; i < myMusicList.getChildCount(); i++) {
                                if (myMusicList.getChildAt(i).equals(turnOnCheckbox) || myMusicList.getChildAt(i).equals(turnOffCheckbox) || myMusicList.getChildAt(i).equals((TextView)v.findViewById(R.id.music_settings))){
                                    myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                                }else {
                                    myMusicList.getChildAt(i).setVisibility(View.GONE);
                                }
                            }

                        }
                    }
                });
                turnOffCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if (checked){
                            turnedOff=true;
                            turnOnCheckbox.setChecked(false);
                            selectBtn.setText("OK");
                            for (int i = 0; i < myMusicList.getChildCount(); i++) {
                                if (myMusicList.getChildAt(i).equals(turnOnCheckbox) || myMusicList.getChildAt(i).equals(turnOffCheckbox) || myMusicList.getChildAt(i).equals((TextView)v.findViewById(R.id.music_settings))){
                                    myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                                }else {
                                    myMusicList.getChildAt(i).setVisibility(View.GONE);
                                }
                            }
                        }else {
                            turnedOff=false;
                            turnOnCheckbox.setChecked(true);
                            selectBtn.setText("Select");
                            for (int i = 0; i < myMusicList.getChildCount(); i++) {
                                myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
                saveChoicesCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if (checked){
                            //Save to database
                            CheckBox defaultPlaylistCheckbox = v.findViewById(R.id.checkboxRecMusic);
                            if (defaultPlaylistCheckbox.isChecked()){
                                audioList.add("default");
                            }else {
                                audioList.remove("default");
                            }
                            for (Audio audio: audioSelectedList){
                                audioList.add(audio.name);
                            }
                            savedChoices=true;


                        }else {
                            //Dont save to database
                            savedChoices=false;
                        }
                    }
                });

                selectBtn.setTextSize(12);
                selectBtn.setGravity(Gravity.CENTER);
                selectBtn.setTextColor(Color.parseColor("#ffffff"));
                selectBtn.setBackground(getResources().getDrawable(R.drawable.close_btn,getTheme()));
                selectBtn.setPaddingRelative(50,-100,50,-100);
                selectBtn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                closeBtn.setTextSize(12);
                closeBtn.setGravity(Gravity.CENTER);
                closeBtn.setTextColor(Color.parseColor("#ffffff"));
                closeBtn.setBackground(getResources().getDrawable(R.drawable.close_btn,getTheme()));
                closeBtn.setPaddingRelative(50,-100,50,-100);
                closeBtn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                // Set layout parameters for the close button
                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                buttonLayoutParams.setMargins(0, 0, 0, 5); // Set bottom margin here, e.g., 16 pixels
                closeBtn.setLayoutParams(buttonLayoutParams);
                selectBtn.setLayoutParams(buttonLayoutParams);



                for (Audio audio : filesAudio) {

                    Log.d("audio",audio.name);
                    buttonLayoutParams.setMargins(29,0,0,0);
                    ContextThemeWrapper contextWrapper = new ContextThemeWrapper(SingleWorkout.this, R.style.CustomCheckbox);
                    CheckBox myMusicCheckBox=new CheckBox(contextWrapper);
                    myMusicCheckBox.setText(audio.name);
                    myMusicCheckBox.setTypeface(ResourcesCompat.getFont(SingleWorkout.this,R.font.aldrich));
                    myMusicCheckBox.setLayoutParams(buttonLayoutParams);
                    myMusicCheckBox.setTextColor(Color.parseColor("#B5000000"));


                    myMusicCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked){
                                for (Audio audio : filesAudio){
                                    if (audio.name.equals(myMusicCheckBox.getText())){
                                        audioSelectedList.add(audio);

                                    }
                                }
                            }else {
                                for (Audio audio : filesAudio){
                                    if (audio.name.equals(myMusicCheckBox.getText())){
                                        audioSelectedList.remove(audio);
                                    }
                                }
                            }
                        }
                    });

                    for (CheckBox checkBox : myMusicCheckboxes){
                        if (myMusicCheckBox.getText().equals(checkBox.getText())){
                            if (checkBox.isChecked()){
                                myMusicCheckBox.setChecked(true);

                            }else {
                                if (counter==0){
                                    for (String audioName:audioList){
                                        if (checkBox.getText().equals(audioName)){
                                            checkBox.setChecked(true);
                                            counter++;
                                        }
                                    }
                                }

                                if (counter==0){
                                    myMusicCheckBox.setChecked(false);
                                }

                            }
                        }
                    }

                    myMusicList.addView(myMusicCheckBox);
                    if (!myMusicCheckboxes.contains(myMusicCheckBox)){
                        myMusicCheckboxes.add(myMusicCheckBox);
                    }


                }
                for (CheckBox checkBox : myMusicCheckboxes){
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked){
                                for (Audio audio : filesAudio){
                                    if (audio.name.equals(checkBox.getText())){
                                        audioSelectedList.add(audio);

                                    }
                                }
                            }else {
                                for (Audio audio : filesAudio){
                                    if (audio.name.equals(checkBox.getText())){
                                        audioSelectedList.remove(audio);
                                    }
                                }
                            }
                        }
                    });
                }

                if (turnOnCheckbox.isChecked() && !turnedOff){
                    turnOnCheckbox.setChecked(true);
                    turnOffCheckbox.setChecked(false);
                    selectBtn.setText("Select");
                    for (int i = 0; i < myMusicList.getChildCount(); i++) {
                        myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                }else {
                    turnOffCheckbox.setChecked(true);
                    turnOnCheckbox.setChecked(false);
                    selectBtn.setText("OK");
                    for (int i = 0; i < myMusicList.getChildCount(); i++) {
                        if (myMusicList.getChildAt(i).equals(turnOnCheckbox) || myMusicList.getChildAt(i).equals(turnOffCheckbox) || myMusicList.getChildAt(i).equals((TextView)v.findViewById(R.id.music_settings))){
                            myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                        }else {
                            myMusicList.getChildAt(i).setVisibility(View.GONE);
                        }
                    }
                }
                selectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (turnOffCheckbox.isChecked()){
                            player.clearMediaItems();
                            player.stop();
                            //player.release();
                        }else {
                            //Check for duplicates
                            for (CheckBox checkBox : myMusicCheckboxes){
                                if (checkBox.isSelected()){
                                    for (Audio audio : filesAudio){
                                        if (audio.name.equals(checkBox.getText())){
                                            audioSelectedList.add(audio);
                                        }
                                    }
                                }


                            }
                            player.clearMediaItems();


                            ArrayList<String> audioSelectedListNames=new ArrayList<>();

                            for(Audio audioSelected : audioSelectedList){
                                //Toast.makeText(SingleWorkout.this, audioSelected.name+" selected", Toast.LENGTH_SHORT).show();
                                player.addMediaItem(MediaItem.fromUri(audioSelected.uri));
                                audioSelectedListNames.add(audioSelected.name);
                            }
                            if (savedChoices){
                                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.getUid()).child("audioList").setValue(audioSelectedListNames);
                            }
                            // Prepare the player.
                            player.prepare();
                            // Start the playback.
                            player.play();
                            player.setVolume(0.5F);
                        }
                        pauseBtn.setText("CONTINUE");
                        dialog.dismiss();

                    }

                });
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pauseBtn.setText("CONTINUE");
                        dialog.dismiss();
                    }
                });




            }

        });
        voiceCommandsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (voiceCmdsActive){
                    player.setVolume(0.5F);
                    voiceCmdsActive=Boolean.FALSE;
                    //Save to database
                    // Change image to view
                    voiceCommandsView.setBackgroundResource(R.drawable.volume_off);
                    voiceCommandsView.setImageResource(R.drawable.volume_off);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        voiceCommandsView.setImageDrawable(getResources().getDrawable(R.drawable.volume_off, getApplicationContext().getTheme()));
                    } else {
                        voiceCommandsView.setImageDrawable(getResources().getDrawable(R.drawable.volume_off));
                    }
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                }else {
                    voiceCmdsActive=Boolean.TRUE;
                    //Save to database
                    // Change image to view
                    voiceCommandsView.setBackgroundResource(R.drawable.volume_up);
                    voiceCommandsView.setImageResource(R.drawable.volume_up);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        voiceCommandsView.setImageDrawable(getResources().getDrawable(R.drawable.volume_up, getApplicationContext().getTheme()));
                    } else {
                        voiceCommandsView.setImageDrawable(getResources().getDrawable(R.drawable.volume_up));
                    }
                    textToSpeech = new TextToSpeech(SingleWorkout.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {

                                int result = textToSpeech.setLanguage(Locale.US);
                                if (result == TextToSpeech.LANG_MISSING_DATA
                                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    Log.e(TAG, "Language not supported");
                                } else {

                                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                        @Override
                                        public void onStart(String s) {

                                        }

                                        @Override
                                        public void onDone(String s) {
                                            if (s.equals("1")){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startCountdownTimer(45*1000);
                                                        float i=player.getVolume();
                                                        while (i<0.55F){
                                                            player.setVolume(i);
                                                            i+=0.0005F;
                                                        }
                                                    }
                                                });
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        float i=player.getVolume();
                                                        while (i<0.55F){
                                                            player.setVolume(i);
                                                            i+=0.0005F;
                                                        }
                                                    }
                                                });
                                            }

                                        }

                                        @Override
                                        public void onError(String s) {

                                        }



                                    });
                                    //speak("First exercise, plank hip dips. Repeat for 45 seconds.","1");




                                }


                            }else {
                                Log.e(TAG, "Initialization failed");
                            }
                        }
                    });

                }

            }
        });
        helpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause
                textToSpeech.stop();
                stopCountdownTimer();
                player.pause();
                pauseWearableActivity();

                if (titleOfWorkoutExercise.getText().equals("Regular plank")){
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
                } else if (titleOfWorkoutExercise.getText().equals("Mountain Climber")){
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
                } else if (titleOfWorkoutExercise.getText().equals("Dolphin pose")){
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
                } else if (titleOfWorkoutExercise.getText().equals("Single leg plank")){
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
                } else if (titleOfWorkoutExercise.getText().equals("Single arm plank")){
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
                }else if (titleOfWorkoutExercise.getText().equals("Plank jacks")){
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
                } else if (titleOfWorkoutExercise.getText().equals("Commando plank")){
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
                }
                else if (titleOfWorkoutExercise.getText().equals("Spiderman plank")){
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
                }
                else if (titleOfWorkoutExercise.getText().equals("Side plank")){
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
                }

            }
        });



        textToSpeech = new TextToSpeech(SingleWorkout.this, this::onInit);
        player.prepare();
        player.play();
        player.setVolume(0.2F);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed...
                textToSpeech.stop();
                stopCountdownTimer();
                player.pause();
                pauseBtn.setText("CONTINUE");
                pauseWearableActivity();
                Blurry.with(SingleWorkout.this).radius(25).sampling(2).onto(singleUserLayout);
                AlertDialog.Builder builder = new AlertDialog.Builder(SingleWorkout.this, R.style.CustomDialogTheme);
                inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.activity_inflated_pause_workout, null);
                builder.setView(v);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                // Set the custom background drawable
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawableResource(R.drawable.dialog_transparent_bg);
                }


                dialog.show();

                WindowManager.LayoutParams layoutParams = window.getAttributes();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                layoutParams.width = (int) (displayMetrics.widthPixels * 0.5);
                layoutParams.height = (int) (displayMetrics.heightPixels * 0.5);
                window.setAttributes(layoutParams);

                // Edit text of info
                TextView continueTxtView = v.findViewById(R.id.continueTxtView);
                TextView finishWorkoutTxtView = v.findViewById(R.id.finishTxtView);
                TextView exitWorkoutTxtView= v.findViewById(R.id.exitTxtView);
                LinearLayout continueLinearLayout=v.findViewById(R.id.backgroundOfContinue);
                LinearLayout finishLayout=v.findViewById(R.id.backgroundOfFinish);

                continueTxtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startCountdownTimer(Long.parseLong(timeTxt.getText().toString())*1000);
                        player.play();
                        pauseBtn.setText("PAUSE");
                        continueWearableActivity();
                        dialog.dismiss();
                        Blurry.delete(singleUserLayout);
                    }
                });

                finishWorkoutTxtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        long endTime = SystemClock.uptimeMillis();
                        long elapsedMillis = endTime - startTime;
                        long elapsedSeconds = elapsedMillis / 1000;
                        totalMinutes = elapsedSeconds/60;
                        remainingSeconds=elapsedSeconds%60;

                        textToSpeech.stop();
                        player.stop();
                        player.release();
                        stopWearableActivity();
                        dialog.dismiss();
                        Blurry.delete(singleUserLayout);

                        // Transfer data from watch to database
                        Intent intent = new Intent(SingleWorkout.this, CurrentWorkoutStats.class);
                        intent.putExtra("selected_theme",user_theme);
                        intent.putExtra("age",age);
                        intent.putExtra("weight",weight);
                        intent.putExtra("height",height);
                        intent.putExtra("username",username);
                        intent.putExtra("gender",gender);
                        intent.putExtra("experience",experience);
                        intent.putExtra("target",target);
                        intent.putStringArrayListExtra("savedAudioList", (ArrayList<String>) audioList);
                        intent.putIntegerArrayListExtra("heartRateValues",MyPhoneListenerService.Companion.getArraylistOfHeartRates());
                        intent.putExtra("workoutTime",String.format("%02d min %02d sec",totalMinutes,remainingSeconds));
                        intent.putExtra("totalMinutes",totalMinutes);
                        intent.putExtra("avgHeartRatesList",avgHeartRatesList);
                        intent.putExtra("caloriesList",caloriesList);
                        intent.putExtra("workoutTimesList",workoutTimesList);
                        intent.putExtra("datesList",datesList);
                        startActivity(intent);
                        finish();
                    }
                });
                exitWorkoutTxtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SingleWorkout.this,Workouts.class);

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
        });

    }

    
    private void startCountdownTimer(long totalTimeInMillis) {
        remainingTimeTxt.setText("Remaining time");
        long intervalInMillis = 1000; // 1 second interval
        if (!timeTxt.getText().equals("00") && !timeTxt.getText().toString().equals(String.format("%02d", totalTimeInMillis/1000))){
            totalTimeInMillis=Integer.parseInt(timeTxt.getText().toString());
        }
        long finalTotalTimeInMillis = totalTimeInMillis;
        countDownTimer = new CountDownTimer(finalTotalTimeInMillis, intervalInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Calculate remaining seconds
                long secondsRemaining = millisUntilFinished / 1000;

                // Update the TextView with the remaining seconds
                timeTxt.setText(String.format("%02d", secondsRemaining));
                if (timeTxt.getText().toString().equals("44")){

                    if (titleOfWorkoutExercise.getText().equals("Mountain Climber")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }

                        speak("Start in plank position. Put your hands below your shoulders and bring one knee towards your elbow on the same side.","2");
                    }else if (titleOfWorkoutExercise.getText().equals("Regular plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Put your elbows on the ground and make a triangle base with your forearms,legs,spine and neck in one straight line.","3");
                    }else if (titleOfWorkoutExercise.getText().equals("Plank hip dips")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Starting in elbow plank position, rotate your hip to one side and dip your body to lightly tap the floor. Rotate between sides for 45 seconds.","3");
                    }else if (titleOfWorkoutExercise.getText().equals("Dolphin pose")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Starting in elbow plank position, press your forearms and elbows into the floor while raising your hips to form an upside down V. ","3");
                    }else if (titleOfWorkoutExercise.getText().equals("Side plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Put one forearm on the floor, your fingers should face in front, your elbow must be right under your shoulder and your feet stack. Switch side after 22 seconds.","3");
                    }else if (titleOfWorkoutExercise.getText().equals("Single leg plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Starting in elbow plank position, raise one leg off the floor and hold it up. Switch leg after 22 seconds.","3");
                    }else if (titleOfWorkoutExercise.getText().equals("Spiderman plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Starting in elbow plank position, bring one knee in towards your elbow at the same side. Alternate between sides for 45 seconds.","3");
                    }else if (titleOfWorkoutExercise.getText().equals("Single arm plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Starting in high plank position, lift one arm up. Switch arm after 22 seconds.","3");
                    }else if (titleOfWorkoutExercise.getText().equals("Commando plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Starting in elbow plank position, push yourself up to high plank with your right arm and then your left arm. Repeat for 45 seconds.","3");
                    }else if (titleOfWorkoutExercise.getText().equals("Plank jacks")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Starting in elbow plank position, jump your legs wide and then back together.","3");
                    }


                }
                if (timeTxt.getText().toString().equals("30")){

                    if (titleOfWorkoutExercise.getText().equals("Regular plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("You should feel a burn in your abs, arms, shoulders and back. The burn is getting more intense but don't give up. Stay strong until the timer ends!","4");
                    } else if (titleOfWorkoutExercise.getText().equals("Mountain Climber")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Remember to do this slow and controlled. Feel the burn in your inner and lower abs for each rep.","5");
                    } else if (titleOfWorkoutExercise.getText().equals("Dolphin pose")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("You should feel a burn in your abs, arms, shoulders and back. The burn is getting more intense but don't give up. Stay strong until the timer ends!","4");
                    } else if (titleOfWorkoutExercise.getText().equals("Single leg plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("In this exercise you are working on your midsection, upper body and lower body. Try your best to not drop your leg to the floor.","5");
                    }  else if (titleOfWorkoutExercise.getText().equals("Side plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("In this exercise you are working on your midsection, upper body and lower body. Try your best to not drop your leg to the floor.","5");
                    } else if (titleOfWorkoutExercise.getText().equals("Spiderman plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("In this exercise you are working on your midsection, upper body and lower body. Try your best to not drop your leg to the floor.","5");
                    }else if (titleOfWorkoutExercise.getText().equals("Single arm plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Try your best to hold up your body and keep it in one straight line.","5");
                    }else if (titleOfWorkoutExercise.getText().equals("Plank jacks")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("The faster you jump the more intense it is.","5");
                    } else if (titleOfWorkoutExercise.getText().equals("Commando plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("This is a high impact exercise for your upper body producing maximum burn.","5");
                    }


                }
                if (String.format("%02d", secondsRemaining).equals("00") && !exerciseBreak && !pauseBtn.getText().toString().equals("GO TO NEXT EXERCISE") && !titleOfWorkoutExercise.getText().equals("Plank jacks")){
                    countDownTimer.cancel();
                    if (workoutExercises.length/2==currentExercise+1){
                        startCountdownTimer(60*1000);
                    }else {
                        startCountdownTimer(20*1000);
                    }

                    remainingTimeTxt.setText("Minibreak remaining time");

                    if (titleOfWorkoutExercise.getText().equals("Regular plank")){
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Rest. Next exercise, mountain climber","5");
                        Glide.with(SingleWorkout.this).asGif().load(R.drawable.mountain_climber).into(workoutImageView);
                        titleOfWorkoutExercise.setText("Mountain Climber");
                    } else if (titleOfWorkoutExercise.getText().equals("Mountain Climber")) {
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Rest. Next exercise, dolphin pose","5");
                        Glide.with(SingleWorkout.this).asGif().load(R.drawable.dolphin_pose).into(workoutImageView);
                        titleOfWorkoutExercise.setText("Dolphin pose");
                    } else if (titleOfWorkoutExercise.getText().equals("Dolphin pose")) {
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Rest. Next exercise, single leg plank","5");
                        Glide.with(SingleWorkout.this).asGif().load(R.drawable.single_leg_plank).into(workoutImageView);
                        titleOfWorkoutExercise.setText("Single leg plank");
                    } else if (titleOfWorkoutExercise.getText().equals("Single leg plank")) {
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Rest. Next exercise, plank hip dips","5");
                        Glide.with(SingleWorkout.this).asGif().load(R.drawable.gif_plank_hip_dips).into(workoutImageView);
                        titleOfWorkoutExercise.setText("Plank hip dips");
                    } else if (titleOfWorkoutExercise.getText().equals("Plank hip dips")) {
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Rest. Next exercise, side plank left and right","5");
                        Glide.with(SingleWorkout.this).asGif().load(R.drawable.side_plank_left).into(workoutImageView);
                        titleOfWorkoutExercise.setText("Side plank");
                    } else if (titleOfWorkoutExercise.getText().equals("Side plank")) {
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Rest. Next exercise, spiderman plank","5");
                        Glide.with(SingleWorkout.this).asGif().load(R.drawable.spiderman_plank).into(workoutImageView);
                        titleOfWorkoutExercise.setText("Spiderman plank");
                    } else if (titleOfWorkoutExercise.getText().equals("Spiderman plank")) {
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Rest. Next exercise, single arm plank","5");
                        Glide.with(SingleWorkout.this).asGif().load(R.drawable.burpees).into(workoutImageView);
                        titleOfWorkoutExercise.setText("Single arm plank");
                    } else if (titleOfWorkoutExercise.getText().equals("Single arm plank")) {
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Rest. Next exercise, commando plank","5");
                        Glide.with(SingleWorkout.this).asGif().load(R.drawable.plank_up_down).into(workoutImageView);
                        titleOfWorkoutExercise.setText("Commando plank");
                    } else if (titleOfWorkoutExercise.getText().equals("Commando plank")) {
                        if (voiceCmdsActive){
                            player.setVolume(0.15F);
                        }
                        speak("Rest. Next exercise, plank jacks","5");
                        Glide.with(SingleWorkout.this).asGif().load(R.drawable.plank_jacks).into(workoutImageView);
                        titleOfWorkoutExercise.setText("Plank jacks");
                    }

                    pauseBtn.setText("GO TO NEXT EXERCISE");
                    exerciseBreak=Boolean.TRUE;
                }
            }

            @Override
            public void onFinish() {
                //timeTxt.setText("00");
                //stopCountdownTimer();
                if (titleOfWorkoutExercise.getText().equals("Plank jacks")){
                    pauseBtn.setText("FINISH WORKOUT SESSION");
                }else {

                    timeTxt.setText("45");
                    stopCountdownTimer();
                    startCountdownTimer(Long.parseLong(timeTxt.getText().toString())*1000);
                    pauseBtn.setText("PAUSE");
                    exerciseBreak=Boolean.FALSE;
                    //next exercise handle
                    currentExercise++;
                }




            }
        };

        countDownTimer.start();
    }
    private void stopCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language not supported");
            } else {

                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String s) {

                    }

                    @Override
                    public void onDone(String s) {
                        if (s.equals("1")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startCountdownTimer(45*1000);
                                    float i=player.getVolume();
                                    while (i<0.55F){
                                        player.setVolume(i);
                                        i+=0.05F;
                                    }

                                    //UI changes
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    float i=player.getVolume();
                                    while (i<0.55F){
                                        player.setVolume(i);
                                        i+=0.05F;
                                    }
                                }
                            });
                        }

                    }

                    @Override
                    public void onError(String s) {

                    }



                });
                if (voiceCmdsActive){
                    player.setVolume(0.15F);
                }
                if (titleOfWorkoutExercise.getText().equals("Regular plank")){
                    speak("First exercise, regular plank. Repeat for 45 seconds.","1");
                }

                //player.setVolume(0.5F);




            }


        }else {
            Log.e(TAG, "Initialization failed");
        }


    }
    public void exerciseInstructions(String videoEmbed,Integer drawableGif,String startingPositionTitleText,String startingPositionAnalysisText,String secondaryPositionTitleText,String secondaryPositionAnalysisText,String thirdPositionTitleText,String thirdPositionAnalysisText,String finalPositionTitleText,String finalPositionAnalysisText,String firstBenefitTitleText,String firstBenefitAnalysisText,String secondBenefitTitleText,String secondBenefitAnalysisText,String thirdBenefitTitleText,String thirdBenefitAnalysisText,String fourthBenefitTitleText,String fourthBenefitAnalysisText,String overallAnalysisText,String websiteLinkText){
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleWorkout.this, R.style.CustomDialogTheme);
        inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_exercise_instructions, null);
        builder.setView(v);
        builder.setNegativeButton("Close", (dialog, which) -> {
        });
        builder.setCancelable(false);
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
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCountdownTimer(Long.parseLong(timeTxt.getText().toString())*1000);
                player.play();
                pauseBtn.setText("PAUSE");
                continueWearableActivity();
                dialog.dismiss();
            }
        });
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
        Glide.with(SingleWorkout.this).asGif().load(drawableGif).into(gif);

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



    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (player != null){
            player.stop();
            player.release();
        }

        super.onDestroy();
    }
    private void speak(String text,String utteranceId){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }


    // Method to fetch all audio files, including MP3 files in the Downloads folder


     public class Audio {
        private final Uri uri;
        private final String name;
        private final int duration;
        private final int size;

        public Audio(Uri uri, String name, int duration, int size) {
            this.uri = uri;
            this.name = name;
            this.duration = duration;
            this.size = size;
        }
    }
    public List<Audio> fetchAudioFiles() {

        List<Audio> audioList = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
        };


        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                projection,
                null,
                null,
                null
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given audio.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                audioList.add(new Audio(contentUri, name, duration, size));
            }
        }
        return audioList;

    }


    private Collection<String> getNodes() {
        try {
            return Tasks.await(Wearable.getNodeClient(getApplicationContext()).getConnectedNodes()).stream()
                    .map(node -> node.getId())
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void startWearableActivity() {
        new Thread(() -> {
            Collection<String> nodes = getNodes();
            if (nodes != null && !nodes.isEmpty()) {
                transcriptionNodeId = nodes.stream().findFirst().orElse(null);
                if (transcriptionNodeId != null) {
                    try {
                        Tasks.await(Wearable.getMessageClient(getApplicationContext()).sendMessage(
                                        transcriptionNodeId,
                                        START_ACTIVITY_PATH,
                                        "activity started".getBytes()
                                ));
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void pauseWearableActivity(){
        new Thread(() -> {
            Collection<String> nodes = getNodes();
            if (nodes != null && !nodes.isEmpty()) {
                transcriptionNodeId = nodes.stream().findFirst().orElse(null);
                try {
                    Tasks.await(Wearable.getMessageClient(getApplicationContext()).sendMessage(
                            transcriptionNodeId,
                            "/pause-activity",
                            "activity paused".getBytes()
                    ));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void continueWearableActivity(){
        new Thread(() -> {
            Collection<String> nodes = getNodes();
            if (nodes != null && !nodes.isEmpty()) {
                transcriptionNodeId = nodes.stream().findFirst().orElse(null);
                try {
                    Tasks.await(Wearable.getMessageClient(getApplicationContext()).sendMessage(
                            transcriptionNodeId,
                            "/continue-activity",
                            "activity continues".getBytes()
                    ));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void stopWearableActivity() {
        new Thread(() -> {
            Collection<String> nodes = getNodes();
            if (nodes != null && !nodes.isEmpty()) {
                transcriptionNodeId = nodes.stream().findFirst().orElse(null);
                try {
                    Tasks.await(Wearable.getMessageClient(getApplicationContext()).sendMessage(
                            transcriptionNodeId,
                            "/stop-activity",
                            "activity finished".getBytes()
                    ));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Check if the screen is turning off
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOff = !powerManager.isInteractive();

        if (isScreenOff) {
            player.pause();
            stopCountdownTimer();
            textToSpeech.stop();
            if (pauseBtn.getText().equals("GO TO NEXT EXERCISE")){
                // Nothing to change
            }else {
                pauseBtn.setText("CONTINUE");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }






}
