package com.example.bloodbank.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lokesh chennamchetty
 * @date 21/09/2019
 */

public class User implements Parcelable, Serializable {

    private String userName;
    private String email;
    private String phoneNumber;
    private String age;
    private String state;
    private String district;
    private String city;
    private String bloodGroup;
    private String password;
    private Date birthDate;
    private boolean isMale;
    private boolean isAvailableInCaseOfEmergency;

    public User(){}


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isAvailableInCaseOfEmergency() {
        return isAvailableInCaseOfEmergency;
    }

    public void setAvailableInCaseOfEmergency(boolean availableInCaseOfEmergency) {
        isAvailableInCaseOfEmergency = availableInCaseOfEmergency;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(email);
        parcel.writeString(phoneNumber);
        parcel.writeString(age);
        parcel.writeString(state);
        parcel.writeString(district);
        parcel.writeString(city);
        parcel.writeString(bloodGroup);
        parcel.writeByte((byte) (isMale ? 1 : 0));
        parcel.writeByte((byte) (isAvailableInCaseOfEmergency ? 1 : 0));
    }

    protected User(Parcel in) {
        userName = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        age = in.readString();
        state = in.readString();
        district = in.readString();
        city = in.readString();
        bloodGroup = in.readString();
        isMale = in.readByte() != 0;
        isAvailableInCaseOfEmergency = in.readByte() != 0;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
