package com.example.testmock.model;

public class UserProfile {
    private String fullName;
    private String email;
    private String birthday;
    private String profileImage;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    }

    public UserProfile(String fullName, String email, String birthday, String profileImage) {
        this.fullName = fullName;
        this.email = email;
        this.birthday = birthday;
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}

