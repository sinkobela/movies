package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OmdbRecord(@JsonProperty("Search") List<Search> search) {
}
