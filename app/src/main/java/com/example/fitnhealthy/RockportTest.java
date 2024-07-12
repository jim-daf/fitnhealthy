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

import android.os.Environment;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;


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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import jp.wasabeef.blurry.Blurry;

public class RockportTest extends AppCompatActivity implements Player.Listener{
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String username,user_theme,gender,experience,target;
    private TextToSpeech textToSpeech;
    private Boolean voiceCmdsActive,exerciseBreak;
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
    AppCompatButton pauseBtn,finishBtn;
    DisplayMetrics displayMetrics;
    LayoutInflater inflater;
    List<Audio> audioSelectedList;
    List<CheckBox> myMusicCheckboxes;
    Boolean checkedAudio;
    private static final String TAG = "MainActivity";
    public int currentExercise,counterOfCheckboxSetTrue,counterOfCheckboxSetFalse;
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

    WorkoutSample workout;
    CheckBox saveChoicesCheckbox,turnOnCheckbox,turnOffCheckbox;
    Timer timer;
    TimerTask timerTask;
    Double time;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));

        setContentView(R.layout.activity_other_workouts);

        auth = FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        displayMetrics=   new DisplayMetrics();
        
        counter=0;
        defaultChecked=false;
        time=0.0;
        timer=new Timer();


        
        turnedOff=false;

        







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
        finishBtn=findViewById(R.id.finishBtn);



        workoutExercises =(Parcelable[]) intent.getParcelableArrayExtra("WorkoutExercises");
        workoutExerciseSamples=new WorkoutExerciseSample[workoutExercises.length];
        workout=(WorkoutSample) intent.getParcelableExtra("Workout");
        currentExercise = 0;


        for (Parcelable workout : workoutExercises) {
            if (workout instanceof WorkoutExerciseSample) {
                workoutExerciseSamples[currentExercise] = (WorkoutExerciseSample) workout;
                if (workoutExerciseSamples[currentExercise].getTitle().equals("Walking")) {
                    Glide.with(this).asGif().load(R.drawable.walking_anim).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Walking");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Cycling")) {
                    Glide.with(this).asGif().load(R.drawable.cycling_anim).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Cycling");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Running")) {
                    Glide.with(this).asGif().load(R.drawable.running_anim).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Running");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Tennis")) {
                    Glide.with(this).asGif().load(R.drawable.tennis_anim).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Tennis");
                } else if (workoutExerciseSamples[currentExercise].getTitle().equals("Volleyball")) {
                    Glide.with(this).asGif().load(R.drawable.volley_anim).into(workoutImageView);
                    titleOfWorkoutExercise.setText("Volleyball");
                }
                break;
            }
        }




        startWearableActivity();
        startTime = SystemClock.uptimeMillis();
        startTimer();
        pauseWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timerTask.cancel();
                player.pause();
                pauseWearableActivity();

                Blurry.with(RockportTest.this).radius(25).sampling(2).onto(singleUserLayout);
                AlertDialog.Builder builder = new AlertDialog.Builder(RockportTest.this, R.style.CustomDialogTheme);
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
                        startTimer();
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


                        player.stop();
                        player.release();
                        stopWearableActivity();
                        dialog.dismiss();
                        Blurry.delete(singleUserLayout);

                        // Transfer data from watch to database
                        Intent intent = new Intent(RockportTest.this, CurrentWorkoutStats.class);
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

                        player.stop();
                        player.release();
                        stopWearableActivity();
                        dialog.dismiss();
                        Intent intent = new Intent(RockportTest.this,Workouts.class);
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
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerTask.cancel();
                player.stop();
                player.release();
                long endTime = SystemClock.uptimeMillis();
                long elapsedMillis = endTime - startTime;
                long elapsedSeconds = elapsedMillis / 1000;
                totalMinutes = elapsedSeconds/60;
                remainingSeconds=elapsedSeconds%60;

                stopWearableActivity();

                // Transfer data from watch to database
                Intent intent = new Intent(RockportTest.this, CurrentWorkoutStats.class);
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
        });
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pauseWearableActivity();
                if (pauseBtn.getText().toString().equals("CONTINUE")){
                    startTimer();
                    player.play();
                    pauseBtn.setText("PAUSE");
                    continueWearableActivity();
                } else if (pauseBtn.getText().toString().equals("PAUSE")) {

                    timerTask.cancel();
                    player.pause();
                    pauseBtn.setText("CONTINUE");
                    pauseWearableActivity();
                }


            }
        });







        textToSpeech = new TextToSpeech(RockportTest.this, this::onInit);
        player.prepare();
        player.play();
        player.setVolume(0.2F);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed...

                timerTask.cancel();
                player.pause();
                pauseBtn.setText("CONTINUE");
                pauseWearableActivity();
                Blurry.with(RockportTest.this).radius(25).sampling(2).onto(singleUserLayout);
                AlertDialog.Builder builder = new AlertDialog.Builder(RockportTest.this, R.style.CustomDialogTheme);
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
                        startTimer();
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


                        player.stop();
                        player.release();
                        stopWearableActivity();
                        dialog.dismiss();
                        Blurry.delete(singleUserLayout);

                        // Transfer data from watch to database
                        Intent intent = new Intent(RockportTest.this, CurrentWorkoutStats.class);
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
                        Intent intent = new Intent(RockportTest.this,Workouts.class);

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


    @Override
    protected void onDestroy() {

        if (player != null){
            player.stop();
            player.release();
        }

        super.onDestroy();
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
            timerTask.cancel();

            if (pauseBtn.getText().equals("GO TO NEXT EXERCISE")){
                // Nothing to change
            }else {
                pauseBtn.setText("CONTINUE");
            }
        }
    }
    private void startTimer(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        time++;
                        timeTxt.setText(getTimerText());

                    }
                });

            }
        };
        timer.schedule(timerTask,0,1000);
    }
    private String getTimerText(){
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) %  3600)%60;
        int minutes = ((rounded % 86400) %  3600)/60;
        int hours = (rounded % 86400) /  3600;
        return formatTime(seconds,minutes,hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }


    @Override
    protected void onResume() {
        super.onResume();


    }






}
