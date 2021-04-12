package com.example.studioxottawa.staff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.studioxottawa.R;
import com.example.studioxottawa.news.EmptyActivity;
import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.Product;
import com.example.studioxottawa.welcome.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Report extends AppCompatActivity {

    private MyListAdapter myAdapter;
    private ListView myList;
    private static ArrayList<User> allUsers = new ArrayList<User>();
    private static ArrayList<Event> eventsPurchasedList;
    private static ArrayList<Product> productsPurchasedList;
    private TextView text;

    /**
     * @param savedInstanceState - the Bundle object that is passed into the onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        text = (TextView)findViewById(R.id.user_num);
        text.setText("Total Users:  "+ allUsers.size());

        Log.i("gycreport", "MyList Ready");
        myList = findViewById(R.id.ListyView);
        myList.setAdapter( myAdapter = new MyListAdapter());

        myList.setOnItemClickListener( (list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();

            // Retrieve user from the list based on the index
            User u = allUsers.get(position);
            dataToPass.putString("FULL_NAME", u.fullName );
            dataToPass.putString("EMAIL", u.email );
            dataToPass.putString("PHONE", u.PhoneNumber );
            String eventDetail = "";
            int counter = 1;
            for(Event event : u.getEventsPurchased()){
                eventDetail = eventDetail+counter+". "+event.getName()+" by "+event.getStaff()+", at "+event.getTime()+", "+event.getDate()+"\n\n";
                counter+=1;
            }
            dataToPass.putString("EVENT", eventDetail );
            String productDetail = "";
            int counter1 = 1;
            for(Product product : u.getProductsPurchased()){
                productDetail = productDetail+counter1+". "+product.getItem()+" x "+product.getQuantity()+", unit price: $ "+product.getPrice()+"\n\n";
                counter1+=1;
            }
            dataToPass.putString("PRODUCT", productDetail );

            Log.i("datatopass", u.fullName+u.email+eventDetail);
            Intent nextActivity = new Intent(Report.this, ReportDetail.class);
            nextActivity.putExtra("data", dataToPass); //send data to next activity
            startActivity(nextActivity); //make the transition

            myAdapter.notifyDataSetChanged();
        }   );
    }

    /**
     * Used to load users from Firebase database
     */
    public static void loadUsers() {
        allUsers.clear();
        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("Users");

        referenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){

                    String fullName = String.valueOf(ds.child("fullName").getValue());
                    String phoneNumber = String.valueOf(ds.child("PhoneNumber").getValue());
                    String email = String.valueOf(ds.child("email").getValue());

                    DataSnapshot eventsPurchasedSnapshot = ds.child("Events Purchased");
                    eventsPurchasedList= new ArrayList();
                    for(DataSnapshot dsEvent : eventsPurchasedSnapshot.getChildren()){
                        String name = String.valueOf(dsEvent.child("name").getValue());
                        String staff = String.valueOf(dsEvent.child("staff").getValue());
                        String time = String.valueOf(dsEvent.child("time").getValue());
                        String date = String.valueOf(dsEvent.child("date").getValue());
                        String uid = String.valueOf(dsEvent.child("uid").getValue());
                        Log.i("gycevent", name+staff+time+date+uid);
                        eventsPurchasedList.add(new Event(name, date, time, staff, uid));
                    }

                    DataSnapshot productPurchasedSnapshot = ds.child("Products Purchased");
                    productsPurchasedList= new ArrayList();

                    for(DataSnapshot dsProduct : productPurchasedSnapshot.getChildren()){
                        String item = String.valueOf(dsProduct.child("item").getValue());
                        String pricelong = (dsProduct.child("price").getValue()).toString();
                        double price = Double.parseDouble(pricelong);
                        String quantitylong = (dsProduct.child("quantity").getValue()).toString();
                        int quantity = Integer.parseInt(quantitylong);

                        productsPurchasedList.add(new Product(item, price, quantity));
                    }

                    allUsers.add(new User(fullName, email, phoneNumber, eventsPurchasedList, productsPurchasedList));
                    Log.i("gycreport", fullName+" "+email+" "+phoneNumber+" "+ allUsers.size());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    /**
     * Adaptor for report list view
     */
    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return allUsers.size();
        }

        public Object getItem(int position) { return allUsers.get(position); }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View old, ViewGroup parent) {
            Log.i("gycreport", " getview ");
            View newView = null;
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.row_layout, parent, false);
            User u = (User)getItem(position);
            TextView tViewName = newView.findViewById(R.id.fullName);
            tViewName.setText(u.fullName);
            TextView tViewEmail = newView.findViewById(R.id.email);
            tViewEmail.setText(u.email);
            TextView tViewPhone = newView.findViewById(R.id.phoneNumber);
            tViewPhone.setText(u.PhoneNumber);

            //return it to be put in the table
            return newView;
        }
    }
}


