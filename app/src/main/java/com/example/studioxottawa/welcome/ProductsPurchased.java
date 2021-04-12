package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.studioxottawa.R;
import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * @Author Ahmed Albarghouti
 * @Date April 2021
 * @Purpose Displays current user's already purchased products
 */
public class ProductsPurchased extends AppCompatActivity {
    // Declaring elements
    ListView products;
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private static ArrayList<Product> productPurchasedList = new ArrayList<>();
    private PurchasedProductListAdapter listAdapter = new PurchasedProductListAdapter();
    private Bitmap i1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_purchased);

        //init elements
        products = findViewById(R.id.products_purchasedLV);
        products.setAdapter(listAdapter);

    }
    /**
     * @Purpose Loads current user's already purchased products into purchasedProducts arraylist
     */
    void loadPurchasedProducts(){
        productPurchasedList.clear();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        referenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot eventsPurchasedSnapshot = snapshot.child("Products Purchased");
                for(DataSnapshot dsEvent : eventsPurchasedSnapshot.getChildren()){
                    String item = String.valueOf(dsEvent.child("item").getValue());
                    String quantity = String.valueOf(dsEvent.child("quantity").getValue());
                    String price = String.valueOf(dsEvent.child("price").getValue());
                    String date = String.valueOf(dsEvent.child("date").getValue());
                    String bitmap = String.valueOf(dsEvent.child("bitmap").getValue());
                    Product prod = new Product(item,Double.parseDouble(price),Integer.parseInt(quantity));
                    prod.setDate(date);
                    prod.setBitmap(bitmap);
                    productPurchasedList.add(prod);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param input byte array of soon to be bitmap
     * @return bitmap decoded from Byte Array param
     */
    private Bitmap decodeFromStringToImage(String input){
        byte[] decodingBytes= Base64.decode(input,0);
        return BitmapFactory.decodeByteArray(decodingBytes,0,decodingBytes.length);
    }

    class PurchasedProductListAdapter extends BaseAdapter{

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return productPurchasedList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return productPurchasedList.get(position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView = null;
            //Initializing a LayoutInflater and inflating the service_layout xml
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.product_row, parent, false);

            //populating the Textviews,and ImageViews with appropriate data.
            Product product = productPurchasedList.get(position);
            // Initialize Textview for the item title.
            TextView item = newView.findViewById(R.id.serviceTitle);
            // populating the item title Textview with the current items title.
            item.setText(String.valueOf(product.getItem()));
            // initializing the Price Textview.
            TextView price = newView.findViewById(R.id.servicePrice);
            // populating the Price Textview with the current item price.
            String money = "$";
            price.setText(money + String.valueOf(formatter.format(product.getPrice())));

            TextView date = newView.findViewById(R.id.servicePurchaseDate);
            date.setText(String.valueOf(product.getDate()));

            ImageView thumbnail = newView.findViewById(R.id.serviceImage);
            // if product bitmap is empty set thumbnail to the default i1. if not grab the product image and use it as the thumbnail
            i1 = BitmapFactory.decodeResource(getResources(), R.drawable.big_logo);
            if(product.getBitmap().isEmpty()){
                thumbnail.setImageBitmap(i1);
            }else {
                thumbnail.setImageBitmap(decodeFromStringToImage(product.getBitmap()));
            }
            //return it to be put in the table
            return newView;
        }
    }
}