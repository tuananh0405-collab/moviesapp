package com.example.testmock.api.model;

import com.google.gson.annotations.SerializedName;

public class CrewMember {
    @SerializedName("name")
    private String name;

    @SerializedName("department")
    private String department;

    @SerializedName("job")
    private String job;

    // Add other relevant fields as needed

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
