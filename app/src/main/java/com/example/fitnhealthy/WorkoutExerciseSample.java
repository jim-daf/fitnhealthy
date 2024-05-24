package com.example.fitnhealthy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class WorkoutExerciseSample implements Parcelable {
    private String title;
    private String duration;


    public WorkoutExerciseSample() {
        // Default constructor
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }



    public static final Creator<WorkoutExerciseSample> CREATOR = new Creator<WorkoutExerciseSample>() {
        @Override
        public WorkoutExerciseSample createFromParcel(Parcel in) {
            return new WorkoutExerciseSample(in);
        }

        @Override
        public WorkoutExerciseSample[] newArray(int size) {
            return new WorkoutExerciseSample[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // Parcelable implementation
    protected WorkoutExerciseSample(Parcel in) {
        title = in.readString();
        duration = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(duration);

    }
}