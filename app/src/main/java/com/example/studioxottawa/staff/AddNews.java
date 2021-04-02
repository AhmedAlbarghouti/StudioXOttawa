package com.example.studioxottawa.staff;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studioxottawa.R;
import com.example.studioxottawa.news.News;
import com.example.studioxottawa.news.NewsActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class AddNews extends AppCompatActivity{

    private EditText title, description, imageURL;
    private Button createNews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        title = findViewById(R.id.add_news_title);
        description = findViewById(R.id.news_description);
        imageURL = findViewById(R.id.add_image_url);
        createNews = findViewById(R.id.add_news_button);

        createNews.setOnClickListener(click -> {
            createNews();
            Toast.makeText(AddNews.this, "News Created Successfully!",Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void createNews() {
        String newsTitle = title.getText().toString().trim();
        String newsDescription = description.getText().toString().trim();
        String newsImageURL = imageURL.getText().toString().trim();
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        Date curDate = new Date(System.currentTimeMillis());
        String createDate = formatter.format(curDate);

        DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("News");
        long id = NewsActivity.maxNewsID + 1;
        NewsActivity.maxNewsID = id;
        News addNews = new News(newsTitle, newsDescription, newsImageURL, createDate, id);
        eventsReference.child(String.valueOf(UUID.randomUUID())).setValue(addNews);

    }


}