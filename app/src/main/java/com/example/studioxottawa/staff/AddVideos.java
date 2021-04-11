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

        videoName=(EditText)findViewById(R.id.new_video_name);
        videoID=(EditText)findViewById(R.id.new_video_url);
        thumbnail=(ImageView)findViewById(R.id.new_video_image);
        add_video=(Button)findViewById(R.id.AddVideoBtn);
        update=(Button)findViewById(R.id.updateLibraryBtn);
        free =(RadioButton)findViewById(R.id.freeType);
        prem = (RadioButton)findViewById(R.id.premiumType);
        Button img= findViewById(R.id.imageSelectBtn);
        img.setOnClickListener(click-> openGallery());

        free.setOnClickListener(click->{free.setChecked(true); prem.setChecked(false);});
        prem.setOnClickListener(click->{prem.setChecked(true); free.setChecked(false);});

        add_video.setOnClickListener(click->{addVideo();});
        update.setOnClickListener(click->{updateLibrary();});

    }

    public void  addVideo(){
        String item= videoName.getText().toString();
        String url= videoID.getText().toString();
        Video newVid;

        if(!item.isEmpty()  && !(url.isEmpty())) {

            if (free.isChecked())
                newVid = new Video(item, url, imageBitmap, Video.FREE_MODIFIER);
            else
                newVid = new Video(item, url, imageBitmap, Video.PREMIUM_MODIFIER);

            DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Videos");
            eventsReference.child(newVid.getUID()).setValue(newVid);
            Toast.makeText(this, getString(R.string.video_add_success),Toast.LENGTH_SHORT).show();
            finish();
        }else{

            Toast.makeText(this, getString(R.string.invalid_input),Toast.LENGTH_SHORT).show();
        }
    }

    public void updateLibrary() {
        UpdateLibrary updater = new UpdateLibrary(getBaseContext());
        updater.run(getString(R.string.updated_library));
    }

    private void openGallery() {
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GO_TO_GALLERY);
    }

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