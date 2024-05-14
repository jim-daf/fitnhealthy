package com.example.fitnhealthy;




import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

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
import android.os.PowerManager;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;

import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SingleWorkout extends AppCompatActivity implements TextToSpeech.OnInitListener,Player.Listener{
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String user_theme;
    ConstraintLayout singleUserLayout;
    ImageView helpWorkout,pauseWorkout,workoutImageView,musicImageView,voiceCommandsView,helpImageView;
    TextView titleOfWorkout,remainingTimeTxt,timeTxt;
    Button pauseBtn,continueBtn;
    DisplayMetrics displayMetrics;
    LayoutInflater inflater;
    List<Audio> audioSelectedList;
    private TextToSpeech textToSpeech;
    CountDownTimer countDownTimer;
    private static final String TAG = "MainActivity";

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String STOP_ACTIVITY_PATH = "/stop-activity";
    private static final String WEAR_CAPABILITY = "wear";
    private String transcriptionNodeId;
    private ClientDataViewModel clientDataViewModel;
    private DataClient dataClient;
    private MessageClient messageClient;
    ExoPlayer player;
    private CapabilityClient capabilityClient;
    private List<String> mp3Files,workouts;
    private Boolean voiceCmdsActive,exerciseBreak;
    CheckBox saveChoicesCheckbox,turnOnCheckbox,turnOffCheckbox;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));

        setContentView(R.layout.activity_single_workout);

        auth = FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        displayMetrics=  new DisplayMetrics();
        audioSelectedList=new ArrayList<>();
        player = new ExoPlayer.Builder(SingleWorkout.this).build();

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
        player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music1));
        //player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music2));
        //player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music3));
        //player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music4));
        //player.addMediaItem(MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.gym_music5));



        clientDataViewModel = new ViewModelProvider(this).get(ClientDataViewModel.class);
        dataClient = Wearable.getDataClient(this);
        messageClient = Wearable.getMessageClient(this);
        capabilityClient = Wearable.getCapabilityClient(this);

        voiceCmdsActive=Boolean.TRUE;
        exerciseBreak=Boolean.FALSE;

        currentUser.reload();

        Intent intent = getIntent();
        user_theme = intent.getStringExtra("selected_theme");

        //Layout
        singleUserLayout=(ConstraintLayout) findViewById(R.id.singleWorkoutLayout);

        //TextViews
        remainingTimeTxt=findViewById(R.id.remainingTimeTxt);
        timeTxt=findViewById(R.id.timeTxt);
        titleOfWorkout=findViewById(R.id.titleOfWorkout);

        //ImageViews
        workoutImageView=findViewById(R.id.workoutImageView);
        helpWorkout=findViewById(R.id.helpWorkout);
        pauseWorkout=findViewById(R.id.pauseWorkout);
        musicImageView=findViewById(R.id.music);
        voiceCommandsView=findViewById(R.id.voice);
        helpImageView=findViewById(R.id.helpWorkout);
        //Buttons
        pauseBtn=findViewById(R.id.pauseBtn);





        Glide.with(this).asGif().load(R.drawable.gif_plank_hip_dips).into(workoutImageView);

        startWearableActivity();
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pauseWearableActivity();

                stopWearableActivity();
                if (pauseBtn.getText().toString().equals("CONTINUE")){
                    startCountdownTimer(Long.parseLong(timeTxt.getText().toString())*1000);
                    player.play();
                    pauseBtn.setText("PAUSE");
                } else if (pauseBtn.getText().toString().equals("PAUSE")) {
                    textToSpeech.stop();
                    stopCountdownTimer();
                    player.pause();
                    pauseBtn.setText("CONTINUE");
                }else if(pauseBtn.getText().toString().equals("GO TO NEXT EXERCISE")){
                    exerciseBreak=Boolean.FALSE;
                    //next exercise handle

                }


            }
        });
        musicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Pause
                player.pause();
                stopCountdownTimer();


                File directory = Environment.getExternalStorageDirectory();

                //List<String> filesAudio=fetchAudioFiles(getApplicationContext(),);
                //List<File> directories = getAllDirectories(directory);
                File[] files = directory.listFiles();
                List<Audio> filesAudio= fetchAudioFiles();


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
                            turnOffCheckbox.setChecked(false);
                            selectBtn.setText("Select");
                            for (int i = 0; i < myMusicList.getChildCount(); i++) {
                                myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                            }

                        }else {
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
                            turnOnCheckbox.setChecked(true);
                            selectBtn.setText("Select");
                            for (int i = 0; i < myMusicList.getChildCount(); i++) {
                                myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
                if (turnOnCheckbox.isChecked()){
                    selectBtn.setText("Select");
                    for (int i = 0; i < myMusicList.getChildCount(); i++) {
                        myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                }else {
                    selectBtn.setText("Ok");
                    for (int i = 0; i < myMusicList.getChildCount(); i++) {
                        if (myMusicList.getChildAt(i).equals(turnOnCheckbox) || myMusicList.getChildAt(i).equals(turnOffCheckbox) || myMusicList.getChildAt(i).equals((TextView)v.findViewById(R.id.music_settings))){
                            myMusicList.getChildAt(i).setVisibility(View.VISIBLE);
                        }else {
                            myMusicList.getChildAt(i).setVisibility(View.GONE);
                        }

                    }
                }
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

                    myMusicList.addView(myMusicCheckBox);


                    //Handle myMusic checkbox events
                    myMusicCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                audioSelectedList.add(audio);

                            } else {
                                audioSelectedList.remove(audio);
                            }
                        }
                    });



                }
                // Check if audio file is in musicList and is checked
                for (int i = 0; i < myMusicList.getChildCount(); i++) {
                    if (myMusicList.getChildAt(i) instanceof CheckBox){
                        CheckBox checkBoxInMusicList = (CheckBox) myMusicList.getChildAt(i);
                        if (checkBoxInMusicList.isChecked()){
                            for (Audio audioFile : filesAudio){
                                if (audioFile.name.equals(checkBoxInMusicList.getText().toString())){
                                    audioSelectedList.add(audioFile);
                                }
                            }
                        }
                    }
                }
                selectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (saveChoicesCheckbox.isChecked()){
                            dialog.dismiss();
                        }else {
                            if (turnOffCheckbox.isChecked()){
                                player.clearMediaItems();
                                player.stop();
                                player.release();
                            }else {
                                player.clearMediaItems();
                                for(Audio audioSelected : audioSelectedList){
                                    player.addMediaItem(MediaItem.fromUri(audioSelected.uri));

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
                                            } else if (s.equals("2")) {
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
                //TODO
            }
        });



        textToSpeech = new TextToSpeech(SingleWorkout.this, this::onInit);
        player.prepare();
        player.play();
        player.setVolume(0.2F);

    }

    
    private void startCountdownTimer(long totalTimeInMillis) {


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

                if (timeTxt.getText().toString().equals("35")){
                    player.setVolume(0.15F);
                    speak("You should feel a burn in your abs, arms, shoulders and back. The burn is getting more intense but don't give up. Stay strong until the timer ends!","2");

                }
                if (String.format("%02d", secondsRemaining).equals("00") && !exerciseBreak && !pauseBtn.getText().toString().equals("GO TO NEXT EXERCISE")){
                    countDownTimer.cancel();
                    startCountdownTimer(20*1000);
                    remainingTimeTxt.setText("Minibreak remaining time");

                    pauseBtn.setText("GO TO NEXT EXERCISE");
                    exerciseBreak=Boolean.TRUE;
                }
            }

            @Override
            public void onFinish() {
                timeTxt.setText("00");
                stopCountdownTimer();
                pauseBtn.setText("FINITO");


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
                        } else if (s.equals("2")) {
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
                player.setVolume(0.15F);
                speak("First exercise, plank hip dips. Repeat for 45 seconds.","1");
                //player.setVolume(0.5F);




            }


        }else {
            Log.e(TAG, "Initialization failed");
        }


    }




    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
    private void speak(String text,String utteranceId){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }


    // Method to fetch all audio files, including MP3 files in the Downloads folder


    class Audio {
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



    public void goToNextExercise(){

    }

    @Override
    public void onBackPressed() {
        // Create an intent to start a new activity

        player.stop();
        textToSpeech.stop();
        textToSpeech.shutdown();

        Intent intent = new Intent(SingleWorkout.this, Workouts.class);
        intent.putExtra("selected_theme","light");
        startActivity(intent);
        // Finish the current activity
        finish();
        super.onBackPressed();
    }








}
