
package com.example.studioxottawa.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Product {
    String thumbnail;
    String item;
    Double price;
    int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        setThumbnail(temp);
        return temp;
    }

    public Bitmap getBitmap(){
        try{
            byte [] encodeByte = Base64.decode(getThumbnail(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public Product(String thumbnail, String item, Double price,int quantity) {
        setThumbnail(thumbnail);
        setItem(item);
        setPrice(price);
        setQuantity(quantity);
    }
    public Product(String item, Double price,int quantity) {
        setItem(item);
        setPrice(price);
        setQuantity(quantity);
    }
}