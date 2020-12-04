package com.example.muawen;

public class Order {
    private String Name;
    private String ID;
    private String Brand;
    private String Quantity;
    private String S;
    private String Date;
    private String Time;

    public Order(String name,String status, String barcode, String quan, String id, String prod,String date,String Time) {
        Name=name;
        S = status;
        Brand = barcode;
        Quantity = quan;
        ID = id;
        Date=date;
        this.Time=Time;

    }

    public Order() {
    }

    public String getD() {
        return Date;
    }

    public void setD(String date) {
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public String getT() {
        return Time;
    }

    public void setT(String t) {
        Time = t;
    }

    public String getID() {
        return ID;
    }

    public String getQ() {
        return Quantity;
    }

    public String getB() {
        return Brand;
    }

    public String getS() {
        return S;
    }

    public void setQ(String q) {
        Quantity = q;
    }

    public void setB(String code) {
        Brand = code;
    }

    public void setS(String status) {
        S =status;
    }

    public void setID(String id) {
        this.ID = id;
    }


    public void setName(String name) {
        Name = name;
    }
    @Override
    public boolean equals(Object obj){
        if (obj == null)
            return false;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Order o = (Order) obj;
        return this.getName().equals(o.getName());
    }
}
