package com.assessment.popularity.service;

import com.assessment.popularity.dto.SourceRepositoryDto;
import com.assessment.popularity.github.GithubClient;
import com.assessment.popularity.github.GithubRepository;
import com.assessment.popularity.github.GithubRepositoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RepositoriesServiceTest {

    @Mock
    private GithubClient githubClient;

    @InjectMocks
    private RepositoriesService repositoriesService;

    private static final String REPOSITORY_NAME = "testRepo";
    private static final String LANGUAGE = "java";
    private static final LocalDateTime CREATION_DATE = LocalDateTime.now().minusMonths(1);
    private static final LocalDateTime PUSHED_DATE = LocalDateTime.now().minusDays(15);
    private static final LocalDateTime EARLIEST_CREATION_DATE = LocalDateTime.now().minusMonths(3);

    @BeforeEach
    void setUp() {
    }

    @Test
    void testProcessRepositoriesAllFactorsUsedAndVeryRecent_LastPushInPreviousMonth() {
        GithubRepository repository = GithubRepository.builder()
                .name(REPOSITORY_NAME)
                .language(LANGUAGE)
                .fork(true)
                .forksCount(5)
                .watchers(2)
                .stars(3)
                .createdAt(CREATION_DATE)
                .pushedAt(PUSHED_DATE)
                .build();

        GithubRepositoryResponse mockResponse = GithubRepositoryResponse.builder()
                .items(List.of(repository))
                .build();

        when(githubClient.searchRepositories(REPOSITORY_NAME, LANGUAGE, EARLIEST_CREATION_DATE)).thenReturn(mockResponse);

        List<SourceRepositoryDto> result = repositoriesService.processRepositories(REPOSITORY_NAME, LANGUAGE, EARLIEST_CREATION_DATE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(REPOSITORY_NAME, result.get(0).getRepositoryName());
        assertEquals(1038, result.get(0).getScore()); // Check if score is correctly calculated
        verify(githubClient, times(1)).searchRepositories(REPOSITORY_NAME, LANGUAGE, EARLIEST_CREATION_DATE);
    }

    @Test
    void testProcessRepositoriesAllFactorsUsedAndRelativelyRecent_LastPushInLast6Months() {
        GithubRepository repository = GithubRepository.builder()
                .name(REPOSITORY_NAME)
                .language(LANGUAGE)
                .fork(true)
                .forksCount(5)
                .watchers(2)
                .stars(3)
                .createdAt(CREATION_DATE)
                .pushedAt(LocalDateTime.now().minusDays(32)) // In last 3 month (older than 1 month)
                .build();

        GithubRepositoryResponse mockResponse = GithubRepositoryResponse.builder()
                .items(List.of(repository))
                .build();

        when(githubClient.searchRepositories(REPOSITORY_NAME, LANGUAGE, EARLIEST_CREATION_DATE)).thenReturn(mockResponse);

        List<SourceRepositoryDto> result = repositoriesService.processRepositories(REPOSITORY_NAME, LANGUAGE, EARLIEST_CREATION_DATE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(REPOSITORY_NAME, result.get(0).getRepositoryName());
        assertEquals(538, result.get(0).getScore()); // Check if score is correctly calculated
        verify(githubClient, times(1)).searchRepositories(REPOSITORY_NAME, LANGUAGE, EARLIEST_CREATION_DATE);
    }

    @Test
    void testProcessRepositoriesAllFactorsUsedAndOld_LastPushOlderThan6Months() {
        GithubRepository repository = GithubRepository.builder()
                .name(REPOSITORY_NAME)
                .language(LANGUAGE)
                .fork(true)
                .forksCount(5)
                .watchers(2)
                .stars(3)
                .createdAt(CREATION_DATE)
                .pushedAt(LocalDateTime.now().minusDays(190)) // Older than 6 months
                .build();

        GithubRepositoryResponse mockResponse = GithubRepositoryResponse.builder()
                .items(List.of(repository))
                .build();

        when(githubClient.searchRepositories(REPOSITORY_NAME, LANGUAGE, EARLIEST_CREATION_DATE)).thenReturn(mockResponse);

        List<SourceRepositoryDto> result = repositoriesService.processRepositories(REPOSITORY_NAME, LANGUAGE, EARLIEST_CREATION_DATE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(REPOSITORY_NAME, result.get(0).getRepositoryName());
        assertEquals(138, result.get(0).getScore()); // Check if score is correctly calculated
        verify(githubClient, times(1)).searchRepositories(REPOSITORY_NAME, LANGUAGE, EARLIEST_CREATION_DATE);
    }

}
