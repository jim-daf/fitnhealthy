package com.example.fitnhealthy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WorkoutStats extends AppCompatActivity {


    LineChart chart;
    String user_theme,workoutTimeText,gender;
    String username,experience,target;
    private long age;
    private float weight,height;
    ArrayList<Integer> graph_y_values;
    List<Entry> entries;

    ArrayList<String> audioList;
    LineDataSet dataSet,dateDataset;
    TextView avgHeartRateTxtView,caloriesBurnedTxtView,workoutTimeTxtView;

    private long totalMins;
    private float caloriesBurnedValue;
    ArrayList<Integer> averageHeartRates,calories;
    ArrayList<String> workoutTime;
    ArrayList<LocalDate> datesList,dynamicDatesList;
    ArrayList<String> dateStrings,workoutTimesList,dynamicWorkoutTimesList,dynamicDateStrings;
    ArrayList<Integer> caloriesList,dynamicCaloriesList;
    ArrayList<Float> dynamicAvgHeartRatesArrayList;
    float[] avgHeartRatesList;
    String spinnerValue;


    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference reference;
    AppCompatButton returnHomeBtn;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));

        setContentView(R.layout.activity_workout_stats);

        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("/Users").child(user.getUid());

        datesList=new ArrayList<>();
        caloriesList=new ArrayList<>();
        workoutTimesList=new ArrayList<>();
        dynamicDateStrings=new ArrayList<>();
        dynamicWorkoutTimesList=new ArrayList<>();
        dynamicCaloriesList=new ArrayList<>();
        dynamicAvgHeartRatesArrayList=new ArrayList<>();
        dynamicDatesList=new ArrayList<>();


        Intent intent = getIntent();
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
        dateStrings=intent.getStringArrayListExtra("datesList");
        caloriesList=intent.getIntegerArrayListExtra("caloriesList");
        workoutTimesList=intent.getStringArrayListExtra("workoutTimesList");
        avgHeartRatesList=new float[datesList.size()];
        avgHeartRatesList=intent.getFloatArrayExtra("avgHeartRatesList");
        /*graph_y_values=intent.getIntegerArrayListExtra("heartRateValues");
        workoutTimeText=intent.getStringExtra("workoutTime");
        totalMins=intent.getLongExtra("totalMinutes",0);

         */

        // Spinner setup
        Spinner spinner = (Spinner) findViewById(R.id.spinner_time);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stats, R.layout.spinner_selected_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_settings);
        spinner.setAdapter(adapter);

        //Listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner.getBackground().setColorFilter(Color.parseColor("#B5000000"), PorterDuff.Mode.SRC_ATOP);
        // First value
        if (spinner.getSelectedItem().equals("Last month")){
            for (int i = 0; i < dateStrings.size(); i++) {

                LocalDate workoutDate = LocalDate.parse(dateStrings.get(i));
                LocalDate currentDate = LocalDate.now();
                Period period = Period.between(workoutDate,currentDate);
                if (period.getYears()<1 && period.getMonths()<=1 && period.getDays()>=0){
                    dynamicAvgHeartRatesArrayList.add(avgHeartRatesList[i]);
                    dynamicCaloriesList.add(caloriesList.get(i));
                    dynamicWorkoutTimesList.add(workoutTimesList.get(i));
                    dynamicDateStrings.add(dateStrings.get(i));
                }
            }

        } else if (spinner.getSelectedItem().equals("Last 2 months")) {
            for (int i = 0; i < dateStrings.size(); i++) {
                LocalDate workoutDate = LocalDate.parse(dateStrings.get(i));
                LocalDate currentDate = LocalDate.now();
                Period period = Period.between(workoutDate,currentDate);
                if (period.getYears()<1 && period.getMonths()<=2 && period.getDays()>=0){
                    dynamicAvgHeartRatesArrayList.add(avgHeartRatesList[i]);
                    dynamicCaloriesList.add(caloriesList.get(i));
                    dynamicWorkoutTimesList.add(workoutTimesList.get(i));
                    dynamicDateStrings.add(dateStrings.get(i));
                }
            }
        } else if (spinner.getSelectedItem().equals("Last week")) {

        }

        // 1. Put avg heart rates of last {spinner value} to chart
        /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/Users").child(user.getUid()).child("Sessions");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    for (DataSnapshot subChild : child.getChildren()) {
                        if (subChild.getKey().equals("Date")){
                            LocalDate currentDate = LocalDate.now();
                            String workoutDate = (String) subChild.getValue();
                            LocalDate workoutLocalDate = LocalDate.parse(workoutDate);
                            period = Period.between(workoutLocalDate,currentDate);
                            datesList.add(workoutLocalDate);
                        }
                        if (spinner.getSelectedItem().equals("Last year")){

                            if (period.getYears()<2 && period.getYears()>=1){
                                if (subChild.getKey().equals("AverageHeartRate")){
                                    averageHeartRates.add((int) subChild.getValue());
                                } else if (subChild.getKey().equals("Calories")) {
                                    calories.add((int) subChild.getValue());
                                } else if (subChild.getKey().equals("WorkoutTime")) {
                                    workoutTime.add((String) subChild.getValue());
                                }
                            }

                        } else if (spinner.getSelectedItem().equals("Last 2 months")){

                            if (period.getMonths()<=2 && period.getYears()<1){
                                if (subChild.getKey().equals("AverageHeartRate")){
                                    averageHeartRates.add((int) subChild.getValue());
                                } else if (subChild.getKey().equals("Calories")) {
                                    calories.add((int) subChild.getValue());
                                } else if (subChild.getKey().equals("WorkoutTime")) {
                                    workoutTime.add((String) subChild.getValue());
                                }
                            }

                        } else if (spinner.getSelectedItem().equals("All time")){

                            if (subChild.getKey().equals("AverageHeartRate")){
                                averageHeartRates.add((int) subChild.getValue());
                            } else if (subChild.getKey().equals("Calories")) {
                                calories.add((int) subChild.getValue());
                            } else if (subChild.getKey().equals("WorkoutTime")) {
                                workoutTime.add((String) subChild.getValue());
                            }


                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WorkoutStats.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

         */
        // 2. Put dates to chart of last {spinner value} that correspond to the heart rates (get them from database)

        // 1. Put calories of last {spinner value}
        // 2. Put workout time of last {spinner value}
        // 3. Calculate and put number of workouts of last {spinner value} (will be the size of array of avg heart rate values)
        // 4. Calculate and put avg heart rate value of all avgs heart rate values
        // 5. Calculate and put improval since last {spinner value}(f.e. month) based on heart rate

        // for loop to database to get avg heart rate values+calories+workout time according to spinner value
        // Listener to spinner changes




        chart = (LineChart) findViewById(R.id.chart);
        avgHeartRateTxtView=(TextView) findViewById(R.id.avgHeartRateVal);
        caloriesBurnedTxtView=(TextView) findViewById(R.id.caloriesBurnedVal);
        workoutTimeTxtView=(TextView) findViewById(R.id.workoutTimeVal);


        workoutTimeTxtView.setText(workoutTimeText);


        entries = new ArrayList<Entry>();



        float total=0f;


        //Create dataset
        for (int i = 0; i < dynamicAvgHeartRatesArrayList.size(); i++) {

            Entry dataPoint=new Entry((float) i,(float) dynamicAvgHeartRatesArrayList.get(i));
            entries.add(dataPoint);
            total+=dynamicAvgHeartRatesArrayList.get(i);

        }
        float avg=total/dynamicAvgHeartRatesArrayList.size();
        avgHeartRateTxtView.setText(String.valueOf(avg)+" bpm");

        // Calories
        int totalCalories=0;
        for (int i = 0; i < dynamicCaloriesList.size(); i++) {
            totalCalories+=dynamicCaloriesList.get(i);
        }
        caloriesBurnedTxtView.setText(String.valueOf(totalCalories));
        /*
        //Workout Time

        for (int i = 0; i < workoutTimesList.size(); i++) {


        }

         */
        //workoutTimeTxtView.setText();

        ArrayList<String> xAxisLabels = new ArrayList<>();
        xAxisLabels.addAll(dynamicDateStrings);

        dataSet = new LineDataSet(entries,"Heart rate values");
        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setGranularity(1f);
        xAxis.setLabelCount(xAxisLabels.size(), true);


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

        returnHomeBtn=findViewById(R.id.returnHomeBtn);
        returnHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkoutStats.this, Home.class);
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





}

