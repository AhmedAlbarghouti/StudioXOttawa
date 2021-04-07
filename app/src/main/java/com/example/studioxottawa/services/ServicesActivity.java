package com.example.studioxottawa.services;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studioxottawa.Checkout.Cart;
import com.example.studioxottawa.R;
import com.example.studioxottawa.welcome.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServicesActivity extends AppCompatActivity {

    private MyListAdapter adapter;
    private AlertDialog.Builder builder;
    private NumberFormat formatter = new DecimalFormat("#0.00");
    private Bitmap i1;
    private SharedPreferences prefs=null;
    private ArrayList<Product> productList = new ArrayList<>();
    private ArrayList<String> stringList=new ArrayList<>();
    private ListView servicesView;
    private ImageButton goToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);


        loadServices();
        loadCart();

        goToCart=findViewById(R.id.cartButton);
        goToCart.setOnClickListener(btn->{
            if(productList.isEmpty() && stringList.isEmpty()) {
                Toast.makeText(ServicesActivity.this, "Cart is Empty", Toast.LENGTH_SHORT).show();
            }else {

                Intent viewCart = new Intent(ServicesActivity.this, Cart.class);

                viewCart.putStringArrayListExtra("List",stringList);
                startActivityForResult(viewCart,1);
            }
        });

        updateCartIcon();

        builder = new AlertDialog.Builder(this);
        servicesView = findViewById(R.id.serviceContainer);
        servicesView.setAdapter(adapter = new MyListAdapter());

           servicesView.setOnItemClickListener((parent, view, position, id) -> {

               builder.setTitle("Add "+productList.get(position).getItem()+" to Cart?");
               builder.setPositiveButton("Add",(dialogInterface, i) -> {
                   saveSharedPrefs(productList.get(position).getItem()+"::");
                   stringList.add(productList.get(position).getItem());
                   updateCartIcon();
               });
               builder.setNegativeButton("Cancel",(dialogInterface, i) -> dialogInterface.cancel()).create().show();

               adapter.notifyDataSetChanged();

           });

          i1 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.logo_studioxottawa);



    }

    private void loadCart(){
        prefs= getSharedPreferences("Cart",Context.MODE_PRIVATE);
        String items= prefs.getString("Items","");
        if(!items.isEmpty()) {
            String[] savedItems=items.split("::");
            for(String product: savedItems){
                stringList.add(product);
            }
        }
    }
    private void deleteSharedPrefs() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
    }
    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Items", stringToSave);
        editor.commit();
    }
    public void updateCartIcon(){
        if(!productList.isEmpty() || !stringList.isEmpty()){
            goToCart.setImageResource(R.drawable.shopping_cart_with_item);
        }else{
            goToCart.setImageResource(R.drawable.shopping_cart_empty);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==1000) {
            productList.clear();
            stringList.clear();
            deleteSharedPrefs();
            loadServices();
            updateCartIcon();

                 }

        }

    public void loadServices(){
        DatabaseReference referenceServices=FirebaseDatabase.getInstance().getReference().child("Products");
        referenceServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String item= String.valueOf(ds.child("item").getValue());
                    String price= String.valueOf(ds.child("price").getValue());
                    String quantity= String.valueOf(ds.child("quantity").getValue());
                    Product temp= new Product(item,Double.parseDouble(price),Integer.parseInt(quantity));
                    productList.add(temp);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return productList.size();
        }

        public Product getItem(int position) { return productList.get(position); }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View old, ViewGroup parent) {
            Log.i("gycreport", " getview ");
            View newView = null;
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.service_layout, parent, false);
            Product u = getItem(position);
            TextView item = newView.findViewById(R.id.serviceTitle);
            item.setText("  "+u.getItem());
            TextView price = newView.findViewById(R.id.servicePrice);
            price.setText("  "+formatter.format(u.getPrice()));
            ImageView thumbnail = newView.findViewById(R.id.serviceImage);
            thumbnail.setImageBitmap(i1);
            //return it to be put in the table
            return newView;
        }
    }
}

