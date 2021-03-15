package com.example.studioxottawa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameET;
    EditText passwordET;
    EditText firstNameET;
    EditText lastNameET;
    EditText phoneET;
    EditText emailET;
    Button registerButton;
    Button cancelButton;
    Boolean registerFlag = false;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passET);
        firstNameET = findViewById(R.id.firstNameET);
        lastNameET = findViewById(R.id.lastNameET);
        phoneET = findViewById(R.id.phoneET);
        emailET = findViewById(R.id.emailET);
        registerButton = findViewById(R.id.registerButton);
        cancelButton = findViewById(R.id.cancelButton);

        registerButton.setOnClickListener(click-> {
            // Validate username and password
            String username = usernameET.getText().toString();
            String password = passwordET.getText().toString();
            String firstName = firstNameET.getText().toString();
            String lastName = lastNameET.getText().toString();
            String phone = phoneET.getText().toString();
            String email = emailET.getText().toString();

            // Check if username already exist
            Thread thread = new RegisterActivity.CheckThread(username, null, null, null, null, null, null);
            thread.start();
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(user == null){
                // if username doesn't exist, then register
                Thread thread1 = new RegisterActivity.InsertThread(username, password, firstName, lastName, phone, email, 0);
                thread1.start();
                try {
                    thread1.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(registerFlag){
                    Toast.makeText(getApplicationContext(),"Successfully Registered!",Toast.LENGTH_LONG).show();
                    Intent goBack = new Intent(this, LoginActivity.class);
                    startActivity(goBack);
                }else{
                    Toast.makeText(getApplicationContext(),"Registration failed, please try again later. Thanks!",Toast.LENGTH_LONG).show();
                    Intent goBack = new Intent(this, LoginActivity.class);
                    startActivity(goBack);
                }

            }else{
                Toast.makeText(getApplicationContext(),"User already exists!",Toast.LENGTH_LONG).show();
                Intent goBack = new Intent(this, LoginActivity.class);
                startActivity(goBack);
            }
        });

        cancelButton.setOnClickListener(click ->{
            Intent goBack = new Intent(this, LoginActivity.class);
            startActivity(goBack);
        });
    }

    /**
     * inner class that deals with thread synchronization
     */
    private class CheckThread extends Thread{
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;
        private String permission;

        public CheckThread(String username, String password, String firstName, String lastName, String phoneNumber, String email, String permission){
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.permission = permission;
        }
        public void run(){
            user = DBConnection.checkMysql(this.username);
        }
    }

    /**
     * inner class that deals with thread synchronization
     */
    private class InsertThread extends Thread{
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;
        private int permission;

        public InsertThread(String username, String password, String firstName, String lastName, String phoneNumber, String email, int permission){
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.permission = permission;
        }
        public void run(){
            registerFlag = DBConnection.insertMysql(this.username, this.password, this.firstName, this.lastName, this.phoneNumber, this.email, this.permission);
        }
    }

}
