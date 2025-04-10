package com.movies.record;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.NonNull;

public record Movie(@NonNull @JsonAlias("Title") String title,
                    @NonNull @JsonAlias({"Year", "release_date"}) String year,
                    @NonNull @JsonAlias("Director") String director) {
}
