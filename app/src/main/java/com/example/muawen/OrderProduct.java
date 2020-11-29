package com.example.muawen;

public class OrderProduct {
    private  String Brand ;
    private  String Name ;
    private int quantity ;

    private long size ;
private double price ;


    public OrderProduct(String brand, String name, int quantity,  long size,double price) {
        Brand = brand;
        Name = name;
        this.quantity = quantity;
this.price = price ;
        this.size = size;
    }



    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getSize() { return size; }

    public void setSize(long size) { this.size = size; }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
