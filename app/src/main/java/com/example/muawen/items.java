package com.example.muawen;




public class items  {
    private String Add_day;
    private String Exp_date;
    private String Product_ID;
    private double Current_wieght;
    private double Original_weight;
    private long quantity;
    private long  Current_quantity;
    private String Suggested_item;
    private String Suggestion_flag;
    private String Sensor;



    public items() {}


    public items(String add_day, String exp_date, double current_wieght, double original_weight, long quantity, long current_quantity, String product_ID, String suggested_item,String suggestion_flag, String sensor) {
        Add_day = add_day;
        Exp_date = exp_date;
        Current_wieght = current_wieght;
        Original_weight = original_weight;
        this.quantity = quantity;
        Current_quantity =  current_quantity;
        Product_ID = product_ID;
        Suggested_item= suggested_item;
        Suggestion_flag =suggestion_flag ;
        Sensor =sensor;


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

    public double getCurrent_wieght() {
        return Current_wieght;
    }

    public void setCurrent_wieght(double current_wieght) {
        Current_wieght = current_wieght;
    }

    public double getOriginal_weight() {
        return Original_weight;
    }

    public void setOriginal_weight(double original_weight) {
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

    public String getSuggested_item() {
        return Suggested_item;
    }

    public void setSuggested_item(String suggested_item) {
        Suggested_item = suggested_item;
    }

    public String getSuggestion_flag() {
        return Suggestion_flag;
    }

    public void setSuggestion_flag(String suggestion_flag) {
        Suggestion_flag = suggestion_flag;
    }

    public String getSensor() {
        return Sensor;
    }

    public void setSensor(String sensor) {
        Sensor = sensor;
    }
}
