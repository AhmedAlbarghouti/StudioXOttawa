package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studioxottawa.R;
import com.example.studioxottawa.news.NewsFragment;
import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.Product;
import com.example.studioxottawa.staff.Report;
import com.example.studioxottawa.staff.ReportDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    EditText emailET,passwordET;
    Button signButton;
    TextView lostPassword;
    TextView signUpTV;
    FirebaseAuth mAuth;

    public static User currentUser;
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
        NewsFragment.loadNews();

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
                        Log.i("gycuid", user.getUid());
                        loadUserInfo(user.getUid());
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

    private void loadUserInfo(String uid ){

        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        referenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {

                    String fullName = String.valueOf(ds.child("fullName").getValue());
                    String phoneNumber = String.valueOf(ds.child("PhoneNumber").getValue());
                    String email = String.valueOf(ds.child("email").getValue());

                    DataSnapshot eventsPurchasedSnapshot = ds.child("Events Purchased");
                    ArrayList<Event> eventsPurchasedList= new ArrayList();
                    for(DataSnapshot dsEvent : eventsPurchasedSnapshot.getChildren()){
                        String name = String.valueOf(dsEvent.child("name").getValue());
                        String staff = String.valueOf(dsEvent.child("staff").getValue());
                        String time = String.valueOf(dsEvent.child("time").getValue());
                        String date = String.valueOf(dsEvent.child("date").getValue());
                        String uid = String.valueOf(dsEvent.child("uid").getValue());
                        Log.i("gycevent", name+staff+time+date+uid);
                        eventsPurchasedList.add(new Event(name, date, time, staff, uid));
                    }

                    DataSnapshot productPurchasedSnapshot = ds.child("Products Purchased");
                    ArrayList<Product> productsPurchasedList= new ArrayList();
                    for(DataSnapshot dsProduct : productPurchasedSnapshot.getChildren()){
                        String item = String.valueOf(dsProduct.child("item").getValue());
                        String pricelong = (dsProduct.child("price").getValue()).toString();
                        double price = Double.parseDouble(pricelong);
                        String quantitylong = (dsProduct.child("quantity").getValue()).toString();
                        int quantity = Integer.parseInt(quantitylong);

                        productsPurchasedList.add(new Product(item, price, quantity));
                    }

                    currentUser = new User(fullName, email, phoneNumber, eventsPurchasedList, productsPurchasedList);

                    Log.i("gycCurrentUser", currentUser.fullName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}