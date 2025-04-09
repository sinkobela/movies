package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchDto {

    @JsonProperty("imdbID")
    private String imdbID;

}
