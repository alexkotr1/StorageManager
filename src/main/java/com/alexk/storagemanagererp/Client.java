package com.alexk.storagemanagererp;

public class Client {
    private String clientName;
    private String phone;
    private String address;
    private String city;
    private String taxCode;

    public Client(String clientName, String phone, String address, String taxCode, String city) {
        this.clientName = clientName;
        this.phone = phone;
        this.address = address;
        this.taxCode = taxCode;
        this.city = city;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity(){
        return city;
    }

    public void setCity(String city){
        this.city = city;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public void saveClient(){

    }

}
