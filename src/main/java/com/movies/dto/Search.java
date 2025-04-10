package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Search(@JsonProperty("imdbID") String imdbID) {
}
