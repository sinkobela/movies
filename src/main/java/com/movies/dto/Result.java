package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Result(String id, String title, @JsonProperty("release_date") String releaseDate) {
}
