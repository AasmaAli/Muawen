package com.example.muawen;

import java.util.ArrayList;

public class Order {

    private String Customer_id ;
    private String status ;
    private int totalPrice  ;
    private ArrayList <Product> products ;


    public Order(String customer_id, String status, int totalPrice, ArrayList<Product> products) {
        Customer_id = customer_id;
        this.status = status;
        this.totalPrice = totalPrice;
        this.products = products;
    }

    public String getCustomer_id() {
        return Customer_id;
    }

    public void setCustomer_id(String customer_id) {
        Customer_id = customer_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}