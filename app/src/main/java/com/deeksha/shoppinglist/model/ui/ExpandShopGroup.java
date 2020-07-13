package com.deeksha.shoppinglist.model.ui;

import com.deeksha.shoppinglist.model.common.ShopItem;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExpandShopGroup implements Comparable<ExpandShopGroup>, Serializable {

    private String shoppingId;
    private String date;
    private String name;
    private ArrayList<ShopItem> Items;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShoppingId() {
        return shoppingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShoppingId(String shoppingId) {
        this.shoppingId = shoppingId;
    }

    public ArrayList<ShopItem> getItems() {
        return Items;
    }

    public void setItems(ArrayList<ShopItem> Items) {
        this.Items = Items;
    }

    @Override
    public int compareTo(ExpandShopGroup expandShopGroup) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy'T'HH:mm:ssZ");
        try {
            return sdf.parse(getDate()).compareTo(sdf.parse(expandShopGroup.getDate()));
        } catch (ParseException e) {
            return 1;
        }
    }
}

