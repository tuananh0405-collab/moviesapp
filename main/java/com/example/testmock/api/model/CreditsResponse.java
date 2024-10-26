package com.example.testmock.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreditsResponse {
    @SerializedName("cast")
    private List<CastMember> cast;

    @SerializedName("crew")
    private List<CrewMember> crew;

    public List<CastMember> getCast() {
        return cast;
    }

    public void setCast(List<CastMember> cast) {
        this.cast = cast;
    }

    public List<CrewMember> getCrew() {
        return crew;
    }

    public void setCrew(List<CrewMember> crew) {
        this.crew = crew;
    }
}
