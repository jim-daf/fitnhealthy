package com.example.fitnhealthy;

import static android.opengl.ETC1.getHeight;
import static android.opengl.ETC1.getWidth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.ref.Reference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CurrentWorkoutStats extends AppCompatActivity {


    LineChart chart;
    String user_theme,workoutTimeText,gender;
    String username,experience,target;
    private long age;
    private float weight,height;
    ArrayList<Integer> graph_y_values;
    List<Entry> entries;

    ArrayList<String> audioList,datesList,workoutTimesList;
    ArrayList<Integer> caloriesList;
    float[] avgHeartRatesList;
    LineDataSet dataSet;
    TextView avgHeartRate,caloriesBurned,workoutTime,maxVo2TxtView;

    private long totalMins;
    private float caloriesBurnedValue;

    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference reference;
    AppCompatButton saveBtn;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));

        setContentView(R.layout.activity_current_workout_stats);

        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("/Users").child(user.getUid());



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
        graph_y_values=intent.getIntegerArrayListExtra("heartRateValues");
        workoutTimeText=intent.getStringExtra("workoutTime");
        totalMins=intent.getLongExtra("totalMinutes",0);
        datesList=intent.getStringArrayListExtra("datesList");
        caloriesList=intent.getIntegerArrayListExtra("caloriesList");
        workoutTimesList=intent.getStringArrayListExtra("workoutTimesList");
        avgHeartRatesList=new float[datesList.size()];
        avgHeartRatesList=intent.getFloatArrayExtra("avgHeartRatesList");

        chart = (LineChart) findViewById(R.id.chart);
        avgHeartRate=(TextView) findViewById(R.id.avgHeartRateValue);
        caloriesBurned=(TextView) findViewById(R.id.caloriesBurnedValue);
        workoutTime=(TextView) findViewById(R.id.workoutTimeValue);
        maxVo2TxtView=(TextView) findViewById(R.id.maxVo2Value);


        workoutTime.setText(workoutTimeText);
        // Create a new HashMap
        HashMap<String, Float> METS = new HashMap<>();

        // Add key-value pairs to the HashMap
        METS.put("Walking", 4.0F);
        METS.put("Cycling", 7.0F);
        METS.put("Running", 7.0F);
        METS.put("Tennis", 8.0F);
        METS.put("Planks", 8.0F);
        METS.put("Pushups", 8.5F);

        //Create dataset for the graphical representation of heart rates
        entries = new ArrayList<Entry>();
        if (!graph_y_values.isEmpty()){
            entries.add(new Entry(0f,0f));
        }

        float total=0f;
        for (int i = 1; i < graph_y_values.size(); i++) {

            Entry dataPoint=new Entry((float) (i),(float) graph_y_values.get(i));
            entries.add(dataPoint);
            total+=graph_y_values.get(i);

        }
        // Calculate average heart rate
        float avg=total/graph_y_values.size();
        // Set value to Textview
        avgHeartRate.setText(String.valueOf(avg)+" bpm");

        // if rockport has not been done find Vo2MAX
        float MHR = (float) (208-(0.7*age));
        int restingHeartRate=70;
        float maxVo2=(float) (15.3*(MHR/(float) restingHeartRate));
        // ELSE GET DATABASE VALUE FOR VO2 MAX
        if (graph_y_values.isEmpty()){
            caloriesBurnedValue= (float) ((METS.get("Running")*3.5*weight)/200);
            int caloriesBurnedIntegerValue=(int) caloriesBurnedValue;
            caloriesBurned.setText(String.valueOf(caloriesBurnedIntegerValue) + " kcal");
            maxVo2TxtView.setText(String.valueOf(maxVo2)+ "ml/kg/min");
        }else {
            // if rockport has not been done
            restingHeartRate=graph_y_values.get(0);
            maxVo2=(float) (15.3*(MHR/(float) restingHeartRate));
            maxVo2TxtView.setText(String.valueOf(maxVo2)+ "ml/kg/min");
            if (gender.equals("Male")){
                caloriesBurnedValue= (float) (totalMins*((0.6309*avg + 0.1988*weight + 0.2017*age -55.0969)/4.184));
                int caloriesBurnedIntegerValue=(int) caloriesBurnedValue;
                caloriesBurned.setText(String.valueOf(caloriesBurnedIntegerValue) + " kcal");

            } else if (gender.equals("Female")) {

                caloriesBurnedValue= (float) (totalMins*((0.4472*avg - 0.1263*weight + 0.074*age -20.4022)/4.184));
                int caloriesBurnedIntegerValue=(int) caloriesBurnedValue;
                caloriesBurned.setText(String.valueOf(caloriesBurnedIntegerValue));

            }
        }



        /*
        if (man){
            caloriesBurned = (totalMins*(0.6309*avgHeartRate + 0.1988*weight + 0.2017*age -55.0969))/4.184
        }else if (woman){
            caloriesBurned = (totalMins*(0.4472×avgHeartRate - 0.1263×weight + 0.074×age - 20.4022))/4.184
        }

         */


        dataSet = new LineDataSet(entries,"Heart rate values");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.setDescription(null);
        chart.setHovered(true);
        chart.setNoDataText("No data to present");
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });




        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView textView = findViewById(R.id.text_view);
        saveBtn=findViewById(R.id.saveStatsBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save to database
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("/Users").child(user.getUid()).child("Sessions");
                DatabaseReference newSessionRef = databaseReference.push();
                newSessionRef.setValue(caloriesBurnedValue);
                DatabaseReference caloriesBurnedRef = newSessionRef.child("Calories");
                caloriesBurnedRef.setValue((int) caloriesBurnedValue);
                DatabaseReference workoutTimeRef = newSessionRef.child("WorkoutTime");
                workoutTimeRef.setValue(workoutTimeText);
                DatabaseReference avgHeartRateRef = newSessionRef.child("AverageHeartRate");
                avgHeartRateRef.setValue(avg);
                DatabaseReference date = newSessionRef.child("Date");
                date.setValue(LocalDate.now().toString());
                Intent intent = new Intent(CurrentWorkoutStats.this, Workouts.class);
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

        /*
        liveChart.setDataset(dataset).drawYBounds().drawBaseline().drawFill(true).drawBaselineConditionalColor()
                .drawLastPointLabel().drawVerticalGuidelines(4)
                .drawHorizontalGuidelines(4).drawTouchOverlayAlways().drawStraightPath()
                .setOnTouchCallbackListener(new LiveChart.OnTouchCallback() {
                    @Override
                    public void onTouchCallback(@NonNull DataPoint dataPoint) {
                        textView.setText(String.format("(%.0f, %.0f)", dataPoint.getX(), dataPoint.getY()));
                    }

                    @Override
                    public void onTouchFinished() {

                    }
                })
                .drawDataset();
*/
    }





}
