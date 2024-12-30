package com.assessment.popularity.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SourceRepositoryDto {
    private String repositoryName;
    private String language;
    private String createdAt;
    private String lastUpdated;
    private Integer score;
}
