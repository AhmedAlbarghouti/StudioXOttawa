package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.studioxottawa.R;
import com.example.studioxottawa.aboutus.AboutusActivity;
import com.example.studioxottawa.contact.ContactUs;
import com.example.studioxottawa.staff.StaffMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MoreFragment extends Fragment {

    private Button accountInfoBtn,eventsBtn,productsBtn,contactBtn,aboutBtn,signOutBtn,adminBtn;
    private FirebaseUser user;
    private DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_more_fragment,container,false);

        accountInfoBtn = root.findViewById(R.id.account_info_button);
        eventsBtn = root.findViewById(R.id.events_booked_button);
        productsBtn = root.findViewById(R.id.products_purchased_button);
        contactBtn = root.findViewById(R.id.contact_us_button);
        aboutBtn = root.findViewById(R.id.about_us_button);
        signOutBtn = root.findViewById(R.id.logout_button);
        adminBtn = root.findViewById(R.id.admin_portal_button);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        String userID = user.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User signInUser = snapshot.getValue(User.class);
                Boolean isStaff = signInUser.staff;
                if (isStaff) {
                    adminBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        accountInfoBtn.setOnClickListener(click -> {
            goToAccountInfo();
        });

        eventsBtn.setOnClickListener(click -> {
            goToEventsBooked();
        });

        productsBtn.setOnClickListener(click ->{
            goToProductsPurchased();
        });

        aboutBtn.setOnClickListener(click -> {
            goToAboutUs();
        });

        contactBtn.setOnClickListener(click -> {
            goToContactUs();
        });
        
        signOutBtn.setOnClickListener(click ->{
            signOutUser();
        });

        adminBtn.setOnClickListener(click ->{
            goToAdminPortal();
        });


        return root;
    }

    private void goToProductsPurchased() {
        Intent products = new Intent(getActivity(), EventsBooked.class);
        startActivity(products);
    }

    private void goToEventsBooked() {
        Intent events = new Intent(getActivity(), EventsBooked.class);
        startActivity(events);
    }

    private void goToAccountInfo() {
        Intent account = new Intent(getActivity(), ViewAccountDetails.class);
        startActivity(account);
    }

    private void goToAdminPortal() {
        Intent admin = new Intent(getActivity(), StaffMenu.class);
        startActivity(admin);
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(),LoginActivity.class));
    }

    private void goToContactUs() {
        Intent contactus = new Intent(getActivity(), ContactUs.class);
        startActivity(contactus);
    }

    private void goToAboutUs() {
        Intent aboutus = new Intent(getActivity(), AboutusActivity.class);
        startActivity(aboutus);
    }
}