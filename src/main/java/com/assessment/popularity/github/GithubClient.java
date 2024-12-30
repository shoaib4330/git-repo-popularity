package com.assessment.popularity.github;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class GithubClient {

    private static final String SORT_CRITERIA = "stars";
    private static final String SEARCH_PATH = "/search/repositories";

    @Value("${github.access.token}")
    private String apiAccessToken;

    private WebClient webClient;

    public GithubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public GithubRepositoryResponse searchRepositories(String name, String language, LocalDateTime earliestCreationDate) {
        String processedQuery = createSearchParameter(name, language, earliestCreationDate);

        GithubRepositoryResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_PATH)
                        .queryParam("q", processedQuery)
                        .queryParam("sort", SORT_CRITERIA)
                        .queryParam("order", "desc")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiAccessToken)
                .retrieve()
                .bodyToMono(GithubRepositoryResponse.class)
                .block(); // Blocking only, as it's a synchronous request

        return response;
    }

    String createSearchParameter(String name, String language, LocalDateTime earliestCreationDate) {
        StringBuilder query = new StringBuilder(name.concat("+language:").concat(language));

        // Worth checking if date is valid, and not in future
        if (earliestCreationDate != null) {
            if (earliestCreationDate.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("The earliestCreationDate cannot be in the future.");
            }

            // Todo: Revisit this, this could be removed, as we already receive date in ISO8601
            String formattedDate = earliestCreationDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            query.append("+created:>").append(formattedDate);
        }

        // todo: look to encode using UriUtils.encodeQueryParam(query.toString(), StandardCharsets.UTF_8);
        return query.toString();
    }


}