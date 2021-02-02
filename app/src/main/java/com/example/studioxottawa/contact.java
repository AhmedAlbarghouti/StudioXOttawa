package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class contact extends AppCompatActivity {


    private static String[] ADDRESS={"testemail@gmail.com"};//replace with Studioxottawa@gmail.com later
    private EditText subject ;
    private EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

         subject = findViewById(R.id.subjectEditText);
         message = findViewById(R.id.message_editText);

         Button send = findViewById(R.id.sendButton);

         send.setOnClickListener(view -> sendEmail());

    }

     public void sendEmail(){

            Intent intent=new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL,ADDRESS);
            intent.putExtra(Intent.EXTRA_SUBJECT,subject.getText().toString());
            intent.putExtra(Intent.EXTRA_TEXT,message.getText().toString());

            startActivity(Intent.createChooser(intent,"Choose one application"));
     }
    }
