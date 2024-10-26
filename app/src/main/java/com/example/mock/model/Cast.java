package com.example.mock.model;

import com.google.gson.annotations.SerializedName;

public class Cast {

    @SerializedName("name")
    private String name;

    @SerializedName("profile_path")
    private String profilePath;

    @SerializedName("character")
    private String character;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}
