package com.movies.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.movies.dto.Movie;

import java.util.List;

public interface ExternalApiService {

    List<Movie> searchMovie(String title) throws JsonProcessingException;
}
