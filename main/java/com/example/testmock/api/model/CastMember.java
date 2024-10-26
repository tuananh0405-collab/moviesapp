package com.example.testmock.api.model;

import com.google.gson.annotations.SerializedName;

public class CastMember {
    @SerializedName("name")
    private String name;

    @SerializedName("character")
    private String character;

    @SerializedName("profile_path")
    private String profilePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}
