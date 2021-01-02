package com.example.biydaalt;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String userId, name, age, email, permission, position, phone, address;

    public  User(){

    }

    public User(String name, String age, String email, String phone, String address){
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.permission = "false";
        this.position = "client";
    }

    protected User(Parcel in) {
        userId = in.readString();
        name = in.readString();
        age = in.readString();
        email = in.readString();
        permission = in.readString();
        position = in.readString();
        phone = in.readString();
        address = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(name);
        parcel.writeString(age);
        parcel.writeString(email);
        parcel.writeString(permission);
        parcel.writeString(position);
        parcel.writeString(phone);
        parcel.writeString(address);
    }
}
