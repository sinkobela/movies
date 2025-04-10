package com.movies.controller;

import com.movies.dto.Movie;
import com.movies.service.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @InjectMocks
    private MovieController movieController;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MovieService movieService;


    @Test
    public void testSearchMovies_expectSuccess() throws Exception {
        List<Movie> movies = getMovies();

        when(movieService.searchMovies("Potter", "OMDB")).thenReturn(movies);

        performSearchByTitle("Potter", "OMDB");

        verify(movieService, times(1)).searchMovies("Potter", "OMDB");
    }

    @Test
    public void testSearchMovies_expectNoResults() throws Exception {
        when(movieService.searchMovies("Nonexistent Movie", "TMDB")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/movies/{movieTitle}", "Nonexistent Movie")
                        .param("apiName", "TMDB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(movieService, times(1)).searchMovies("Nonexistent Movie", "TMDB");
    }

    private void performSearchByTitle(String title, String apiName) throws Exception {
        mockMvc.perform(get("/movies/{movieTitle}", "Potter")
                        .param("apiName", "OMDB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Harry Potter and the Philosopher's Stone"))
                .andExpect(jsonPath("$[1].title").value("Harry Potter and the Chamber of Secrets"))
                .andExpect(jsonPath("$[2].title").value("Harry Potter and the Prisoner of Azkaban"));
    }

    private List<Movie> getMovies() {
        Movie philosophersStone = new Movie("Harry Potter and the Philosopher's Stone", "2001", "Chris Columbus");
        Movie chamberOfSecrets = new Movie("Harry Potter and the Chamber of Secrets", "2002", "Chris Columbus");
        Movie prisonerOfAzkaban = new Movie("Harry Potter and the Prisoner of Azkaban", "2004", "Alfonso Cuarón");
        return List.of(philosophersStone, chamberOfSecrets, prisonerOfAzkaban);
    }
}
