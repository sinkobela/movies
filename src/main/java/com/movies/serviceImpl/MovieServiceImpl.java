package com.movies.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.movies.dto.MovieDto;
import com.movies.model.MovieSearchPattern;
import com.movies.repository.MovieSearchPatternRepository;
import com.movies.service.MovieService;
import com.movies.service.OmdbService;
import com.movies.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);
    private final MovieSearchPatternRepository patternRepository;
    private final TmdbService tmdbService;
    private final OmdbService omdbService;


    public List<MovieDto> searchMovies(String title, String apiName) throws JsonProcessingException {
        logger.info("Entering MovieService searchMovies method with parameters: title={}, apiName={}", title, apiName);

        List<MovieDto> movieDtos = new ArrayList<>();
        title = title.toLowerCase();
        apiName = apiName.toLowerCase();

        if (apiName.equalsIgnoreCase("omdb")) {
            movieDtos.addAll(omdbService.searchMovie(title));
        } else if (apiName.equalsIgnoreCase("tmdb")) {
            movieDtos.addAll(tmdbService.searchMovie(title));
        } else throw new IllegalArgumentException("Invalid API Name: " + apiName);

        saveSearchPattern(title, apiName);

        return movieDtos;
    }

    private void saveSearchPattern(String title, String apiName) {
        logger.info("Entering MovieService saveSearchPattern method with title={}, apiName={}", title, apiName);

        MovieSearchPattern movieSearchPattern = patternRepository.findFirstByTitleAndApiName(title, apiName).orElse(null);

        if (movieSearchPattern == null) {
            patternRepository.save(new MovieSearchPattern(title, apiName, 1));
        } else {
            movieSearchPattern.setSearchCount(movieSearchPattern.getSearchCount() + 1);
            patternRepository.save(movieSearchPattern);
        }
    }
}
