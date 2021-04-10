package com.example.studioxottawa.contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studioxottawa.R;
import com.google.android.material.navigation.NavigationView;

/***
 * Contact class
 * this class allows the current user to send an email to Studio X Ottawa.
 *
 * Variable
 *     private static String[] ADDRESS
 *     private EditText subject
 *     private EditText message
 */
public class contact extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{


    private static String[] ADDRESS={"testemail@gmail.com"};//replace with Studioxottawa@gmail.com later
    private EditText subject ;
    private EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
         //initializing the Views the subject , message textfield.
         subject = findViewById(R.id.subjectEditText);
         message = findViewById(R.id.message_editText);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

         // initializing the send button
         Button send = findViewById(R.id.sendButton);

         send.setOnClickListener(view -> sendEmail());


         DrawerLayout drawer = findViewById(R.id.drawer_layout);
         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tBar, R.string.Open, R.string.Close);
         drawer.addDrawerListener(toggle);
         toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }//on create

    /***
     * this message retrieves all the information entered in the textfields,
     * launches an Email client and populates the necessary fields with the information
     * entered by the user in the fields.
     */
     public void sendEmail(){
            // initializing intent to open an email client.
            Intent intent=new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            //passing along the email address to be used.
            intent.putExtra(Intent.EXTRA_EMAIL,ADDRESS);
            // passing along the subject line of the email.
            intent.putExtra(Intent.EXTRA_SUBJECT,subject.getText().toString());
           // passing along the message body of the email.
            intent.putExtra(Intent.EXTRA_TEXT,message.getText().toString());

            startActivity(Intent.createChooser(intent,"Choose one application"));
     }//sendEmail

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
//        AlertDialog.Builder adb= new AlertDialog.Builder(this);
        switch(item.getItemId())
        {
                case R.id.contact_nav_aboutUs:
                    Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();

                break;
                case R.id.contact_nav_latin_dance:
                    Toast.makeText(this, "Services", Toast.LENGTH_SHORT).show();
                break;

                case R.id.contatc_nav_fitness:
                    Toast.makeText(this, "Schedule", Toast.LENGTH_SHORT).show();
                break;

                case R.id.contatc_nav_rental:
                    Toast.makeText(this, "Video On Demand", Toast.LENGTH_SHORT).show();

                break;

                case R.id.contatc_nav_news:
                    Toast.makeText(this, "Photos", Toast.LENGTH_SHORT).show();

                break;

                case R.id.contatc_nav_photos:
                    Toast.makeText(this, "NEWS", Toast.LENGTH_SHORT).show();

                break;


        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_toolbar_menu, menu);
        return true;
    }
}
