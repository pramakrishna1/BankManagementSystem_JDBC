package com.bank.model;

public class BankDetails {
    private int id;
    private String accNo;
    private String name;
    private long amount;
    private String phoneNumber;
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BankDetails{" +
                "Account number ='" + accNo + '\'' +
                ", Name ='" + name + '\'' +
                ", Amount = Rs." + amount +
                "/-, Phone number ='" + phoneNumber + '\'' +
                ", Address ='" + address + '\'' +
                '}';
    }
}
