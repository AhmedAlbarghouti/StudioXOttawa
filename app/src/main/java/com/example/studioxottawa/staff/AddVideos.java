package com.example.studioxottawa.staff;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.studioxottawa.R;
import com.example.studioxottawa.VODPlayer.Video;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddVideos extends AppCompatActivity {
    private EditText videoName, videoID;
    private ImageView thumbnail;
    private Button add_video, update;
    private Bitmap imageBitmap = null;
    private RadioButton free, prem;
    private static final int GO_TO_GALLERY=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_videos);
        //Gets all the references to elements on screen that will be used
        videoName=(EditText)findViewById(R.id.new_video_name);
        videoID=(EditText)findViewById(R.id.new_video_url);
        thumbnail=(ImageView)findViewById(R.id.new_video_image);
        add_video=(Button)findViewById(R.id.AddVideoBtn);
        update=(Button)findViewById(R.id.updateLibraryBtn);
        free =(RadioButton)findViewById(R.id.freeType);
        prem = (RadioButton)findViewById(R.id.premiumType);
        Button img= findViewById(R.id.imageSelectBtn);
        img.setOnClickListener(click-> openGallery());

        //Sets the radio buttons to toggle eachother so only one can be active at a time
        free.setOnClickListener(click->{free.setChecked(true); prem.setChecked(false);});
        prem.setOnClickListener(click->{prem.setChecked(true); free.setChecked(false);});

        //Adds listeners to each button to run their respective scripts
        add_video.setOnClickListener(click->{addVideo();});
        update.setOnClickListener(click->{updateLibrary();});

    }

    //Method to create a new video object from user input.
    public void  addVideo(){
        String item= videoName.getText().toString();
        String url= videoID.getText().toString();
        Video newVid;

        if(!item.isEmpty()  && !(url.isEmpty())) {
            //Generates either a free or premium video depending on the user's radio button selection
            if (free.isChecked())
                newVid = new Video(item, url, imageBitmap, Video.FREE_MODIFIER);
            else
                newVid = new Video(item, url, imageBitmap, Video.PREMIUM_MODIFIER);

            //Gets a firebase reference to pass the new video to
            DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Videos");
            eventsReference.child(newVid.getUID()).setValue(newVid);
            Toast.makeText(this, getString(R.string.video_add_success),Toast.LENGTH_SHORT).show();
            finish();
        }else{

            Toast.makeText(this, getString(R.string.invalid_input),Toast.LENGTH_SHORT).show();
        }
    }

    //Passes the context to the library updater, and runs with the success message stored in the strings. Used to pull youtube API data to the database
    public void updateLibrary() {
        UpdateLibrary updater = new UpdateLibrary(getBaseContext());
        updater.run(getString(R.string.updated_library));
    }

    //Method to open the user's image gallery to select a thumbnail for a video.
    private void openGallery() {
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GO_TO_GALLERY);
    }

    //Method to handle setting the thumbnail on screen and generating a bitmap for the video from the selected image.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("RESULT CODE",String.valueOf(resultCode));
        if(resultCode==RESULT_OK ){
            Uri image = data.getData();
            InputStream imageStream=null;
            try {
                imageStream = getContentResolver().openInputStream(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            thumbnail.setImageBitmap(selectedImage);
            imageBitmap=selectedImage;
        }
    }
}