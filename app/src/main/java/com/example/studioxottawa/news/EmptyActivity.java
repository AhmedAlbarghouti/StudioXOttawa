package com.example.studioxottawa.news;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studioxottawa.R;

public class EmptyActivity extends AppCompatActivity {

    /**
     * @param savedInstanceState - the Bundle object that is passed into the onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = getIntent().getExtras();

        DetailsFragment dFragment = new DetailsFragment();
        dFragment.setArguments( dataToPass );
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, dFragment)
                .commit();
    }
}
