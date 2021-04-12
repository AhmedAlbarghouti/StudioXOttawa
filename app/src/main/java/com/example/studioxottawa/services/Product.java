
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
 *     private String item:  the product name
 *     private Double price: the price associated with the product
 *     private String bitmap: the image associated with the product
 *     private string date : returns the date the product was purchased.
 */
public class Product implements Parcelable{
    private String item;
    private Double price;
    private String bitmap;
    private String date;
    private int quantity;

    /**
     * the retrieve the private variable for the date of purchase
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     * sets the purchases date of the product
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * returns the based 64 bitmap string of the item
     * @return bitmap
     */
    public String getBitmap() {
        return bitmap;
    }

    /**
     * Sets the bitmap param of the object
     * @param bitmap
     */
    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * converts a bitmap image passed through its parameter to a string based 64 text.
     * @param image
     */
    public void compress(Bitmap image) {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100,out);
        byte[] bytes= out.toByteArray();
        this.bitmap= Base64.encodeToString(bytes,Base64.DEFAULT);

    }



    /**
     * Product constructior method used the initialize the private variables of the class
     * @param item
     * @param price
     * @param quantity
     */
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

    /**
     * retrieve the value of the item variable
     * @return
     */
    public String getItem() {
        return item;
    }

    /**
     * sets the value of the item variable
     * @param item
     */
    public void setItem(String item) {
        this.item = item;
    }
    /**
     *retrieve the value price variable
     * @return price
     */
    public Double getPrice() {
        return price;
    }

    /**
     *sets the value price variable
     * @param price
     */
    public void setPrice(Double price) {
        this.price = price;
    }
    /**
     * Retrieve the value of quantity variable
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * sets the value of the quantity variable
     * @param quantity
     */

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