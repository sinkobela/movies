package com.movies.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movies.dto.Movie;
import com.movies.dto.OmdbRecord;
import com.movies.dto.Search;
import com.movies.service.ExternalApiService;
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
public class OmdbServiceImpl implements ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(OmdbServiceImpl.class);
    private final ObjectMapper objectMapper;
    @Value("${omdb.url}")
    private String baseUrl;
    @Value("${omdb.apikey}")
    private String apiKey;


    public List<Movie> searchMovie(String title) throws JsonProcessingException {
        logger.info("Entering OmdbService searchMovie method with title {}", title);

        OmdbRecord omdb = searchByKeyword(title);

        return searchByImdbId(omdb);
    }

    private OmdbRecord searchByKeyword(String title) throws JsonProcessingException {
        logger.info("Entering OmdbService searchByKeyword method with title {}", title);
        RestTemplate restTemplate = new RestTemplate();

        String url = createUrlForSearchByTitle(title);
        String jsonResponse = restTemplate.getForObject(url, String.class);

        return mapJsonToOmdbRecord(jsonResponse);
    }

    private String createUrlForSearchByTitle(String title) {
        logger.info("Entering OmdbService createUrlForSearchByTitle method with title {}", title);

        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("s", title)
                .queryParam("apikey", apiKey)
                .build()
                .toUriString();
    }

    private OmdbRecord mapJsonToOmdbRecord(String json) throws JsonProcessingException {
        logger.info("Entering OmdbService mapJsonToOmdbRecord method");

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

    private List<Movie> searchByImdbId(OmdbRecord omdb) throws JsonProcessingException {
        logger.info("Entering OmdbService searchByImdbId method");
        RestTemplate restTemplate = new RestTemplate();
        List<Movie> movies = new ArrayList<>();

        for (Search search : omdb.search()) {
            String url = createUrlForSearchByImdbId(search.imdbID());
            String jsonResponse = restTemplate.getForObject(url, String.class);
            movies.add(mapJsonToMovie(jsonResponse));
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

    private Movie mapJsonToMovie(String json) throws JsonProcessingException {
        logger.info("Entering OmdbService mapJsonToMovie method");

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }
}
