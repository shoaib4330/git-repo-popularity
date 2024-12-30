package com.assessment.popularity.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
// todo: use a customised object mapper application wide, instead of annotating individual fields
public class GithubRepository {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("fork")
    private Boolean fork;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonProperty("pushed_at")
    private LocalDateTime pushedAt;
    @JsonProperty("language")
    private String language;
    @JsonProperty("forks_count")
    private Integer forksCount;
    @JsonProperty("watchers")
    private Integer watchers;
    @JsonProperty("open_issues")
    private Integer openIssues;
    @JsonProperty("stargazers_count")
    private Integer stars;
}
