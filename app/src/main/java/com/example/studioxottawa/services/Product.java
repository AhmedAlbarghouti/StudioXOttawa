
package com.example.studioxottawa.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Product Model Class
 * used to set the information for the products being sold in the store.
 *
 * Variables
 *     private String item
 *     private Double price
 *     private String bitmap
 */
public class Product implements Parcelable{
    private String item;
    private Double price;
    private String bitmap;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }
    public void compress(Bitmap image) {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100,out);
        byte[] bytes= out.toByteArray();
        this.bitmap= Base64.encodeToString(bytes,Base64.DEFAULT);

    }

    int quantity;

    public Product(String item, Double price, int quantity) {
        setItem(item);
        setPrice(price);
        setQuantity(quantity);
        setBitmap("");
        setDate("");
    }


    protected Product(Parcel in) {
        item = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        quantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(item);
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }
        dest.writeInt(quantity);
    }
}