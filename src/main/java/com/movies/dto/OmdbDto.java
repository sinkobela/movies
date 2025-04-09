package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OmdbDto {

    @JsonProperty("Search")
    List<SearchDto> search;

}
