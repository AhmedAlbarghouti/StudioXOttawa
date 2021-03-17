package com.example.studioxottawa.Checkout;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.content.Intent;
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
import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.schedule.Schedule;
import com.example.studioxottawa.services.Product;
import com.example.studioxottawa.services.ServicesActivity;
import com.example.studioxottawa.welcome.MainActivity;
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
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckoutActivityJava extends AppCompatActivity {
    // 10.0.2.2 is the Android emulator's alias to localhost
    private static final String BACKEND_URL = "https://still-everglades-60303.herokuapp.com/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret = "sk_test_51ILUoQJBRyYbLiOnRqvcjq1l6erjzTKcz7FpqnM1AZ6Phfh2NM4r8wjA0kqSldEHhjwUyTLnAB0qHRlwLxtUKoaQ00YSQCu08T";
    private Stripe stripe;

    private double price = 0;
//    private static Intent intent= getIntent().getParcelableExtra("EventObj");


    private ArrayList<Product> products= new ArrayList<>();
    ArrayList<String> productsPurchase;
    private NumberFormat formatter = new DecimalFormat("#0.00");
//
//    private  static ArrayList<String> products= new ArrayList<String>();
//    private MyListAdapter myAdapter;
    private MyListAdapter adapter;
    private AlertDialog.Builder builder;
    private ListView servicesView;
    private Bitmap i1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_java);
        // Configure the SDK with your Stripe publishable key so it can make requests to Stripe
        price = getIntent().getExtras().getDouble("Total Price");

        productsPurchase = getIntent().getStringArrayListExtra("forPay");
        servicesView = findViewById(R.id.ItemPurchView);
        servicesView.setAdapter(adapter = new MyListAdapter());
        i1 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.logo_studioxottawa);

        servicesView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.notifyDataSetChanged();
        });


        loadProducts();
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51ILUoQJBRyYbLiOnhQMkiSrSTRnoRK6Py4gWV6rIXfPCWreERj4gb3B13wur8jzi3ZfL2mzGBPOItwABmqoAQLKk00vLxexHqx")
        );


        startCheckout();
    }

    public void loadProducts(){
        for (String s : productsPurchase) {
            DatabaseReference referenceProduct = FirebaseDatabase.getInstance().getReference().child("Products").child(s);
            referenceProduct.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String item = String.valueOf(snapshot.child("item").getValue());
                    String price = String.valueOf(snapshot.child("price").getValue());
                    String quantity = String.valueOf(snapshot.child("quantity").getValue());
                    Product temp = new Product( item, Double.parseDouble(price), 1);

                    products.add(temp);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        adapter.notifyDataSetChanged();

    }
    private void startCheckout() {
        // Create a PaymentIntent by calling the server's endpoint.

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        Map<String, Object> payMap = new HashMap<>();
        Map<String, Object> itemMap = new HashMap<>();
        List<Map<String, Object>> itemList = new ArrayList<>();
        payMap.put("currency", "usd");
        itemMap.put("id", "photo_subscription");
        TextView amountText = findViewById(R.id.amountText);

        amountText.setText(String.valueOf(formatter.format(price)));
        double amount = price * 100;
        itemMap.put("amount", amount);
        itemMap.put("email", "jamesRunnings@gmail.com");
        itemList.add(itemMap);
        payMap.put("items", itemList);

        String json = new Gson().toJson(payMap);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));
        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);


//                                                   FIX WHAT'S IN THE BOX. ITS THROWING A NULL EXCEPTION. Do more research
//                                                   -----------------------------------------------------------------------
//               -----------------------------------------------------------------------------------------------------------------------------------------------------------|
//              |  ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);            |
//              |  stripe.confirmPayment(this, confirmParams);                                                                                                              |
//              | -----------------------------------------------------------------------------------------------------------------------------------------------------------
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
        // Handle the result of stripe.confirmPayment
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
                    Toast.makeText(activity, "Error: " + e.toString(), Toast.LENGTH_LONG).show());
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
                        Toast.makeText(activity, "Error: " + response.toString(), Toast.LENGTH_LONG).show());
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
                Boolean isEvent =getIntent().getExtras().getBoolean("isService");


                activity.displayAlert(
                        "Payment completed", "Payment Successful! Thank you"
//                        gson.toJson(paymentIntent)
                );
                if(isEvent){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Users");
                    eventsReference.child(user.getUid()).child("Events Purchased").child(event.getUid()).setValue(event);
                    Intent schedule = new Intent(CheckoutActivityJava.this, MainActivity.class);
                    startActivity(schedule);
                }else if(!isEvent){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Users");
                    for(Product p: products) {
                        eventsReference.child(user.getUid()).child("Products Purchased").child(p.getItem()).setValue(p);
                    }
                    Intent mainAct = new Intent(CheckoutActivityJava.this, MainActivity.class);
                    startActivity(mainAct);
                }
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

    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return products.size();
        }

        public Product getItem(int position) { return products.get(position); }

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