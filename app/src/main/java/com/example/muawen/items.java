package com.example.muawen;

public class items {
    private String Add_day , Exp_date;
    private int Current_wieght;
    private int Original_weight;

    public items(String add_day, String exp_date, int current_wieght, int original_weight, int quantity) {
        Add_day = add_day;
        Exp_date = exp_date;
        Current_wieght = current_wieght;
        Original_weight = original_weight;
        this.quantity = quantity;
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

    public int getCurrent_wieght() {
        return Current_wieght;
    }

    public void setCurrent_wieght(int current_wieght) {
        Current_wieght = current_wieght;
    }

    public int getOriginal_weight() {
        return Original_weight;
    }

    public void setOriginal_weight(int original_weight) {
        Original_weight = original_weight;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private int quantity;




}
