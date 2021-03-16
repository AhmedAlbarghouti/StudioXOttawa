package com.example.studioxottawa.services;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studioxottawa.Checkout.Cart;
import com.example.studioxottawa.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServicesActivity extends AppCompatActivity {

    private ServiceAdapter adapter;
    private AlertDialog.Builder builder;


    private ArrayList<Product> productList = new ArrayList<>();
//    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        final DatabaseReference rootRef;

//        context=this;

        builder = new AlertDialog.Builder(this);
        RecyclerView servicesView = findViewById(R.id.serviceContainer);
        adapter = new ServiceAdapter();



        loadServices();

        servicesView.setHasFixedSize(true);
        servicesView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL));
        servicesView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        servicesView.setAdapter(adapter);




//        Bitmap i1 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.tshirt);
//        Bitmap i2 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.towel);
//        Bitmap i3 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.water);
//        Bitmap i4 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.shoes);
//
//        Product p1 = new Product(i1, "T-Shirt", 42.50);
//        Product p2 = new Product(i2, "Towel", 10.00);
//        Product p3 = new Product(i3, "Bottled Water", 3.50);
//        Product p4 = new Product(i4, "Dance Shoes", 80.00);
//
//        adapter.add(p1);
//        adapter.add(p2);
//        adapter.add(p3);
//        adapter.add(p4);
//        productList.add(p1);
//        productList.add(p2);
//        productList.add(p3);
//        productList.add(p4);

//        rootRef=FirebaseDatabase.getInstance().getReference().child("Products");
//                for (Product p : productList) {
//
////                        rootRef.child("Products").child(p.getItem()).setValue(p);
//
//
//
//                }
//            }


    }


    private class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {


        private ArrayList<Product> productList = new ArrayList<>();
        private ArrayList<String> stringList=new ArrayList<>();
        private ImageButton goToCart;
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView thumbnailView;
            private final TextView itemView;
            private final TextView priceView;


            public ViewHolder(@NonNull View view) {
                super(view);
                thumbnailView = (ImageView) view.findViewById(R.id.serviceImage);
                itemView = (TextView) view.findViewById(R.id.serviceTitle);
                priceView = (TextView) view.findViewById(R.id.servicePrice);
                goToCart=findViewById(R.id.cartButton);
                goToCart.setOnClickListener(btn->{
                    if(productList.isEmpty()) {
                        Toast.makeText(ServicesActivity.this, "Cart is Empty", Toast.LENGTH_SHORT).show();
                    }else {

                        Intent viewCart = new Intent(ServicesActivity.this, Cart.class);

                        viewCart.putStringArrayListExtra("List",stringList);
                        startActivityForResult(viewCart,1);
                    }
                });

                view.setOnClickListener(v-> {
                    builder.setTitle("Add "+itemView.getText().toString()+" to Cart?");
                    builder.setPositiveButton("Add",(dialogInterface, i) -> {
                        Bitmap image= ((BitmapDrawable)thumbnailView.getDrawable()).getBitmap();
                        stringList.add(itemView.getText().toString());
                        updateCartIcon();
                    });
                    builder.setNegativeButton("Cancel",(dialogInterface, i) -> dialogInterface.cancel()).create().show();
                });


            }

            public ImageView getThumbnailView() { return thumbnailView; }

            public TextView getItemView() { return itemView; }

            public TextView getPriceView() { return priceView; }


        }


        public void add(Product p) {
            productList.add(p);
        }

        public void updateCartIcon(){
            if(!productList.isEmpty()){
                goToCart.setImageResource(R.drawable.shopping_cart_with_item);
            }else{
                goToCart.setImageResource(R.drawable.shopping_cart_empty);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_layout, parent, false);
            return new ViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.getThumbnailView().setImageBitmap(productList.get(position).getBitmap());
            holder.getItemView().setText((productList.get(position).getItem()));
            holder.getPriceView().setText(String.valueOf(productList.get(position).getPrice()));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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
                    String thumbnail= String.valueOf(ds.child("thumbnail").getValue());
                    Product temp= new Product(thumbnail,item,Double.parseDouble(price),Integer.parseInt(quantity));
                    adapter.add(temp);
                    productList.add(temp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}