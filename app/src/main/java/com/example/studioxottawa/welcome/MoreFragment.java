package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.studioxottawa.R;
import com.example.studioxottawa.aboutus.AboutusActivity;
import com.example.studioxottawa.contact.ContactUs;
import com.example.studioxottawa.staff.Report;
import com.example.studioxottawa.staff.StaffMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * @Author Ahmed Albarghouti
 * @Purpose Fragment that contains less important options that are needed
 * @Date April 2021
 */

public class MoreFragment extends Fragment {

    private Button adminBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_more_fragment,container,false);
        // init elements
        Button accountInfoBtn = root.findViewById(R.id.account_info_button);
        Button eventsBtn = root.findViewById(R.id.events_booked_button);
        Button productsBtn = root.findViewById(R.id.products_purchased_button);
        Button contactBtn = root.findViewById(R.id.contact_us_button);
        Button aboutBtn = root.findViewById(R.id.about_us_button);
        Button signOutBtn = root.findViewById(R.id.logout_button);
        adminBtn = root.findViewById(R.id.admin_portal_button);
        Button websiteBtn = root.findViewById(R.id.website_Button);

        EventsBooked obj = new EventsBooked();
        obj.loadBookedEvents(); //starts loading booked events into app

        ProductsPurchased pobj = new ProductsPurchased();
        pobj.loadPurchasedProducts(); //starts loading purchased products into app


        // gets current logged in user's information and checks if they are a staff member
        // Yes = reveal Admin Portal
        // No = keep invisible
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        String userID = user.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User signInUser = snapshot.getValue(User.class);
                Boolean isStaff = signInUser.staff;
                if (isStaff) {
                    adminBtn.setVisibility(View.VISIBLE);
                    Report.loadUsers();
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

        websiteBtn.setOnClickListener(click -> {
            Intent website = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.studioxottawa.com/"));
                    startActivity(website);
        });


        return root;
    }

    /**
     * @Purpose method is called if related button is clicked
     *          starts purchased Products activity
     */
    private void goToProductsPurchased() {
        Intent products = new Intent(getActivity(), ProductsPurchased.class);
        startActivity(products);
    }
    /**
     * @Purpose method is called if related button is clicked
     *          starts booked events activity
     */
    private void goToEventsBooked() {
        Intent events = new Intent(getActivity(), EventsBooked.class);
        startActivity(events);
    }
    /**
     * @Purpose method is called if related button is clicked
     *          starts account details activity
     */
    private void goToAccountInfo() {
        Intent account = new Intent(getActivity(), ViewAccountDetails.class);
        startActivity(account);
    }
    /**
     * @Purpose method is called if related button is clicked
     *          starts staff menu activity
     */
    private void goToAdminPortal() {
        Intent admin = new Intent(getActivity(), StaffMenu.class);
        startActivity(admin);
    }
    /**
     * @Purpose method is called if related button is clicked
     *          signs current user out
     *          starts login activity
     */
    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        LoginActivity.currentUser = null;
        startActivity(new Intent(getActivity(),LoginActivity.class));
    }
    /**
     * @Purpose method is called if related button is clicked
     *          starts Contact Us activity
     */
    private void goToContactUs() {
        Intent contactus = new Intent(getActivity(), ContactUs.class);
        startActivity(contactus);
    }
    /**
     * @Purpose method is called if related button is clicked
     *          starts About Us activity
     */
    private void goToAboutUs() {
        Intent aboutus = new Intent(getActivity(), AboutusActivity.class);
        startActivity(aboutus);
    }
}