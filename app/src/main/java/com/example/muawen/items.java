package com.example.muawen;




public class items  {
    private String Add_day;
    private String Exp_date;
    private String Product_ID;
    private long Current_wieght;
    private long Original_weight;
    private long quantity;
    private long  Current_quantity;



    public items(){}

    public items(String add_day, String exp_date, long current_wieght, long original_weight, long quantity, long current_quantity, String product_ID) {
        Add_day = add_day;
        Exp_date = exp_date;
        Current_wieght = current_wieght;
        Original_weight = original_weight;
        this.quantity = quantity;
        Current_quantity =  current_quantity;
        Product_ID = product_ID;
    }

    public String getAdd_day() {
        return Add_day;
    }

    public void setAdd_day(String add_day) {
        Add_day = add_day;
    }

    public String getExp_date() {
        return Exp_date;
    }

    public void setExp_date(String exp_date) {
        Exp_date = exp_date;
    }

    public long getCurrent_wieght() {
        return Current_wieght;
    }

    public void setCurrent_wieght(long current_wieght) {
        Current_wieght = current_wieght;
    }

    public long getOriginal_weight() {
        return Original_weight;
    }

    public void setOriginal_weight(long original_weight) {
        Original_weight = original_weight;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getCurrent_quantity() { return Current_quantity; }

    public void setCurrent_quantity(long current_quantity) { Current_quantity = current_quantity; }

    public String getProduct_ID() { return Product_ID; }

    public void setProduct_ID(String product_ID) { Product_ID = product_ID; }

}
