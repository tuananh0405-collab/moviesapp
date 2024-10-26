package com.example.mock.model.response;

import com.example.mock.model.Cast;
import com.example.mock.model.Crew;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastResponse {

    @SerializedName("cast")
    private List<Cast> castList;

    @SerializedName("crew")
    private List<Crew> crewList;

    public List<Cast> getCastList() {
        return castList;
    }

    public void setCastList(List<Cast> castList) {
        this.castList = castList;
    }

    public List<Crew> getCrewList() {
        return crewList;
    }

    public void setCrewList(List<Crew> crewList) {
        this.crewList = crewList;
    }
}
