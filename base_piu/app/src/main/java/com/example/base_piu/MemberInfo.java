package com.example.base_piu;

public class MemberInfo {
    String name;
    String phoneNumber;
    String birthday;
    String address;
    String downloadUri;

    public MemberInfo(String name, String phoneNumber, String birthday, String address, String downloadUri) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.address = address;
        this.downloadUri = downloadUri;
    }

    public MemberInfo(String name, String phoneNumber, String birthday, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


    public String getBirthday() {
        return birthday;
    }


    public String getAddress() {
        return address;
    }
}
