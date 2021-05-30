package com.example.android.quakereport;

public class Earthquake {

    private double mMagnitude;
    private String mLocation;
    private long mTimeInMilliSecond;
    private String mUrl;

    public Earthquake(double magnitude, String location, long timeInMilliSecond, String url){
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliSecond = timeInMilliSecond;
        mUrl = url;
    }

    public double getMagnitude() {return mMagnitude;}

    public String getLocation() {return mLocation;}

    public long getTimeInMilliSecond() {return mTimeInMilliSecond;}

    public String getUrl() { return mUrl; }

}
