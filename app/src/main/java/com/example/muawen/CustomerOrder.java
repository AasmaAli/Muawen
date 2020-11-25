package com.example.muawen;

import java.util.ArrayList;

public class CustomerOrder {


        private String Customer_id ;
        private String status ;
        private double totalPrice  ;
        private String date ;
        private String time ;
        private ArrayList<OrderProduct> products ;



        public  CustomerOrder(String customer_id, String status, double totalPrice , String date , String time , ArrayList<OrderProduct> products) {
            Customer_id = customer_id;
            this.status = status;
            this.totalPrice = totalPrice;
            this.date= date ;
            this.time = time;
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

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public ArrayList<OrderProduct> getProducts() {
            return products;
        }

        public void setProducts(ArrayList<OrderProduct> products) {
            this.products = products;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            date = date;
        }
        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

}
