package com.example.studioxottawa.Checkout;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.studioxottawa.R;
import com.example.studioxottawa.schedule.BookAppointments;
import com.example.studioxottawa.schedule.Event;

import com.example.studioxottawa.services.Product;
import com.example.studioxottawa.services.ServicesActivity;
import com.example.studioxottawa.welcome.MainActivity;
import com.example.studioxottawa.welcome.WelcomePage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.Stripe;
import com.stripe.android.model.Address;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.model.ShippingMethod;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.view.ShippingInfoWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Stripe Checkout
 * This class implements stripe payment to allow user to purchase products on the app.
 *
 * Variables
 *     private static final String BACKEND_URL
 *     private OkHttpClient httpClient
 *     private String paymentIntentClientSecret
 *     private Stripe stripe
 *     private double price
 *     private ArrayList<Product> products
 *     private NumberFormat formatter
 *     private MyListAdapter adapter
 *     private AlertDialog.Builder builder
 *     private ListView checkoutListView
 *     private Bitmap i1
 * @Author: Stripe,Inc.
 */
public class CheckoutActivityJava extends AppCompatActivity {

    private static final String BACKEND_URL = "https://still-everglades-60303.herokuapp.com/";
    private OkHttpClient httpClient = new OkHttpClient();
    // configure with stripe secret key
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private double price = 0;
    private ArrayList<Product> products= new ArrayList<>();
    private NumberFormat formatter = new DecimalFormat("#0.00");
    private MyListAdapter adapter;
    private AlertDialog.Builder builder;
    private ListView checkoutListView;
    private Bitmap i1;
    FirebaseUser user=  FirebaseAuth.getInstance().getCurrentUser();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_java);
        //
        checkoutListView=findViewById(R.id.ItemPurchView);
        checkoutListView.setAdapter(adapter= new MyListAdapter());
        i1= BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.big_logo);

        price = getIntent().getExtras().getDouble("Total Price");
        load();
        /** Configure the SDK with your Stripe publishable key so it can make requests to Stripe**/
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51ILUoQJBRyYbLiOnhQMkiSrSTRnoRK6Py4gWV6rIXfPCWreERj4gb3B13wur8jzi3ZfL2mzGBPOItwABmqoAQLKk00vLxexHqx")
        );

        try {
            startCheckout();
        }catch (Exception e){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }catch (Error e){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }
    /**
     * This method loads the products that is in the current user's cart from the Firebase realtime database  and load it into an arraylist of products
     */
    public void load(){
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
//                   creating a Product object, and storing those Product objects in an ArrayList of Products.
                    Product temp= new Product(item,Double.parseDouble(price),Integer.parseInt(quantity));
                    if(!bitmap.equals("null") && !(bitmap.isEmpty())) {
                        temp.setBitmap(bitmap);
                    }

                    products.add(temp);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        adapter.notifyDataSetChanged();
    }

    private Bitmap decodeFromStringToImage(String input){
        byte[] decodingBytes= Base64.decode(input,0);
        return BitmapFactory.decodeByteArray(decodingBytes,0,decodingBytes.length);
    }
    /***
     * This method create the PaymentIntent and add send the transactional information to
     * the backend to make a Stripe Payment
     */
    private void startCheckout() {

        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        Map<String, Object> payMap = new HashMap<>();
        Map<String, Object> itemMap = new HashMap<>();
        // list of map objects to be sent to the backend.
        List<Map<String, Object>> itemList = new ArrayList<>();
        //Adding the Currency to the payMap
        payMap.put("currency", "cad");
        TextView amountText = findViewById(R.id.amountText);


        amountText.setText(String.valueOf(formatter.format(price)));
        double amount = price * 100;
        //adding the total amount to the itemMap
        itemMap.put("amount", amount);
        itemMap.put("email", "jamesRunnings@gmail.com");
        itemList.add(itemMap);
/**        sending the list of Map objects to the backend.**/
        payMap.put("items", itemList);

        String json = new Gson().toJson(payMap);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));
        /** Hook up the pay button to the card widget and stripe instance**/
        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {

            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);
            }
        });
    }

    private void displayAlert(@NonNull String title, @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(title).setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** Handle the result of stripe.confirmPayment**/
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> responseMap = gson.fromJson(Objects.requireNonNull(response.body()).string(), type);
        paymentIntentClientSecret = responseMap.get("clientSecret");

    }

    private  final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<CheckoutActivityJava> activityRef;

        PayCallback(@NonNull CheckoutActivityJava activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, getString(R.string.onFailureError) + e.toString(), Toast.LENGTH_LONG).show());
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, getString(R.string.onFailureError) + response.toString(), Toast.LENGTH_LONG).show());
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<CheckoutActivityJava> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivityJava activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Event event= getIntent().getParcelableExtra("EventObj");
                boolean isEvent =getIntent().getExtras().getBoolean("isService");




                Toast.makeText(CheckoutActivityJava.this, getString(R.string.PaymentSuccess),Toast.LENGTH_LONG).show();


                /**Grabbing a reference to or creating (if it doesnt exits) the current user's Products and Events purchased tables to update new purchase*/
                DatabaseReference prodRef= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Products Purchased");
                DatabaseReference eventRef= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Events Purchased");
                /**Pattern to check which item belongs to what table*/
                Pattern pattern=Pattern.compile("\\d+\\/\\d+\\/\\d+", Pattern.CASE_INSENSITIVE);
                /**Getting the current date and time to update the date purchased of the product*/
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                for(Product p : products){

                    Matcher matcher=pattern.matcher(p.getItem());
                    isEvent=matcher.find();
                    if(isEvent){

                       String[] arr= p.getItem().split("--");
                       for(String pp: arr){
                           Log.i("SSSS",pp);
                        }
                        Event event2= new Event(arr[0],arr[1],arr[2],arr[3]);
                        eventRef.child(p.getItem().replaceAll("\\d+\\/\\d+\\/\\d+","")).setValue(event2);
                    }else{
                        p.setDate(cal.getTime().toString());
                        prodRef.child(String.valueOf(UUID.randomUUID())).setValue(p);

                    }


                }
                /**Removing the items from the cart of the user once the items have been purchased*/
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(user.getUid());
                ref.removeValue();
                products.clear();
                adapter.notifyDataSetChanged();
                startActivity(new Intent(CheckoutActivityJava.this,MainActivity.class));



            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }

    /**
     * Checkout Activity MyListAdapter
     *  its purpose is to keep track of the items of the productlist as they scroll into and out of view on screen,
     *  inflate the layout xml file for ServiceActivity and populate the appropriate Textviews accordingly.
     */
    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return products.size();
        }

        public Product getItem(int position) { return products.get(position); }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View old, ViewGroup parent) {

            View newView = null;
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.payment_layout, parent, false);
            Product u = getItem(position);
            TextView item = newView.findViewById(R.id.paymentTitle);
            item.setText(String.valueOf(u.getItem()));
            TextView price = newView.findViewById(R.id.paymentPrice);
            price.setText("X "+u.getQuantity()+" @ "+String.valueOf(formatter.format(u.getPrice())));
            ImageView thumbnail = newView.findViewById(R.id.paymentImage);
            if(u.getBitmap().isEmpty()) {
                thumbnail.setImageBitmap(i1);
            }else{
                thumbnail.setImageBitmap(decodeFromStringToImage(u.getBitmap()));
            }
            //return it to be put in the table
            return newView;
        }
    }//adapter class end
}//class end