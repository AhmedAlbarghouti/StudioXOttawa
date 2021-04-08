package com.example.studioxottawa.services;


import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.studioxottawa.Checkout.Cart;
import com.example.studioxottawa.R;
import com.example.studioxottawa.news.NewsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;


public class ServicesActivity extends Fragment {

    private MyListAdapter adapter;
    private AlertDialog.Builder builder;
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private Bitmap i1;
    private SharedPreferences prefs=null;
    private ArrayList<Product> productList = new ArrayList<>();
    private ArrayList<String> stringList=new ArrayList<>();
    private ListView servicesView;
    private ImageButton goToCart;
    private String stringToSave="";
    private StringBuilder sb= new StringBuilder(stringToSave);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_services, container, false);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_services);

        builder = new AlertDialog.Builder(root.getContext());
        servicesView = root.findViewById(R.id.serviceContainer);
        servicesView.setAdapter(adapter = new MyListAdapter());
        goToCart = root.findViewById(R.id.cartButton);
        i1 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_studioxottawa);

        loadServices();
        loadCart();


        Log.e("PRODUCT LIST SIZE", productList.size() + "");
        Log.e("STRING LIST SIZE", stringList.size() + "");
        goToCart.setOnClickListener(btn -> {

            if (stringList.size() == 0) {
                Toast.makeText(root.getContext(), "Cart is Empty", Toast.LENGTH_SHORT).show();
            } else {
//                Fragment selectedFragment= new Cart();
//
//                Bundle bundle= new Bundle();
//                bundle.putStringArrayList("List",stringList);
//                selectedFragment.setArguments(bundle);
//                getParentFragmentManager().beginTransaction().replace(R.id.menu_fragment_container,selectedFragment).commit();
////
                 Intent viewCart = new Intent(root.getContext(), Cart.class);
                viewCart.putStringArrayListExtra("List", stringList);
                startActivityForResult(viewCart, 1);




            }
        });

        updateCartIcon();


        servicesView.setOnItemClickListener((parent, view, position, id) -> {
            builder.setTitle("Add " + productList.get(position).getItem() + " to Cart?");

            builder.setPositiveButton("Add", (dialogInterface, i) -> {

                sb.append(productList.get(position).getItem());
                sb.append( "::");
                saveSharedPrefs(sb.toString());
                stringList.add(productList.get(position).getItem());
                updateCartIcon();
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel()).create().show();
            adapter.notifyDataSetChanged();
        });
        return root;
    }

    public void loadCart(){
        prefs=  this.getActivity().getSharedPreferences("Cart",Context.MODE_PRIVATE);
        String items= prefs.getString("Items","");
        if(!items.isEmpty()) {
            String[] savedItems=items.split("::");
            stringList.addAll(Arrays.asList(savedItems));
        }
    }
    private void deleteSharedPrefs() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
    }
    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Items", stringToSave);
        editor.apply();
    }
    public void updateCartIcon(){
        if(!productList.isEmpty() || !stringList.isEmpty()){
            goToCart.setImageResource(R.drawable.shopping_cart_with_item);
        }else{
            goToCart.setImageResource(R.drawable.shopping_cart_empty);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            View newView = null;
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.service_layout, parent, false);
            Product product = getItem(position);
            TextView item = newView.findViewById(R.id.serviceTitle);
            item.setText(String.valueOf(product.getItem()));
            TextView price = newView.findViewById(R.id.servicePrice);
            price.setText(String.valueOf(formatter.format(product.getPrice())));
            ImageView thumbnail = newView.findViewById(R.id.serviceImage);
            thumbnail.setImageBitmap(i1);
            //return it to be put in the table
            return newView;
        }
    }
}

