package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreditsDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("crew")
    private List<CrewDto>  crew;
}
