package com.movies.record;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Search(@JsonProperty("imdbID") String imdbID) {
}
