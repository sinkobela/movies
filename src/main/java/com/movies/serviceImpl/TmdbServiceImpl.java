package com.movies.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movies.dto.*;
import com.movies.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class TmdbServiceImpl implements ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(TmdbServiceImpl.class);
    private final ObjectMapper objectMapper;
    @Value("${tmdb.url}")
    private String baseUrl;
    @Value("${tmdb.api.key}")
    private String apiKey;
    @Value("${tmdb.api.read.access.token}")
    private String bearerToken;


    public List<Movie> searchMovie(String title) throws JsonProcessingException {
        logger.info("Entering TmdbService searchMovie method with title {}", title);

        List<Result> results = searchByTitle(title).results();
        List<Credits> credits = searchById(results);
        Map<Result, Credits> map = pairResultsWithCredits(results, credits);

        return getMovies(map);
    }

    private Map<Result, Credits> pairResultsWithCredits(List<Result> results, List<Credits> creditsList) {
        logger.info("Entering TmdbService pairResultsWithCredits method");
        Map<Result, Credits> pairedMap = new HashMap<>();

        Map<String, Credits> creditsMap = creditsList.stream()
                .collect(toMap(Credits::id, credit -> credit));

        for (Result result : results) {
            Credits matchingCredit = creditsMap.get(result.id());
            if (matchingCredit != null) {
                pairedMap.put(result, matchingCredit);
            }
        }
        return pairedMap;
    }

    private List<Movie> getMovies(Map<Result, Credits> map) {
        logger.info("Entering TmdbService getMovies method");
        List<Movie> movies = new ArrayList<>();

        for (Result result : map.keySet()) {
            String year = getReleaseYear(result.releaseDate());
            String director = getDirector(map.get(result).crew());

            movies.add(new Movie(result.title(), year, director));
        }
        return movies;
    }

    private String getReleaseYear(String releaseDate) {
        logger.info("Entering TmdbService getReleaseYear method with releaseDate {}", releaseDate);

        if (releaseDate.length() > 3) {
            LocalDate date = LocalDate.parse(releaseDate);
            int year = date.getYear();
            return String.valueOf(year);
        }
        return "N/A";
    }

    private String getDirector(List<Crew> crews) {
        logger.info("Entering TmdbService getDirector method");

        Crew crew = crews.stream()
                .findFirst()
                .orElse(new Crew("N/A"));
        return crew.name();
    }

    private TmdbRecord searchByTitle(String title) throws JsonProcessingException {
        logger.info("Entering TmdbService searchByTitle method with title {}", title);

        String url = createUrlForSearchByTitle(title);
        ResponseEntity<String> response = makeExternalCall(url);

        return mapJsonToTmdbRecord(response.getBody());
    }

    private String createUrlForSearchByTitle(String title) {
        logger.info("Entering TmdbService createUrlForSearchByTitle method with title {}", title);

        return UriComponentsBuilder.fromUriString(baseUrl + "/search/movie?")
                .queryParam("api-key", apiKey)
                .queryParam("query", title)
                .queryParam("include_adult", true)
                .build()
                .toUriString();
    }

    private ResponseEntity<String> makeExternalCall(String url) {
        logger.info("Entering TmdbService makeExternalCall method with url {}", url);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = createHttpEntity();

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                String.class
        );
    }

    private HttpEntity<String> createHttpEntity() {
        logger.info("Entering TmdbService createHttpEntity method");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        headers.set("Accept", "application/json");

        return new HttpEntity<>(headers);
    }

    private TmdbRecord mapJsonToTmdbRecord(String json) throws JsonProcessingException {
        logger.info("Entering TmdbService mapJsonToTmdbRecord method");

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

    private List<Credits> searchById(List<Result> results) throws JsonProcessingException {
        logger.info("Entering TmdbService searchById method");
        List<Credits> credits = new ArrayList<>();

        for (Result r : results) {
            String url = createUrlForSearchById(r.id());
            ResponseEntity<String> response = makeExternalCall(url);
            Credits director = selectOnlyDirector(mapJsonToCredits(response.getBody()));
            credits.add(director);
        }
        return credits;
    }

    private String createUrlForSearchById(String id) {
        logger.info("Entering TmdbService createUrlForSearchById method with id {}", id);

        return UriComponentsBuilder.fromUriString(baseUrl + "/movie/" + id + "/credits")
                .queryParam("api-key", apiKey)
                .build()
                .toUriString();
    }

    private Credits selectOnlyDirector(Credits credits) {
        logger.info("Entering TmdbService selectOnlyDirector method");

        Crew director = credits.crew()
                .stream()
                .filter(crew -> "Directing".equalsIgnoreCase(crew.knownForDepartment()))
                .filter(crew -> "Directing".equalsIgnoreCase(crew.department()))
                .filter(crew -> "Director".equalsIgnoreCase(crew.job()))
                .findFirst()
                .orElse(new Crew("N/A"));

        return new Credits(credits.id(), List.of(director));
    }

    private Credits mapJsonToCredits(String json) throws JsonProcessingException {
        logger.info("Entering TmdbService mapJsonToCredits method");

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }
}
