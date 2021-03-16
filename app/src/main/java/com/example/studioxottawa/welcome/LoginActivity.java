package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studioxottawa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailET,passwordET;
    Button signButton;
    TextView lostPassword;
    TextView signUpTV;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordET = findViewById(R.id.passET);
        emailET = findViewById(R.id.usernameET);
        signButton = findViewById(R.id.signButton);
        lostPassword = findViewById(R.id.lostpassTV);
        signUpTV = findViewById(R.id.signupTV);
        mAuth = FirebaseAuth.getInstance();

        signButton.setOnClickListener(click-> {
            userLogin();
//            Intent main = new Intent(this,MainActivity.class);
//            String username = emailET.getText().toString();
//            main.putExtra("USER_NAME",username);
//            startActivity(main);
        });

        lostPassword.setOnClickListener(click-> {
            startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
        });

        signUpTV.setOnClickListener(click ->{
            Intent register = new Intent(this,RegisterActivity.class);
            startActivity(register);
        });
    }

    private void userLogin() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        if(email.isEmpty()){
            emailET.setError("Email is required!");
            emailET.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Please provide valid a email!");
            emailET.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordET.setError("Password is required!");
            passwordET.requestFocus();
            return;
        }

        if(password.length() < 6){
            passwordET.setError("Password length should be at least 6 characters!");
            passwordET.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Your Email is unverified! check your email to verify your account",Toast.LENGTH_LONG).show();

                    }

                }else{
                    Toast.makeText(LoginActivity.this, "Your username/password was incorrect!",Toast.LENGTH_LONG).show();

                }
            }
        });
    }


}