package com.movies.record;

import java.util.List;

public record Credits(String id, List<Crew> crew) {
}
