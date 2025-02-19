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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.studioxottawa.R;
import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.Product;
import com.example.studioxottawa.services.ServicesActivity;
import com.example.studioxottawa.welcome.MainActivity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 *     private firebase user
 *     ListView myList
 */
public class Cart extends Fragment {
    public static ArrayList<Product> products = new ArrayList<>();
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private double price = 0;
    private CartAdapter myAdapter;
    private TextView priceTv;
    private Boolean isEvent = false;
    private Bitmap i1;
    Pattern pattern=Pattern.compile("\\d+\\/\\d+\\/\\d+", Pattern.CASE_INSENSITIVE);
    private FragmentActivity fragmentActivity;
    private FirebaseUser user=  FirebaseAuth.getInstance().getCurrentUser();

    ListView myList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_cart, container, false);
        fragmentActivity=this.getActivity();
        i1 = BitmapFactory.decodeResource(getResources(), R.drawable.big_logo);
/**
 * Making sure that list is empty prior to loading items from database. To avoid Duplicates
 */
        if(!products.isEmpty()) {
            products.clear();
        }


        /**Initializing the Textviews, MyList,Buttons**/
        priceTv = root.findViewById(R.id.amountText);
        myList = root.findViewById(R.id.ItemPurchView);
        Button placeOrder = root.findViewById(R.id.checkoutButton);
        Button cancel = root.findViewById(R.id.cancelButton);

        /**on item cancel head back to main activity**/
        cancel.setOnClickListener(e -> {
            Intent intent = new Intent(this.getActivity(),  MainActivity.class);
            startActivity(intent);
        });

        /**
         * setting the Adapter on LongItemClick listener to remove an item from the screen
         */

        myList.setAdapter(myAdapter = new CartAdapter(root.getContext()));
        myList.setOnItemLongClickListener((parent, view, position, id) -> {
            String[] prod =  products.get(position).getItem().split("--");


            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(user.getUid());
            ref.child(prod[0]).removeValue();
            products.remove(position);
            myAdapter.notifyDataSetChanged();
            return true;
        });
        load();

        placeOrder.setOnClickListener(btn -> {

            Intent pay = new Intent(root.getContext(), CheckoutActivityJava.class);
            pay.putExtra("isService", isEvent);
            pay.putExtra("Total Price", price);
            startActivityForResult(pay, 1);
        });
        return root;
    }
    /**
     * This method loads the products that is in the current user's cart from the Firebase realtime database  and load it into an arraylist of products
     */
    public void load(){
        /**
         * grabbing a reference of the firebase Cart table in order to display the item the current user has in his cart.
         */
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(user.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String item= String.valueOf(ds.child("item").getValue());
                    String price= String.valueOf(ds.child("price").getValue());
                    String quantity= String.valueOf(ds.child("quantity").getValue());
                    String bitmap= String.valueOf(ds.child("bitmap").getValue());

                    /**
                     * placing all data that is in the Cart table  in the products array list.
                     */
                    Product temp= new Product(item,Double.parseDouble(price),Integer.parseInt(quantity));
                    if(!bitmap.equals("null") && !(bitmap.isEmpty())) {
                        temp.setBitmap(bitmap);
                    }
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

    /**
     * this method calculate the price of each item in the cart
     * and displays the total
     */
    public void  calculatePrice(){
        price=0;
        for (Product p: products){

            price+=p.getPrice()*p.getQuantity();
        }
        priceTv.setText(formatter.format(price));

    }
    /**This method will decode Compressed Base64 strings back into its bitmap*/
    private Bitmap decodeFromStringToImage(String input){
        byte[] decodingBytes= Base64.decode(input,0);
        return BitmapFactory.decodeByteArray(decodingBytes,0,decodingBytes.length);
    }
    private class CartAdapter extends BaseAdapter{

        private Context context;
        /**To set the context of the cartAdapter**/
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

        /**
         * getView method loops through the list provided and brings the item up on screen
         * using this method the view on screen can be modified accordingly
         * @param i
         * @param view
         * @param viewGroup
         * @return
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if(view==null){
                holder=new ViewHolder();

                LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view=inflater.inflate(R.layout.row_layout_checkout,null,true);

                /**Initializing textviews to display the product name and price */
                holder.item =(TextView) view.findViewById(R.id.check_item_desc);
                holder.thumbnail=(ImageView) view.findViewById(R.id.imageThumb);
                holder.price=(TextView)  view.findViewById(R.id.check_item_price);
                holder.quantity=(TextView) view.findViewById(R.id.quantityET);
                /** Initilization the plus and minus buttons to update quantity in cart **/
                holder.btn_minus=(ImageButton) view.findViewById(R.id.minusBtn);
                holder.btn_plus=(ImageButton) view.findViewById(R.id.addBtn);

                view.setTag(holder);
            }else{
                holder =(ViewHolder)view.getTag();
            }
            Product product = (Product)getItem(i);
            /**Check for a specific pattern in the name of the item**/
            Matcher matcher=pattern.matcher(product.getItem());
            isEvent=matcher.find();
            /**If the item matches the pattern above, it is an Event and should not display plus minus btns*/
            if(isEvent){
                holder.btn_plus.setVisibility(View.INVISIBLE);
                holder.btn_minus.setVisibility(View.INVISIBLE);
            }else{
                holder.btn_plus.setVisibility(View.VISIBLE);
                holder.btn_minus.setVisibility(View.VISIBLE);
            }
            holder.item.setText(product.getItem());
            if(product.getBitmap().isEmpty()){
                holder.thumbnail.setImageBitmap(i1);
            }else {
                holder.thumbnail.setImageBitmap(decodeFromStringToImage(product.getBitmap()));
            }

            holder.price.setText(formatter.format(product.getPrice()));
            holder.quantity.setText(String.valueOf(product.getQuantity()));

            /**Plus and minus  button onclick to update quantity of products in cart.
             * then the quantity is also updated in the database
             * After that the new total price is calculated
             */
            holder.btn_plus.setOnClickListener(view1 -> {
                int qty = Integer.parseInt(holder.quantity.getText().toString())+1;
                product.setQuantity(qty);
                holder.quantity.setText(String.valueOf(product.getQuantity()));
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(user.getUid());

                ref.child(product.getItem()).setValue(product);
                calculatePrice();
            });

            /**  minus  button onclick to update quantity of products in cart.
             * then the quantity is also updated in the database
             * After that the new total price is calculated
             */
            holder.btn_minus.setOnClickListener(view1 -> {

                int qty = Integer.parseInt(holder.quantity.getText().toString());
                if (qty > 0) {
                    qty -=1;

                    product.setQuantity(qty);
                    holder.quantity.setText(String.valueOf(product.getQuantity()));
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(user.getUid());


                    ref.child(product.getItem()).setValue(product);
                    calculatePrice();
                }
                // removing Item from cart
                /**  If quantity is equals to zero than the item is removed from the cart.
                 */
                if(qty==0){
//
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(user.getUid());
                    ref.child(product.getItem()).removeValue();
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