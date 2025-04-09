package com.movies.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movies.dto.MovieDto;
import com.movies.dto.OmdbDto;
import com.movies.dto.SearchDto;
import com.movies.service.OmdbService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OmdbServiceImpl implements OmdbService {

    private static final Logger logger = LoggerFactory.getLogger(OmdbServiceImpl.class);
    private final ObjectMapper objectMapper;
    @Value("${omdb.url}")
    private String baseUrl;
    @Value("${omdb.apikey}")
    private String apiKey;


    public List<MovieDto> searchMovie(String title) throws JsonProcessingException {
        logger.info("Entering OmdbService searchMovie method with title {}", title);

        OmdbDto omdbDto = searchByKeyword(title);

        return searchByImdbId(omdbDto);
    }

    private OmdbDto searchByKeyword(String title) throws JsonProcessingException {
        logger.info("Entering OmdbService searchByKeyword method with title {}", title);
        RestTemplate restTemplate = new RestTemplate();

        String url = createUrlForSearchByTitle(title);
        String jsonResponse = restTemplate.getForObject(url, String.class);

        return mapJsonToOmdbDto(jsonResponse);
    }

    private String createUrlForSearchByTitle(String title) {
        logger.info("Entering OmdbService createUrlForSearchByTitle method with title {}", title);

        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("s", title)
                .queryParam("apikey", apiKey)
                .build()
                .toUriString();
    }

    private OmdbDto mapJsonToOmdbDto(String json) throws JsonProcessingException {
        logger.info("Entering OmdbService mapJsonToOmdbDto method");

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

    private List<MovieDto> searchByImdbId(OmdbDto omdbDto) throws JsonProcessingException {
        logger.info("Entering OmdbService searchByImdbId method");
        RestTemplate restTemplate = new RestTemplate();
        List<MovieDto> movies = new ArrayList<>();

        for (SearchDto searchDto : omdbDto.getSearch()) {
            String url = createUrlForSearchByImdbId(searchDto.getImdbID());
            String jsonResponse = restTemplate.getForObject(url, String.class);
            movies.add(mapJsonToMovieDto(jsonResponse));
        }
        return movies;
    }

    private String createUrlForSearchByImdbId(String imdbId) {
        logger.info("Entering OmdbService createUrlForSearchByImdbId method with imdbId {}", imdbId);

        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("i", imdbId)
                .queryParam("apikey", apiKey)
                .build()
                .toUriString();
    }

    private MovieDto mapJsonToMovieDto(String json) throws JsonProcessingException {
        logger.info("Entering OmdbService mapJsonToMovieDto method");

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }
}
