package com.example.android.fitness;

public class Exercise {
    private String mTitle;
    private String mTime;
    private String mTargetMuscles;
    private String mImageUrl;

    public Exercise(String mTitle, String mTime, String mTargetMuscles, String mImageUrl) {
        this.mTitle = mTitle;
        this.mTime = mTime;
        this.mTargetMuscles = mTargetMuscles;
        this.mImageUrl = mImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getTargetMuscles() {
        return mTargetMuscles;
    }

    public void setTargetMuscles(String mTargetMuscles) {
        this.mTargetMuscles = mTargetMuscles;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
