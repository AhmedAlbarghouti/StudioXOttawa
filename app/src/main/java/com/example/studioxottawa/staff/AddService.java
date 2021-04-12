package com.example.studioxottawa.staff;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import java.io.FileNotFoundException;
import java.io.InputStream;

/***
 * Add Service Class
 * this class allows admin to the add new products and services to the database.
 * Variables
 *     private EditText productName
 *     private EditText productPrice
 *     private Button add_product
 *     private ImageView productImage
 *     private Bitmap imageBitmap
 *     private static final int GO_TO_GALLERY
 */
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
        Button img= findViewById(R.id.imageSelectBtn);
        img.setOnClickListener(click->{openGallery();});
        imageBitmap=BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.big_logo);

        productImage.setOnClickListener(btn->{
            openGallery();
        });

        add_product.setOnClickListener(btn->{
            createProduct();


        });
    }



    /***
     * this method accepts the admin inputs and uses them to create a new product in the database.
     */
    public void  createProduct(){
        String item= productName.getText().toString();
        String price= productPrice.getText().toString();

        if(!item.isEmpty()  && !(price.contains("[a-zA-Z]+") )&& !(price.isEmpty())) {

            Product newProduct = new Product(item, Double.parseDouble(price), 0);
            if(!imageBitmap.equals(null)) {

                newProduct.compress(imageBitmap);
            }

            DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Products");
            eventsReference.child(newProduct.getItem()).setValue(newProduct);
            Toast.makeText(this, getString(R.string.successful_product_adding),Toast.LENGTH_SHORT).show();
            finish();
        }else{

            Toast.makeText(this, getString(R.string.invalid_input),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * this method opens the phone gallery and allows user to select an image to be used for
     * the product being adapted
     */
    private void openGallery() {
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GO_TO_GALLERY);
    }

    /***
     * this method upon returning to the app  after the image is selected it sets the image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("RESULT CODE",String.valueOf(resultCode));
        if(resultCode==RESULT_OK ){
             Uri image = data.getData();
             InputStream imageStream=null;
            try {
                imageStream = getContentResolver().openInputStream(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
             Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            productImage.setImageBitmap(selectedImage);
            imageBitmap=selectedImage;
        }
    }

}