package com.assessment.popularity.github;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GithubClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @InjectMocks
    private GithubClient githubClient;

    private static final String API_ACCESS_TOKEN = "dummy-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchRepositories_withValidParams() {
        var mockResponse = GithubRepositoryResponse.builder()
                .totalCount(1)
                .items(List.of(GithubRepository.builder().build()))
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GithubRepositoryResponse.class)).thenReturn(Mono.just(mockResponse)); // Return fixed response


        GithubRepositoryResponse response = githubClient.searchRepositories("testRepo", "java", null);

        assertNotNull(response);
        assertEquals(1, response.getTotalCount());
        verify(webClient, times(1)).get(); // Ensure that the WebClient's get method was called once
        verify(responseSpec, times(1)).bodyToMono(GithubRepositoryResponse.class); // Ensure that bodyToMono was called
    }

    @Test
    void testSearchRepositories_withInvalidDate() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            githubClient.searchRepositories("testRepo", "java", LocalDateTime.now().plusDays(1));
        });

        assertEquals("The earliestCreationDate cannot be in the future.", exception.getMessage());
    }


    @Test
    void testCreateSearchParameter_withValidParams() {
        String query = githubClient.createSearchParameter("testRepo", "java", LocalDateTime.now().minusDays(1));

        assertTrue(query.contains("testRepo+language:java"));
        assertTrue(query.contains("created:>"));
    }

    @Test
    void testCreateSearchParameter_withNullDate() {
        String query = githubClient.createSearchParameter("testRepo", "java", null);

        assertTrue(query.contains("testRepo+language:java"));
        assertFalse(query.contains("created:>"));
    }
}
