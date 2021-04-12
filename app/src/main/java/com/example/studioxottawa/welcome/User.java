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
     *
     * @param fullName user's full name
     * @param email user's email
     * @param phoneNumber user's phone number
     */
    public User(String fullName,  String email, String phoneNumber) {
        this(fullName, email, phoneNumber, null, null);
    }

    /**
     *
     * @param fullName user's full name
     * @param email user's email
     * @param phoneNumber user's phone number
     * @param eventsPurchased user's arrayList of booked events
     * @param productsPurchased user's arrayList of purchased products
     */
    public User(String fullName,  String email, String phoneNumber, ArrayList<Event> eventsPurchased, ArrayList<Product> productsPurchased){
        this.fullName = fullName;
        PhoneNumber = phoneNumber;
        this.email = email;
        this.staff = false;
        this.eventsPurchased.addAll(eventsPurchased);
        this.productsPurchased.addAll(productsPurchased);
    }

    /**
     *
     * @return arrayList of booked events
     */
    public ArrayList<Event> getEventsPurchased() {
        return eventsPurchased;
    }

    /**
     *
     * @param e newly purchased event by user
     */
    public void addEventsPurchased(Event e) {
        this.eventsPurchased.add(e);
    }

    /**
     *
     * @return arrayList of purchased products
     */
    public ArrayList<Product> getProductsPurchased() {
        return productsPurchased;
    }

    /**
     *
     * @param p newly purchased product by user
     */
    public void addProductsPurchased(Product p) {
        this.productsPurchased.add(p);
    }


}
