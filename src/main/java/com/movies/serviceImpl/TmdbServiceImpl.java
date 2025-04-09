package com.movies.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movies.dto.*;
import com.movies.service.TmdbService;
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
public class TmdbServiceImpl implements TmdbService {

    private static final Logger logger = LoggerFactory.getLogger(TmdbServiceImpl.class);
    private final ObjectMapper objectMapper;
    @Value("${tmdb.url}")
    private String baseUrl;
    @Value("${tmdb.api.key}")
    private String apiKey;
    @Value("${tmdb.api.read.access.token}")
    private String bearerToken;


    public List<MovieDto> searchMovie(String title) throws JsonProcessingException {
        logger.info("Entering TmdbService searchMovie method with title {}", title);

        List<ResultDto> resultDtos = searchByTitle(title).getResults();
        List<CreditsDto> creditsDtos = searchById(resultDtos);
        Map<ResultDto, CreditsDto> map = pairResultsWithCredits(resultDtos, creditsDtos);

        return getMovies(map);
    }

    private Map<ResultDto, CreditsDto> pairResultsWithCredits(List<ResultDto> results, List<CreditsDto> creditsList) {
        logger.info("Entering TmdbService pairResultsWithCredits method");
        Map<ResultDto, CreditsDto> pairedMap = new HashMap<>();

        Map<String, CreditsDto> creditsMap = creditsList.stream()
                .collect(toMap(CreditsDto::getId, credit -> credit));

        for (ResultDto result : results) {
            CreditsDto matchingCredit = creditsMap.get(result.getId());
            if (matchingCredit != null) {
                pairedMap.put(result, matchingCredit);
            }
        }
        return pairedMap;
    }

    private List<MovieDto> getMovies(Map<ResultDto, CreditsDto> map) {
        logger.info("Entering TmdbService getMovies method");
        List<MovieDto> movies = new ArrayList<>();

        for (ResultDto resultDto : map.keySet()) {
            String year = getReleaseYear(resultDto.getRelease_date());
            String director = getDirector(map.get(resultDto).getCrew());

            movies.add(new MovieDto(resultDto.getTitle(), year, director));
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

    private String getDirector(List<CrewDto> crewDtos) {
        logger.info("Entering TmdbService getDirector method");

        CrewDto crewDto = crewDtos.stream()
                .findFirst()
                .orElse(new CrewDto("N/A"));
        return crewDto.getName();
    }

    private TmdbDto searchByTitle(String title) throws JsonProcessingException {
        logger.info("Entering TmdbService searchByTitle method with title {}", title);

        String url = createUrlForSearchByTitle(title);
        ResponseEntity<String> response = makeExternalCall(url);

        return mapJsonToTmdbDto(response.getBody());
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

    private TmdbDto mapJsonToTmdbDto(String json) throws JsonProcessingException {
        logger.info("Entering TmdbService mapJsonToTmdbDto method");

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

    private List<CreditsDto> searchById(List<ResultDto> resultDtos) throws JsonProcessingException {
        logger.info("Entering TmdbService searchById method");
        List<CreditsDto> creditsDto = new ArrayList<>();

        for (ResultDto resultDto : resultDtos) {
            String url = createUrlForSearchById(resultDto.getId());
            ResponseEntity<String> response = makeExternalCall(url);
            CreditsDto director = selectOnlyDirector(mapJsonToCreditsDto(response.getBody()));
            creditsDto.add(director);
        }
        return creditsDto;
    }

    private String createUrlForSearchById(String id) {
        logger.info("Entering TmdbService createUrlForSearchById method with id {}", id);

        return UriComponentsBuilder.fromUriString(baseUrl + "/movie/" + id + "/credits")
                .queryParam("api-key", apiKey)
                .build()
                .toUriString();
    }

    private CreditsDto selectOnlyDirector(CreditsDto creditsDto) {
        logger.info("Entering TmdbService selectOnlyDirector method");

        CrewDto crewDto = creditsDto.getCrew()
                .stream()
                .filter(crew -> "Directing".equalsIgnoreCase(crew.getKnownForDepartment()))
                .filter(crew -> "Directing".equalsIgnoreCase(crew.getDepartment()))
                .filter(crew -> "Director".equalsIgnoreCase(crew.getJob()))
                .findFirst()
                .orElse(new CrewDto("N/A"));

        creditsDto.setCrew(List.of(crewDto));
        return creditsDto;
    }

    private CreditsDto mapJsonToCreditsDto(String json) throws JsonProcessingException {
        logger.info("Entering TmdbService mapJsonToCreditsDto method");

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }
}
