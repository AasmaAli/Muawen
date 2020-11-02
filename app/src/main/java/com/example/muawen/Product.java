package com.example.muawen;

public class Product {



        String Brand ;
        String Name ;
        String size ;
        double Price ;


        public Product( String Brand, String Name, String size, double Price) {

            this.Brand = Brand;
            this.Name = Name;
            this.size = size;
            this.Price = Price;
        }

    public Product() {
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

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public double getPrice() {
            return Price;
        }

        public void setPrice(double price) {
            Price = price;
        }
    

}
