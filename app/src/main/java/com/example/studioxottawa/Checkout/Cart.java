package com.example.studioxottawa.Checkout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.studioxottawa.R;
import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.Product;
import com.example.studioxottawa.services.ServicesActivity;
import com.example.studioxottawa.welcome.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/***
 * Cart  class
 * This Class allows the current user to manipulate the items in their cart.
 * Variables
 *     public static ArrayList<Product> products = new ArrayList<>();
 *     private final NumberFormat formatter
 *     private double price
 *     private CartAdapter myAdapter
 *     private TextView priceTv
 *     private Boolean isEvent = false
 *     private Bitmap i1
 *     private FragmentActivity fragmentActivity
 *     ListView myList
 */
public class Cart extends Fragment {
    public static ArrayList<Product> products = new ArrayList<>();
    public static ArrayList<String> forPay;
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private double price = 0;
    private CartAdapter myAdapter;
    private TextView priceTv;
    private Boolean isEvent = false;
    private String eventName;
    private Event event;
    private Bitmap i1;
    private FragmentActivity fragmentActivity;
    ListView myList;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_cart, container, false);
        fragmentActivity=this.getActivity();
        i1 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_studioxottawa);

        if(!products.isEmpty()) {
            products.clear();
        }

        priceTv = root.findViewById(R.id.amountText);
        myList = root.findViewById(R.id.ItemPurchView);
        Button placeOrder = root.findViewById(R.id.checkoutButton);
        Button cancel = root.findViewById(R.id.cancelButton);


        cancel.setOnClickListener(e -> {
            fragmentActivity.finish();
        });





        myList.setAdapter(myAdapter = new CartAdapter(root.getContext()));
        myList.setOnItemLongClickListener((parent, view, position, id) -> {
            products.remove(position);
            myAdapter.notifyDataSetChanged();
            return true;
        });
            load();

        placeOrder.setOnClickListener(btn -> {

            Intent pay = new Intent(root.getContext(), CheckoutActivityJava.class);
            pay.putExtra("isService", isEvent);
            pay.putExtra("EventObj", event);
            pay.putExtra("Total Price", price);
            pay.putStringArrayListExtra("forPay", forPay);
            startActivityForResult(pay, 1);
        });
        return root;
    }
    /**
     * This method loads the products that is in the current user's cart from the Firebase realtime database  and load it into an arraylist of products
     */
    public void load(){
        DatabaseReference referenceServices=FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.userID).child("Cart");

                    referenceServices.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            products.clear();
                            for(DataSnapshot ds: snapshot.getChildren()){
                                String item= String.valueOf(ds.child("item").getValue());
                                String price= String.valueOf(ds.child("price").getValue());
                                String quantity= String.valueOf(ds.child("quantity").getValue());
                                if(item.contains("\\d+\\/\\d+\\/\\d+")){
                                    isEvent=true;
                                }
                                Log.e("Item",item);
                                Log.e("price",price);
                                Log.e("quantity",quantity);
                                Product temp= new Product(item,Double.parseDouble(price),Integer.parseInt(quantity));
                                products.add(temp);
                            }
                            calculatePrice();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });
                    myAdapter.notifyDataSetChanged();
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

                holder.item =(TextView) view.findViewById(R.id.check_item_desc);
                holder.thumbnail=(ImageView) view.findViewById(R.id.imageThumb);
                holder.price=(TextView)  view.findViewById(R.id.check_item_price);
                holder.quantity=(TextView) view.findViewById(R.id.quantityET);

                holder.btn_minus=(ImageButton) view.findViewById(R.id.minusBtn);
                holder.btn_plus=(ImageButton) view.findViewById(R.id.addBtn);

                view.setTag(holder);
            }else{
                holder =(ViewHolder)view.getTag();
            }

            if(isEvent){

                holder.btn_minus.setVisibility(view.INVISIBLE);
                holder.btn_plus.setVisibility(View.INVISIBLE);
                holder.quantity.setVisibility(View.INVISIBLE);

            }

            Product product = (Product)getItem(i);

            holder.item.setText(product.getItem());
            holder.thumbnail.setImageBitmap(i1);
            holder.price.setText(formatter.format(product.getPrice()));
            holder.quantity.setText(String.valueOf(product.getQuantity()));



            holder.btn_plus.setOnClickListener(view1 -> {
                int qty = Integer.parseInt(holder.quantity.getText().toString())+1;
                product.setQuantity(qty);
                holder.quantity.setText(String.valueOf(product.getQuantity()));
                DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Users");


                eventsReference.child(MainActivity.userID).child("Cart").child(product.getItem()).setValue(product);
                calculatePrice();
            });

            holder.btn_minus.setOnClickListener(view1 -> {

                int qty = Integer.parseInt(holder.quantity.getText().toString());
                if (qty > 0) {
                    qty -=1;

                    product.setQuantity(qty);
                    holder.quantity.setText(String.valueOf(product.getQuantity()));
                    DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Users");


                    eventsReference.child(MainActivity.userID).child("Cart").child(product.getItem()).setValue(product);
                    calculatePrice();
                }
                // removing Item from cart
                if(qty==0){
//
                    DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Users");
                    eventsReference.child(MainActivity.userID).child("Cart").child(product.getItem()).removeValue();
                    products.remove(product); //new
                    myAdapter.notifyDataSetChanged();

                }
            });



            return view;
        }
    }
    private class ViewHolder{
        protected ImageButton btn_plus,btn_minus;
        protected TextView item,price,quantity;
        protected ImageView thumbnail;
    }
}