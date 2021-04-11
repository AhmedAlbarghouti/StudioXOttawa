package com.example.studioxottawa.staff;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studioxottawa.R;
import com.example.studioxottawa.news.News;

import com.example.studioxottawa.news.NewsFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class AddNews extends AppCompatActivity{

    private EditText title, description;
    private Button addImageBtn, createNews;
    private ImageView image;
    private Bitmap imageBitmap;
    private static final int GO_TO_GALLERY=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        title = findViewById(R.id.add_news_title);
        description = findViewById(R.id.news_description);
        createNews = findViewById(R.id.add_news_button);

        image = findViewById(R.id.add_image);
        addImageBtn = findViewById(R.id.add_image_btn);
        addImageBtn.setOnClickListener(click->{openGallery();});

        createNews.setOnClickListener(click -> {
            createNews();
            Toast.makeText(AddNews.this, "News Created Successfully!",Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void createNews() {
        String newsTitle = title.getText().toString().trim();
        String newsDescription = description.getText().toString().trim();

        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        Date curDate = new Date(System.currentTimeMillis());
        String createDate = formatter.format(curDate);
        String newsImageURL = "null";
        //Choose photo from phone
        if(!imageBitmap.equals(null)) {
            newsImageURL = compress(imageBitmap);
        }

        DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("News");
        long id = NewsFragment.maxNewsID + 1;
        NewsFragment.maxNewsID = id;
        News addNews = new News(newsTitle, newsDescription, newsImageURL, createDate, id);
        eventsReference.child(String.valueOf(UUID.randomUUID())).setValue(addNews);

    }

    /***
     * Used to convert bitmap to string
     */
    private String compress(Bitmap image) {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100,out);
        byte[] bytes= out.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    /**
     * this method opens the phone gallery and allows user to select an image to be used for
     * the product being adapted
     */
    private void openGallery() {
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GO_TO_GALLERY);
    }

    /***
     * this method upon returning to the app  after the image is selected it sets the image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("RESULT CODE",String.valueOf(resultCode));
        if(resultCode==RESULT_OK ){
            Uri imageTemp = data.getData();
            InputStream imageStream=null;
            try {
                imageStream = getContentResolver().openInputStream(imageTemp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            image.setImageBitmap(selectedImage);
            imageBitmap=selectedImage;
        }
    }


}