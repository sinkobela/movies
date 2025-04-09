package com.movies.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "movie_search_patterns")
public class MovieSearchPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    @Column(name = "search_count", nullable = false)
    private int searchCount;

    public MovieSearchPattern(String title, String apiName, int searchCount) {
        this.title = title;
        this.apiName = apiName;
        this.searchCount = searchCount;
    }
}
