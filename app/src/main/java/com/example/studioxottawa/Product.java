package com.example.studioxottawa;

import android.graphics.Bitmap;

public class Product {
    Bitmap thumbnail;
    String item;
    Double price;

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
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

    public Product(Bitmap thumbnail, String item, Double price) {
        setThumbnail(thumbnail);
        setItem(item);
        setPrice(price);
    }
}
