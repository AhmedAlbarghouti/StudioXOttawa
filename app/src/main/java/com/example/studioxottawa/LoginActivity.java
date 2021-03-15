package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {

    EditText usernameET;
    EditText passwordET;
    Button signButton;
    TextView lostPassword;
    TextView signupTV;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passET);
        signButton = findViewById(R.id.signButton);
        lostPassword = findViewById(R.id.lostpassTV);
        signupTV = findViewById(R.id.signupTV);


        signButton.setOnClickListener(click-> {
            // Validate username and password
            String username = usernameET.getText().toString();
            String password = passwordET.getText().toString();

            Thread thread = new LoginActivity.SelectThread(username, password, null, null, null, null, null);
            thread.start();
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(user == null){
                Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
            }else if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                Intent main = new Intent(this, MainActivity.class);
                main.putExtra("USER_NAME", username);
                startActivity(main);
                return;
            }


//            Intent main = new Intent(this,MainActivity.class);
//            String username = usernameET.getText().toString();
//            String password = passwordET.getText().toString();
//            main.putExtra("USER_NAME",username);
//            startActivity(main);
        });

        lostPassword.setOnClickListener(click-> {
            Toast.makeText(getApplicationContext(),"An email was sent to your email address to set a new password",Toast.LENGTH_SHORT).show();
        });

        signupTV.setOnClickListener(click ->{
            Toast.makeText(getApplicationContext(),"Successfully Registered!",Toast.LENGTH_SHORT).show();
        });


//        Button db = (Button)findViewById(R.id.db_Button);
//        db.setOnClickListener(click -> {
//            Log.i("gyc", "start");
//            new Thread(new Runnable(){
//                public void run(){
//                    DBConnection.selectMysql();
//                }
//            }).start();
//        });
    }

    /**
     * inner class that deals with thread synchronization
     */
    private class SelectThread extends Thread{
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;
        private String permission;

        public SelectThread(String username, String password, String firstName, String lastName, String phoneNumber, String email, String permission){
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.permission = permission;

        }

        public void run(){
            user = DBConnection.selectMysql(this.username, this.password);
        }
    }
}
