package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.studioxottawa.R;
import com.example.studioxottawa.aboutus.AboutusActivity;

public class AccountFragment extends Fragment {

    private Button accountInfoBtn,eventsBtn,productsBtn,contactBtn,aboutBtn,signOutBtn,adminBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_account_fragment,container,false);

        accountInfoBtn = root.findViewById(R.id.account_info_button);
        eventsBtn = root.findViewById(R.id.events_booked_button);
        productsBtn = root.findViewById(R.id.products_purchased_button);
        contactBtn = root.findViewById(R.id.contact_us_button);
        aboutBtn = root.findViewById(R.id.about_us_button);
        signOutBtn = root.findViewById(R.id.logout_button);
        adminBtn = root.findViewById(R.id.admin_portal_button);

        aboutBtn.setOnClickListener(click -> {
            goToAboutUs();
        });


        return root;
    }

    private void goToAboutUs() {
        Intent aboutus = new Intent(getActivity(), AboutusActivity.class);
        startActivity(aboutus);
    }
}