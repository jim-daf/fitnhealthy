package com.example.fitnhealthy;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class WorkoutSample implements Parcelable {


    public WorkoutSample() {
        // Default constructor
    }

    protected WorkoutSample(Parcel in) {
        titleOfWorkout = in.readString();
        duration = in.readString();
        numOfExercises = in.readString();
    }

    public static final Creator<WorkoutSample> CREATOR = new Creator<WorkoutSample>() {
        @Override
        public WorkoutSample createFromParcel(Parcel in) {
            return new WorkoutSample(in);
        }

        @Override
        public WorkoutSample[] newArray(int size) {
            return new WorkoutSample[size];
        }
    };

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNumOfExercises() {
        return numOfExercises;
    }

    public void setNumOfExercises(String numOfExercises) {
        this.numOfExercises = numOfExercises;
    }

    public String getTitleOfWorkout() {
        return titleOfWorkout;
    }

    public void setTitleOfWorkout(String titleOfWorkout) {
        this.titleOfWorkout = titleOfWorkout;
    }

    private String titleOfWorkout;
    private String duration;
    private String numOfExercises;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(titleOfWorkout);
        dest.writeString(duration);
        dest.writeString(numOfExercises);
    }
}
