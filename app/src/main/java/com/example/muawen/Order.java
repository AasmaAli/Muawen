package com.example.muawen;

public class Order {
    private String Name;
    private String ID;
    private String P;
    private String B;
    private String Q;
    private String S;
    private String D;
    private String T;

    public Order(String name,String status, String barcode, String quan, String id, String prod,String date,String Time) {
        Name=name;
        S = status;
        B = barcode;
        Q = quan;
        ID = id;
        P = prod;
        D=date;
        T=Time;

    }

    public Order() {
    }

    public String getD() {
        return D;
    }

    public void setD(String date) {
        D = date;
    }

    public String getName() {
        return Name;
    }

    public String getT() {
        return T;
    }

    public void setT(String t) {
        T = t;
    }

    public String getID() {
        return ID;
    }

    public String getP() {
        return P;
    }

    public String getQ() {
        return Q;
    }

    public String getB() {
        return B;
    }

    public String getS() {
        return S;
    }

    public void setQ(String q) {
        Q = q;
    }

    public void setB(String code) {
        B = code;
    }

    public void setS(String status) {
        S =status;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public void setP(String p) {
        P = p;
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
