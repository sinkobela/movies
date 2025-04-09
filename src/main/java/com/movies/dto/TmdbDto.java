package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TmdbDto {

    @JsonProperty("results")
    List<ResultDto> results;
}
