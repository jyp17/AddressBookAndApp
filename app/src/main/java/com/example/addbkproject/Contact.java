package com.example.addbkproject;

public class Contact {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private int id;

    public Contact(String firstName, String lastName, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = isPhoneValid(phone)? phone: "N/A";
    }

    public Contact(String firstName, String lastName, String address, String phone, int id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = isPhoneValid(phone)? phone: "N/A";
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = isPhoneValid(phone)? phone: "N/A";
    }

    public int getId() {
        return id;
    }

    public void setId() {
        this.id = id;
    }

    public boolean isPhoneValid(String phone) {
        if(phone.length() > 14 || phone.length() < 10) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  firstName + " " + lastName + "\n" + address + "\n" + phone;
    }

}
