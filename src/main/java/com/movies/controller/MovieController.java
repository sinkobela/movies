package com.movies.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.movies.dto.MovieDto;
import com.movies.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/movies")
@RequiredArgsConstructor
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final MovieService movieService;

    @Cacheable(value = "movieCache")
    @GetMapping(value = "/{movieTitle}")
    public List<MovieDto> searchMovies(@PathVariable String movieTitle, @RequestParam String apiName) throws JsonProcessingException {
        logger.info("Entering MovieController searchMovies method with parameters: movieTitle={}, apiName={}",
                movieTitle, apiName);

        return movieService.searchMovies(movieTitle, apiName);
    }
}
