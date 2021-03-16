package com.example.studioxottawa.Checkout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studioxottawa.R;
import com.example.studioxottawa.services.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    private ArrayList<Product> products= new ArrayList<>();
    private ArrayList<Product> productTwo= new ArrayList<>();
    private NumberFormat formatter = new DecimalFormat("#0.00");
    private double price=0;
    private  CartAdapter myAdapter;
    private TextView priceTv;
    ArrayList<String> json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


      json= getIntent().getExtras().getStringArrayList("List");

      loadServices();
//
//        for(String s : json){
//            Log.e("JJson",s);
//            String[] arr =s.split(",");
//            Bitmap image= StringToBitMap(arr[0]);
//            products.add(new Product(arr[0],arr[1],Double.valueOf(arr[2]),Integer.parseInt(arr[3])));
//        }
//        Bitmap i1 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.tshirt);
//        Bitmap i2 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.towel);
//        Bitmap i3 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.water);
//        Bitmap i4 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.shoes);
        priceTv= findViewById(R.id.amountText);
//
//        Product p1 = new Product(i1, "T-Shirt", 42.50);
//        Product p2 = new Product(i2, "Towel", 10.00);
//        Product p3 = new Product(i3, "Bottled Water", 3.50);
//        Product p4 = new Product(i4, "Dance Shoes", 80.00);
//
//        products.add(p1);
//        products.add(p2);
//        products.add(p3);
//        products.add(p4);




        calculatePrice();
        ListView myList= findViewById(R.id.ItemPurchView);
        myList.setAdapter(myAdapter=new CartAdapter(this));

        Button placeOrder= findViewById(R.id.checkoutButton);
        placeOrder.setOnClickListener(btn->{
            Intent pay = new Intent(this,CheckoutActivityJava.class);
            pay.putExtra("Total Price",price);
            startActivity(pay);
        });
    }


    public void loadServices(){

        for(String s: json) {
        DatabaseReference referenceServices=FirebaseDatabase.getInstance().getReference().child("Products").child(s);
        referenceServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String item= String.valueOf(snapshot.child("item").getValue());
                    String price= String.valueOf(snapshot.child("price").getValue());
                    String quantity= String.valueOf(snapshot.child("quantity").getValue());
                    String thumbnail= String.valueOf(snapshot.child("thumbnail").getValue());
                    Product temp= new Product(thumbnail,item,Double.parseDouble(price),Integer.parseInt(quantity));
                    products.add(temp);
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }
    }
//        DatabaseReference referenceServices=FirebaseDatabase.getInstance().getReference().child("Products");
//        referenceServices.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot ds: snapshot.getChildren()){
//                    String item= String.valueOf(ds.child("item").getValue());
//                    String price= String.valueOf(ds.child("quantity").getValue());
//                    String quantity= String.valueOf(ds.child("quantity").getValue());
//                    String thumbnail= String.valueOf(ds.child("thumbnail").getValue());
//                    Product temp= new Product(thumbnail,item,Double.parseDouble(price),Integer.parseInt(quantity));
//                    products.add(temp);
//                }
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    public void  calculatePrice(){
        price=0;
        for (Product p: products){

            price+=p.getPrice()*p.getQuantity();
        }
        priceTv.setText(formatter.format(price));

    }
    private class CartAdapter extends BaseAdapter{

        private Context context;

        public CartAdapter(Context context){
            this.context=context;
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Product getItem(int i) {
            return products.get(i);
        }

        @Override
        public long getItemId(int i) {
            return (long) i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if(view==null){
                holder=new ViewHolder();
                LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view=inflater.inflate(R.layout.row_layout_checkout,null,true);
                holder.desc=(TextView) view.findViewById(R.id.check_item_desc);
                holder.thumbnail=(ImageView) view.findViewById(R.id.imageThumb);
                holder.price=(TextView)  view.findViewById(R.id.check_item_price);
                holder.quantity=(TextView) view.findViewById(R.id.quantityET);
                holder.btn_minus=(ImageButton) view.findViewById(R.id.minusBtn);
                holder.btn_plus=(ImageButton) view.findViewById(R.id.addBtn);
                view.setTag(holder);
            }else{
                holder =(ViewHolder)view.getTag();
            }
            Product product= getItem(i);
            holder.desc.setText(product.getItem());
            holder.thumbnail.setImageBitmap(product.getBitmap());
            holder.price.setText(formatter.format(product.getPrice()));
            holder.btn_plus.setOnClickListener(view1 -> {
                int qty= Integer.parseInt(holder.quantity.getText().toString())+1;
                product.setQuantity(qty);
                holder.quantity.setText(String.valueOf(qty));
                calculatePrice();
            });

            holder.btn_minus.setOnClickListener(view1 -> {
                int qty= Integer.parseInt(holder.quantity.getText().toString());
                if(qty>0) {
                    qty-=1;
                    product.setQuantity(qty);
                    holder.quantity.setText(String.valueOf(qty));
                    calculatePrice();
                }
            });
            return view;
        }
    }
    private class ViewHolder{
        protected ImageButton btn_plus,btn_minus;
        protected TextView desc,price,quantity;
        protected ImageView thumbnail;
    }
}