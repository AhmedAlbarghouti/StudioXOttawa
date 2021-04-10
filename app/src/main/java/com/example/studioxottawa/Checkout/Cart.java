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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

//public class Cart extends AppCompatActivity {
public class Cart extends Fragment {
    public static ArrayList<Product> products = new ArrayList<>();
    public static ArrayList<String> forPay;
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private double price = 0;
    private CartAdapter myAdapter;
    private TextView priceTv;
    private ArrayList<String> product;
    private Boolean isEvent = false;
    private String eventKey;
    private String eventName;
    private Event event;
    private Bitmap i1;
    private FragmentActivity fragmentActivity;
    ListView myList;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cart);
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_cart, container, false);
        fragmentActivity=this.getActivity();
        i1 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_studioxottawa);
        forPay = new ArrayList<String>();
        product = getArguments().getStringArrayList("List");

//        product = getIntent().getExtras().getStringArrayList("List");
//
//        eventKey = getIntent().getExtras().getString("UID");
//        isEvent = getIntent().getExtras().getBoolean("isService");
        priceTv = root.findViewById(R.id.amountText);
        myList = root.findViewById(R.id.ItemPurchView);
        Button placeOrder = root.findViewById(R.id.checkoutButton);
        Button cancel = root.findViewById(R.id.cancelButton);


        cancel.setOnClickListener(e -> {
            fragmentActivity.finish();
        });

        if (isEvent) {
            loadEvent();
        } else {
            loadProducts();
        }



        myList.setAdapter(myAdapter = new CartAdapter(root.getContext()));
        myList.setOnItemLongClickListener((parent, view, position, id) -> {
            products.remove(position);
            myAdapter.notifyDataSetChanged();
            return true;
        });


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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1000) {
            forPay.clear();
            products.clear();
            myAdapter.notifyDataSetChanged();
            fragmentActivity.setResult(1000);
            fragmentActivity.finish();
        }

}
    public void loadEvent() {

        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("Events").child(eventKey);
        referenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String name = String.valueOf(snapshot.child("name").getValue());
                String date = String.valueOf(snapshot.child("date").getValue());
                String time = String.valueOf(snapshot.child("time").getValue());
                String staff = String.valueOf(snapshot.child("staff").getValue());
                String uid = String.valueOf(snapshot.child("uid").getValue());

                event=new Event(name,date,time,staff,uid);
                eventName= name+" "+date+" "+time+" with "+staff;
                Product temp = new Product(eventName,25.00,1);
                products.add(temp);
                calculatePrice();
                Log.i("value", name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void loadProducts(){
        for (String s : product) {
            DatabaseReference referenceProduct = FirebaseDatabase.getInstance().getReference().child("Products").child(s);
            referenceProduct.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String item = String.valueOf(snapshot.child("item").getValue());
                    String price = String.valueOf(snapshot.child("price").getValue());
//                    String quantity = String.valueOf(snapshot.child("quantity").getValue());
                    Product temp = new Product( item, Double.parseDouble(price), 1);


                    if(!(forPay.contains(item))){
                        forPay.add(item);
                    }
                    products.add(temp);
                    calculatePrice();
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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

            holder.btn_plus.setOnClickListener(view1 -> {
                int qty = Integer.parseInt(holder.quantity.getText().toString())+1;
                product.setQuantity(qty);
                holder.quantity.setText(String.valueOf(qty));

                calculatePrice();
            });

            holder.btn_minus.setOnClickListener(view1 -> {

                int qty = Integer.parseInt(holder.quantity.getText().toString());
                if (qty > 0) {
                    qty -=1;

                    product.setQuantity(qty);
                    holder.quantity.setText(String.valueOf(qty));

                    calculatePrice();
                }
                // removing Item from cart
                if(qty==0){

                    products.remove(product); //new
                    forPay.remove(product.getItem());//new
                    ServicesActivity.stringToSave=ServicesActivity.stringToSave.replace(product.getItem()+"::","");
                    ServicesActivity.saveSharedPrefs(ServicesActivity.stringToSave);
                    if(products.size()==0){
                        ServicesActivity.productList.clear();
                        ServicesActivity.stringList.clear();
                        ServicesActivity.deleteSharedPrefs();
                        ServicesActivity.updateCartIcon();

                    }
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