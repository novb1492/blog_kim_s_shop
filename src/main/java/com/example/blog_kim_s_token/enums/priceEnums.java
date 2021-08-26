package com.example.blog_kim_s_token.enums;

public enum priceEnums {
    
    failConfrimPrice(null,0),
    sucConfrimPrice(null,0);
    private  String messege;
    private  int totalPrice;
   

    priceEnums(String messege,int totalPrice){
        this.messege=messege;
        this.totalPrice=totalPrice;
    }
    public void setMessege(String messege) {
        this.messege=messege;
    }
    public void setPrice(int errorPrice) {
        this.totalPrice=errorPrice;
    }
    public String getMessege() {
        return messege;
    }
    public int gettotalPrice() {
        return totalPrice;
    }
}
