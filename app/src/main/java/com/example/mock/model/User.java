package com.example.mock.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class User implements Parcelable {
    private String name;
    private String email;
    private String dob;
    private boolean isMale;
    private Bitmap avt;

    public User() {
    }

    public User(String name, String email, String dob, boolean isMale, Bitmap avt) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.isMale = isMale;
        this.avt = avt;
    }

    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        dob = in.readString();
        isMale = in.readByte() != 0;
        avt = in.readParcelable(Bitmap.class.getClassLoader());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public Bitmap getAvt() {
        return avt;
    }

    public void setAvt(Bitmap avt) {
        this.avt = avt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(dob);
        parcel.writeByte((byte) (isMale ? 1 : 0));
        parcel.writeParcelable(avt, i);
    }
}
