package com.example.studioxottawa.welcome;

import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.Product;

import java.util.ArrayList;

public class User {

    public String fullName, PhoneNumber, email;
    public Boolean staff;
    private ArrayList<Event> eventsPurchased = new ArrayList<>();
    private ArrayList<Product> productsPurchased = new ArrayList<>();

    /**
     * Default constructor
     */
    public User(){

    }

    /**
     * Chained constructor
     */
    public User(String fullName,  String email, String phoneNumber) {
        this(fullName, email, phoneNumber, null, null);
    }

    public User(String fullName,  String email, String phoneNumber, ArrayList<Event> eventsPurchased, ArrayList<Product> productsPurchased){
        this.fullName = fullName;
        PhoneNumber = phoneNumber;
        this.email = email;
        this.staff = false;
        this.eventsPurchased.addAll(eventsPurchased);
        this.productsPurchased.addAll(productsPurchased);
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
