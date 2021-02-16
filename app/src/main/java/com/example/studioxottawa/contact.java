package com.example.studioxottawa;

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

import com.google.android.material.navigation.NavigationView;

public class contact extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{


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

         Toolbar tBar = findViewById(R.id.toolbar);
         setSupportActionBar(tBar);

         DrawerLayout drawer = findViewById(R.id.drawer_layout);
         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tBar, R.string.Open, R.string.Close);
         drawer.addDrawerListener(toggle);
         toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }//on create

     public void sendEmail(){

            Intent intent=new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL,ADDRESS);
            intent.putExtra(Intent.EXTRA_SUBJECT,subject.getText().toString());
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
                    Toast.makeText(this, "Latin Dance", Toast.LENGTH_SHORT).show();
                break;

                case R.id.contatc_nav_fitness:
                    Toast.makeText(this, "Fitness Lesson", Toast.LENGTH_SHORT).show();
                break;

                case R.id.contatc_nav_rental:
                    Toast.makeText(this, "Rental Service", Toast.LENGTH_SHORT).show();

                break;

                case R.id.contatc_nav_news:
                    Toast.makeText(this, "NEWS", Toast.LENGTH_SHORT).show();

                break;

                case R.id.contatc_nav_photos:
                    Toast.makeText(this, "PHOTOS", Toast.LENGTH_SHORT).show();

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
