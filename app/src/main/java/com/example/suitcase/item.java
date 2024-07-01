package com.example.suitcase;

public class item {

    private String itemName;
    private String itemPrice;
    private String itemDescription;
    private String itemImage;
    private String itemID;
    private boolean isChecked;




    public item(String itemName, String itemPrice, String itemDescription, String itemImage , String itemID, boolean isChecked) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemDescription = itemDescription;
        this.itemImage = itemImage;
        this.itemID = itemID;
        this.isChecked = isChecked;

    }
    public item(){

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
    public void setItemID(String itemID) {
        this.itemID = itemID;
    }
    public String getItemID() {
        return itemID;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


}



