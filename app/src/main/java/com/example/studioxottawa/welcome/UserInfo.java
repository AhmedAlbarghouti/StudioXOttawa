package com.example.studioxottawa.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studioxottawa.R;
import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.Product;

import java.util.ArrayList;

public class UserInfo  extends AppCompatActivity {

    private ArrayList<Event> eventsPurchasedList;
    private ArrayList<Product> productsPurchasedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        String fullName = LoginActivity.currentUser.fullName;
        String email = LoginActivity.currentUser.email;
        String phone = LoginActivity.currentUser.PhoneNumber;
        eventsPurchasedList = LoginActivity.currentUser.getEventsPurchased();
        String eventDetail = "";
        int counter = 1;
        for(Event event : eventsPurchasedList){
            eventDetail = eventDetail+counter+". "+event.getName()+" by "+event.getStaff()+", at "+event.getTime()+", "+event.getDate()+"\n\n";
            counter+=1;
        }
        productsPurchasedList = LoginActivity.currentUser.getProductsPurchased();
        String productDetail = "";
        int counter1 = 1;
        for(Product product : productsPurchasedList){
            productDetail = productDetail+counter1+". "+product.getItem()+" x "+product.getQuantity()+", unit price: $ "+product.getPrice()+"\n\n";
            counter1+=1;
        }

        TextView tViewName = (TextView)findViewById(R.id.fullName2);
        tViewName.setText("Name:\n"+fullName+"\n");
        TextView tViewEmail = findViewById(R.id.email2);
        tViewEmail.setText("Email:\n"+email+"\n");
        TextView tViewPhone = findViewById(R.id.phoneNumber2);
        tViewPhone.setText("Phone:\n"+phone+"\n");
        TextView tViewEvents = findViewById(R.id.event2);
        tViewEvents.setText("Event purchased:\n"+eventDetail);
        TextView tViewProduct = findViewById(R.id.product2);
        tViewProduct.setText("Product purchased:\n"+productDetail);
    }






}
