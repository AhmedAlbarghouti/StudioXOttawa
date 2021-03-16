package com.example.studioxottawa.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.UUID;

public class Event implements Parcelable {
    private String name;
    private String date;
    private String time;
    private String staff;
    private String uid;

    //Default constructor
    public Event(){

    }



    public Event(String name, String date, String time, String staff) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.staff = staff;
        uid = String.valueOf(UUID.randomUUID());
    }

    public Event(String name, String date, String time, String staff, String uid) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.staff = staff;
        this.uid = uid;
    }

    protected Event(Parcel in) {
        name = in.readString();
        date = in.readString();
        time = in.readString();
        staff = in.readString();
        uid = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(staff);
        dest.writeString(uid);
    }
}
