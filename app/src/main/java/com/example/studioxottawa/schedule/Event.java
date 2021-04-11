package com.example.studioxottawa.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.UUID;

/**
 * @Author Ahmed Albarghouti
 * @Date Feb 2021
 * @Purpose Simple Model class for events
 */
public class Event implements Parcelable {
    private String name;
    private String date;
    private String time;
    private String staff;
    private String uid;

    //Default constructor
    public Event(){

    }


    /**
     *
     * @param name
     * @param date
     * @param time
     * @param staff
     */
    public Event(String name, String date, String time, String staff) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.staff = staff;
        uid = String.valueOf(UUID.randomUUID());
    }

    /**
     *
     * @param name
     * @param date
     * @param time
     * @param staff
     * @param uid
     */
    public Event(String name, String date, String time, String staff, String uid) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.staff = staff;
        this.uid = uid;
    }

    /**
     *
     * @param in
     */
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

    /**
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     *
     * @return staff
     */
    public String getStaff() {
        return staff;
    }

    /**
     *
     * @param staff
     */
    public void setStaff(String staff) {
        this.staff = staff;
    }

    /**
     *
     * @return uid
     */
    public String getUid() {
        return uid;
    }

    /**
     *
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     *
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(staff);
        dest.writeString(uid);
    }
}
