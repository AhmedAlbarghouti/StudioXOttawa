package com.example.studioxottawa.schedule;

import java.util.Calendar;

public class Event{
    private String name;
    private Calendar date;
    private String time;
    private String staff;

    //Default constructor
    public Event(){

    }

    public Event(String name, Calendar date, String time, String staff) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.staff = staff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
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


}
