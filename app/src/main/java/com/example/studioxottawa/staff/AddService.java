package com.example.studioxottawa.staff;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.studioxottawa.R;
import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class AddService extends AppCompatActivity {
    private EditText productName,productPrice;
    private Button add_product;
    private ImageView productImage;
    private Bitmap imageBitmap;
    private static final int GO_TO_GALLERY=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        productName=(EditText)findViewById(R.id.new_product_name);
        productPrice=(EditText)findViewById(R.id.new_product_price);
        productImage=(ImageView)findViewById(R.id.new_product_image);
        add_product=(Button)findViewById(R.id.AddProductBtn);
        imageBitmap=BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.logo_studioxottawa);


        add_product.setOnClickListener(btn->{
            createProduct();
            Toast.makeText(this,"Product Successfully Added",Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public void  createProduct(){
        String item= productName.getText().toString();
        String price= productPrice.getText().toString();


        Product newProduct= new Product(BitMapToString(imageBitmap),item,Double.parseDouble(price),0);


        DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Products");
        eventsReference.child(newProduct.getItem()).setValue(newProduct);
    }
//    private void openGallery() {
//        Intent intent= new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(intent,GO_TO_GALLERY);
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==GO_TO_GALLERY && resultCode==RESULT_OK && data !=null){
//            Bitmap image= (Bitmap) data.getExtras().get("data");
//            productImage.setImageBitmap(image);
//            imageBitmap=image;
//        }
//    }
}