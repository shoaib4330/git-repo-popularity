package com.assessment.popularity.service;

import com.assessment.popularity.dto.SourceRepositoryDto;
import com.assessment.popularity.github.GithubClient;
import com.assessment.popularity.github.GithubRepository;
import com.assessment.popularity.github.GithubRepositoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepositoriesService {

    private static final Logger log = LoggerFactory.getLogger(RepositoriesService.class);

    private static final Integer RECENCY_FACTOR_WEIGHT = 10;  // multiply recency by a factor of 10. Actively managed is an important factor
    private static final Integer FORK_FACTOR_WEIGHT = 5;
    private static final Integer STARS_FACTOR_WEIGHT = 3;
    private static final Integer WATCHERS_FACTOR_WEIGHT = 2;

    private final GithubClient githubClient;

    public RepositoriesService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    public List<SourceRepositoryDto> processRepositories(String repositoryName, String language, LocalDateTime earliestCreationDate) {
        GithubRepositoryResponse response = githubClient.searchRepositories(repositoryName, language, earliestCreationDate);

        log.info("Response from Github: {}", response);

        return response.getItems().stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparingInt(SourceRepositoryDto::getScore).reversed()) // Sort by score in descending order
                .collect(Collectors.toList());
    }

    private SourceRepositoryDto convertToDto(GithubRepository repository) {
        return SourceRepositoryDto.builder()
                .repositoryName(repository.getName())
                .score(calculateScore(repository))
                .language(repository.getLanguage())
                .createdAt(repository.getCreatedAt().toString())
                .lastUpdated(repository.getPushedAt().toString())
                .build();
    }

    private int calculateScore(GithubRepository repository) {
        int forksScore = repository.getForksCount() != null ? repository.getForksCount() * FORK_FACTOR_WEIGHT : 0;
        int watchersScore = repository.getWatchers() != null ? repository.getWatchers() * WATCHERS_FACTOR_WEIGHT : 0;
        int starsScore = repository.getStars() != null ? repository.getStars() * STARS_FACTOR_WEIGHT : 0;
        int recencyScore = calculateRecencyScore(repository.getPushedAt());

        return forksScore + watchersScore + starsScore + RECENCY_FACTOR_WEIGHT * recencyScore;
    }

    private int calculateRecencyScore(LocalDateTime pushedAt) {
        if (pushedAt == null) {
            return 0;
        }
        long daysSinceLastUpdate = ChronoUnit.DAYS.between(pushedAt, LocalDateTime.now());
        if (daysSinceLastUpdate <= 30) {
            return 100; // Very recent
        } else if (daysSinceLastUpdate <= 180) {
            return 50; // Relatively recent
        } else {
            return 10; // Old
        }
    }
}
