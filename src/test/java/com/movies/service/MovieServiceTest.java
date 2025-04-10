package com.movies.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.movies.dto.Movie;
import com.movies.model.MovieSearchPattern;
import com.movies.repository.MovieSearchPatternRepository;
import com.movies.serviceImpl.MovieServiceImpl;
import com.movies.serviceImpl.OmdbServiceImpl;
import com.movies.serviceImpl.TmdbServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MovieServiceTest {

    @Autowired
    private MovieServiceImpl movieService;
    @MockitoBean
    private OmdbServiceImpl omdbService;
    @MockitoBean
    private TmdbServiceImpl tmdbService;
    @MockitoBean
    private MovieSearchPatternRepository patternRepository;

    private final Movie sampleMovie = new Movie("Inception", "2010", "Christopher Nolan");

    @Test
    void searchMovies_shouldUseOmdbService_whenApiNameIsOmdb() throws JsonProcessingException {
        when(omdbService.searchMovie("inception")).thenReturn(List.of(sampleMovie));
        when(patternRepository.findFirstByTitleAndApiName("inception", "omdb"))
                .thenReturn(Optional.of(new MovieSearchPattern("inception", "omdb", 1)));

        List<Movie> result = movieService.searchMovies("Inception", "OMDB");

        verify(omdbService).searchMovie("inception");
        verify(patternRepository).save(any(MovieSearchPattern.class));
        assertThat(result).containsExactly(sampleMovie);
    }

    @Test
    void searchMovies_shouldUseTmdbService_whenApiNameIsTmdb() throws JsonProcessingException {
        when(tmdbService.searchMovie("inception")).thenReturn(List.of(sampleMovie));
        when(patternRepository.findFirstByTitleAndApiName("inception", "tmdb"))
                .thenReturn(Optional.empty());

        List<Movie> result = movieService.searchMovies("Inception", "TMDB");

        verify(tmdbService).searchMovie("inception");
        verify(patternRepository).save(any(MovieSearchPattern.class));
        assertThat(result).containsExactly(sampleMovie);
    }

    @Test
    void searchMovies_shouldThrowException_whenApiNameIsInvalid() {
        String invalidApi = "invalid-api";

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> movieService.searchMovies("Inception", invalidApi));

        assertThat(exception.getMessage()).contains("Invalid API Name");
        verifyNoInteractions(omdbService, tmdbService);
    }

    @Test
    void saveSearchPattern_shouldIncrementCount_whenPatternExists() throws JsonProcessingException {
        MovieSearchPattern existing = new MovieSearchPattern("inception", "omdb", 1);
        when(patternRepository.findFirstByTitleAndApiName("inception", "omdb"))
                .thenReturn(Optional.of(existing));
        when(omdbService.searchMovie("inception")).thenReturn(List.of(sampleMovie));

        movieService.searchMovies("inception", "omdb");

        ArgumentCaptor<MovieSearchPattern> captor = ArgumentCaptor.forClass(MovieSearchPattern.class);
        verify(patternRepository).save(captor.capture());

        assertThat(captor.getValue().getSearchCount()).isEqualTo(2);
    }
}
