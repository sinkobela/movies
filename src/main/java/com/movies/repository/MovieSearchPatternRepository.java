package com.movies.repository;

import com.movies.model.MovieSearchPattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieSearchPatternRepository extends JpaRepository<MovieSearchPattern, Integer> {

    Optional<MovieSearchPattern> findFirstByTitleAndApiName(String title, String apiName);
}
