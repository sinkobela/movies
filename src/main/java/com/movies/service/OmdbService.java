package com.movies.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.movies.dto.MovieDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OmdbService {

    List<MovieDto> searchMovie(String title) throws JsonProcessingException;
}
