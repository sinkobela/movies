package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CrewDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("known_for_department")
    private String knownForDepartment;

    @JsonProperty("department")
    private String department;

    @JsonProperty("job")
    private String job;

    public CrewDto(String name) {
        this.name = name;
    }
}
