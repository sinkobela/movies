package com.movies.record;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Result(String id, String title, @JsonProperty("release_date") String releaseDate) {
}
