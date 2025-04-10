package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Crew(String name, @JsonProperty("known_for_department") String knownForDepartment, String department, String job) {

    public Crew(String name) {
        this(name, null, null, null);
    }
}
