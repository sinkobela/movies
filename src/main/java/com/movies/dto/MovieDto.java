package com.movies.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    @NonNull
    @JsonAlias("Title")
    private String title;

    @NonNull
    @JsonAlias({"Year", "release_date"})
    private String year;

    @NonNull
    @JsonAlias("Director")
    private String director;
}
