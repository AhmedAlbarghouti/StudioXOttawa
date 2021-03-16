package com.example.studioxottawa.welcome;

public class User {

    public String fullName, PhoneNumber, email;
    public Boolean staff;

    public User(){

    }

    public User(String fullName,  String email,String phoneNumber) {
        this.fullName = fullName;
        PhoneNumber = phoneNumber;
        this.email = email;
        this.staff = false;
    }


}
