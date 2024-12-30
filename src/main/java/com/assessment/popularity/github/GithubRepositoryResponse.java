package com.assessment.popularity.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GithubRepositoryResponse {
    @JsonProperty("total_count")
    private Integer totalCount;
    @JsonProperty("items")
    private List<GithubRepository> items;
}
