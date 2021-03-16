package com.example.studioxottawa.welcome;

import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.Product;

import java.util.ArrayList;

public class User {

    public String fullName, PhoneNumber, email;
    public Boolean staff;
    private ArrayList<Event> eventsPurchased = new ArrayList<>();
    private ArrayList<Product> productsPurchased = new ArrayList<>();


    public User(){

    }

    public User(String fullName,  String email,String phoneNumber) {
        this.fullName = fullName;
        PhoneNumber = phoneNumber;
        this.email = email;
        this.staff = false;
    }

    public ArrayList<Event> getEventsPurchased() {
        return eventsPurchased;
    }

    public void addEventsPurchased(Event e) {
        this.eventsPurchased.add(e);
    }

    public ArrayList<Product> getProductsPurchased() {
        return productsPurchased;
    }

    public void addProductsPurchased(Product p) {
        this.productsPurchased.add(p);
    }


}
