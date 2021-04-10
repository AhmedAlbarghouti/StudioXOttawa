package com.example.studioxottawa.services;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.studioxottawa.Checkout.Cart;
import com.example.studioxottawa.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * ServiceActivity Class
 * This class displays all the products available on the store and
 * Allows user to add items to cart to be purchased later.
 *
 * Variables
 *     private MyListAdapter adapter
 *     private AlertDialog.Builder builder
 *     private final NumberFormat formatter
 *     private  Bitmap i1;
 *     private  ArrayList<Product> productList
 *     private  ListView servicesView
 *     private ImageButton goToCart
 *     private boolean hasItem
 *     private String UID
 *
 */
public class ServicesActivity extends Fragment {

    private MyListAdapter adapter;
    private AlertDialog.Builder builder;
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private   Bitmap i1;
    private   ArrayList<Product> productList = new ArrayList<>();
    private   ArrayList<String> stringList=new ArrayList<>();
    private   ListView servicesView;
    private   ImageButton goToCart;
    private boolean hasItem;
    private   String UID="";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_services, container, false);

        //Initializing the AlertDialogs,Adapter,and Cart button.
        builder = new AlertDialog.Builder(root.getContext());
        servicesView = root.findViewById(R.id.serviceContainer);
        servicesView.setAdapter(adapter = new MyListAdapter());
        goToCart = root.findViewById(R.id.cartButton);
        //retrieving the user id of the current logged on user.
        UID=getArguments().getString("UID");

        i1 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_studioxottawa);
        //load products available to purchase
        loadServices();
        //check if cart has item
        checkCart();

        goToCart.setOnClickListener(btn -> {

            // checking if the cart is empty. If it is it notifies the user that the cart is empty
            // if the cart is not empty, it navigates the user to the cart fragment to view the item contained in the cart.
            if (!hasItem) {
                Toast.makeText(root.getContext(), getString(R.string.EmptyCart), Toast.LENGTH_SHORT).show();
            }
            else {
                // getting the fragment to be navigated to.
                Fragment selectedFragment= new Cart();
                Bundle bundle= new Bundle();
                bundle.putStringArrayList("List",stringList);
                bundle.putString("UID",UID);
                selectedFragment.setArguments(bundle);
                // transitioning to the next fragment
                getParentFragmentManager().beginTransaction().replace(R.id.menu_fragment_container,selectedFragment).commit();

            }
        });




        // setting onCLickListener for list to give the user the option to add the item they clicked to their cart.
        servicesView.setOnItemClickListener((parent, view, position, id) -> {
            builder.setTitle(getString(R.string.sa_add )+" "+ productList.get(position).getItem() +" "+ getString(R.string.sa_toCart));

            builder.setPositiveButton(getString(R.string.sa_dialog_add), (dialogInterface, i) -> {

                //grabbing the specific item that the user clicked using the position variable of the Listener, setting the product quantity and adding it to the Carts on the database.
                DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Users");
                Product productInCart= productList.get(position);
                productInCart.setQuantity(1);

                eventsReference.child(UID).child("Cart").child(productInCart.getItem()).setValue(productInCart);

                stringList.add(productList.get(position).getItem());
                checkCart();
                //notifies the adapter that there has been a change in the list to be displayed
                adapter.notifyDataSetChanged();
            });
            //cancels adding the item to cart
            builder.setNegativeButton(getString(R.string.sa_cancel), (dialogInterface, i) -> dialogInterface.cancel()).create().show();
            //
        });
        return root;
    }

    /**
     * this method connects with the Firebase database to check if the current logged in user has any items in their cart.
     * if they do it changes the cart to red icon and set hasItem boolean to true.
     * if not it changes the cart icon to black and set hasItem boolean to false.
     *
     */
    public void checkCart(){

        DatabaseReference referenceServices=FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("Cart");
        referenceServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // checking if the current user has nodes under the Cart label.
                if(snapshot.exists()){
                    goToCart.setImageResource(R.drawable.shopping_cart_with_item);
                    hasItem=true;
                }else{

                    goToCart.setImageResource(R.drawable.shopping_cart_empty);
                    hasItem=false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }


    /**
     * This method loads the products being sold from the Firebase realtime database  Products table, creates a Product object with the data
     * and populates the array list of products called ProductList with each item.
     */
    public void loadServices(){
        // calling an instance of the database to grab all the nodes in the products table
        DatabaseReference referenceServices=FirebaseDatabase.getInstance().getReference().child("Products");
        referenceServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // grabbing the item, price, and quantity value for each node in the products table, storing them in a string object,

                for(DataSnapshot ds: snapshot.getChildren()){
                    String item= String.valueOf(ds.child("item").getValue());
                    String price= String.valueOf(ds.child("price").getValue());
                    String quantity= String.valueOf(ds.child("quantity").getValue());
                    // creating a Product object, and storing those Product objects in an ArrayList of Products.
                    Product temp= new Product(item,Double.parseDouble(price),Integer.parseInt(quantity));
                    productList.add(temp);
                }
                // once all nodes are added to the ArrayList notifying MyListAdapter of changes.
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    /**
     * The MyListAdapter private inner class extends BaseAdapter
     * its purpose is to keep track of the items of the productlist as they scroll into and out of view on screen,
     * inflate the layout xml file for ServiceActivity and populate the appropriate Textviews accordingly.
     */
    private class MyListAdapter extends BaseAdapter {

        // getting the number of items to be displayed in the list
        public int getCount() {
            return productList.size();
        }

        // grabbing a specific item at a specific location on screen
        public Product getItem(int position) { return productList.get(position); }


        public long getItemId(int position) {
            return (long) position;
        }
        // this method loops through items on screen and display each item appropriately
        public View getView(int position, View old, ViewGroup parent) {
            View newView = null;
            //Initializing a LayoutInflater and inflating the service_layout xml
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.service_layout, parent, false);

            //populating the Textviews,and ImageViews with appropriate data.
            Product product = getItem(position);
            // Initialize Textview for the item title.
            TextView item = newView.findViewById(R.id.serviceTitle);
            // populating the item title Textview with the current items title.
            item.setText(String.valueOf(product.getItem()));
            // initializing the Price Textview.
            TextView price = newView.findViewById(R.id.servicePrice);
            // populating the Price Textview with the current item price.
            price.setText(String.valueOf(formatter.format(product.getPrice())));
            ImageView thumbnail = newView.findViewById(R.id.serviceImage);
            thumbnail.setImageBitmap(i1);
            //return it to be put in the table
            return newView;
        }
    }
}

