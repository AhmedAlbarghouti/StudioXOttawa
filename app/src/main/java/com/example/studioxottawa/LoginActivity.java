package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText usernameET;
    Button signButton;
    TextView lostPassword;
    TextView signupTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET = findViewById(R.id.usernameET);
        signButton = findViewById(R.id.signButton);
        lostPassword = findViewById(R.id.lostpassTV);
        signupTV = findViewById(R.id.signupTV);

        signButton.setOnClickListener(click-> {
            Intent main = new Intent(this,MainActivity.class);
            String username = usernameET.toString();
            main.putExtra("USER_NAME",username);
            startActivity(main);
        });

        lostPassword.setOnClickListener(click-> {
            Toast.makeText(getApplicationContext(),"An email was sent to your email address to set a new password",Toast.LENGTH_SHORT).show();
        });

        signupTV.setOnClickListener(click ->{
            Toast.makeText(getApplicationContext(),"Successfully Registered!",Toast.LENGTH_SHORT).show();
        });
    }
}