package com.movies.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.movies.dto.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> searchMovies(String title, String apiName) throws JsonProcessingException;
}
