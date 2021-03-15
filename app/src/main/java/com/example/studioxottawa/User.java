package com.example.studioxottawa;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String permission;

    public User(String username, String password, String firstName, String lastName, String phoneNumber, String email, String permission){
        this.setUsername(username);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setPhoneNumber(phoneNumber);
        this.setEmail(email);
        this.setPermission(permission);
    }


    public void setUsername(String s){this.username = s;}

    public String getUsername(){return this.username;}

    public void setPassword(String s){this.password = s;}

    public String getPassword(){return this.password;}

    public void setFirstName(String s){this.firstName = s;}

    public String getFirstName(){return this.firstName;}

    public void setLastName(String s){this.lastName = s;}

    public String getLastName(){return this.lastName;}

    public void setPhoneNumber(String s){this.phoneNumber = s;}

    public String getPhoneNumber(){return this.phoneNumber;}

    public void setEmail(String s){this.email = s;}

    public String getEmail(){return this.email;}

    public void setPermission(String s){this.permission = s;}

    public String getPermission(){return this.permission;}

}
