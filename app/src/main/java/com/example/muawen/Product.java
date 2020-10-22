package com.example.muawen;

public class Product {



        String Brand ;
        String Name ;
        String size ;
        int Price ;


        public Product( String Brand, String Name, String size, int Price) {

            this.Brand = Brand;
            this.Name = Name;
            this.size = size;
            this.Price = Price;
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

        public int getPrice() {
            return Price;
        }

        public void setPrice(int price) {
            Price = price;
        }
    

}
