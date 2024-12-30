package com.assessment.popularity.configuration;

import com.assessment.popularity.interceptors.WebClientLoggingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient(@Value("${github.api.url}") String githubApiUrl) {
        return WebClient.builder()
                .filter(WebClientLoggingFilter.logRequestAndResponse())
                .baseUrl(githubApiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
