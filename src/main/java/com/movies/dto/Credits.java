package com.movies.dto;

import java.util.List;

public record Credits(String id, List<Crew> crew) {
}
