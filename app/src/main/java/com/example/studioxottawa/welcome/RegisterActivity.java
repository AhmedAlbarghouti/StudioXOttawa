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
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView signInTV;
    EditText fullNameET,phoneNumberET,emailET,passwordET;
    Button createNewAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fullNameET = findViewById(R.id.nameET);
        phoneNumberET = findViewById(R.id.phonenumberET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passET);
        signInTV = findViewById(R.id.signInTV);
        createNewAccount = findViewById(R.id.registerButton);
        signInTV.setOnClickListener(click ->{
            Intent signIn = new Intent(this,LoginActivity.class);
            startActivity(signIn);
        });
        
        createNewAccount.setOnClickListener(click ->{
            registerUser();
        });


    }

    private void registerUser() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String fullName = fullNameET.getText().toString().trim();
        String phoneNumber = phoneNumberET.getText().toString().trim();

        if(fullName.isEmpty()){
            fullNameET.setError("Full Name is required!");
            fullNameET.requestFocus();
            return;
        }

        if(phoneNumber.isEmpty()){
            phoneNumberET.setError("Phone Number is required!");
            phoneNumberET.requestFocus();
            return;
        }
        if(!Patterns.PHONE.matcher(phoneNumber).matches()){
            phoneNumberET.setError("Please provide a valid Phone Number!");
            phoneNumberET.requestFocus();
            return;
        }

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

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(fullName,email,phoneNumber);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "Account Created Successfully! check your email to verify your account!",Toast.LENGTH_LONG).show();
                                        finish();
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Failed To Create Account! Try again!",Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        }

                    }
                });


    }
}