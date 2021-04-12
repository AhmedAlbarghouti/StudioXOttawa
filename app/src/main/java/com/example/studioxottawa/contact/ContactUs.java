package com.example.studioxottawa.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.studioxottawa.R;
/***
 * Contact class
 * this class allows the current user to send an email to Studio X Ottawa.
 *
 * Variable
 *     private static String[] ADDRESS
 *     private EditText subject
 *     private EditText message
 */
public class ContactUs extends AppCompatActivity {


    private static String[] ADDRESS={"testemail@gmail.com"};//replace with Studioxottawa@gmail.com later
    private EditText subject ;
    private EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        //initializing the Views the subject , message textfield.
        subject = findViewById(R.id.subjectEditText);
        message = findViewById(R.id.message_editText);

        Button send = findViewById(R.id.sendButton);
        // setting send button onclick listener to run sendEmail method
        send.setOnClickListener(view -> sendEmail());



    }//on create
    /***
     * this message retrieves all the information entered in the textfields,
     * launches an Email client and populates the necessary fields with the information
     * entered by the user in the fields.
     */
    public void sendEmail(){
        /**initializing intent to open an email client.*/
        Intent intent=new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        /**passing along the email address to be used.*/
        intent.putExtra(Intent.EXTRA_EMAIL,ADDRESS);
        /** passing along the subject line of the email.*/
        intent.putExtra(Intent.EXTRA_SUBJECT,subject.getText().toString());
        /** passing along the message body of the email.**/
        intent.putExtra(Intent.EXTRA_TEXT,message.getText().toString());

        startActivity(Intent.createChooser(intent,getString(R.string.choose_email_client)));
    }//sendEmail






}
