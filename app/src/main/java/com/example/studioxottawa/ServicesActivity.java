package com.example.studioxottawa;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ServicesActivity extends AppCompatActivity {

    private ServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        adapter = new ServiceAdapter();

        RecyclerView servicesView = findViewById(R.id.serviceContainer);


        servicesView.setHasFixedSize(true);
        servicesView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL));
        servicesView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        servicesView.setAdapter(adapter);

        Bitmap i1 = BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.tshirt);
        Bitmap i2 = BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.towel);
        Bitmap i3 = BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.water);
        Bitmap i4 = BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.shoes);

        Product p1 = new Product(i1,"T-Shirt",42.50);
        Product p2 = new Product(i2,"Towel",10.00);
        Product p3 = new Product(i3,"Bottled Water",3.50);
        Product p4 = new Product(i4,"Dance Shoes",80.00);

        adapter.add(p1);
        adapter.add(p2);
        adapter.add(p3);
        adapter.add(p4);
    }

    private class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

        private ArrayList<Product> productList = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView thumbnailView;
            private final TextView itemView;
            private final TextView priceView;

            public ViewHolder(View view) {
                super(view);
                /*view.setOnClickListener(v->{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                    LayoutInflater inflater = LayoutInflater.from(getBaseContext());
                    builder.setTitle(R.string.PurchaseTitle);
                    View purchaseView = inflater.inflate(R.layout.service_layout, null);



                    builder.setView(purchaseView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    Toast confirm = Toast.makeText(getApplicationContext(), R.string.PurchaseConfirm, Toast.LENGTH_SHORT);
                                }
                            }, 1000);
                        }
                    });
                    AlertDialog purchasePopup = builder.create();

                    ImageView SI = purchasePopup.findViewById(R.id.serviceImage);
                    BitmapDrawable BD = (BitmapDrawable)getThumbnailView().getDrawable();
                    SI.setImageBitmap(BD.getBitmap());
                    TextView ST = purchasePopup.findViewById(R.id.serviceTitle);
                    ST.setText(getItemView().getText());
                    TextView SP = purchasePopup.findViewById(R.id.servicePrice);
                    SP.setText((getPriceView().getText()));

                    purchasePopup.create();
                });*/

                thumbnailView = (ImageView) view.findViewById(R.id.serviceImage);
                itemView = (TextView) view.findViewById(R.id.serviceTitle);
                priceView = (TextView) view.findViewById(R.id.servicePrice);

            }

            public ImageView getThumbnailView() { return thumbnailView; }

            public TextView getItemView() { return itemView; }

            public TextView getPriceView() { return priceView; }


        }

        public void add(Product p) {
            productList.add(p);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_layout, parent, false);
            return new ViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.getThumbnailView().setImageBitmap(productList.get(position).getThumbnail());
            holder.getItemView().setText((productList.get(position).getItem()));
            holder.getPriceView().setText(String.valueOf(productList.get(position).getPrice()));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

    }
}